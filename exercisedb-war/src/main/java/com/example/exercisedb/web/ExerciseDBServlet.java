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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.transaction.UserTransaction;

/**
 * Main database servlet
 */
@WebServlet({ "/exercisedbServlet" })
public class ExerciseDBServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(ExerciseDBServlet.class.getCanonicalName());

	private static final long serialVersionUID = 1L;

	private static final String SCHEMA = "test1";

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
			case "reset": {
				PrintWriter writer = startResponse(request, response, started, HttpServletResponse.SC_OK);
				dropTables(writer);
				ensureTables(writer);
				finishResponse(writer, started);
				break;
			}
			case "ensuretables": {
				PrintWriter writer = startResponse(request, response, started, HttpServletResponse.SC_OK);
				ensureTables(writer);
				finishResponse(writer, started);
				break;
			}
			case "droptables": {
				PrintWriter writer = startResponse(request, response, started, HttpServletResponse.SC_OK);
				dropTables(writer);
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

	private Connection getConnection() throws SQLException {
		SQLException firstException = null;
		for (int i = 0; i < 100; i++) {
			try {
				return getActualConnection();
			} catch (SQLException s) {
				if (firstException == null) {
					firstException = s;
				}
				String message = s.getMessage();
				if (message != null) {
					if (message.contains("Connection has been closed")
							|| message.contains("terminating connection due to administrator command")) {
						// Stale exception so try again
						if (LOG.isLoggable(Level.WARNING))
							LOG.warning("getConnection received stale connection: " + s);
						continue;
					}
				}
				throw s;
			}
		}
		throw firstException;
	}

	private Connection getActualConnection() throws SQLException {
		Connection conn = database1.getConnection();
		DatabaseMetaData dbm = conn.getMetaData();
		try (ResultSet rs = dbm.getSchemas()) {
		}
		return conn;
	}

	private void ensureTables(PrintWriter writer) throws SQLException {
		List<String> tableNames = getExistingTableNames();
		if (tableNames.size() == 0) {
			try (Connection conn = getConnection()) {
				executeSimpleQuery(writer, conn, "CREATE SCHEMA IF NOT EXISTS " + SCHEMA + "");
				writer.println("Created schema");

				executeSimpleQuery(writer, conn, "CREATE TABLE " + SCHEMA + ".table1 (ID INT NOT NULL, DATA1 TEXT)");
				writer.println("Created table");
			}
		} else {
			writer.println("Schema and tables already exist");
		}
	}

	private void dropTables(PrintWriter writer) throws SQLException {
		List<String> tableNames = getExistingTableNames();
		if (tableNames.size() > 0) {
			try (Connection conn = getConnection()) {
				for (String tableName : tableNames) {
					executeSimpleQuery(writer, conn, "DROP TABLE " + SCHEMA + "." + tableName);
					writer.println("Dropped table " + tableName);
				}
			}
		} else {
			writer.println("No tables to drop");
		}
	}

	private boolean executeSimpleQuery(PrintWriter writer, Connection conn, String sql) throws SQLException {
		try (PreparedStatement query = conn.prepareStatement(sql)) {
			return query.execute();
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
		try (Connection conn = getConnection()) {
			DatabaseMetaData dbm = conn.getMetaData();
			try (ResultSet tables = dbm.getTables(null, SCHEMA, null, null)) {
				while (tables.next()) {
					tableNames.add(tables.getString("TABLE_NAME"));

				}
			}
		}
		return tableNames;
	}
}
