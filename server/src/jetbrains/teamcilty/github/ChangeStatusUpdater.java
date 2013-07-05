/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.teamcilty.github;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.serverSide.executors.ExecutorServices;
import jetbrains.buildServer.util.ExceptionUtil;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.teamcilty.github.api.GitHubApi;
import jetbrains.teamcilty.github.api.GitHubApiFactory;
import jetbrains.teamcilty.github.api.GitHubChangeState;
import jetbrains.teamcilty.github.ui.UpdateChangeStatusFeature;
import jetbrains.teamcilty.github.ui.UpdateChangesConstants;
import jetbrains.teamcilty.github.util.LoggerHelper;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 06.09.12 3:29
 */
public class ChangeStatusUpdater {
  private static final Logger LOG = LoggerHelper.getInstance(ChangeStatusUpdater.class);
  private final ExecutorService myExecutor;
  @NotNull
  private final GitHubApiFactory myFactory;
  private final WebLinks myWeb;

  public ChangeStatusUpdater(@NotNull final ExecutorServices services,
                             @NotNull final GitHubApiFactory factory,
                             @NotNull final WebLinks web) {
    myFactory = factory;
    myWeb = web;
    myExecutor = services.getLowPriorityExecutorService();
  }

  private String getComment(@NotNull RepositoryVersion version,
                            @NotNull SRunningBuild build,
                            boolean completed,
                            @NotNull String hash) {
    StringBuilder comment = new StringBuilder("[Build ").append(build.getBuildNumber()).append("](").append(myWeb.getViewResultsUrl(build)).append(") ");
    if (completed) {
      comment.append("outcome was **").append(build.getStatusDescriptor().getStatus().getText()).append("**");
    } else {
      comment.append("is now running");
    }
    comment.append(" using a merge of ")
            .append(hash)
            .append("\n");
    String text = build.getStatusDescriptor().getText();
    if (completed && text != null) {
      comment.append("Summary: ").append(text).append(" Build time: ").append(getFriendlyDuration(build.getDuration()));
      if (build.getBuildStatus() != Status.NORMAL){
        final List<STestRun> failedTests = build.getFullStatistics().getFailedTests();
        if (!failedTests.isEmpty()) {
          comment.append("\n### Failed tests\n");
          comment.append("```\n");
          for (int i = 0; i < failedTests.size(); i++) {
            STestRun testRun = failedTests.get(i);
            comment.append("").append(testRun.getTest().getName().toString()).append(": ");
            comment.append(getFailureText(testRun.getFailureInfo())).append("\n\n");
            if (i==10){
              comment.append("\n##### there are ")
                      .append(build.getFullStatistics().getFailedTestCount() - i)
                      .append(" more failed tests, see build details\n");
              break;
            }
          }
          comment.append("```\n");
        }
      }
    }

    return comment.toString();
  }

  private static String getFriendlyDuration(long millis) {
    long second = (millis / 1000) % 60;
    long minute = (millis / (1000 * 60)) % 60;
    long hour = (millis / (1000 * 60 * 60)) % 24;
    return hour + ":" + minute + ":" + second;
  }

  private static String getFailureText(TestFailureInfo failureInfo) {
    if (failureInfo == null) {
      return "<no details avaliable>";
    } else {
      StringBuffer sb = new StringBuffer(failureInfo.getStacktraceMessage());
      sb.append(failureInfo.getShortStacktrace());
      return sb.toString();
    }
  }

