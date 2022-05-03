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

	private static final String CLASS = ApplicationInitializer.class.getName();
	private static final Logger LOG = Logger.getLogger(CLASS);

	@Resource(lookup = Database.JNDINAME)
	private DataSource database;

	@PostConstruct
	private void onStartup() {
		if (LOG.isLoggable(Level.FINER))
			LOG.entering(CLASS, "onStartup");

		try {

			Database.ensureTables(database);

			if (LOG.isLoggable(Level.INFO))
				LOG.info(toString() + " confirmed " + Database.SCHEMA + "." + Database.TABLE + " exists");

		} catch (SQLException e) {
			if (LOG.isLoggable(Level.SEVERE)) {
				LOG.severe("Could not initialize database: " + e);
				e.printStackTrace();
			}
		}

		if (LOG.isLoggable(Level.FINER))
			LOG.exiting(CLASS, "onStartup");
	}
}
