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

import jetbrains.teamcilty.github.api.impl.HttpClientWrapperImpl;
import org.apache.http.client.methods.HttpGet;
import org.testng.annotations.Test;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 21.05.13 10:27
 */
public class SSLTest {
  @Test
  public void test_no_cert_auth_check() throws IOException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    HttpClientWrapperImpl cli = new HttpClientWrapperImpl();
    try {
      cli.execute(new HttpGet("https://secure.diverse.org.ru/jonny/svn/util"));
    } finally {
      cli.dispose();
    }
  }
}
