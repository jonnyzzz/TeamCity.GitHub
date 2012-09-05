package jetbrains.teamcilty.github.api;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 05.09.12 23:39
 */
public class GitHubApi {
  @NotNull
  private final FeedClient myClient;
  private final String myUrl;
  private final String myUserName;
  private final String myRepoName;
  private final String myPassword;

  public GitHubApi(@NotNull final FeedClient client,
                   @NotNull final String url,
                   @NotNull final String userName,
                   @NotNull final String repoName,
                   @NotNull final String password) {
    myClient = client;
    myUrl = url;
    myUserName = userName;
    myRepoName = repoName;
    myPassword = password;
  }

  public String readChangeStatus(@NotNull final String hash) throws IOException {
    HttpGet post = new HttpGet(getStatusUrl(hash));
    try {
      HttpResponse execute = myClient.execute(post);

      HttpEntity entity = execute.getEntity();
      entity.writeTo(System.out);
      if (execute.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
        throw new IOException("Failed to complete request to GitHub. Status: " + execute.getStatusLine());
      }
      return "TBD";
    } finally {
      post.abort();
    }
  }

  public String setChangeStatus(@NotNull final String hash,
                                @NotNull GitHubChangeState status,
                                @NotNull String targetUrl,
                                @NotNull String description) throws IOException, AuthenticationException {
    String url = getStatusUrl(hash);
    HttpPost post = new HttpPost(url);
    post.setEntity(new StringEntity(" { state : \"success\", target_url : null, description : \"aaa\"  } ", "application/json", "utf-8"));
    post.addHeader(new BasicScheme().authenticate(new UsernamePasswordCredentials(myUserName, myPassword), post));
    try {
      HttpResponse execute = myClient.execute(post);

      HttpEntity entity = execute.getEntity();
      entity.writeTo(System.out);
      if (execute.getStatusLine().getStatusCode() != HttpURLConnection.HTTP_OK) {
        throw new IOException("Failed to complete request to GitHub. Status: " + execute.getStatusLine());
      }
      return "TBD";
    } finally {
      post.abort();
    }
  }

  private String getStatusUrl(String hash) {
    return myUrl + "/repos/" + myUserName + "/" + myRepoName + "/statuses/" + hash;
  }
}
