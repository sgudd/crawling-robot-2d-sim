package de.hfu.in.machinelearning.crawling_robot_2d_sim.command;

import de.hfu.in.machinelearning.crawling_robot_2d_sim.RobotSimulation;
import de.hfu.in.machinelearning.crawling_robot_2d_sim.SimulationCanvas;

/**
 * Implementation for the real-time (visualized) simulation. Calls of the
 * {@link #execute(int, int, int)}-Method are redirected to the simulation
 * canvas and will wait until the command has finished. If the thread gets
 * interrupted while waiting {@code null} is returned.
 */
public class RealtimeSimulationHandler implements SimulationHandler {

	private final SimulationCanvas canvas;
	private final RobotSimulation simulation;
	private double[] result;

	public RealtimeSimulationHandler(RobotSimulation simulation, SimulationCanvas canvas) {
		this.canvas = canvas;
		this.simulation = simulation;
	}

	public synchronized double[] execute(int duration, int arm1Direction, int arm2Direction) {
		canvas.setCommand(new SimulationCommand(duration, arm1Direction, arm2Direction, state -> {
			result = state;
			synchronized (RealtimeSimulationHandler.this) {
				RealtimeSimulationHandler.this.notify();
			}
		}));
		try {
			wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}

	@Override
	public void reset() {
		simulation.reset();
	}
}
