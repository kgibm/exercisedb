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
package com.example.exercisedb.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.exercisedb.loadrunner.LoadRunner;

/**
 * Load runner servlet
 */
@WebServlet({ "/" + LoadRunnerServlet.URL })
@ServletSecurity(@HttpConstraint(rolesAllowed = "users"))
public class LoadRunnerServlet extends HttpServlet {

	public static final String URL = "loadrunnerServlet";

	private static final String CLASS = LoadRunnerServlet.class.getCanonicalName();
	private static final Logger LOG = Logger.getLogger(CLASS);

	private static final long serialVersionUID = 1L;

	@Resource(lookup = "concurrent/executorService1")
	private ManagedExecutorService executorService;
	
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Date started = new Date();
		PrintWriter writer = null;
		try {
			String activity = request.getParameter("activity");
			if (activity == null || activity.length() == 0) {
				throw new IllegalArgumentException("Invalid activity");
			}

			String urlString = request.getParameter("url");
			if (urlString == null) {
				throw new IllegalArgumentException("URL not provided");
			}
			if (urlString.endsWith("/")) {
				urlString = urlString.substring(0, urlString.length() - 1);
			}

			URL url = new URL(urlString);
			URL target = new URL(url + "/" + ExerciseDBServlet.URL + "?action=" + activity);

			String concurrentusersString = request.getParameter("concurrentusers");
			if (concurrentusersString == null || concurrentusersString.length() == 0) {
				throw new IllegalArgumentException("Invalid concurrent users");
			}
			int concurrentusers = Integer.parseInt(concurrentusersString);
			if (concurrentusers <= 0) {
				throw new IllegalArgumentException("Concurrent users must be greater than 0");
			}

			String totalrequestsString = request.getParameter("totalrequests");
			if (totalrequestsString == null || totalrequestsString.length() == 0) {
				throw new IllegalArgumentException("Invalid total requests");
			}
			int totalrequests = Integer.parseInt(totalrequestsString);
			if (totalrequests <= 0) {
				throw new IllegalArgumentException("Total requests must be greater than 0");
			}

			String info = "Starting load runner to " + target + " with " + concurrentusers
					+ " concurrent users and " + totalrequests + " total requests";

			if (LOG.isLoggable(Level.INFO))
				LOG.info(info);

			// Now start the actual threads
			LoadRunner loadRunner = new LoadRunner();
			loadRunner.setTarget(target);
			loadRunner.setConcurrentUsers(concurrentusers);
			loadRunner.setTotalRequests(totalrequests);
			loadRunner.setExecutorService(executorService);
			executorService.submit(loadRunner);

			writer = startResponse(request, response, started, HttpServletResponse.SC_OK);
			writer.println(info);

			finishResponse(writer, started);

		} catch (Throwable e) {
			if (writer == null) {
				writer = startResponse(request, response, started, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			e.printStackTrace(writer);
			finishResponse(writer, started);
		}

		if (LOG.isLoggable(Level.FINER))
			LOG.exiting(CLASS, "service");
	}

	private PrintWriter startResponse(HttpServletRequest request, HttpServletResponse response, Date started,
			int status) throws IOException {
		response.setStatus(status);
		response.setContentType("text/plain");
		PrintWriter writer = response.getWriter();
		writer.println("Request started at " + started);
		writer.flush();
		return writer;
	}

	private void finishResponse(PrintWriter writer, Date started) {
		Date finished = new Date();
		long diff = finished.getTime() - started.getTime();
		writer.println("Request finished at " + finished + ". Processing took " + diff + " ms");
	}
}
