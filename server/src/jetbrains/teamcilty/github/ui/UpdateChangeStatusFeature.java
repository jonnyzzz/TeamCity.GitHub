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
      private void checkNotEmpty(@NotNull final Map<String, String> properties,
                                 @NotNull final String key,
                                 @NotNull final String message,
                                 @NotNull final Collection<InvalidProperty> res) {
        if (jetbrains.buildServer.util.StringUtil.isEmptyOrSpaces(properties.get(key))) {
          res.add(new InvalidProperty(key, message));
        }
      }

      @NotNull
      public Collection<InvalidProperty> process(@Nullable final Map<String, String> p) {
        final Collection<InvalidProperty> result = new ArrayList<InvalidProperty>();
        if (p == null) return result;

        checkNotEmpty(p, c.getUserNameKey(), "Username must be specified", result);
        checkNotEmpty(p, c.getPasswordKey(), "Password must be specified", result);
        checkNotEmpty(p, c.getRepositoryNameKey(), "Repository name must be specified", result);

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
