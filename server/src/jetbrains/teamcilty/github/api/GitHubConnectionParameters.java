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

import org.eclipse.egit.github.core.client.GitHubClient;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vladsilav.Rassokhin@jetbrains.com
 */
public abstract class GitHubConnectionParameters {
  public static final Pattern HOST_PATTERN = Pattern.compile("(?:https?://)?([^/]+)(?:/.*)?");
  @NotNull
  protected final String myUrl;

  protected GitHubConnectionParameters(@NotNull final String url) {
    myUrl = url;
  }

  public static String getHost(@NotNull final String url) {
    final Matcher matcher = HOST_PATTERN.matcher(url);
    if (!matcher.find()) {
      return url;
    }
    return matcher.group(1);
  }

  @NotNull
  public String getUrl() {
    return myUrl;
  }

  public abstract void applyCredentials(@NotNull final GitHubClient client);

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
    public void applyCredentials(@NotNull final GitHubClient client) {
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
    public void applyCredentials(@NotNull final GitHubClient client) {
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
    public void applyCredentials(@NotNull final GitHubClient client) {
      client.setCredentials(null, null);
    }
  }
}
