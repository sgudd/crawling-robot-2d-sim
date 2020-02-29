package de.hfu.in.machinelearning.crawling_robot_2d_sim.command;

import de.hfu.in.machinelearning.crawling_robot_2d_sim.RobotSimulation;
import de.hfu.in.machinelearning.crawling_robot_2d_sim.models.Constants;

/**
 * Implementation for headless simulation. Any calls to
 * {@link HeadlessSimulationHandler#execute(int, int, int)} update the world
 * immediately and return without delay.
 */
public class HeadlessSimulationHandler implements SimulationHandler {

	private final RobotSimulation simulation;

	public HeadlessSimulationHandler(RobotSimulation simulation) {
		this.simulation = simulation;
	}

	@Override
	public double[] execute(int duration, int arm1Direction, int arm2Direction) {
		simulation.applyCommand(arm1Direction, arm2Direction);
		simulation.update((double) duration / 1.0e3);
		simulation.applyCommand(Constants.DIRECTION_NONE, Constants.DIRECTION_NONE);
		return simulation.getVehicleStateVector();
	}

	@Override
	public void reset() {
		simulation.reset();
	}
}
