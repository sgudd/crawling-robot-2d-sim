package de.hfu.in.machinelearning.crawling_robot_2d_sim.rpc;

import de.hfu.in.machinelearning.crawling_robot_2d_sim.App;
import de.hfu.in.machinelearning.crawling_robot_2d_sim.command.SimulationHandler;

/**
 * Handles RPC-Calls. Calls are passed to the {@link SimulationHandler} of the
 * {@link App}-Instance.
 */
public class RpcCallHandler {

	public Object[] action(int duration, int arm1Direction, int arm2Direction) {
		try {
			double[] state = App.getHandler().execute(duration, arm1Direction, arm2Direction);
			Object[] result = new Object[state.length];
			for (int i = 0; i < state.length; ++i)
				result[i] = state[i];
			return result;
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
			throw ex;
		}
	}

	public Object[] reset() {
		App.getHandler().reset();
		return null;
	}
}
