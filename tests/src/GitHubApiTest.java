import jetbrains.buildServer.BaseTestCase;
import jetbrains.teamcilty.github.api.FeedHttpClientHolder;
import jetbrains.teamcilty.github.api.GitHubApi;
import jetbrains.teamcilty.github.api.GitHubChangeState;
import org.apache.http.auth.AuthenticationException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 05.09.12 23:51
 */
public class GitHubApiTest extends BaseTestCase {
  private GitHubApi myApi;

  @BeforeMethod
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    myApi = new GitHubApi(new FeedHttpClientHolder(), "https://api.github.com", "jonnyzzz", "TeamCity.GitHub", "oooo");
  }

  @Test
  public void test_read_status() throws IOException {
    myApi.readChangeStatus("605e36e23f7a64515691da631190baaf45fdaed9");
  }

  @Test
  public void test_set_status() throws IOException, AuthenticationException {
    myApi.setChangeStatus("605e36e23f7a64515691da631190baaf45fdaed9",
            GitHubChangeState.Pending,
            "http://teamcity.jetbrains.com",
            "test status"
    );
  }

}
