/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.teamcilty.github.api;

import jetbrains.buildServer.version.ServerVersionHolder;
import jetbrains.teamcilty.github.api.impl.ApacheHttpBasedGitHubClient;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vladsilav.Rassokhin@jetbrains.com
 */
public abstract class GitHubConnectionParameters {
  @NotNull
  protected final String myUrl;

  protected GitHubConnectionParameters(@NotNull final String url) {
    myUrl = url;
  }

  public static String getHost(@NotNull final String url) {
    final Pattern pattern = Pattern.compile("(?:https?://)?([^/]+)(?:/.*)?");
    final Matcher matcher = pattern.matcher(url);
    if (!matcher.find()) {
      return url;
    }
    return matcher.group(1);
  }

  @NotNull
  public String getUrl() {
    return myUrl;
  }

  protected abstract void applyToGitHubClient(@NotNull final GitHubClient client);

  @NotNull
  public GitHubClient create() {
    GitHubClient client;
    try {
      client = ApacheHttpBasedGitHubClient.createClient(myUrl);
    } catch (IllegalArgumentException e) {
      client = new ApacheHttpBasedGitHubClient(getHost(myUrl));
    }
    client.setUserAgent("JetBrains TeamCity " + ServerVersionHolder.getVersion().getDisplayVersion());
    applyToGitHubClient(client);

    return client;
  }

  public static class Basic extends GitHubConnectionParameters {
    @NotNull
    private final String myUsername;
    @NotNull
    private final String myPassword;

    public Basic(@NotNull final String url, @NotNull final String username, @NotNull final String password) {
      super(url);
      myUsername = username;
      myPassword = password;
    }

    @Override
    protected void applyToGitHubClient(@NotNull final GitHubClient client) {
      client.setCredentials(myUsername, myPassword);
    }

    @NotNull
    public String getUsername() {
      return myUsername;
    }

    @NotNull
    public String getPassword() {
      return myPassword;
    }
  }

  public static class OAuth2 extends GitHubConnectionParameters {
    @NotNull
    private final String myOAuth2Token;

    public OAuth2(@NotNull final String url, @NotNull final String token) {
      super(url);
      myOAuth2Token = token;
    }

    @Override
    protected void applyToGitHubClient(@NotNull final GitHubClient client) {
      client.setOAuth2Token(myOAuth2Token);
    }

    @NotNull
    public String getOAuth2Token() {
      return myOAuth2Token;
    }
  }

  public static class Anonymous extends GitHubConnectionParameters {
    public Anonymous(@NotNull final String url) {
      super(url);
    }

    @Override
    protected void applyToGitHubClient(@NotNull final GitHubClient client) {
      client.setCredentials(null, null);
    }
  }
}
