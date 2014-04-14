package jetbrains.teamcilty.github.api.impl;

import jetbrains.teamcilty.github.api.GitHubApiAuthentication;
import org.apache.http.auth.UsernamePasswordCredentials;

public class GitHubApiTokenAuthentication implements GitHubApiAuthentication {
  private final String myToken;

  public GitHubApiTokenAuthentication(String token) {
    myToken = token;
  }

  public UsernamePasswordCredentials buildCredentials() {
    return new UsernamePasswordCredentials(myToken, "x-oauth-basic");
  }
}
