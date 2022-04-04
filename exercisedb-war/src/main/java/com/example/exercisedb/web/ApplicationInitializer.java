package com.example.exercisedb.web;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton
@Startup
public class ApplicationInitializer {

	private static final Logger LOG = Logger.getLogger(ApplicationInitializer.class.getName());

	@PostConstruct
	private void onStartup() {
		if (LOG.isLoggable(Level.INFO))
			LOG.info(toString() + " started");

		// code

		if (LOG.isLoggable(Level.INFO))
			LOG.info(toString() + " finished");
	}
}
