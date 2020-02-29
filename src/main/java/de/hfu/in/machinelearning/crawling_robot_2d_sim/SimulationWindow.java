package de.hfu.in.machinelearning.crawling_robot_2d_sim;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

import de.hfu.in.machinelearning.crawling_robot_2d_sim.models.Constants;
import de.hfu.in.machinelearning.crawling_robot_2d_sim.utilities.Settings;
import de.hfu.in.machinelearning.crawling_robot_2d_sim.utilities.Texts;

/**
 * The main window for the graphical visualization of the simulation. Only used
 * in visualized mode.
 */
public class SimulationWindow extends JFrame implements ActionListener {
	private static final long serialVersionUID = 6171846801166871667L;
	private final SimulationCanvas canvas;
	private final RobotSimulation simulation;

	public SimulationWindow(RobotSimulation simulation) {
		super(Texts.get("SimulationWindow.title"));

		this.simulation = simulation;

		addWindowListener(new ShutdownListener());
		addKeyListener(new DebugKeyListener());

		JMenuBar menubar = new JMenuBar();
		JMenu fileMenu = new JMenu(Texts.get("SimulationWindow.fileMenu"));
		JMenuItem item = new JMenuItem(Texts.get("SimulationWindow.reset"));
		item.setActionCommand("reset");
		item.addActionListener(this);
		fileMenu.add(item);
		item = new JMenuItem(Texts.get("SimulationWindow.quit"));
		item.setActionCommand("quit");
		item.addActionListener(this);
		fileMenu.add(item);
		menubar.add(fileMenu);

		JMenu helpMenu = new JMenu(Texts.get("SimulationWindow.helpMenu"));
		item = new JMenuItem(Texts.get("SimulationWindow.about"));
		item.setActionCommand("about");
		item.addActionListener(this);
		helpMenu.add(item);
		menubar.add(helpMenu);

		setJMenuBar(menubar);

		setLayout(new BorderLayout());
		canvas = new SimulationCanvas(simulation);
		add(canvas, BorderLayout.CENTER);

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(Settings.getInteger("window.width", 500), Settings.getInteger("window.height", 500));
	}

	public SimulationCanvas getCanvas() {
		return canvas;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			canvas.initialize();
			canvas.start();
		}
	}

	private final class ShutdownListener extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent e) {
			dispose();
		}
	}

	private final class DebugKeyListener extends KeyAdapter {

		@Override
		public void keyPressed(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_LEFT) {
				canvas.setArm1Direction(Constants.DIRECTION_COUNTERCLOCKWISE);
			} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
				canvas.setArm1Direction(Constants.DIRECTION_CLOCKWISE);
			} else if (e.getKeyCode() == KeyEvent.VK_UP) {
				canvas.setArm2Direction(Constants.DIRECTION_COUNTERCLOCKWISE);
			} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
				canvas.setArm2Direction(Constants.DIRECTION_CLOCKWISE);
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
				canvas.setArm1Direction(Constants.DIRECTION_NONE);
			} else if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
				canvas.setArm2Direction(Constants.DIRECTION_NONE);
			} else if (e.getKeyCode() == KeyEvent.VK_R) {
				simulation.reset();
			}
		}
	}
	
	@Override
	public void dispose() {
		// shut down the loop-thread of the canvas
		canvas.dispose();
		// store any settings that may have changed
		Settings.save();
		// dispose the window
		super.dispose();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "quit":
			dispose();
			break;
		case "reset":
			simulation.reset();
			break;
		case "about":
			// TODO about-window
			break;
		}
	}
}
