import com.intellij.openapi.util.io.FileUtil;
import jetbrains.buildServer.BaseTestCase;
import jetbrains.buildServer.util.PropertiesUtil;
import jetbrains.teamcilty.github.api.GitHubApi;
import jetbrains.teamcilty.github.api.GitHubChangeState;
import jetbrains.teamcilty.github.api.impl.GitHubApiImpl;
import jetbrains.teamcilty.github.api.impl.HttpClientWrapperImpl;
import org.apache.http.auth.AuthenticationException;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 05.09.12 23:51
 */
public class GitHubApiTest extends BaseTestCase {
  private static final String URL = "URL";
  private static final String USERNAME = "username";
  private static final String REPOSITORY = "repository";
  private static final String PASSWORD_REV = "password-rev";
  private GitHubApi myApi;
  private String myRepoName;

  @BeforeMethod
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    final Properties ps = readGitHubAccount();
    myRepoName = ps.getProperty(REPOSITORY);

    myApi = new GitHubApiImpl(
            new HttpClientWrapperImpl(),
            ps.getProperty(URL),
            ps.getProperty(USERNAME),
            rewind(ps.getProperty(PASSWORD_REV)));
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
   *
   * This method would be used to fetch parameters for the test
   * and allow to avoid committing createntials with source file.
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
    myApi.readChangeStatus(myRepoName, "605e36e23f7a64515691da631190baaf45fdaed9");
  }

  @Test
  public void test_set_status() throws IOException, AuthenticationException {
    myApi.setChangeStatus(myRepoName, "605e36e23f7a64515691da631190baaf45fdaed9",
            GitHubChangeState.Pending,
            "http://teamcity.jetbrains.com",
            "test status"
    );
  }
}
