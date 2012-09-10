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

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 05.09.12 23:26
 */
public class UpdateChangesConstants {
  public String getServerKey() { return "guthub_host"; }
  public String getUserNameKey() { return "guthub_username"; }
  public String getPasswordKey() { return Constants.SECURE_PROPERTY_PREFIX + "guthub_username"; }
  public String getRepositoryNameKey() { return "guthub_repo"; }
  public String getRepositoryOwnerKey() { return "guthub_owner"; }
}
