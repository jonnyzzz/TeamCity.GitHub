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

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 05.09.12 22:44
 */
public class UpdateChanesStatusController extends BaseController {
  @NotNull
  private final PluginDescriptor myDescriptor;

  public UpdateChanesStatusController(@NotNull final PluginDescriptor descriptor,
                                      @NotNull final UpdateChangePaths paths,
                                      @NotNull final WebControllerManager web) {
    myDescriptor = descriptor;
    web.registerController(paths.getControllerPath(), this);
  }

  @Nullable
  @Override
  protected ModelAndView doHandle(@NotNull final HttpServletRequest request,
                                  @NotNull final HttpServletResponse response) throws Exception {
    final ModelAndView mw = new ModelAndView(myDescriptor.getPluginResourcesPath("feature.jsp"));
    //TODO: find build type settings and check for VCS roots to note the user
    //TODO: may also introduce check-connection like API here too
    return mw;
  }
}
