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

import com.google.gson.Gson;
import org.apache.http.entity.StringEntity;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;

/**
* Created by Eugene Petrenko (eugene.petrenko@gmail.com)
* Date: 04.03.13 22:32
*/
public class GSonEntity extends StringEntity {
  @NotNull
  private final String myText;

  public GSonEntity(@NotNull Gson gson, @NotNull final Object object) throws UnsupportedEncodingException {
    this(gson.toJson(object));
  }

  public GSonEntity(@NotNull final String text) throws UnsupportedEncodingException {
    super(text, "application/json", "UTF-8");
    myText = text;
  }

  @NotNull
  public String getText() {
    return myText;
  }
}
