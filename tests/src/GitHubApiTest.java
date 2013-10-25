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

import com.intellij.openapi.util.io.FileUtil;
import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.util.PropertiesUtil;
import jetbrains.teamcilty.github.api.GitHubConnectionParameters;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.teamcilty.github.api.GitHubApi;
import jetbrains.teamcilty.github.api.GitHubChangeState;
import jetbrains.teamcilty.github.api.impl.GitHubApiImpl;
import org.apache.http.auth.AuthenticationException;
import org.eclipse.egit.github.core.CommitStatus;
import org.eclipse.egit.github.core.RepositoryId;
import org.jetbrains.annotations.NotNull;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 05.09.12 23:51
 */
public class GitHubApiTest extends BaseTestCase {
  private static final String URL = "URL";
  private static final String TOKEN = "token";
  private static final String USERNAME = "username";
  private static final String REPOSITORY = "repository";
  private static final String OWNER = "owner";
  private static final String PASSWORD_REV = "password-rev";
  public static final String COMMIT_ID = "cee389bbaf785b07362880124d3aff5ac97807cf";
  private GitHubApi myApi;
  private RepositoryId myRepositoryId;

  @BeforeMethod
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    final Properties ps = readGitHubAccount();
    final String user = ps.getProperty(USERNAME);

    final String repoName = ps.getProperty(REPOSITORY);
    final String repoOwner = ps.getProperty(OWNER, user);
    myRepositoryId = RepositoryId.create(repoOwner, repoName);
    final String token = ps.getProperty(TOKEN);
    myApi = new GitHubApiImpl(new GitHubConnectionParameters.OAuth2(ps.getProperty(URL), token));
  }

  private static String rewind(String s) {
    StringBuilder sb = new StringBuilder();
    for (char c : s.toCharArray()) {
      sb.insert(0, c);
    }
    return sb.toString();
  }

  /**
   * It's not possible to store username/password in the test file,
   * this cretentials are stored in a properties file
   * under user home directory.
   * <p/>
   * This method would be used to fetch parameters for the test
   * and allow to avoid committing createntials with source file.
   *
   * @return username, repo, password
   */
  @NotNull
  public static Properties readGitHubAccount() {
    File propsFile = new File(System.getenv("USERPROFILE"), ".github.test.account");
    System.out.println("Loading properites from: " + propsFile);
    try {
      if (!propsFile.exists()) {
        FileUtil.createParentDirs(propsFile);
        Properties ps = new Properties();
        ps.setProperty(URL, "api.github.com");
        ps.setProperty(OWNER, "VladRassokhin");
        ps.setProperty(REPOSITORY, "TeamCity.GitHub.Testing");
        ps.setProperty(TOKEN, "b997275bc0e097d5b9477856fc2b78fa6c6f8dec");  // Safe to publish token =) only repo:status
//        PropertiesUtil.storeProperties(ps, propsFile, "mock properties");
        return ps;
      } else {
        return PropertiesUtil.loadProperties(propsFile);
      }
    } catch (IOException e) {
      throw new RuntimeException("Could not read Amazon Access properties: " + e.getMessage(), e);
    }
  }

  @Test
  public void test_read_status() throws IOException {
    final CommitStatus status = myApi.getChangeStatus(myRepositoryId, COMMIT_ID);
    Assert.assertNotNull(status);
    System.out.println("status.getId() = " + status.getId());
    System.out.println("status.getState() = " + status.getState());
  }

  @Test
  public void test_set_status() throws IOException, AuthenticationException {
    final CommitStatus status = new CommitStatus();
    status.setState(GitHubChangeState.Success.getState());
    status.setTargetUrl("http://teamcity.jetbrains.com");
    status.setDescription("test status");
    // afb8f77357c011b6650fcd0426aa8c55bdfbfc8c // pr 1 merge
    // cee389bbaf785b07362880124d3aff5ac97807cf // pr 1 head
    myApi.setChangeStatus(myRepositoryId, COMMIT_ID, status);
  }

  @Test
  public void test_set_longer_status() throws IOException, AuthenticationException {
    final CommitStatus status = new CommitStatus();
    status.setState(GitHubChangeState.Pending.getState());
    status.setTargetUrl("http://teamcity.jetbrains.com");
    status.setDescription("test status" + StringUtil.repeat("test", " ", 1000));
    myApi.setChangeStatus(myRepositoryId, COMMIT_ID, status);
  }

  @Test
  public void test_resolve_pull_request() throws IOException {
    String hash = myApi.findPullRequestCommit(myRepositoryId, "refs/pull/1/merge");
    System.out.println(hash);
    Assert.assertEquals(hash, "cee389bbaf785b07362880124d3aff5ac97807cf");
  }

  @Test
  public void test_resolve_pull_request_2() throws IOException {
    String hash = myApi.findPullRequestCommit(myRepositoryId, "refs/pull/1/head");
    System.out.println(hash);
    Assert.assertEquals(hash, "cee389bbaf785b07362880124d3aff5ac97807cf");
  }

  @Test
  public void test_is_merge_pull() {
    Assert.assertTrue(myApi.isPullRequestMergeBranch("refs/pull/42/merge"));
    Assert.assertFalse(myApi.isPullRequestMergeBranch("refs/pull/42/head"));
  }

  @Test(expectedExceptions = IOException.class)
  public void test_set_status_failure() throws IOException, AuthenticationException {
    enableDebug();
    final CommitStatus status = new CommitStatus();
    status.setState(GitHubChangeState.Pending.getState());
    status.setTargetUrl("http://teamcity.jetbrains.com");
    status.setDescription("test status");
    myApi.setChangeStatus(myRepositoryId, "wrong_hash", status);
  }

  @Test
  public void test_parent_hashes() throws IOException {
    enableDebug();
    Collection<String> parents = myApi.getCommitParents(myRepositoryId, "3d8e40b01ce7d6e2ae6654d74092b26bf66371c3");
    System.out.println(parents);
  }
}
