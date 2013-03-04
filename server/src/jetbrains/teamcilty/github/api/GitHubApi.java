/*
 * Copyright 2000-2012 JetBrains s.r.o.
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

package jetbrains.teamcilty.github.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 06.09.12 2:39
 */
public interface GitHubApi {
  String readChangeStatus(@NotNull String repoOwner,
                          @NotNull String repositoryName,
                          @NotNull String hash) throws IOException;

  void setChangeStatus(@NotNull String repoOwner,
                       @NotNull String repositoryName,
                       @NotNull String hash,
                       @NotNull GitHubChangeState status,
                       @NotNull String targetUrl,
                       @NotNull String description) throws IOException;


  /**
   * checks if specified branch represents GitHub pull request merge branch,
   * i.e. /refs/pull/X/merge
   * @param branchName branch name
   * @return true if branch is pull's merge
   */
  boolean isPullRequestMergeBranch(@NotNull String branchName);

  /**
   * this method parses branch name and attempts to detect
   * /refs/pull/X/head revision for given branch
   *
   * The main use-case for it is to resolve /refs/pull/X/merge branch
   * into head commit hash in order to call github status API
   *
   * @param repoOwner repository owner name (who owns repo where you see pull request)
   * @param repoName repository name (where you see pull request)
   * @param branchName detected branch name in TeamCity, i.e. /refs/pull/X/merge
   * @return found /refs/pull/X/head or null
   * @throws IOException on communication error
   */
  @Nullable
  String findPullRequestCommit(@NotNull String repoOwner,
                               @NotNull String repoName,
                               @NotNull String branchName) throws IOException;

  /**
   * return parent commits for given commit
   * @param repoOwner repo owner
   * @param repoName repo name
   * @param hash commit hash
   * @return colleciton of commit parents
   * @throws IOException
   */
  @NotNull
  Collection<String> getCommitParents(@NotNull String repoOwner,
                                      @NotNull String repoName,
                                      @NotNull String hash) throws IOException;
}
