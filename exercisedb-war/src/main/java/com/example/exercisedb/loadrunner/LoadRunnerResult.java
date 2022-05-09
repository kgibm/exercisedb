package com.example.exercisedb.loadrunner;

public class LoadRunnerResult {
	public UserResult totalResults = new UserResult();
	
	@Override
	public String toString() {
		return super.toString() + " { totalResults: " + totalResults + "}";
	}
}
