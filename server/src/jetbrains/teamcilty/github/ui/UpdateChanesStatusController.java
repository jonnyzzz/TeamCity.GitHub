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
    return mw;
  }
}
