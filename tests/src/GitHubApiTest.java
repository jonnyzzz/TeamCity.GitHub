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

import com.intellij.openapi.util.io.FileUtil;
import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.util.PropertiesUtil;
import jetbrains.buildServer.util.StringUtil;
import jetbrains.teamcilty.github.api.GitHubApi;
import jetbrains.teamcilty.github.api.GitHubApiFactory;
import jetbrains.teamcilty.github.api.GitHubChangeState;
import jetbrains.teamcilty.github.api.impl.*;
import org.apache.http.auth.AuthenticationException;
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
  private static final String USERNAME = "username";
  private static final String REPOSITORY = "repository";
  private static final String OWNER = "owner";
  private static final String PASSWORD_REV = "password-rev";
  private static final String PR_COMMIT = "prcommit";
  private static final String ACCESS_TOKEN = "githubtoken";
  private GitHubApi myApi;
  private GitHubApi accessTokenApi;
  private String myRepoName;
  private String myRepoOwner;
  private String myPrCommit;

  @BeforeMethod
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    final Properties ps = readGitHubAccount();
    final String user = ps.getProperty(USERNAME);

    myRepoName = ps.getProperty(REPOSITORY);
    myRepoOwner = ps.getProperty(OWNER, user);

    final GitHubApiFactory factory = new GitHubApiFactoryImpl(new HttpClientWrapperImpl());

    myApi = factory.openGitHub(ps.getProperty(URL),
            new GitHubApiPasswordAuthentication(user, rewind(ps.getProperty(PASSWORD_REV)))
    );

    accessTokenApi = factory.openGitHub(ps.getProperty(URL),
            new GitHubApiTokenAuthentication(ps.getProperty(ACCESS_TOKEN)));

    myPrCommit = ps.getProperty(PR_COMMIT);
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
        ps.setProperty(URL, "https://api.github.com");
        ps.setProperty(USERNAME, "jonnyzzz");
        ps.setProperty(REPOSITORY, "TeamCity.GitHub");
        ps.setProperty(PASSWORD_REV, rewind("some-password-written-end-to-front"));
        ps.setProperty(PR_COMMIT, "4e86fc6dcef23c733f36bc8bbf35fb292edc9cdb");
        ps.setProperty(ACCESS_TOKEN, "insert a github personal access token here");
        PropertiesUtil.storeProperties(ps, propsFile, "mock properties");
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
    String hash = myApi.findPullRequestCommit(myRepoOwner, myRepoName, "refs/pull/1/merge");
    assert hash != null;
    String change = myApi.readChangeStatus(myRepoOwner, myRepoName, hash);
    System.out.println(change);
  }

  @Test
  public void test_set_status() throws IOException, AuthenticationException {
    String hash = myApi.findPullRequestCommit(myRepoOwner, myRepoName, "refs/pull/1/merge");
    assert hash != null;
    myApi.setChangeStatus(myRepoOwner, myRepoName, hash,
            GitHubChangeState.Pending,
            "http://teamcity.jetbrains.com",
            "test status"
    );
  }

  @Test
  public void test_set_status_with_token() throws IOException, AuthenticationException {
    String hash = accessTokenApi.findPullRequestCommit(myRepoOwner, myRepoName, "refs/pull/1/merge");
    assert hash != null;
    myApi.setChangeStatus(myRepoOwner, myRepoName, hash,
            GitHubChangeState.Pending,
            "http://teamcity.jetbrains.com",
            "test status"
    );
  }

  @Test
  public void test_set_longer_status() throws IOException, AuthenticationException {
    String hash = myApi.findPullRequestCommit(myRepoOwner, myRepoName, "refs/pull/1/merge");
    assert hash != null;
    myApi.setChangeStatus(myRepoOwner, myRepoName, hash,
            GitHubChangeState.Pending,
            "http://teamcity.jetbrains.com",
            "test status" + StringUtil.repeat("test", " ", 1000)
    );
  }

  @Test
  public void test_resolve_pull_request() throws IOException {
    String hash = myApi.findPullRequestCommit(myRepoOwner, myRepoName, "refs/pull/1/merge");
    System.out.println(hash);
    Assert.assertEquals(hash, myPrCommit);
  }

  @Test
  public void test_resolve_pull_request_2() throws IOException {
    String hash = myApi.findPullRequestCommit(myRepoOwner, myRepoName, "refs/pull/1/head");
    System.out.println(hash);
    Assert.assertEquals(hash, myPrCommit);
  }

  @Test
  public void test_is_merge_pull() {
    Assert.assertTrue(myApi.isPullRequestMergeBranch("refs/pull/42/merge"));
    Assert.assertFalse(myApi.isPullRequestMergeBranch("refs/pull/42/head"));
  }

  @Test(expectedExceptions = IOException.class)
  public void test_set_status_failure() throws IOException, AuthenticationException {
    enableDebug();
    myApi.setChangeStatus(myRepoOwner, myRepoName, "wrong_hash",
            GitHubChangeState.Pending,
            "http://teamcity.jetbrains.com",
            "test status"
    );
  }

  @Test
  public void test_parent_hashes() throws IOException {
    enableDebug();
    String hash = myApi.findPullRequestCommit(myRepoOwner, myRepoName, "refs/pull/1/merge");
    assert hash != null;
    Collection<String> parents = myApi.getCommitParents(myRepoOwner, myRepoName, hash);
    System.out.println(parents);
  }

}
