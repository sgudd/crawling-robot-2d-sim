package de.hfu.in.machinelearning.crawling_robot_2d_sim;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;

import de.hfu.in.machinelearning.crawling_robot_2d_sim.command.SimulationCommand;
import de.hfu.in.machinelearning.crawling_robot_2d_sim.models.BasicBody;
import de.hfu.in.machinelearning.crawling_robot_2d_sim.models.Constants;
import de.hfu.in.machinelearning.crawling_robot_2d_sim.utilities.Settings;

/**
 * Canvas that handles the visualization of the robot simulation in 2D.
 */
public class SimulationCanvas extends Canvas {
	private static final long serialVersionUID = 7102467556911209990L;
	private final Thread simulationLoop;
	private final double scaleFactor = 32.0;
	private final RobotSimulation simulation;
	private final boolean checkeredBackground;

	private long nanoClock;
	private int dir1 = Constants.DIRECTION_NONE;
	private int dir2 = Constants.DIRECTION_NONE;

	private SimulationCommand currentCommand;

	private static final Color backgroundColor = Color.white;
	private static final Color backgroundLineColor = new Color(230, 230, 230);
	private static final Stroke backgroundStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
			new float[] { 4 }, 0);

	public SimulationCanvas(RobotSimulation simulation) {
		super();
		this.simulation = simulation;
		simulationLoop = new SimulationThread();
		checkeredBackground = Settings.getInteger("canvas.checkeredBackground", 1) != 0;
		setFocusable(false);
	}

	public void dispose() {
		simulationLoop.interrupt();
		try {
			simulationLoop.join(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Starts the simulation loop thread that periodically draws the current state
	 * of the simulation.
	 */
	public void start() {
		nanoClock = System.nanoTime();
		simulationLoop.start();
	}

	private void loop() {
		// obtain buffer strategy and graphics
		BufferStrategy buf = getBufferStrategy();
		Graphics2D g = (Graphics2D) buf.getDrawGraphics();

		int w = getWidth();
		int h = getHeight();

		// determine the vehicle position
		double posX = (simulation.getVehicleXPosition() * scaleFactor);

		// clear the screen
		g.setColor(backgroundColor);
		g.fillRect(0, 0, w, h);
		if (checkeredBackground)
			drawBackground(g, w, h, (int) posX);

		// flip the y-axis
		g.transform(AffineTransform.getScaleInstance(1, -1));
		// move origin to center
		g.transform(AffineTransform.getTranslateInstance(((double) w / 2) - posX, -h / 2));

		synchronized (simulation) {
			// advance the simulation
			updateWorld();
			// render all bodies
			renderWorld(g);
		}

		// dispose graphics
		g.dispose();

		// flip buffers
		if (!buf.contentsLost())
			buf.show();

		if (currentCommand == null)
			simulation.applyCommand(dir1, dir2);
	}

	private void drawBackground(Graphics2D g, int w, int h, int posX) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(backgroundLineColor);
		g2d.setStroke(backgroundStroke);
		posX = -posX % 40;
		if (posX > 0)
			posX -= 40;
		for (int x = posX; x <= w; x += 40)
			g2d.drawLine(x, 0, x, h);
		for (int y = 0; y <= h; y += 40)
			g2d.drawLine(posX, y, w - posX, y);
		g2d.dispose();
	}

	private void renderWorld(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		for (org.dyn4j.dynamics.Body body : simulation.getWorldBodies())
			render(g, (BasicBody) body);
	}

	private void render(Graphics2D g, BasicBody body) {
		body.render(g, scaleFactor);
	}

	private long updateNanoClock() {
		long now = System.nanoTime();
		long elapsed = now - nanoClock;
		nanoClock = now;
		// System.out.println("FPS = " + (1000 / (elapsed / 1e6)));
		return elapsed;
	}

	private void updateWorld() {
		long timeElapsed = updateNanoClock();

		// if a command task is active it needs to be updated (and maybe finalized)
		// first
		if (currentCommand != null) {
			// if a command is active the thread must be synchronized to ensure the command
			// is not being started before it has been fully applied to the simulation
			long taskTimeLeft = currentCommand.getTimeLeft();
			if (taskTimeLeft <= timeElapsed) { // the simulation task is finished with this iteration
				// update the world for the exact task time
				simulation.update(taskTimeLeft);
				// update the command time, it should be zero after this
				currentCommand.update(taskTimeLeft);
				// execute the callback handler
				currentCommand.finish(simulation.getVehicleStateVector());
				// the command is completed at this point, delete the reference
				currentCommand = null;
				// the remaining time will still be progressed outside of the if-block
				timeElapsed = timeElapsed - taskTimeLeft;
			} else
				currentCommand.update(timeElapsed);
		}
		// advance the world
		simulation.update(timeElapsed);
	}

	/**
	 * Allows the execution of commands in real time. If there is already a command
	 * in progress this method will throw an {@link IllegalStateException}.
	 * 
	 * @param command The command to be executed
	 * @throws IllegalStateException
	 */
	public void setCommand(SimulationCommand command) {
		if (currentCommand != null)
			throw new IllegalStateException("There is already a command being executed!");
		synchronized (simulation) {
			currentCommand = command;
			simulation.applyCommand(command);
		}
	}

	private final class SimulationThread extends Thread {
		@Override
		public void run() {
			try {
				while (!isInterrupted()) {
					SimulationCanvas.this.loop();
					Thread.sleep(5);
				}
			} catch (InterruptedException e) {
			}
			System.err.println("Simulation loop exited.");
		}
	}

	/**
	 * Changes the direction of the lower arm manually. Can be used for manual
	 * control of the robot.
	 * 
	 * @param arm1
	 */
	public void setArm1Direction(int arm1) {
		dir1 = arm1;
	}

	/**
	 * Changes the direction of the upper arm manually. Can be used for manual
	 * control of the robot.
	 * 
	 * @param arm2
	 */
	public void setArm2Direction(int arm2) {
		dir2 = arm2;
	}

	/**
	 * Changes the direction of both arms manually. Can be used for manual control
	 * of the robot.
	 * 
	 * @param arm1
	 * @param arm2
	 */
	public void setArmDirection(int arm1, int arm2) {
		dir1 = arm1;
		dir2 = arm2;
	}

	/**
	 * Creates the buffer strategy for the canvas. Call this method AFTER the JFrame
	 * has been made visible.
	 */
	public void initialize() {
		setIgnoreRepaint(true);
		createBufferStrategy(2);
	}
}
