package jetbrains.teamcilty.github.api.impl;

import jetbrains.teamcilty.github.api.GitHubApi;
import jetbrains.teamcilty.github.api.GitHubApiFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 06.09.12 2:54
 */
public class GitHubApiFactoryImpl implements GitHubApiFactory {
  private final HttpClientWrapper myWrapper;

  public GitHubApiFactoryImpl(@NotNull final HttpClientWrapper wrapper) {
    myWrapper = wrapper;
  }

  @NotNull
  public GitHubApi openGitHub(@NotNull String url, @NotNull String username, @NotNull String password) {
    return new GitHubApiImpl(myWrapper, url, username, password);
  }
}
