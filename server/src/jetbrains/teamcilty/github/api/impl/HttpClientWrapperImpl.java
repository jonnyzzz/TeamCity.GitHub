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

package jetbrains.teamcilty.github.api.impl;

import jetbrains.buildServer.serverSide.TeamCityProperties;
import jetbrains.buildServer.version.ServerVersionHolder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.apache.http.impl.conn.SchemeRegistryFactory;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ProxySelector;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by Eugene Petrenko (eugene.petrenko@gmail.com)
 * Date: 11.08.11 16:24
 */
public class HttpClientWrapperImpl implements HttpClientWrapper {
  private final HttpClient myClient;

  public HttpClientWrapperImpl() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
    final String serverVersion = ServerVersionHolder.getVersion().getDisplayVersion();

    final HttpParams ps = new BasicHttpParams();

    DefaultHttpClient.setDefaultHttpParams(ps);
    final int timeout = TeamCityProperties.getInteger("teamcity.github.http.timeout", 300 * 1000);
    HttpConnectionParams.setConnectionTimeout(ps, timeout);
    HttpConnectionParams.setSoTimeout(ps, timeout);
    HttpProtocolParams.setUserAgent(ps, "JetBrains TeamCity " + serverVersion);

    final SchemeRegistry schemaRegistry = SchemeRegistryFactory.createDefault();
    final SSLSocketFactory sslSocketFactory = new SSLSocketFactory(new TrustStrategy() {
      public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        return !TeamCityProperties.getBoolean("teamcity.github.verify.ssl.certificate");
      }
    });
    schemaRegistry.register(new Scheme("https", 443, sslSocketFactory));

    final DefaultHttpClient httpclient = new DefaultHttpClient(new ThreadSafeClientConnManager(schemaRegistry), ps);

    httpclient.setRoutePlanner(new ProxySelectorRoutePlanner(
            httpclient.getConnectionManager().getSchemeRegistry(),
            ProxySelector.getDefault()));
    httpclient.addRequestInterceptor(new RequestAcceptEncoding());
    httpclient.addResponseInterceptor(new ResponseContentEncoding());
    httpclient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(3, true));

    myClient = httpclient;
  }

  @NotNull
  public HttpResponse execute(@NotNull HttpUriRequest request) throws IOException {
    return myClient.execute(request);
  }

  public void dispose() {
    myClient.getConnectionManager().shutdown();
  }

}
