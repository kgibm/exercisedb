package com.example.exercisedb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class Database {
	private static final Logger LOG = Logger.getLogger(Database.class.getCanonicalName());

	public static final String SCHEMA = "test1";
	public static final String TABLE = "table1";

	private static final String RANDOM_STRING = getRandomString(1024);

	public static long insert(DataSource database) throws SQLException {
		try (Connection conn = getConnection(database)) {
			long id = -1;
			try (PreparedStatement sql = conn.prepareStatement("INSERT INTO " + SCHEMA + "." + TABLE + " (DATA1) values (?)",
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

	public static String select(DataSource database, long id) throws SQLException {
		try (Connection conn = getConnection(database)) {
			try (PreparedStatement sql = conn
					.prepareStatement("SELECT DATA1 FROM " + SCHEMA + "." + TABLE + " WHERE ID = ?")) {
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

	public static long count(DataSource database) throws SQLException {
		try (Connection conn = getConnection(database)) {
			try (PreparedStatement sql = conn.prepareStatement("SELECT COUNT(*) FROM " + SCHEMA + "." + TABLE)) {
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

	public static void delete(DataSource database, long id) throws SQLException {
		try (Connection conn = getConnection(database)) {
			try (PreparedStatement sql = conn.prepareStatement("DELETE FROM " + SCHEMA + "." + TABLE + " WHERE ID = ?")) {
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

	public static String getRandomString(int length) {
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		while (sb.length() < length) {
			sb.append(chars.charAt(random.nextInt(chars.length())));
		}
		return sb.toString();
	}

	public static Connection getConnection(DataSource database) throws SQLException {
		SQLException firstException = null;
		for (int i = 0; i < 100; i++) {
			try {
				Connection conn = database.getConnection();
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

	public static boolean ensureTables(DataSource database) throws SQLException {
		List<String> tableNames = getExistingTableNames(database);
		if (tableNames.size() == 0) {
			try (Connection conn = getConnection(database)) {
				executeSimpleQuery(conn, "CREATE SCHEMA IF NOT EXISTS " + SCHEMA + "");
				executeSimpleQuery(conn, "CREATE TABLE " + SCHEMA
						+ "." + TABLE + " (ID INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY, DATA1 TEXT)");
				return true;
			}
		} else {

			return false;
		}
	}

	public static void dropTables(DataSource database) throws SQLException {
		try (Connection conn = getConnection(database)) {
			executeSimpleQuery(conn, "DROP SCHEMA IF EXISTS " + SCHEMA + " CASCADE");
		}
	}

	public static boolean executeSimpleQuery(Connection conn, String sql) throws SQLException {
		try (PreparedStatement query = conn.prepareStatement(sql)) {
			return query.execute();
		}
	}

	public static List<String> getExistingTableNames(DataSource database) throws SQLException {
		List<String> tableNames = new ArrayList<>();
		try (Connection conn = getConnection(database)) {
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
