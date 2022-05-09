package com.example.exercisedb.loadrunner;

public class UserResult {
	public int count;
	public long totalExecutionTime;
	public long maxExecutionTime;

	public void add(UserResult other) {
		count += other.count;
		totalExecutionTime += other.totalExecutionTime;
		if (other.maxExecutionTime > maxExecutionTime) {
			maxExecutionTime = other.maxExecutionTime;
		}
	}

	@Override
	public String toString() {
		return super.toString() + " { count: " + count + ", totalExecutionTime: " + totalExecutionTime
				+ ", maxExecutionTime: " + maxExecutionTime + ", averageExecutionTime: "
				+ String.format("%.2f", getAverageExecutionTime()) + "}";
	}

	public double getAverageExecutionTime() {
		return count == 0 ? 0 : (double) totalExecutionTime / (double) count;
	}
}
