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

import jetbrains.teamcilty.github.api.GitHubConnectionParameters;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Vladsilav Rassokhin (vlad.rassokhin@gmail.com)
 */
public class HostMatherTest {
  @Test
  public void testMatcher() throws Exception {
    final String GITHUB_COM = "github.com";
    final String API_GITHUB_COM = "api." + GITHUB_COM;

    Assert.assertEquals(GitHubConnectionParameters.getHost("http://api.github.com/"), API_GITHUB_COM);
    Assert.assertEquals(GitHubConnectionParameters.getHost("https://api.github.com/"), API_GITHUB_COM);
    Assert.assertEquals(GitHubConnectionParameters.getHost("http://api.github.com/v3"), API_GITHUB_COM);
    Assert.assertEquals(GitHubConnectionParameters.getHost("https://api.github.com/v3"), API_GITHUB_COM);
    Assert.assertEquals(GitHubConnectionParameters.getHost("api.github.com/v3"), API_GITHUB_COM);
    Assert.assertEquals(GitHubConnectionParameters.getHost("https://github.com/"), GITHUB_COM);
    Assert.assertEquals(GitHubConnectionParameters.getHost("https://myserver.com"), "myserver.com");

  }
}
