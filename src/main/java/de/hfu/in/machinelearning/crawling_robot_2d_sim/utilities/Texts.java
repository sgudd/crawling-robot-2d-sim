package de.hfu.in.machinelearning.crawling_robot_2d_sim.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Class for retrieval of UI-texts.
 */
public final class Texts {

	private static final Properties properties;

	static {
		InputStream input = Texts.class.getResourceAsStream("/texts.properties");
		properties = new Properties();

		try {
			properties.load(input);
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String get(String key) {
		return properties.getProperty(key);
	}

	public static String get(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}
}
