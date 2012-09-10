/*
 * Copyright 2000-2012 JetBrains s.r.o.
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

package jetbrains.teamcilty.github.api.impl;

import com.google.gson.Gson;
import jetbrains.teamcilty.github.api.GitHubApi;
import jetbrains.teamcilty.github.api.GitHubChangeState;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.message.BasicHeader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 05.09.12 23:39
 */
public class GitHubApiImpl implements GitHubApi {
  @NotNull
  private final HttpClientWrapper myClient;
  private final Gson myGson = new Gson();
  private final String myUrl;
  private final String myUserName;
  private final String myPassword;

  public GitHubApiImpl(@NotNull final HttpClientWrapper client,
                       @NotNull final String url,
                       @NotNull final String userName,
                       @NotNull final String password) {
    myClient = client;
    myUrl = url;
    myUserName = userName;
    myPassword = password;
  }

  public String readChangeStatus(@NotNull final String repoOwner,
                                 @NotNull final String repoName,
                                 @NotNull final String hash) throws IOException {
    final HttpGet post = new HttpGet(getStatusUrl(repoOwner, repoName, hash));
    try {
      final HttpResponse execute = myClient.execute(post);
      if (execute.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
        throw new IOException("Failed to complete request to GitHub. Status: " + execute.getStatusLine());
      }
      return "TBD";
    } finally {
      post.abort();
    }
  }

  @SuppressWarnings("UnusedDeclaration")
  private static class CommitStatus {
    private String state;
    private String target_url;
    private String description;

    private CommitStatus(String state, String target_url, String description) {
      this.state = state;
      this.target_url = target_url;
      this.description = description;
    }
  }

  @NotNull
  private String serializeGSon(@Nullable Object o) {
    return o == null ? "" : myGson.toJson(o);
  }

  private class GSonEntity extends StringEntity {
    private GSonEntity(@NotNull final Object object) throws UnsupportedEncodingException {
      super(serializeGSon(object), "application/json", "UTF-8");
    }
  }

  public void setChangeStatus(@NotNull final String repoOwner,
                              @NotNull final String repoName,
                              @NotNull final String hash,
                              @NotNull final GitHubChangeState status,
                              @NotNull final String targetUrl,
                              @NotNull final String description) throws IOException {
    final HttpPost post = new HttpPost(getStatusUrl(repoOwner, repoName, hash));
    try {
      post.setEntity(new GSonEntity(new CommitStatus(status.getState(), targetUrl, description)));
      post.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials(myUserName, myPassword), post));
      post.setHeader(new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "UTF-8"));

      final HttpResponse execute = myClient.execute(post);
      if (execute.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_CREATED) {
        throw new IOException("Failed to complete request to GitHub. Status: " + execute.getStatusLine());
      }
    } catch (AuthenticationException e) {
      throw new IOException(e);
    } finally {
      post.abort();
    }
  }

  private String getStatusUrl(@NotNull final String ownerName,
                              @NotNull final String repoName,
                              @NotNull final String hash) {
    return myUrl + "/repos/" + ownerName + "/" + repoName + "/statuses/" + hash;
  }
}
