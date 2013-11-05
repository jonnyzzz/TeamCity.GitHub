package jetbrains.teamcilty.github.api.impl;

import jetbrains.teamcilty.github.api.Util;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;

public class ApacheHttpBasedGitHubClientTest {

  private HttpClientWrapper myWrapper;

  @BeforeMethod
  public void setUp() throws Exception {
    myWrapper = new HttpClientWrapperImpl();
  }

  @Test(dataProvider = "uris")
  public void testGetUri(String base, String expected) throws Exception {
    final URI uri = Util.fixURI(URI.create(base));
    final ApacheHttpBasedGitHubClient client = new ApacheHttpBasedGitHubClient(myWrapper, uri);
    Assert.assertEquals(client.getUri("REQUEST").toString(), expected);
    Assert.assertEquals(client.getUri("/REQUEST").toString(), expected);
  }

  @DataProvider(name = "uris")
  public static Object[][] uris() {
    return new String[][]{
            {"api.github.com", "https://api.github.com/api/v3/REQUEST"},
            {"api.github.com/api/v3", "https://api.github.com/api/v3/REQUEST"},
            {"github.com", "https://api.github.com/api/v3/REQUEST"},
            {"gist.github.com", "https://api.github.com/api/v3/REQUEST"},
            {"https://api.github.com/api/v3", "https://api.github.com/api/v3/REQUEST"},
            {"github.mycompany", "https://github.mycompany/api/v3/REQUEST"},
            {"github.mycompany/api/v3", "https://github.mycompany/api/v3/REQUEST"},
            {"github.mycompany/path", "https://github.mycompany/path/REQUEST"},
            {"http://github.mycompany", "http://github.mycompany/api/v3/REQUEST"},
            {"https://github.mycompany/path", "https://github.mycompany/path/REQUEST"},
    };
  }
}
