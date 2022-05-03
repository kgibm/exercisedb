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
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.example.exercisedb.Database;

/**
 * Main database servlet
 */
@WebServlet({ "/exercisedbServlet" })
public class ExerciseDBServlet extends HttpServlet {

	@SuppressWarnings("unused")
	private static final Logger LOG = Logger.getLogger(ExerciseDBServlet.class.getCanonicalName());

	private static final long serialVersionUID = 1L;

	@Resource(lookup = "jdbc/database1")
	private DataSource database;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Date started = new Date();
		PrintWriter writer = null;
		try {
			String action = request.getParameter("action");
			switch (action == null ? "" : action.toLowerCase()) {
			case "listtables": {
				List<String> tableNames = Database.getExistingTableNames(database);
				writer = startResponse(request, response, started, HttpServletResponse.SC_OK);
				writer.println("There are " + tableNames.size() + " tables in the database: " + tableNames);
				finishResponse(writer, started);
				break;
			}
			case "reset": {
				Database.dropTables(database);
				writer = startResponse(request, response, started, HttpServletResponse.SC_OK);
				writer.println("Dropped schema " + Database.SCHEMA);
				if (Database.ensureTables(database)) {
					writer.println("Created schema and table");
				} else {
					writer.println("Schema and tables already exist");
				}
				finishResponse(writer, started);
				break;
			}
			case "ensuretables": {
				writer = startResponse(request, response, started, HttpServletResponse.SC_OK);
				if (Database.ensureTables(database)) {
					writer.println("Created schema and table");
				} else {
					writer.println("Schema and tables already exist");
				}
				finishResponse(writer, started);
				break;
			}
			case "droptables": {
				Database.dropTables(database);
				writer = startResponse(request, response, started, HttpServletResponse.SC_OK);
				writer.println("Dropped schema " + Database.SCHEMA);
				finishResponse(writer, started);
				break;
			}
			case "insert": {
				long id = Database.insert(database);
				writer = startResponse(request, response, started, HttpServletResponse.SC_OK);
				writer.println("Inserted 1 row with ID " + id);
				finishResponse(writer, started);
				break;
			}
			case "insertselect": {
				long id = Database.insert(database);
				writer = startResponse(request, response, started, HttpServletResponse.SC_OK);
				writer.println("Inserted 1 row with ID " + id);
				String data = Database.select(database, id);
				writer.println("Selected row " + id + " with data " + data);
				finishResponse(writer, started);
				break;
			}
			case "insertselectdelete": {
				long id = Database.insert(database);
				writer = startResponse(request, response, started, HttpServletResponse.SC_OK);
				writer.println("Inserted 1 row with ID " + id);
				String data = Database.select(database, id);
				writer.println("Selected row " + id + " with data " + data);
				Database.delete(database, id);
				writer.println("Deleted row with ID " + id);
				finishResponse(writer, started);
				break;
			}
			case "count": {
				long count = Database.count(database);
				writer = startResponse(request, response, started, HttpServletResponse.SC_OK);
				writer.println("Count of rows: " + count);
				finishResponse(writer, started);
				break;
			}
			case "testthrow": {
				throw new Error("Test error");
			}
			default:
				writer = startResponse(request, response, started, HttpServletResponse.SC_OK);
				if (action == null || action.trim().length() == 0) {
					writer.println("No action specified");
				} else {
					writer.println("Unknown action '" + action + "'");
				}
				finishResponse(writer, started);
				break;
			}

		} catch (Throwable e) {
			if (writer == null) {
				writer = startResponse(request, response, started, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
			e.printStackTrace(writer);
			finishResponse(writer, started);
		}
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
