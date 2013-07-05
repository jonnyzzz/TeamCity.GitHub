
package jetbrains.teamcilty.github.api.impl.data;

import org.jetbrains.annotations.NotNull;

/**
 * @author Tomaz Cerar
 */
@SuppressWarnings("UnusedDeclaration")
public class IssueComment {
  @NotNull
  public String body;


  public IssueComment(@NotNull String body) {
    this.body = body;
  }
}
