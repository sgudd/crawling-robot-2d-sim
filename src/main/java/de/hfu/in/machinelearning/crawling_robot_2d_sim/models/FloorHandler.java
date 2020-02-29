package de.hfu.in.machinelearning.crawling_robot_2d_sim.models;

import java.awt.Color;

import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import de.hfu.in.machinelearning.crawling_robot_2d_sim.utilities.Settings;

/**
 * Creates and manages three floor tiles that simulate an infinite floor in both
 * x-directions. The {@link #updateFor(double)}-Method needs to be called in the
 * after every world-update with the current x-position of the robot to ensure
 * the tiles are moved around accordingly as soon as the robot passes a certain
 * point.
 */
public final class FloorHandler {

	private final World world;
	private final BasicBody[] tiles;
	private final static double TILE_WIDTH = Settings.getDouble("floor.tileWidth", 20.0);
	private final static double TILE_HEIGHT = Settings.getDouble("floor.tileHeight", 0.4);

	public FloorHandler(World world, double y) {
		this(world, new Vector2(0.0, y));
	}

	public FloorHandler(World world, Vector2 position) {
		this.world = world;
		tiles = new BasicBody[3];
		tiles[0] = createTile();
		tiles[1] = createTile();
		tiles[2] = createTile();
		tiles[0].translate(position.copy().add(-TILE_WIDTH, 0.0));
		tiles[1].translate(position);
		tiles[2].translate(position.copy().add(TILE_WIDTH, 0.0));
		initialize();
	}

	public void initialize() {
		world.addBody(tiles[0]);
		world.addBody(tiles[1]);
		world.addBody(tiles[2]);
	}

	/**
	 * Updates the floor tile bodies according to the current (vehicle-) position.
	 * If the position is greater than the center of the right most tile, the left
	 * most tile will be shifted to the right. If the position is smaller than the
	 * center of the left most tile, the right most tile will be shifted to the
	 * left. This creates an seemingly infinite floor in both directions.
	 * 
	 * @param currentPosition
	 */
	public void updateFor(double currentPosition) {
		double limitX = tiles[2].getWorldCenter().x;
		if (Double.compare(currentPosition, limitX) > 0) {
			// move the leftmost tile by 3x width to the right
			tiles[0].translate(TILE_WIDTH * 3.0, 0.0);
			BasicBody tmp = tiles[0];
			tiles[0] = tiles[1];
			tiles[1] = tiles[2];
			tiles[2] = tmp;
		} else {
			limitX = tiles[0].getWorldCenter().x;
			if (Double.compare(currentPosition, limitX) < 0) {
				// move the rightmost tile by 3x width to the left
				tiles[2].translate(-TILE_WIDTH * 3.0, 0.0);
				BasicBody tmp = tiles[2];
				tiles[2] = tiles[1];
				tiles[1] = tiles[0];
				tiles[0] = tmp;
			}
		}
	}

	public void reset() {
		double x = tiles[1].getTransform().getTranslationX();
		tiles[0].translate(-x, 0.0);
		tiles[1].translate(-x, 0.0);
		tiles[2].translate(-x, 0.0);
	}

	private static BasicBody createTile() {
		BasicBody tile = new TextureBody(null, Color.darkGray.darker()); // TODO add texture
		tile.addFixture(Geometry.createRectangle(TILE_WIDTH, TILE_HEIGHT));
		tile.setMass(MassType.INFINITE);
		return tile;
	}
}
