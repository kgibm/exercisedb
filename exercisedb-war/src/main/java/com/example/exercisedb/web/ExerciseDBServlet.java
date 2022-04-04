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
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * 
 */
@WebServlet({ "/exercisedbServlet" })
public class ExerciseDBServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Resource(lookup = "jdbc/database1")
	DataSource database1;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Date started = new Date();
		try {
			String action = request.getParameter("action");
			switch (action == null ? "" : action.toLowerCase()) {
			case "listtables": {
				List<String> tableNames = getExistingTableNames();
				PrintWriter writer = startResponse(request, response, started, HttpServletResponse.SC_OK);
				writer.println("There are " + tableNames.size() + " tables in the database: " + tableNames);
				finishResponse(writer, started);
				break;
			}
			case "ensuretables": {
				PrintWriter writer = startResponse(request, response, started, HttpServletResponse.SC_OK);
				finishResponse(writer, started);
				break;
			}
			case "testthrow": {
				throw new Error("Test error");
			}
			default:
				PrintWriter writer = startResponse(request, response, started, HttpServletResponse.SC_OK);
				if (action == null || action.trim().length() == 0) {
					writer.println("No action specified");
				} else {
					writer.println("Unknown action '" + action + "'");
				}
				finishResponse(writer, started);
				break;
			}

		} catch (Throwable e) {
			PrintWriter writer = startResponse(request, response, started,
					HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
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

	private List<String> getExistingTableNames() throws SQLException {
		List<String> tableNames = new ArrayList<>();
		try (Connection conn = database1.getConnection()) {
			DatabaseMetaData dbm = conn.getMetaData();
			try (ResultSet tables = dbm.getTables(null, null, null, null)) {
				while (tables.next()) {
					tableNames.add(tables.getString("TABLE_NAME"));

				}
			}
		}
		return tableNames;
	}
}
