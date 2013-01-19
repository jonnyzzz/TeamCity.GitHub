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

package jetbrains.teamcilty.github.api;

import org.eclipse.egit.github.core.CommitStatus;
import org.jetbrains.annotations.NotNull;

/**
* Created by Eugene Petrenko (eugene.petrenko@gmail.com)
* Date: 06.09.12 0:13
*/
public enum GitHubChangeState {
  Pending(CommitStatus.STATE_PENDING),
  Success(CommitStatus.STATE_SUCCESS),
  Error(CommitStatus.STATE_ERROR),
  Failure(CommitStatus.STATE_FAILURE),
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
