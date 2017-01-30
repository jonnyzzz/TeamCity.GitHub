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

import jetbrains.buildServer.agent.Constants;
import jetbrains.teamcilty.github.api.GitHubApiAuthenticationType;
import jetbrains.teamcilty.github.api.GitHubApiReportEvent;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 05.09.12 23:26
 */
public class UpdateChangesConstants {
  public String getServerKey() { return "github_host"; }
  public String getUserNameKey() { return "github_username"; }
  public String getPasswordKey() { return Constants.SECURE_PROPERTY_PREFIX + "github_username"; }
  public String getRepositoryNameKey() { return "github_repo"; }
  public String getRepositoryOwnerKey() { return "github_owner"; }
  public String getContextKey() { return "github_context"; }
  public String getReportOnKey() { return "github_report_on"; }
  public String getReportOnStartAndFinishValue() { return GitHubApiReportEvent.ON_START_AND_FINISH.getValue(); }
  public String getReportOnStartValue() { return GitHubApiReportEvent.ON_START.getValue(); }
  public String getReportOnFinishValue() { return GitHubApiReportEvent.ON_FINISH.getValue(); }
  public String getUseCommentsKey() { return "github_comments"; }
  public String getUseGuestUrlsKey() { return "github_guest"; }
  public String getAccessTokenKey() { return Constants.SECURE_PROPERTY_PREFIX +"github_access_token"; }
  public String getAuthenticationTypeKey() { return "github_authentication_type";}
  public String getAuthenticationTypePasswordValue() { return GitHubApiAuthenticationType.PASSWORD_AUTH.getValue();}
  public String getAuthenticationTypeTokenValue() { return GitHubApiAuthenticationType.TOKEN_AUTH.getValue();}
}
