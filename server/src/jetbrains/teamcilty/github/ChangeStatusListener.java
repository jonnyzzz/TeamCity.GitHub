package jetbrains.teamcilty.github;

import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.util.EventDispatcher;
import jetbrains.buildServer.vcs.VcsRootInstance;
import jetbrains.teamcilty.github.ui.UpdateChangeStatusFeature;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static jetbrains.teamcilty.github.ChangeStatusUpdater.Handler;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 05.09.12 22:28
 */
public class ChangeStatusListener {
  @NotNull
  private final ChangeStatusUpdater myUpdater;

  public ChangeStatusListener(@NotNull final EventDispatcher<BuildServerListener> listener,
                              @NotNull final ChangeStatusUpdater updater) {
    myUpdater = updater;
    listener.addListener(new BuildServerAdapter(){
      @Override
      public void buildStarted(SRunningBuild build) {
        updateBuildStatus(build);
      }

      @Override
      public void buildFinished(SRunningBuild build) {
        updateBuildStatus(build);
      }
    });
  }

  private void updateBuildStatus(@NotNull final SRunningBuild build) {
    SBuildType bt = build.getBuildType();
    if (bt == null) return;

    for (SBuildFeatureDescriptor feature : bt.getBuildFeatures()) {
      if (!feature.getType().equals(UpdateChangeStatusFeature.FEATURE_TYPE)) continue;

      final Handler h = myUpdater.getUpdateHandler(feature);

      Map<VcsRootInstance, String> changes = getLatestChangesHash(build);
      for (Map.Entry<VcsRootInstance, String> e : changes.entrySet()) {
        h.scheduleChangeUpdate(e.getValue(), build);
      }
    }
  }

  @NotNull
  private Map<VcsRootInstance, String> getLatestChangesHash(@NotNull final SRunningBuild build) {
    final Map<VcsRootInstance, String> result = new HashMap<VcsRootInstance, String>();
    for (BuildRevision rev : build.getRevisions()) {
      if (!"jetbrains.git".equals(rev.getRoot().getVcsName())) continue;
      result.put(rev.getRoot(), rev.getRevision());
    }
    return result;
  }
}
