package jetbrains.teamcilty.github.api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 06.09.12 2:39
 */
public interface GitHubApi {
  String readChangeStatus(@NotNull String hash) throws IOException;

  void setChangeStatus(@NotNull String hash,
                       @NotNull GitHubChangeState status,
                       @NotNull String targetUrl,
                       @NotNull String description) throws IOException;
}
