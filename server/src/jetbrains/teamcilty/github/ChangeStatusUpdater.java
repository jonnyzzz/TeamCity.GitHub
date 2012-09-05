package jetbrains.teamcilty.github;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.StatusDescriptor;
import jetbrains.buildServer.serverSide.SBuildFeatureDescriptor;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.serverSide.WebLinks;
import jetbrains.buildServer.serverSide.executors.ExecutorServices;
import jetbrains.buildServer.util.ExceptionUtil;
import jetbrains.teamcilty.github.api.GitHubApi;
import jetbrains.teamcilty.github.api.GitHubApiFactory;
import jetbrains.teamcilty.github.api.GitHubChangeState;
import jetbrains.teamcilty.github.ui.UpdateChangeStatusFeature;
import jetbrains.teamcilty.github.ui.UpdateChangesConstants;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 06.09.12 3:29
 */
public class ChangeStatusUpdater {
  private static final Logger LOG = Logger.getInstance(ChangeStatusUpdater.class.getName());

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

  public static interface Handler {
    void scheduleChangeUpdate(@NotNull final String hash, @NotNull final SRunningBuild build);
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
    final String repositoryName = feature.getParameters().get(c.getRepositoryNameKey());

    return new Handler() {
      @NotNull
      private GitHubChangeState resolveState(@NotNull final SRunningBuild build) {
        if (!build.isFinished()) return GitHubChangeState.Pending;
        StatusDescriptor status = build.getStatusDescriptor();
        return status.isSuccessful() ? GitHubChangeState.Success : GitHubChangeState.Error;
      }

      public void scheduleChangeUpdate(@NotNull final String hash, @NotNull final SRunningBuild build) {
        myExecutor.submit(ExceptionUtil.catchAll("set change status on github", new Runnable() {
          public void run() {
            try {

              api.setChangeStatus(
                      repositoryName,
                      hash,
                      resolveState(build),
                      myWeb.getViewResultsUrl(build),
                      build.getStatusDescriptor().getText()
              );

            } catch (IOException e) {
              LOG.warn("Failed to update change status for hash: " + hash + ". " + e.getMessage(), e);
            }
          }
        }));
      }
    };
  }
}
