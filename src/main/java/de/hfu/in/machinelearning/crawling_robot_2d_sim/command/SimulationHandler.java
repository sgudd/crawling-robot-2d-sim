package de.hfu.in.machinelearning.crawling_robot_2d_sim.command;

public interface SimulationHandler {
	/**
	 * Has to apply the given arm-directions for {@code duration} milliseconds and
	 * return the vehicle state-vector after the exact amount of time has passed in
	 * the simulation.
	 * 
	 * @param duration      Duration for how long the command has to be applied
	 * @param arm1Direction Direction of the first arm
	 * @param arm2Direction Direction of the second arm
	 * @return State-vector based on the world-object after the given amount of time
	 *         has passed.
	 */
	public double[] execute(int duration, int arm1Direction, int arm2Direction);

	/**
	 * Has to reset the simulation. After every call of this method the simulation
	 * has to be in the exact same state.
	 */
	public void reset();

}
