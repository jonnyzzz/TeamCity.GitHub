package jetbrains.teamcilty.github.api.impl;

import jetbrains.teamcilty.github.api.GitHubApiAuthentication;
import org.apache.http.auth.UsernamePasswordCredentials;

public class GitHubApiPasswordAuthentication implements GitHubApiAuthentication {
  private final String myUsername;
  private final String myPassword;

  public GitHubApiPasswordAuthentication(String username, String password) {
    myUsername = username;
    myPassword = password;
  }

  public UsernamePasswordCredentials buildCredentials() {
    return new UsernamePasswordCredentials(myUsername, myPassword);
  }
}
