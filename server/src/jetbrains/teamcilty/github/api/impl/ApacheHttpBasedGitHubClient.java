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

import org.apache.http.HttpHeaders;
import org.eclipse.egit.github.core.client.GitHubClient;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.eclipse.egit.github.core.client.IGitHubConstants.HOST_API;
import static org.eclipse.egit.github.core.client.IGitHubConstants.HOST_DEFAULT;
import static org.eclipse.egit.github.core.client.IGitHubConstants.HOST_GISTS;

// TODO: Pass all requests via HttpClientWrapper
public class ApacheHttpBasedGitHubClient extends GitHubClient {
  /**
   * Create API v3 client from URL.
   * <p>
   * This creates an HTTPS-based client with a host that contains the host
   * value of the given URL prefixed with 'api' if the given URL is github.com
   * or gist.github.com
   *
   * @param url
   * @return client
   */
  public static GitHubClient createClient(String url) {
    try {
      String host = new URL(url).getHost();
      if (HOST_DEFAULT.equals(host) || HOST_GISTS.equals(host))
        host = HOST_API;
      return new ApacheHttpBasedGitHubClient(host);
    } catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public ApacheHttpBasedGitHubClient(String hostname) {
    super(hostname);
  }

  @Override
  protected HttpURLConnection configureRequest(HttpURLConnection request) {
    request = super.configureRequest(request);
    request.setRequestProperty(HttpHeaders.ACCEPT_ENCODING, "UTF-8");
    return request;
  }
}
