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
import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.util.FileUtil;
import jetbrains.teamcilty.github.api.GitHubApi;
import jetbrains.teamcilty.github.api.GitHubChangeState;
import jetbrains.teamcilty.github.api.impl.data.CommitInfo;
import jetbrains.teamcilty.github.api.impl.data.CommitStatus;
import jetbrains.teamcilty.github.api.impl.data.PullRequestInfo;
import jetbrains.teamcilty.github.api.impl.data.RepoInfo;
import jetbrains.teamcilty.github.util.LoggerHelper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 05.09.12 23:39
 */
public class GitHubApiImpl implements GitHubApi {
  private static final Logger LOG = LoggerHelper.getInstance(GitHubApiImpl.class);

  @NotNull
  private final HttpClientWrapper myClient;
  private final Gson myGson = new Gson();
  private final GitHubApiPaths myUrls;
  private final String myUserName;
  private final String myPassword;

  public GitHubApiImpl(@NotNull final HttpClientWrapper client,
                       @NotNull final GitHubApiPaths urls,
                       @NotNull final String userName,
                       @NotNull final String password) {
    myClient = client;
    myUrls = urls;
    myUserName = userName;
    myPassword = password;
  }

  public String readChangeStatus(@NotNull final String repoOwner,
                                 @NotNull final String repoName,
                                 @NotNull final String hash) throws IOException {
    final HttpGet post = new HttpGet(myUrls.getStatusUrl(repoOwner, repoName, hash));
    includeAuthentication(post);
    post.setHeader(new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "UTF-8"));

    try {
      final HttpResponse execute = myClient.execute(post);
      if (execute.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
        logFailedRequest(post, null, execute);
        throw new IOException("Failed to complete request to GitHub. Status: " + execute.getStatusLine());
      }
      return "TBD";
    } finally {
      post.abort();
    }
  }

  public void setChangeStatus(@NotNull final String repoOwner,
                              @NotNull final String repoName,
                              @NotNull final String hash,
                              @NotNull final GitHubChangeState status,
                              @NotNull final String targetUrl,
                              @NotNull final String description) throws IOException {
    final GSonEntity requestEntity = new GSonEntity(myGson, new CommitStatus(status.getState(), targetUrl, description));
    final HttpPost post = new HttpPost(myUrls.getStatusUrl(repoOwner, repoName, hash));
    try {
      post.setEntity(requestEntity);
      includeAuthentication(post);
      post.setHeader(new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "UTF-8"));

      final HttpResponse execute = myClient.execute(post);
      if (execute.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_CREATED) {
        logFailedRequest(post, requestEntity.getText(), execute);
        throw new IOException("Failed to complete request to GitHub. Status: " + execute.getStatusLine());
      }
    } finally {
      post.abort();
    }
  }

  private static final Pattern PULL_REQUEST_BRANCH = Pattern.compile("/?refs/pull/(\\d+)/(.*)");

  public boolean isPullRequestMergeBranch(@NotNull String branchName) {
    final Matcher match = PULL_REQUEST_BRANCH.matcher(branchName);
    return match.matches() && "merge".equals(match.group(2));
  }

  @Nullable
  public String findPullRequestCommit(@NotNull String repoOwner,
                                      @NotNull String repoName,
                                      @NotNull String branchName) throws IOException {

    final Matcher matcher = PULL_REQUEST_BRANCH.matcher(branchName);
    if (!matcher.matches()) {
      LOG.debug("Branch " + branchName + " for repo " + repoName + " does not look like pull request");
      return null;
    }

    final String pullRequestId = matcher.group(1);
    if (pullRequestId == null) {
      LOG.debug("Branch " + branchName + " for repo " + repoName + " does not contain pull request id");
      return null;
    }

    //  /repos/:owner/:repo/pulls/:number

    final String requestUrl = myUrls.getPullRequestInfo(repoOwner, repoName, pullRequestId);
    final HttpGet get = new HttpGet(requestUrl);
    includeAuthentication(get);
    get.setHeader(new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "UTF-8"));
    get.setHeader(new BasicHeader(HttpHeaders.ACCEPT, "application/json"));

    final PullRequestInfo pullRequestInfo = processResponse(get, PullRequestInfo.class);

    final RepoInfo head = pullRequestInfo.head;
    if (head != null) {
      return head.sha;
    }
    return null;
  }

  @NotNull
  public Collection<String> getCommitParents(@NotNull String repoOwner, @NotNull String repoName, @NotNull String hash) throws IOException {

    final String requestUrl = myUrls.getCommitInfo(repoOwner, repoName, hash);
    final HttpGet get = new HttpGet(requestUrl);

    final CommitInfo infos = processResponse(get, CommitInfo.class);
    if (infos.parents != null) {
      final Set<String> parents = new HashSet<String>();
      for (CommitInfo p : infos.parents) {
        String sha = p.sha;
        if (sha != null) {
          parents.add(sha);
        }
      }
      return parents;
    }
    return Collections.emptyList();
  }

  @NotNull
  private <T> T processResponse(@NotNull HttpUriRequest request, @NotNull final Class<T> clazz) throws IOException {
    request.setHeader(new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "UTF-8"));
    request.setHeader(new BasicHeader(HttpHeaders.ACCEPT, "application/json"));
    try {
      final HttpResponse execute = myClient.execute(request);
      if (execute.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
        logFailedRequest(request, null, execute);
        throw new IOException("Failed to complete request to GitHub. Status: " + execute.getStatusLine());
      }

      final HttpEntity entity = execute.getEntity();
      if (entity == null) {
        logFailedRequest(request, null, execute);
        throw new IOException("Failed to complete request to GitHub. Empty response. Status: " + execute.getStatusLine());
      }

      try {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        entity.writeTo(bos);
        final String json = bos.toString("utf-8");
        LOG.debug("Parsing json for " + request.getURI().toString() + ": " + json);
        return myGson.fromJson(json, clazz);
      } finally {
        EntityUtils.consume(entity);
      }
    } finally {
      request.abort();
    }
  }

  private void includeAuthentication(@NotNull HttpRequest request) throws IOException {
    try {
      request.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials(myUserName, myPassword), request));
    } catch (AuthenticationException e) {
      throw new IOException("Failed to set authentication for request. " + e.getMessage(), e);
    }
  }

  private void logFailedRequest(@NotNull HttpUriRequest requestUrl,
                                @Nullable String requestEntity,
                                @NotNull HttpResponse execute) throws IOException {
    String responseText = extractResponseEntity(execute);
    if (responseText == null) {
      responseText = "<none>";
    }
    if (requestEntity == null) {
      requestEntity = "<none>";
    }

    LOG.debug("Failed to complete query to GitHub with:\n" +
            "  requestURL: " + requestUrl.getURI().toString() + "\n" +
            "  requestMethod: " + requestUrl.getMethod() + "\n" +
            "  requestEntity: " + requestEntity + "\n" +
            "  response: " + execute.getStatusLine() + "\n" +
            "  responseEntity: " + responseText
    );
  }

  @Nullable
  private String extractResponseEntity(@NotNull final HttpResponse execute) throws IOException {
    final HttpEntity responseEntity = execute.getEntity();
    if (responseEntity == null) return null;
    try {
      final byte[] dataSlice = new byte[256 * 1024]; //limit buffer with 256K
      final InputStream content = responseEntity.getContent();
      try {
        int sz = content.read(dataSlice, 0, dataSlice.length);
        return new String(dataSlice, 0, sz, "utf-8");
      } finally {
        FileUtil.close(content);
      }
    } finally {
      EntityUtils.consume(responseEntity);
    }
  }
}
