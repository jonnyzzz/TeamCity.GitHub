/*
 * Copyright 2000-2013 JetBrains s.r.o.
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

package jetbrains.teamcilty.github.api.impl;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.util.CollectionsUtil;
import jetbrains.buildServer.util.Converter;
import jetbrains.teamcilty.github.api.GitHubApi;
import jetbrains.teamcilty.github.util.LoggerHelper;
import org.eclipse.egit.github.core.*;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 * @author Vladsilav Rassokhin (vlad.rassokhin@gmail.com)
 * @author Tomaz Cerar
 *         Date: 05.09.12 23:39
 */
public class GitHubApiImpl implements GitHubApi {
  private static final Logger LOG = LoggerHelper.getInstance(GitHubApiImpl.class);
  private static final Pattern PULL_REQUEST_BRANCH = Pattern.compile("/?refs/pull/(\\d+)/(.*)");
  private final GitHubClient myClient;
  private final CommitService myCommitService;
  private final PullRequestService myPullRequestService;

  public GitHubApiImpl(@NotNull final GitHubClient client) {
    myClient = client;
    // Test client OK
    try {
      myClient.get(new GitHubRequest().setUri("/"));
    } catch (IOException e) {
      LOG.warn("Client unuseful: " + e.getMessage(), e);
    }
    myCommitService = new CommitService(myClient);
    myPullRequestService = new PullRequestService(myClient);
  }

  @Nullable
  public CommitStatus getChangeStatus(@NotNull final IRepositoryIdProvider repository, @NotNull final String sha1) throws IOException {
    final List<CommitStatus> statuses = myCommitService.getStatuses(repository, sha1);
    if (statuses == null || statuses.isEmpty()) {
      return null;
    }
    final ArrayList<CommitStatus> list = new ArrayList<CommitStatus>(statuses);
    Collections.sort(list, new Comparator<CommitStatus>() {
      public int compare(@NotNull CommitStatus o1, @NotNull CommitStatus o2) {
        // Note reverse order!
        return o2.getUpdatedAt().compareTo(o1.getUpdatedAt());
      }
    });
    return list.iterator().next();
  }

  @NotNull
  public CommitStatus setChangeStatus(@NotNull final IRepositoryIdProvider repository, @NotNull final String sha1, @NotNull final CommitStatus status) throws IOException {
    String description = status.getDescription();
    if (description != null) {
      description = truncateStringValueWithDotsAtEnd(description, 140);
      status.setDescription(description);
    }
    return myCommitService.createStatus(repository, sha1, status);
  }

  @Nullable
  private static String truncateStringValueWithDotsAtEnd(@Nullable final String str, final int maxLength) {
    if (str == null) return null;
    if (str.length() > maxLength) {
      return str.substring(0, maxLength - 2) + "\u2026";
    }
    return str;
  }

  @Nullable
  private static String getPullRequestId(@NotNull IRepositoryIdProvider repo,
                                         @NotNull String branchName) {
    final Matcher matcher = PULL_REQUEST_BRANCH.matcher(branchName);
    if (!matcher.matches()) {
      LOG.debug("Branch " + branchName + " for repo " + repo.generateId() + " does not look like pull request");
      return null;
    }

    final String pullRequestId = matcher.group(1);
    if (pullRequestId == null) {
      LOG.debug("Branch " + branchName + " for repo " + repo.generateId() + " does not contain pull request id");
      return null;
    }
    return pullRequestId;
  }

  public boolean isPullRequestMergeBranch(@NotNull String branchName) {
    final Matcher match = PULL_REQUEST_BRANCH.matcher(branchName);
    return match.matches() && "merge".equals(match.group(2));
  }

  @Nullable
  public String findPullRequestCommit(@NotNull IRepositoryIdProvider repository, @NotNull String branchName) throws IOException {

    final String pullRequestId = getPullRequestId(repository, branchName);
    if (pullRequestId == null) return null;

    //  /repos/:owner/:repo/pulls/:number
    final PullRequest pullRequest = myPullRequestService.getPullRequest(repository, Integer.parseInt(pullRequestId));
    return pullRequest.getHead().getSha();
  }

  @NotNull
  public Collection<String> getCommitParents(@NotNull IRepositoryIdProvider repository, @NotNull String hash) throws IOException {
    final RepositoryCommit commit = myCommitService.getCommit(repository, hash);
    return CollectionsUtil.convertCollection(commit.getParents(), new Converter<String, Commit>() {
      public String createFrom(@NotNull Commit source) {
        return source.getSha();
      }
    });
  }

  public CommitComment postComment(@NotNull final IRepositoryIdProvider repository,
                                   @NotNull final String hash,
                                   @NotNull final String comment) throws IOException {
    final CommitComment cc = new CommitComment();
    cc.setBody(comment);
    return myCommitService.addComment(repository, hash, cc);
  }
}
