package jetbrains.teamcilty.github;

import jetbrains.buildServer.serverSide.BuildServerAdapter;
import jetbrains.buildServer.serverSide.BuildServerListener;
import jetbrains.buildServer.serverSide.SRunningBuild;
import jetbrains.buildServer.util.EventDispatcher;
import jetbrains.buildServer.vcs.SVcsModification;
import jetbrains.buildServer.vcs.SelectPrevBuildPolicy;
import jetbrains.buildServer.vcs.VcsRootInstance;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 05.09.12 22:28
 */
public class ChangeStatusListener {
  public ChangeStatusListener(@NotNull EventDispatcher<BuildServerListener> listener) {
    listener.addListener(new BuildServerAdapter(){
      @Override
      public void buildStarted(SRunningBuild build) {
      }

      @Override
      public void buildFinished(SRunningBuild build) {
      }
    });
  }

  @NotNull
  private Map<VcsRootInstance, String> getLatestChangeHash(@NotNull final SRunningBuild build) {
    final List<SVcsModification> changes = build.getChanges(SelectPrevBuildPolicy.SINCE_LAST_COMPLETE_BUILD, false);
    final Map<VcsRootInstance, String> result = new HashMap<VcsRootInstance, String>();

    for (SVcsModification change : changes) {
      if (!"jetbrains.git".equals(change.getVcsRoot().getVcsName())) continue;
      result.put(change.getVcsRoot(), change.getVersion());
    }

    return result;
  }
}
