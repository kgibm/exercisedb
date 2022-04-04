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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Main database servlet
 */
@WebServlet({ "/exercisedbServlet" })
public class ExerciseDBServlet extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(ExerciseDBServlet.class.getCanonicalName());

	private static final long serialVersionUID = 1L;

	private static final String SCHEMA = "test1";

	private static final String RANDOM_STRING = getRandomString(1024);

	@Resource(lookup = "jdbc/database1")
	private DataSource database1;

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
			case "insert": {
				PrintWriter writer = startResponse(request, response, started, HttpServletResponse.SC_OK);
				long id = insert();
				writer.println("Inserted 1 row with ID " + id);
				finishResponse(writer, started);
				break;
			}
			case "insertselect": {
				PrintWriter writer = startResponse(request, response, started, HttpServletResponse.SC_OK);
				long id = insert();
				writer.println("Inserted 1 row with ID " + id);
				String data = select(id);
				writer.println("Selected row " + id + " with data " + data);
				finishResponse(writer, started);
				break;
			}
			case "insertselectdelete": {
				PrintWriter writer = startResponse(request, response, started, HttpServletResponse.SC_OK);
				long id = insert();
				writer.println("Inserted 1 row with ID " + id);
				String data = select(id);
				writer.println("Selected row " + id + " with data " + data);
				delete(id);
				writer.println("Deleted row with ID " + id);
				finishResponse(writer, started);
				break;
			}
			case "count": {
				PrintWriter writer = startResponse(request, response, started, HttpServletResponse.SC_OK);
				long count = count();
				writer.println("Count of rows: " + count);
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

	private long insert() throws SQLException {
		try (Connection conn = getConnection()) {
			long id = -1;
			try (PreparedStatement sql = conn.prepareStatement("INSERT INTO " + SCHEMA + ".table1 (DATA1) values (?)",
					Statement.RETURN_GENERATED_KEYS)) {
				sql.setString(1, RANDOM_STRING);
				int insertedRows = sql.executeUpdate();
				if (insertedRows == 1) {
					try (ResultSet generatedKeys = sql.getGeneratedKeys()) {
						if (generatedKeys.next()) {
							id = generatedKeys.getLong(1);
						}
					}
					return id;
				} else {
					throw new SQLException("Expected 1 inserted row but received " + insertedRows);
				}
			}
		}
	}

	private String select(long id) throws SQLException {
		try (Connection conn = getConnection()) {
			try (PreparedStatement sql = conn
					.prepareStatement("SELECT DATA1 FROM " + SCHEMA + ".table1 WHERE ID = ?")) {
				sql.setLong(1, id);
				try (ResultSet rs = sql.executeQuery()) {
					if (rs.next()) {
						String data = rs.getString(1);
						return data;
					} else {
						throw new SQLException("Expected to find row with ID " + id);
					}
				}
			}
		}
	}

	private long count() throws SQLException {
		try (Connection conn = getConnection()) {
			try (PreparedStatement sql = conn.prepareStatement("SELECT COUNT(*) FROM " + SCHEMA + ".table1")) {
				try (ResultSet rs = sql.executeQuery()) {
					if (rs.next()) {
						long result = rs.getLong(1);
						return result;
					} else {
						throw new SQLException("Expected to find count");
					}
				}
			}
		}
	}

	private void delete(long id) throws SQLException {
		try (Connection conn = getConnection()) {
			try (PreparedStatement sql = conn.prepareStatement("DELETE FROM " + SCHEMA + ".table1 WHERE ID = ?")) {
				sql.setLong(1, id);
				int updatedRows = sql.executeUpdate();
				if (updatedRows == 1) {
					// nothing, successful
				} else {
					throw new SQLException("Expected 1 updated row but received " + updatedRows);
				}
			}
		}
	}

	private static String getRandomString(int length) {
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		while (sb.length() < length) {
			sb.append(chars.charAt(random.nextInt(chars.length())));
		}
		return sb.toString();
	}

	private Connection getConnection() throws SQLException {
		SQLException firstException = null;
		for (int i = 0; i < 100; i++) {
			try {
				Connection conn = database1.getConnection();
				if (conn.isValid(0)) {
					return conn;
				}
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
		if (firstException == null) {
			firstException = new SQLException("No valid connection found");
		}
		throw firstException;
	}

	private void ensureTables(PrintWriter writer) throws SQLException {
		List<String> tableNames = getExistingTableNames();
		if (tableNames.size() == 0) {
			try (Connection conn = getConnection()) {
				executeSimpleQuery(writer, conn, "CREATE SCHEMA IF NOT EXISTS " + SCHEMA + "");
				writer.println("Created schema");

				executeSimpleQuery(writer, conn, "CREATE TABLE " + SCHEMA
						+ ".table1 (ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, DATA1 TEXT)");
				writer.println("Created table");
			}
		} else {
			writer.println("Schema and tables already exist");
		}
	}

	private void dropTables(PrintWriter writer) throws SQLException {
		try (Connection conn = getConnection()) {
			executeSimpleQuery(writer, conn, "DROP SCHEMA IF EXISTS " + SCHEMA + " CASCADE");
			writer.println("Dropped schema " + SCHEMA);
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
