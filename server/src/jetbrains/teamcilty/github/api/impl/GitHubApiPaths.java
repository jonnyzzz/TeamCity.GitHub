/*
 * Copyright 2000-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.teamcilty.github.api.impl;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 19.04.13 19:17
 */
public class GitHubApiPaths {
  private final String myUrl;

  public GitHubApiPaths(@NotNull String url) {
    while (url.endsWith("/")) url = url.substring(0, url.length() - 1);
    myUrl = url;
  }

  @NotNull
  public String getCommitInfo(@NotNull final String repoOwner,
                              @NotNull final String repoName,
                              @NotNull final String hash) {
    // /repos/:owner/:repo/git/commits/:sha
    return myUrl + "/repos/" + repoOwner + "/" + repoName + "/git/commits/" + hash;
  }

  @NotNull
  public String getStatusUrl(@NotNull final String ownerName,
                             @NotNull final String repoName,
                             @NotNull final String hash) {
    return myUrl + "/repos/" + ownerName + "/" + repoName + "/statuses/" + hash;
  }

  @NotNull
  public String getPullRequestInfo(@NotNull final String repoOwner,
                                   @NotNull final String repoName,
                                   @NotNull final String pullRequestId) {
    return myUrl + "/repos/" + repoOwner + "/" + repoName + "/pulls/" + pullRequestId;
  }

  @NotNull
  public String getAddCommentUrl(@NotNull final String ownerName,
                                 @NotNull final String repoName,
                                 @NotNull final String hash) {
    ///repos/:owner/:repo/commits/:sha/comments
    return myUrl + "/repos/" + ownerName + "/" + repoName + "/commits/" + hash + "/comments";
  }
}
