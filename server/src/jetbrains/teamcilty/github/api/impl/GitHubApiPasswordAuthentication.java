package jetbrains.teamcilty.github.api.impl;

import jetbrains.teamcilty.github.api.GitHubApiAuthentication;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.jetbrains.annotations.NotNull;

public class GitHubApiPasswordAuthentication implements GitHubApiAuthentication {
  private final String myUsername;
  private final String myPassword;

  public GitHubApiPasswordAuthentication(@NotNull final String username, @NotNull final String password) {
    myUsername = username;
    myPassword = password;
  }

  @NotNull
  public UsernamePasswordCredentials buildCredentials() {
    return new UsernamePasswordCredentials(myUsername, myPassword);
  }
}
