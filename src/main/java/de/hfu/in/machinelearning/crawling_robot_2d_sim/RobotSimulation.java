package de.hfu.in.machinelearning.crawling_robot_2d_sim;

import java.util.List;

import org.dyn4j.dynamics.World;

import de.hfu.in.machinelearning.crawling_robot_2d_sim.command.SimulationCommand;
import de.hfu.in.machinelearning.crawling_robot_2d_sim.models.Constants;
import de.hfu.in.machinelearning.crawling_robot_2d_sim.models.FloorHandler;
import de.hfu.in.machinelearning.crawling_robot_2d_sim.models.VehicleHandler;

/**
 * Manages the robot simulation. This class does not depend on any kind of
 * rendering-code and can be used for both headless and visualized simulation
 * alike.
 */
public class RobotSimulation {

	private final World world;
	private final FloorHandler floor;
	private final VehicleHandler vehicle;

	public RobotSimulation() {
		world = new World();
		floor = new FloorHandler(world, -5.0);
		vehicle = new VehicleHandler(world);
	}

	/**
	 * Returns a list of bodies in the world. May be used for rendering purposes.
	 * 
	 * @return The bodies currently in the {@code World}-Object}
	 */
	public List<org.dyn4j.dynamics.Body> getWorldBodies() {
		return world.getBodies();
	}

	/**
	 * Returns the current X-Offset of the Robot relative to the origin. This value
	 * may be used to compute rewards.
	 * 
	 * @return
	 */
	public double getVehicleXPosition() {
		return vehicle.getXPosition();
	}

	/**
	 * Returns the current state-vector of the vehicle. For further details see
	 * {@link VehicleHandler#getStateVector()}.
	 * 
	 * @return
	 */
	public double[] getVehicleStateVector() {
		return vehicle.getStateVector();
	}

	/**
	 * Advances the Simulation by the given amount of nanoseconds.
	 * 
	 * @param elapsedNanoSeconds
	 */
	public void update(long elapsedNanoSeconds) {
		update((double) elapsedNanoSeconds / 1.0e9);
	}

	/**
	 * Advances the Simulation by the given amount of seconds.
	 * 
	 * @param seconds
	 */
	public synchronized void update(double seconds) {
		world.update(seconds, Integer.MAX_VALUE);
		floor.updateFor(vehicle.getXPosition());
	}

	/**
	 * Applies a robot command to the Simulation. This only sets the arm-directions
	 * according to the {@code command} and does not do any changes to the world
	 * itself. The execution of the command has to be done separately by calling
	 * {@link RobotSimulation#update(double)}.
	 * 
	 * @param command
	 */
	public void applyCommand(SimulationCommand command) {
		applyCommand(command.getArm1Direction(), command.getArm2Direction());
	}

	/**
	 * Sets the arm directions of the robot. Parameters can be
	 * {@link Constants#DIRECTION_NONE}, {@link Constants#DIRECTION_CLOCKWISE} or
	 * {@link Constants#DIRECTION_COUNTERCLOCKWISE}.
	 * 
	 * @param arm1
	 * @param arm2
	 */
	public synchronized void applyCommand(int arm1, int arm2) {
		vehicle.setArmDirection(arm1, arm2);
	}

	/**
	 * Resets the simulation. All bodies will be removed from the world, floor and
	 * robot will be re-initialized with new bodies and added to the world again.
	 * This guarantees that the robot is in the exact same state after each call of
	 * this method.
	 */
	public synchronized void reset() {
		world.removeAllBodies();
		world.removeAllJoints();
		floor.reset();
		floor.initialize();
		vehicle.initialize();
		System.gc();
	}
}
