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
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.util.EventDispatcher;
import jetbrains.teamcilty.github.ui.UpdateChangeStatusFeature;
import jetbrains.teamcilty.github.util.LoggerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

import static jetbrains.teamcilty.github.ChangeStatusUpdater.Handler;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 05.09.12 22:28
 */
public class ChangeStatusListener {
  private static final Logger LOG = LoggerHelper.getInstance(ChangeStatusListener.class);

  @NotNull
  private final ChangeStatusUpdater myUpdater;

  public ChangeStatusListener(@NotNull final EventDispatcher<BuildServerListener> listener,
                              @NotNull final ChangeStatusUpdater updater) {
    myUpdater = updater;
    listener.addListener(new BuildServerAdapter(){
      @Override
      public void changesLoaded(@NotNull final SRunningBuild build) {
        updateBuildStatus(build, true);
      }

      @Override
      public void buildInterrupted(@NotNull final SRunningBuild build) {
        updateBuildStatus(build, false);
      }

      @Override
      public void buildFinished(@NotNull final SRunningBuild build) {
        updateBuildStatus(build, false);
      }
    });
  }

  private void updateBuildStatus(@NotNull final SRunningBuild build, boolean isStarting) {
    SBuildType bt = build.getBuildType();
    if (bt == null) return;

    for (SBuildFeatureDescriptor feature : bt.getResolvedSettings().getBuildFeatures()) {
      if (!feature.getType().equals(UpdateChangeStatusFeature.FEATURE_TYPE)) continue;

      final Handler h = myUpdater.getUpdateHandler(feature);

      final Collection<BuildRevision> changes = getLatestChangesHash(build);
      if (changes.isEmpty()) {
        LOG.warn("No revisions were found to update GitHub status. Please check you have Git VCS roots in the build configuration");
      }

      for (BuildRevision e : changes) {
        if (isStarting) {
          h.scheduleChangeStarted(e.getRepositoryVersion(), build);
        } else {
          h.scheduleChangeCompeted(e.getRepositoryVersion(), build);
        }
      }
    }
  }

  @NotNull
  private Collection<BuildRevision> getLatestChangesHash(@NotNull final SRunningBuild build) {
    final Collection<BuildRevision> result = new ArrayList<BuildRevision>();
    for (BuildRevision rev : build.getRevisions()) {
      if (!"jetbrains.git".equals(rev.getRoot().getVcsName())) continue;

      LOG.debug("Found revision to report status to GitHub: " + rev.getRevision() + ", branch: " + rev.getRepositoryVersion().getVcsBranch() + " from root " + rev.getRoot().getName());
      result.add(rev);
    }
    return result;
  }
}
