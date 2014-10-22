package jetbrains.teamcilty.github.api;

import jetbrains.buildServer.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum GitHubApiReportEvent {
  ON_START_AND_FINISH("on start and finish"),
  ON_START("on start"),
  ON_FINISH("on finish");

  private final String myValue;

  GitHubApiReportEvent(@NotNull final String value) {
    myValue = value;
  }

  @NotNull
  public String getValue() {
    return myValue;
  }

  @NotNull
  public static GitHubApiReportEvent parse(@Nullable final String value) {
    //migration
    if (value == null || StringUtil.isEmptyOrSpaces(value)) return ON_START_AND_FINISH;

    for (GitHubApiReportEvent v : values()) {
      if (v.getValue().equals(value)) return v;
    }

    throw new IllegalArgumentException("Failed to parse GitHubApiReportEvent: " + value);
  }
}
