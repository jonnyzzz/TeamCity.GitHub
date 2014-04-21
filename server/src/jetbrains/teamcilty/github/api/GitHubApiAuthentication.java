package jetbrains.teamcilty.github.api;

import org.apache.http.auth.Credentials;
import org.jetbrains.annotations.NotNull;

public interface GitHubApiAuthentication {
  @NotNull
  Credentials buildCredentials();
}
