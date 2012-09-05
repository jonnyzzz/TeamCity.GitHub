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
}
