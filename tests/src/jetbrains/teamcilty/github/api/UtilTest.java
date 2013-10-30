package jetbrains.teamcilty.github.api;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URI;

public class UtilTest {
  @Test
  public void testCheckedURI() throws Exception {
    doCheckedURITest("api.github.com", "api.github.com");
    doCheckedURITest("api.github.com/api/v3", "api.github.com");
    doCheckedURITest("github.com", "api.github.com");
    doCheckedURITest("gist.github.com", "api.github.com");
    doCheckedURITest("https://api.github.com/api/v3", "api.github.com");
    doCheckedURITest("github.mycompany", "github.mycompany");
    doCheckedURITest("github.mycompany/api/v3", "github.mycompany");
    doCheckedURITest("github.mycompany/path", "github.mycompany");
    doCheckedURITest("http://github.mycompany", "github.mycompany");
    doCheckedURITest("https://github.mycompany/path", "github.mycompany");
  }

  public void doCheckedURITest(String url, String host) throws MalformedURLException {
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
}
