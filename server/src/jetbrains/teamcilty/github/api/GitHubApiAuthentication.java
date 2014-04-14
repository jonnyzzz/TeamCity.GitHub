package jetbrains.teamcilty.github.api;

import org.apache.http.auth.Credentials;

public interface GitHubApiAuthentication {
  Credentials buildCredentials();
}
