package com.example.exercisedb.web;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.sql.DataSource;

import com.example.exercisedb.Database;

@Singleton
@Startup
public class ApplicationInitializer {

	private static final Logger LOG = Logger.getLogger(ApplicationInitializer.class.getName());

	@Resource(lookup = "jdbc/database1")
	private DataSource database;

	@PostConstruct
	private void onStartup() {
		if (LOG.isLoggable(Level.INFO))
			LOG.info(toString() + " started");

		try {
			Database.dropTables(database);
			Database.ensureTables(database);
			if (LOG.isLoggable(Level.INFO))
				LOG.info(toString() + " created table " + Database.SCHEMA + "." + Database.TABLE);
		} catch (SQLException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.severe("Could not initialize database: " + e);
				e.printStackTrace();
			}
		}

		if (LOG.isLoggable(Level.INFO))
			LOG.info(toString() + " finished");
	}
}
