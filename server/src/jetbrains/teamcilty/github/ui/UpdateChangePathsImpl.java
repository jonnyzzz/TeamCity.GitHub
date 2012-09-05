package jetbrains.teamcilty.github.ui;

import jetbrains.buildServer.web.openapi.PluginDescriptor;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 05.09.12 22:49
 */
public class UpdateChangePathsImpl implements UpdateChangePaths {
  private final PluginDescriptor myDescriptor;

  public UpdateChangePathsImpl(@NotNull final PluginDescriptor descriptor) {
    myDescriptor = descriptor;
  }

  @NotNull
  public String getControllerPath() {
    return myDescriptor.getPluginResourcesPath("feature.html");
  }
}
