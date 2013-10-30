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

package jetbrains.teamcilty.github.api.impl;

import jetbrains.buildServer.util.StringUtil;
import jetbrains.teamcilty.github.api.Util;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.GitHubResponse;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.util.EncodingUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;

import static org.eclipse.egit.github.core.client.IGitHubConstants.*;

public class ApacheHttpBasedGitHubClient extends GitHubClient {
  protected final HttpClientWrapper myWrapper;
  protected final URI myBaseUri;
  protected String myCredentials;
  protected String myUserAgent;
  protected int myRemainingRequests;
  protected int myRequestLimit;
  protected String myUser;

  protected ApacheHttpBasedGitHubClient(@NotNull HttpClientWrapper wrapper, @NotNull URI uri) {
    super();
    myWrapper = wrapper;
    myBaseUri = uri;
  }

  public static ApacheHttpBasedGitHubClient createClient(@NotNull URI uri, @NotNull HttpClientWrapper wrapper) {
    uri = Util.fixURI(uri);
    return new ApacheHttpBasedGitHubClient(wrapper, uri);
  }

  protected <T extends HttpRequestBase> T configureRequest(T request) {
    if (myCredentials != null) {
      request.setHeader(HttpHeaders.AUTHORIZATION, myCredentials);
    }
    request.setHeader(HttpHeaders.USER_AGENT, myUserAgent);
    request.setHeader(HttpHeaders.ACCEPT, "application/vnd.github.beta+json");
    request.setHeader(HttpHeaders.ACCEPT_ENCODING, CHARSET_UTF8);
    return request;
  }

