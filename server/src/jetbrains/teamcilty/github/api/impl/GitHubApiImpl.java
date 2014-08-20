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
import jetbrains.teamcilty.github.api.impl.data.*;
import jetbrains.teamcilty.github.util.LoggerHelper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
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
 * @author Eugene Petrenko (eugene.petrenko@gmail.com)
 * @author Tomaz Cerar
 *         Date: 05.09.12 23:39
 */
public abstract class GitHubApiImpl implements GitHubApi {
  private static final Logger LOG = LoggerHelper.getInstance(GitHubApiImpl.class);
  private static final Pattern PULL_REQUEST_BRANCH = Pattern.compile("/?refs/pull/(\\d+)/(.*)");

  private final HttpClientWrapper myClient;
  private final GitHubApiPaths myUrls;
  private final Gson myGson = new Gson();

  public GitHubApiImpl(@NotNull final HttpClientWrapper client,
                       @NotNull final GitHubApiPaths urls
  ) {
    myClient = client;
    myUrls = urls;
  }

  @Nullable
  private static String getPullRequestId(@NotNull String repoName,
                                         @NotNull String branchName) {
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
    return pullRequestId;
  }

  public String readChangeStatus(@NotNull final String repoOwner,
                                 @NotNull final String repoName,
                                 @NotNull final String hash) throws IOException {
    final HttpGet post = new HttpGet(myUrls.getStatusUrl(repoOwner, repoName, hash));
    includeAuthentication(post);
    setDefaultHeaders(post);

    try {
      logRequest(post, null);

      final HttpResponse execute = myClient.execute(post);
      if (execute.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
        logFailedResponse(post, null, execute);
        throw new IOException("Failed to complete request to GitHub. Status: " + execute.getStatusLine());
      }
      return "TBD";
    } finally {
      post.abort();
    }
  }

  private void setDefaultHeaders(@NotNull HttpUriRequest request) {
    request.setHeader(new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "UTF-8"));
    request.setHeader(new BasicHeader(HttpHeaders.ACCEPT, "application/json"));
  }

  public void setChangeStatus(@NotNull final String repoOwner,
                              @NotNull final String repoName,
                              @NotNull final String hash,
                              @NotNull final GitHubChangeState status,
                              @NotNull final String targetUrl,
                              @NotNull final String description,
                              @NotNull final String context) throws IOException {
    final GSonEntity requestEntity = new GSonEntity(myGson, new CommitStatus(status.getState(), targetUrl, description, context));
    final HttpPost post = new HttpPost(myUrls.getStatusUrl(repoOwner, repoName, hash));
    try {
      post.setEntity(requestEntity);
      includeAuthentication(post);
      setDefaultHeaders(post);

      logRequest(post, requestEntity.getText());
      final HttpResponse execute = myClient.execute(post);
      if (execute.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_CREATED) {
        logFailedResponse(post, requestEntity.getText(), execute);
        throw new IOException("Failed to complete request to GitHub. Status: " + execute.getStatusLine());
      }
    } finally {
      post.abort();
    }
  }

  public boolean isPullRequestMergeBranch(@NotNull String branchName) {
    final Matcher match = PULL_REQUEST_BRANCH.matcher(branchName);
    return match.matches() && "merge".equals(match.group(2));
  }

  @Nullable
  public String findPullRequestCommit(@NotNull String repoOwner,
                                      @NotNull String repoName,
                                      @NotNull String branchName) throws IOException {

    final String pullRequestId = getPullRequestId(repoName, branchName);
    if (pullRequestId == null) return null;

    //  /repos/:owner/:repo/pulls/:number

    final String requestUrl = myUrls.getPullRequestInfo(repoOwner, repoName, pullRequestId);
    final HttpGet get = new HttpGet(requestUrl);
    includeAuthentication(get);
    setDefaultHeaders(get);

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
    setDefaultHeaders(request);
    try {
      logRequest(request, null);

      final HttpResponse execute = myClient.execute(request);
      if (execute.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
        logFailedResponse(request, null, execute);
        throw new IOException("Failed to complete request to GitHub. Status: " + execute.getStatusLine());
      }

      final HttpEntity entity = execute.getEntity();
      if (entity == null) {
        logFailedResponse(request, null, execute);
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
      setAuthentication(request);
    } catch (AuthenticationException e) {
      throw new IOException("Failed to set authentication for request. " + e.getMessage(), e);
    }
  }

  protected abstract void setAuthentication(@NotNull final HttpRequest request) throws AuthenticationException;


  private void logFailedResponse(@NotNull HttpUriRequest request,
                                 @Nullable String requestEntity,
                                 @NotNull HttpResponse execute) throws IOException {
    String responseText = extractResponseEntity(execute);
    if (responseText == null) {
      responseText = "<none>";
    }
    if (requestEntity == null) {
      requestEntity = "<none>";
    }

    LOG.warn("Failed to complete query to GitHub with:\n" +
            "  requestURL: " + request.getURI().toString() + "\n" +
            "  requestMethod: " + request.getMethod() + "\n" +
            "  requestEntity: " + requestEntity + "\n" +
            "  response: " + execute.getStatusLine() + "\n" +
            "  responseEntity: " + responseText
    );
  }

  private void logRequest(@NotNull HttpUriRequest request,
                          @Nullable String requestEntity) throws IOException {
    if (!LOG.isDebugEnabled()) return;

    if (requestEntity == null) {
      requestEntity = "<none>";
    }

    LOG.debug("Calling GitHub with:\n" +
            "  requestURL: " + request.getURI().toString() + "\n" +
            "  requestMethod: " + request.getMethod() + "\n" +
            "  requestEntity: " + requestEntity
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

  public void postComment(@NotNull final String ownerName,
                          @NotNull final String repoName,
                          @NotNull final String hash,
                          @NotNull final String comment) throws IOException {

    final String requestUrl = myUrls.getAddCommentUrl(ownerName, repoName, hash);
    final GSonEntity requestEntity = new GSonEntity(myGson, new IssueComment(comment));
    final HttpPost post = new HttpPost(requestUrl);
    try {
      post.setEntity(requestEntity);
      includeAuthentication(post);
      setDefaultHeaders(post);

      logRequest(post, requestEntity.getText());

      final HttpResponse execute = myClient.execute(post);
      if (execute.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_CREATED) {
        logFailedResponse(post, requestEntity.getText(), execute);
        throw new IOException("Failed to complete request to GitHub. Status: " + execute.getStatusLine());
      }
    } finally {
      post.abort();
    }
  }
}
