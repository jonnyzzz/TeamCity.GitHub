package jetbrains.teamcilty.github.ui;

import jetbrains.buildServer.serverSide.BuildFeature;
import jetbrains.buildServer.serverSide.InvalidProperty;
import jetbrains.buildServer.serverSide.PropertiesProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 05.09.12 22:41
 */
public class UpdateChangeStatusFeature extends BuildFeature {
  private final UpdateChangePaths myPaths;

  public UpdateChangeStatusFeature(@NotNull final UpdateChangePaths paths) {
    myPaths = paths;
  }

  @NotNull
  @Override
  public String getType() {
    return "teamcity.github.status";
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
    return new PropertiesProcessor() {
      @NotNull
      public Collection<InvalidProperty> process(@Nullable final Map<String, String> properties) {
        final Collection<InvalidProperty> result = new ArrayList<InvalidProperty>();
        if (properties == null) return result;


        return result;
      }
    };
  }

  @Nullable
  @Override
  public Map<String, String> getDefaultParameters() {
    return super.getDefaultParameters();
  }

  @Override
  public boolean isMultipleFeaturesPerBuildTypeAllowed() {
    return true;
  }
}
