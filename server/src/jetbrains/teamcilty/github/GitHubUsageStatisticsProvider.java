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

package jetbrains.teamcilty.github;

import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.SBuildFeatureDescriptor;
import jetbrains.buildServer.serverSide.SBuildType;
import jetbrains.buildServer.serverSide.TeamCityProperties;
import jetbrains.buildServer.usageStatistics.UsageStatisticsProvider;
import jetbrains.buildServer.usageStatistics.UsageStatisticsPublisher;
import jetbrains.buildServer.usageStatistics.presentation.UsageStatisticsPresentationManager;
import jetbrains.buildServer.usageStatistics.presentation.UsageStatisticsPresentationProvider;
import jetbrains.teamcilty.github.ui.UpdateChangeStatusFeature;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Eugene Petrenko on 11/12/13.
 */
public class GitHubUsageStatisticsProvider implements UsageStatisticsProvider, UsageStatisticsPresentationProvider {
  private static final String TEAMCITY_GITHUB = "jonnyzzz.github.status";
  private ProjectManager myProjectManager;

  public GitHubUsageStatisticsProvider(@NotNull final ProjectManager projectManager) {
    myProjectManager = projectManager;
  }

  public void accept(@NotNull UsageStatisticsPublisher publisher) {
    if (!TeamCityProperties.getBooleanOrTrue("teamcity.github.report.statistics")) return;

    int count = 0;
    for (SBuildType buildType : myProjectManager.getActiveBuildTypes()) {
      for (SBuildFeatureDescriptor feature : buildType.getBuildFeatures()) {
        if (UpdateChangeStatusFeature.FEATURE_TYPE.equals(feature.getBuildFeature().getType())) {
          count++;
        }
      }
    }

    publisher.publishStatistic(TEAMCITY_GITHUB, count);
  }

  public void accept(@NotNull UsageStatisticsPresentationManager presentationManager) {
    presentationManager.applyPresentation(TEAMCITY_GITHUB, "Biuld сonfigurations with GitHub status updater", null, null, null);
  }
}

