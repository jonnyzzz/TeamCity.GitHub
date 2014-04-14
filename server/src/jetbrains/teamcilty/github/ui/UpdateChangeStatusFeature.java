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

package jetbrains.teamcilty.github.ui;

import jetbrains.buildServer.parameters.ReferencesResolverUtil;
import jetbrains.buildServer.serverSide.BuildFeature;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.teamcilty.github.api.GitHubApiAuthenticationType;
import jetbrains.teamcilty.github.api.GitHubApiFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 05.09.12 22:41
 */
public class UpdateChangeStatusFeature extends BuildFeature {
  public static final String FEATURE_TYPE = "teamcity.github.status";
  private final UpdateChangePaths myPaths;

  public UpdateChangeStatusFeature(@NotNull final UpdateChangePaths paths) {
    myPaths = paths;
  }

  @NotNull
  @Override
  public String getType() {
    return FEATURE_TYPE;
  }

  @NotNull
  @Override
  public String getDisplayName() {
    return "Report change status to GitHub";
  }

  @Nullable
  @Override
  public String getEditParametersUrl() {
    return myPaths.getControllerPath();
  }

  @NotNull
  @Override
  public String describeParameters(@NotNull Map<String, String> params) {
    return "Update change status into GitHub";
  }

  @Nullable
  @Override
  public PropertiesProcessor getParametersProcessor() {
    final UpdateChangesConstants c = new UpdateChangesConstants();
    return new PropertiesProcessor() {
      private boolean checkNotEmpty(@NotNull final Map<String, String> properties,
                                 @NotNull final String key,
                                 @NotNull final String message,
                                 @NotNull final Collection<InvalidProperty> res) {
        if (isEmpty(properties, key)) {
          res.add(new InvalidProperty(key, message));
          return true;
        }
        return false;
      }

      private boolean isEmpty(@NotNull final Map<String, String> properties,
                              @NotNull final String key) {
        return StringUtil.isEmptyOrSpaces(properties.get(key));
      }

      @NotNull
      public Collection<InvalidProperty> process(@Nullable final Map<String, String> p) {
        final Collection<InvalidProperty> result = new ArrayList<InvalidProperty>();
        if (p == null) return result;

        GitHubApiAuthenticationType authenticationType = GitHubApiAuthenticationType.valueOf(p.get(c.getAuthenticationTypeKey()));
        if (authenticationType == GitHubApiAuthenticationType.PASSWORD_AUTH) {
          checkNotEmpty(p, c.getUserNameKey(), "Username must be specified", result);
          checkNotEmpty(p, c.getPasswordKey(), "Password must be specified", result);
        }

        if (authenticationType == GitHubApiAuthenticationType.TOKEN_AUTH) {
          checkNotEmpty(p, c.getAccessTokenKey(), "Personal Access Token must be specified", result);
        }

        checkNotEmpty(p, c.getRepositoryNameKey(), "Repository name must be specified", result);
        checkNotEmpty(p, c.getRepositoryOwnerKey(), "Repository owner must be specified", result);

        if (!checkNotEmpty(p, c.getServerKey(), "GitHub api URL", result)) {
          final String url = "" + p.get(c.getServerKey());
          if (!ReferencesResolverUtil.mayContainReference(url) && !(url.startsWith("http://") || url.startsWith("https://"))) {
            result.add(new InvalidProperty(c.getServerKey(), "GitHub api URL should start with http:// or https://"));
          }
        }

        return result;
      }
    };
  }

  @Nullable
  @Override
  public Map<String, String> getDefaultParameters() {
    final Map<String, String> map = new HashMap<String, String>();
    map.put(new UpdateChangesConstants().getServerKey(), GitHubApiFactory.DEFAULT_URL);
    return map;
  }

  @Override
  public boolean isMultipleFeaturesPerBuildTypeAllowed() {
    return true;
  }
}