  @NotNull
  public Handler getUpdateHandler(@NotNull final SBuildFeatureDescriptor feature) {
    if (!feature.getType().equals(UpdateChangeStatusFeature.FEATURE_TYPE)) {
      throw new IllegalArgumentException("Unexpected feature type " + feature.getType());
    }

    final UpdateChangesConstants c = new UpdateChangesConstants();
    final GitHubApi api = myFactory.openGitHub(
            feature.getParameters().get(c.getServerKey()),
            feature.getParameters().get(c.getUserNameKey()),
            feature.getParameters().get(c.getPasswordKey()));
    final String repositoryOwner = feature.getParameters().get(c.getRepositoryOwnerKey());
    final String repositoryName = feature.getParameters().get(c.getRepositoryNameKey());
    final boolean addComments = !StringUtil.isEmptyOrSpaces(feature.getParameters().get(c.getUseCommentsKey()));
    return new Handler() {

      public void scheduleChangeStarted(@NotNull RepositoryVersion version, @NotNull SRunningBuild build) {
        scheduleChangeUpdate(version, build, "TeamCity Build " + build.getFullName() + " started", GitHubChangeState.Pending);
      }

      public void scheduleChangeCompeted(@NotNull RepositoryVersion version, @NotNull SRunningBuild build) {
        GitHubChangeState status = build.getStatusDescriptor().isSuccessful() ? GitHubChangeState.Success : GitHubChangeState.Error;
        String text = build.getStatusDescriptor().getText();
        if (text != null) {
          text = ": " + text;
        } else {
          text = "";
        }
        scheduleChangeUpdate(version, build, "TeamCity Build " + build.getFullName() + " finished" + text, status);
      }

      private void scheduleChangeUpdate(@NotNull final RepositoryVersion version,
                                        @NotNull final SRunningBuild build,
                                        @NotNull final String message,
                                        @NotNull final GitHubChangeState status) {
        LOG.info("Scheduling GitHub status update for " +
                "hash: " + version.getVersion() + ", " +
                "branch: " + version.getVcsBranch() + ", " +
                "buildId: " + build.getBuildId() + ", " +
                "status: " + status);

        myExecutor.submit(ExceptionUtil.catchAll("set change status on github", new Runnable() {

          @NotNull
          private String resolveCommitHash() {
            final String vcsBranch = version.getVcsBranch();
            if (vcsBranch != null && api.isPullRequestMergeBranch(vcsBranch)) {
              try {
                final String hash = api.findPullRequestCommit(repositoryOwner, repositoryName, vcsBranch);
                if (hash == null) {
                  throw new IOException("Failed to find head hash for commit from " + vcsBranch);
                }
                LOG.info("Resolved GitHub change commit for " + vcsBranch + " to point to pull request head for " +
                        "hash: " + version.getVersion() + ", " +
                        "newHash: " + hash + ", " +
                        "branch: " + version.getVcsBranch() + ", " +
                        "buildId: " + build.getBuildId() + ", " +
                        "status: " + status);
                return hash;
              } catch (IOException e) {
                LOG.warn("Failed to find status update hash for " + vcsBranch + " for repository " + repositoryName);
              }
            }
            return version.getVersion();
          }

          public void run() {
            final String hash = resolveCommitHash();
            try {
              api.setChangeStatus(
                      repositoryOwner,
                      repositoryName,
                      hash,
                      status,
                      myWeb.getViewResultsUrl(build),
                      message
              );
              LOG.info("Updated GitHub status for hash: " + hash + ", buildId: " + build.getBuildId() + ", status: " + status);
            } catch (IOException e) {
              LOG.warn("Failed to update GitHub status for hash: " + hash + ", buildId: " + build.getBuildId() + ", status: " + status + ". " + e.getMessage(), e);
            }
            if (addComments) {
              try {
                api.postComment(
                        repositoryOwner,
                        repositoryName,
                        version.getVcsBranch(),
                        getComment(version,build,status!=GitHubChangeState.Pending,hash)
                );
                LOG.info("Added comment to GitHub PR : " + version.getVcsBranch() + ", buildId: " + build.getBuildId() + ", status: " + status);
              } catch (IOException e) {
                LOG.warn("Failed add GitHub comment for branch: " + version.getVcsBranch() + ", buildId: " + build.getBuildId() + ", status: " + status + ". " + e.getMessage(), e);
              }
            }
          }
        }));
      }
    };
  }

  public static interface Handler {
    void scheduleChangeStarted(@NotNull final RepositoryVersion hash, @NotNull final SRunningBuild build);

    void scheduleChangeCompeted(@NotNull final RepositoryVersion hash, @NotNull final SRunningBuild build);
  }
}
