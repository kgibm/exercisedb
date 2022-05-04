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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.concurrent.ManagedExecutorService;

public class LoadRunner implements Callable<LoadRunnerResult> {

	private static final String CLASS = LoadRunner.class.getCanonicalName();
	private static final Logger LOG = Logger.getLogger(CLASS);

	private URL target;
	private int concurrentUsers;
	private int totalRequests;
	private ManagedExecutorService executorService;

	@Override
	public LoadRunnerResult call() throws Exception {
		if (LOG.isLoggable(Level.INFO))
			LOG.info(this + " called");

		List<Future<UserResult>> futures = new ArrayList<>();
		for (int i = 0; i < concurrentUsers; i++) {
			User task = createTask();
			futures.add(executorService.submit(task));
		}

		while (!futures.isEmpty()) {
			Future<UserResult> future = futures.remove(0);
			UserResult result = future.get();
			if (LOG.isLoggable(Level.INFO))
				LOG.info(this + " finished " + result);
		}
		
		if (LOG.isLoggable(Level.INFO))
			LOG.info(this + " finished");

		return new LoadRunnerResult();
	}

	private User createTask() {
		User task = new User();
		return task;
	}

	public URL getTarget() {
		return target;
	}

	public void setTarget(URL target) {
		this.target = target;
	}

	public int getConcurrentUsers() {
		return concurrentUsers;
	}

	public void setConcurrentUsers(int concurrentUsers) {
		this.concurrentUsers = concurrentUsers;
	}

	public int getTotalRequests() {
		return totalRequests;
	}

	public void setTotalRequests(int totalRequests) {
		this.totalRequests = totalRequests;
	}

	public ManagedExecutorService getExecutorService() {
		return executorService;
	}

	public void setExecutorService(ManagedExecutorService executorService) {
		this.executorService = executorService;
	}
}
