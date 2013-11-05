package jetbrains.teamcilty.github.api;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URI;

public class UtilTest {
  @Test(dataProvider = "uris")
  public void testFixURI(String url, String host) throws MalformedURLException {
    URI uri = Util.fixURI(URI.create(url));
    Assert.assertNotNull(uri.getHost());
    Assert.assertEquals(uri.getHost(), host);
    Assert.assertNotNull(uri.getScheme());
    Assert.assertNotNull(uri.toURL());
    Assert.assertNotNull(uri.resolve("/"));
    Assert.assertTrue(uri.isAbsolute());
    uri = Util.fixURI(uri);
    Assert.assertNotNull(uri.getHost());
    Assert.assertEquals(uri.getHost(), host);
    Assert.assertNotNull(uri.getScheme());
    Assert.assertNotNull(uri.toURL());
    Assert.assertNotNull(uri.resolve("/"));
    Assert.assertTrue(uri.isAbsolute());
  }

  @DataProvider(name = "uris")
  public static Object[][] uris() {
    return new String[][]{
            {"api.github.com", "api.github.com"},
            {"api.github.com/api/v3", "api.github.com"},
            {"github.com", "api.github.com"},
            {"gist.github.com", "api.github.com"},
            {"https://api.github.com/api/v3", "api.github.com"},
            {"github.mycompany", "github.mycompany"},
            {"github.mycompany/api/v3", "github.mycompany"},
            {"github.mycompany/path", "github.mycompany"},
            {"http://github.mycompany", "github.mycompany"},
            {"https://github.mycompany/path", "github.mycompany"},
    };
  }
}
