package de.hfu.in.machinelearning.crawling_robot_2d_sim.models;

/**
 * Provides constant values/magic numbers that need to be used throughout the
 * project and don't fit in any particular class.
 */
public interface Constants {

	// unused constants for repeatable texture-rendering
	public static final int REPEAT_NONE = 0x00;
	public static final int REPEAT_X = 0x01;
	public static final int REPEAT_Y = 0x02;
	public static final int REPEAT_BOTH = 0x03;
	
	// directions for arm-movements
	public static final int DIRECTION_CLOCKWISE = 1;
	public static final int DIRECTION_COUNTERCLOCKWISE = -1;
	public static final int DIRECTION_NONE = 0;
}
