package de.hfu.in.machinelearning.crawling_robot_2d_sim.utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Class for retrieval and storing of external settings.
 */
public final class Settings {
	private final static Properties properties;

	static {
		properties = new Properties();
		load();
	}

	public static void load() {
		InputStream input = null;
		try {
			input = Texts.class.getResourceAsStream("/settings.default.properties");
			properties.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			input = new FileInputStream("settings.properties");
			properties.load(input);
			input.close();
		} catch (FileNotFoundException e) {
			System.err.println("settings.properties not found, using defaults.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getString(String key) {
		return properties.getProperty(key);
	}

	public static String getString(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	public static int getInteger(String key) {
		return Integer.parseInt(getString(key));
	}

	public static int getInteger(String key, int defaultValue) {
		if (properties.containsKey(key))
			return getInteger(key);
		else
			return defaultValue;
	}

	public static double getDouble(String key) {
		return Double.parseDouble(getString(key));
	}

	public static double getDouble(String key, double defaultValue) {
		if (properties.containsKey(key))
			return getDouble(key);
		else
			return defaultValue;
	}

	public static void set(String key, String value) {
		properties.put(key, value);
	}

	public static void set(String key, int value) {
		set(key, Integer.toString(value));
	}

	public static void set(String key, double value) {
		set(key, Double.toString(value));
	}

	public static void save() {
		try {
			OutputStream out = new FileOutputStream("settings.properties");
			properties.store(out, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
