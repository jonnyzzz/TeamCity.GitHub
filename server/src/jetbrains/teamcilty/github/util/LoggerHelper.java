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

package jetbrains.teamcilty.github.util;

import com.intellij.openapi.diagnostic.Logger;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 04.03.13 9:36
 */
public class LoggerHelper {
  private static final String JETBRAINS_TEAMCITY = "jetbrains.teamcity.";

  @NotNull
  public static Logger getInstance(@NotNull Class<?> clazz) {
    String name = clazz.getName();
    if (name.startsWith(JETBRAINS_TEAMCITY)) {
      name = name.substring(JETBRAINS_TEAMCITY.length());
    }
    //default TeamCity loggers configuration contains only
    //records for 'jetbrains.buildServer.' root category (log4j)
    //so to easily provide logging from plugin it's easier
    //to fix loggers categories at the moment of logger
    //creating
    return Logger.getInstance("jetbrains.buildServer." + name);
  }
}
