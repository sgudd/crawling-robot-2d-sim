package de.hfu.in.machinelearning.crawling_robot_2d_sim;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Locale;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcServer;
import org.apache.xmlrpc.server.XmlRpcServerConfigImpl;
import org.apache.xmlrpc.webserver.WebServer;

import de.hfu.in.machinelearning.crawling_robot_2d_sim.command.HeadlessSimulationHandler;
import de.hfu.in.machinelearning.crawling_robot_2d_sim.command.RealtimeSimulationHandler;
import de.hfu.in.machinelearning.crawling_robot_2d_sim.command.SimulationHandler;
import de.hfu.in.machinelearning.crawling_robot_2d_sim.rpc.RpcCallHandler;
import de.hfu.in.machinelearning.crawling_robot_2d_sim.utilities.Settings;
import de.hfu.in.machinelearning.crawling_robot_2d_sim.utilities.Texts;

/**
 * The main entry-point of the Application.
 */
public class App {
	private static SimulationHandler handler;
	private static WebServer rpcServer;
	
	public static SimulationHandler getHandler() {
		return handler;
	}

	public static void main(String[] args) {
		boolean headless = false;
		int rpcPort = Settings.getInteger("rpc.port", 8080);
		for (int i = 0; i < args.length; ++i) {
			if ("--headless".equals(args[i]))
				headless = true;
			else if ("--port".equals(args[i]))
				rpcPort = Integer.parseInt(args[i + 1]);
		}
		
		// use the system-native Look-And-Feel when possible
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		// set the default locale to english, this affects the output of numbers in string format
		Locale.setDefault(Locale.ENGLISH);
		
		// setup the simulation
		RobotSimulation simulation = new RobotSimulation();
		if (!headless) {
			SimulationWindow window = new SimulationWindow(simulation);
			handler = new RealtimeSimulationHandler(simulation, window.getCanvas());
			window.setVisible(true);
			window.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosed(WindowEvent e) {
					if (rpcServer != null)
						rpcServer.shutdown();
				}
			});
		} else {
			handler = new HeadlessSimulationHandler(simulation);
			System.out.println(Texts.get("App.runningHeadless"));
		}
		try {
			// run the XML-RPC-Server
			setupRpcServer(rpcPort);
		} catch (Exception e) {
			System.err.println(Texts.get("App.rpcFailure"));
			e.printStackTrace();
			rpcServer = null;
		}
	}
	
	private static void setupRpcServer(int port) throws XmlRpcException, IOException {
		rpcServer = new WebServer(port);
    	XmlRpcServer xmlRpcServer = rpcServer.getXmlRpcServer();
    	PropertyHandlerMapping phm = new PropertyHandlerMapping();
    	phm.addHandler("Robot", RpcCallHandler.class);
    	xmlRpcServer.setHandlerMapping(phm);
    	XmlRpcServerConfigImpl serverConfig = (XmlRpcServerConfigImpl) xmlRpcServer.getConfig();
    	serverConfig.setEnabledForExtensions(true);
    	serverConfig.setContentLengthOptional(false);
    	rpcServer.start();
	}
}