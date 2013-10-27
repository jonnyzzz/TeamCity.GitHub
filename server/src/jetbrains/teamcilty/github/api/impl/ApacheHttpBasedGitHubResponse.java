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

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.eclipse.egit.github.core.client.GitHubResponse;

public class ApacheHttpBasedGitHubResponse extends GitHubResponse {
  private final HttpResponse myResponse;

  public ApacheHttpBasedGitHubResponse(HttpResponse response, Object body) {
    super(null, body);
    myResponse = response;
  }

  @Override
  public String getHeader(String name) {
    Header header = myResponse.getLastHeader(name);
    return header != null ? header.getValue() : null;
  }
}
