package jetbrains.teamcilty.github.api;

import org.jetbrains.annotations.NotNull;

/**
* Created by Eugene Petrenko (eugene.petrenko@gmail.com)
* Date: 06.09.12 0:13
*/
public enum GitHubChangeState {
  Pending("pending"),
  Success("success"),
  Error("error"),
  Failure("failure"),
  ;
  private final String myState;

  GitHubChangeState(@NotNull final String state) {
    myState = state;
  }

  @NotNull
  public String getState() {
    return myState;
  }
}
