/*******************************************************************************
 * (c) Copyright IBM Corporation 2022.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.example.exercisedb.loadrunner;

import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.ssl.SSLContexts;

public class User implements Callable<UserResult> {

	private static final String CLASS = User.class.getCanonicalName();
	private static final Logger LOG = Logger.getLogger(CLASS);

	public static final int MAX_HTTP_CLIENT_OUTBOUND_CONNECTIONS_TOTAL = Integer
			.getInteger("MAX_HTTP_CLIENT_OUTBOUND_CONNECTIONS_TOTAL", 100);

	public static final int MAX_HTTP_CLIENT_OUTBOUND_CONNECTIONS_PER_ROUTE = Integer
			.getInteger("MAX_HTTP_CLIENT_OUTBOUND_CONNECTIONS_PER_ROUTE", MAX_HTTP_CLIENT_OUTBOUND_CONNECTIONS_TOTAL);

	public static final SSLContext SSL_CONTEXT = createSSLContext();

	public static final PoolingHttpClientConnectionManager HTTP_CLIENT_CONNECTION_MANAGER = PoolingHttpClientConnectionManagerBuilder
			.create().setMaxConnTotal(MAX_HTTP_CLIENT_OUTBOUND_CONNECTIONS_TOTAL)
			.setMaxConnPerRoute(MAX_HTTP_CLIENT_OUTBOUND_CONNECTIONS_PER_ROUTE)
			.setSSLSocketFactory(new SSLConnectionSocketFactory(SSL_CONTEXT, NoopHostnameVerifier.INSTANCE)).build();

	private URL target;
	private int totalRequests;
	private String userName;
	private String password;

	private static SSLContext createSSLContext() {
		try {
			return SSLContexts.custom().loadTrustMaterial(TrustSelfSignedStrategy.INSTANCE).build();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public UserResult call() throws Exception {
		if (LOG.isLoggable(Level.FINE))
			LOG.fine(this + " started " + totalRequests + " requests");

		String encoding = null;
		if (userName != null && userName.length() > 0) {
			encoding = Base64.getEncoder().encodeToString((userName + ":" + password).getBytes());
		}

		try {
			for (int i = 0; i < totalRequests; i++) {
				try (CloseableHttpClient httpClient = HttpClients.custom()
						.setConnectionManager(HTTP_CLIENT_CONNECTION_MANAGER).setConnectionManagerShared(true)
						.build()) {

					HttpGet get = new HttpGet(target.toString());
					if (encoding != null) {
						get.setHeader(HttpHeaders.AUTHORIZATION, "Basic " + encoding);
					}

					try (CloseableHttpResponse response = httpClient.execute(get)) {
						if (response.getCode() != HttpStatus.SC_OK) {
							throw new RuntimeException("Received unexpected HTTP code " + response.getCode());
						}

						if (LOG.isLoggable(Level.FINEST))
							LOG.finest(EntityUtils.toString(response.getEntity()));
					}
				}
			}
		} catch (Throwable t) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.log(Level.SEVERE, this + " error: " + t, t);
			}
		}

		if (LOG.isLoggable(Level.FINE))
			LOG.fine(this + " finished " + totalRequests + " requests");

		return new UserResult();
	}

	public URL getTarget() {
		return target;
	}

	public void setTarget(URL target) {
		this.target = target;
	}

	public int getTotalRequests() {
		return totalRequests;
	}

	public void setTotalRequests(int totalRequests) {
		this.totalRequests = totalRequests;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
