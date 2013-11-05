package jetbrains.teamcilty.github.api;

import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;

import static com.intellij.openapi.util.text.StringUtil.isEmptyOrSpaces;
import static org.eclipse.egit.github.core.client.IGitHubConstants.*;

public class Util {
  @NotNull
  public static URI fixURI(@NotNull final URI original) throws IllegalArgumentException {
    String host = original.getHost();
    String path = original.getPath();
    String scheme = original.getScheme();
    int port = original.getPort();

    if (isEmptyOrSpaces(host)) {
      if (isEmptyOrSpaces(path)) {
        throw new IllegalArgumentException("Invalid URI: Host name is empty, but path is empty. Original uri " + original);
      } else if (original.isAbsolute()) {
        throw new IllegalArgumentException("Invalid URI: Host name is empty, but uri is absolute. Original uri " + original);
      }
      int i = path.indexOf('/');
      if (i >= 0) {
        host = path.substring(0, i);
        path = path.substring(i);
      } else {
        host = path;
        path = null;
      }
    }
    if (isEmptyOrSpaces(scheme)) {
      scheme = "https";
    }
    if (HOST_DEFAULT.equals(host) || HOST_GISTS.equals(host)) {
      // Change host, use SEGMENT_V3_API as path
      host = HOST_API;
    }
    if (isEmptyOrSpaces(path)) {
      path = SEGMENT_V3_API;
    }
    URI url;
    try {
      url = new URI(scheme, null, host, port, path, null, null);
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("Cannot create modified URI from original: " + original, e);
    }
    try {
      //noinspection ResultOfMethodCallIgnored
      url.toURL();
    } catch (Exception e) {
      throw new IllegalArgumentException("URI " + original.toString() + " is not an URL", e);
    }
    return url;
  }
}
