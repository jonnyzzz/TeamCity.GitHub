package jetbrains.teamcilty.github.api.impl;

import jetbrains.teamcilty.github.api.GitHubApiAuthentication;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.jetbrains.annotations.NotNull;

public class GitHubApiTokenAuthentication implements GitHubApiAuthentication {
  private final String myToken;

  public GitHubApiTokenAuthentication(@NotNull final String token) {
    myToken = token;
  }

  @NotNull
  public UsernamePasswordCredentials buildCredentials() {
    return new UsernamePasswordCredentials(myToken, "x-oauth-basic");
  }
}
