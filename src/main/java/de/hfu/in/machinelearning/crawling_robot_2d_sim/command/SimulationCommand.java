package de.hfu.in.machinelearning.crawling_robot_2d_sim.command;

import java.util.function.Consumer;

/**
 * Represents a simulation command. This is mainly used by the visualized
 * simulation to keep track of the time of the command that has already been
 * executed. And to call-back to the waiting {@link RealtimeSimulationHandler}.
 */
public final class SimulationCommand {
	private final long durationNanos;
	private final int arm1Direction;
	private final int arm2Direction;
	private final Consumer<double[]> callbackFunction;

	private long timePassed;

	public SimulationCommand(int durationMillis, int arm1, int arm2, Consumer<double[]> callback) {
		durationNanos = durationMillis * 1000 * 1000;
		arm1Direction = arm1;
		arm2Direction = arm2;
		callbackFunction = callback;
		timePassed = 0;
	}

	public void update(long time) {
		timePassed += time;
	}

	/**
	 * Returns the time this command has left in nanoseconds.
	 * 
	 * @return
	 */
	public long getTimeLeft() {
		return durationNanos - timePassed;
	}

	/**
	 * Finishes the command, i.e. calls the callback-function that sends the
	 * {@code stateVector} back to the caller of the RPC.
	 * 
	 * @param stateVector
	 */
	public void finish(double[] stateVector) {
		callbackFunction.accept(stateVector);
	}

	public int getArm1Direction() {
		return arm1Direction;
	}

	public int getArm2Direction() {
		return arm2Direction;
	}
}