  protected <T extends HttpEntityEnclosingRequestBase> void sendParams(T request, Object params)
          throws IOException {
    if (params != null) {
      request.setHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_JSON + "; charset=" + CHARSET_UTF8);
      request.setEntity(new GSonEntity(toJson(params)));
    } else {
      request.setHeader(HttpHeaders.CONTENT_LENGTH, "0");
    }
  }

  @Override
  public void delete(String uri, Object params) throws IOException {
    final HttpDeleteWithBody request = new HttpDeleteWithBody(getUri(uri));
    configureRequest(request);

    if (params != null) {
      sendParams(request, params);
    }
    final HttpResponse response = myWrapper.execute(request);
    final int code = response.getStatusLine().getStatusCode();
    updateRateLimits(response);
    if (!isEmpty(code)) {
      throw new RequestException(parseError(getStream(response)), code);
    }
  }

  private InputStream getStream(HttpResponse response) throws IOException {
    HttpEntity entity = response.getEntity();
    if (entity == null) {
      throw new IOException("Empty response. Status: " + response.getStatusLine());
    }
    return entity.getContent();
//    ByteArrayOutputStream bos = new ByteArrayOutputStream();
//    entity.writeTo(bos);
//    return new ByteArrayInputStream(bos.toByteArray());
  }

  @Override
  public GitHubResponse get(GitHubRequest request) throws IOException {
    final HttpGet get = new HttpGet(getUri(request.generateUri()));
    configureRequest(get);

    final String accept = request.getResponseContentType();
    if (accept != null) {
      get.setHeader(HttpHeaders.ACCEPT, accept);
    }

    final HttpResponse response = myWrapper.execute(get);

    final int code = response.getStatusLine().getStatusCode();
    updateRateLimits(response);
    if (isOk(code)) {
      final Type type = request.getType();
      if (type != null) {
        return new ApacheHttpBasedGitHubResponse(response, this.parseJson(getStream(response), type));
      } else {
        return new ApacheHttpBasedGitHubResponse(response, null);
      }
    }
    if (isEmpty(code)) {
      return new ApacheHttpBasedGitHubResponse(response, null);
    }
    throw createException(getStream(response), code, response.getStatusLine().getReasonPhrase());
  }

  @Override
  public <V> V post(String uri, Object params, Type type) throws IOException {
    HttpPost request = new HttpPost(getUri(uri));
    configureRequest(request);
    return sendJson(request, params, type);
  }

  @Override
  public <V> V put(String uri, Object params, Type type) throws IOException {
    HttpPut request = new HttpPut(getUri(uri));
    configureRequest(request);

    return sendJson(request, params, type);
  }

  private <V> V sendJson(final HttpEntityEnclosingRequestBase request, final Object params, final Type type) throws IOException {
    sendParams(request, params);
    final HttpResponse response = myWrapper.execute(request);
    final int code = response.getStatusLine().getStatusCode();
    updateRateLimits(response);
    if (isOk(code)) {
      if (type != null) {
        return parseJson(getStream(response), type);
      } else {
        return null;
      }
    }
    if (isEmpty(code)) {
      return null;
    }
    throw createException(getStream(response), code, response.getStatusLine().getReasonPhrase());
  }

  protected GitHubClient updateRateLimits(HttpResponse response) {
    final Header limit = response.getFirstHeader("X-RateLimit-Limit");
    if (limit != null && !StringUtil.isEmpty(limit.getValue()))
      try {
        myRequestLimit = Integer.parseInt(limit.getValue());
      } catch (NumberFormatException nfe) {
        myRequestLimit = -1;
      }
    else {
      myRequestLimit = -1;
    }

    final Header remaining = response.getFirstHeader("X-RateLimit-Remaining");
    if (remaining != null && !StringUtil.isEmpty(remaining.getValue()))
      try {
        myRemainingRequests = Integer.parseInt(remaining.getValue());
      } catch (NumberFormatException nfe) {
        myRemainingRequests = -1;
      }
    else {
      myRemainingRequests = -1;
    }

    return this;
  }

  public int getRemainingRequests() {
    return myRemainingRequests;
  }

  public int getRequestLimit() {
    return myRequestLimit;
  }

  @Override
  public GitHubClient setUserAgent(String agent) {
    if (agent != null && agent.length() > 0)
      myUserAgent = agent;
    else
      myUserAgent = USER_AGENT;
    return this;
  }

  @Override
  public InputStream postStream(String uri, Object params) throws IOException {
    final HttpPost post = new HttpPost(getUri(uri));
    configureRequest(post);
    sendParams(post, params);
    final HttpResponse response = myWrapper.execute(post);
    return getResponseStream(response);
  }

  private InputStream getResponseStream(HttpResponse response)
          throws IOException {
    final int code = response.getStatusLine().getStatusCode();
    updateRateLimits(response);
    final InputStream stream = getStream(response);
    if (isOk(code)) {
      return stream;
    } else
      throw createException(stream, code, response.getStatusLine().getReasonPhrase());
  }

  @Override
  public InputStream getStream(GitHubRequest request) throws IOException {
    final HttpGet get = new HttpGet(getUri(request.generateUri()));
    configureRequest(get);
    final HttpResponse response = myWrapper.execute(get);
    return getResponseStream(response);
  }

  @Override
  public GitHubClient setCredentials(String user, String password) {
    this.myUser = user;
    if (StringUtil.isNotEmpty(user) && StringUtil.isNotEmpty(password)) {
      myCredentials = "Basic " + EncodingUtils.toBase64(user + ':' + password);
    } else {
      myCredentials = null;
    }
    return this;
  }

  @Override
  public GitHubClient setOAuth2Token(String token) {
    if (token != null && token.length() > 0)
      myCredentials = AUTH_TOKEN + ' ' + token;
    else
      myCredentials = null;
    return this;
  }

  @Override
  public GitHubClient setBufferSize(int bufferSize) {
    return super.setBufferSize(bufferSize);
  }

  @Override
  public String getUser() {
    return myUser;
  }

  @Override
  public void post(String uri) throws IOException {
    post(uri, null, null);
  }

  @Override
  public void put(String uri) throws IOException {
    put(uri, null, null);
  }

  @Override
  public void delete(String uri) throws IOException {
    delete(uri, null);
  }

  @NotNull
  protected URI getUri(@NotNull final String path) {
    return myBaseUri.resolve(path);
  }
}
