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
		response.setContentType("text/plain");

		PrintWriter writer = response.getWriter();

		writer.println("Request started at " + new Date());
		writer.flush();

		try {

			String action = request.getParameter("action");
			switch (action == null ? "" : action) {
			case "listtables":
				List<String> tableNames = getExistingTableNames();
				writer.println("There are " + tableNames.size() + " tables in the database: " + tableNames);
				break;
			default:
				writer.println("No action specified");
				break;
			}

		} catch (Throwable e) {
			e.printStackTrace(writer);
		}

		writer.println("Request finished at " + new Date());
		writer.flush();
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
