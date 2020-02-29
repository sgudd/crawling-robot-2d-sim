package de.hfu.in.machinelearning.crawling_robot_2d_sim.models;

import java.awt.Color;
import java.awt.Graphics2D;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Shape;

/**
 * Represents a body that will be rendered in a given color.
 */
public class SolidColorBody extends BasicBody {

	private final Color color;

	public SolidColorBody() {
		this(ShapeRenderer.getRandomColor());
	}

	public SolidColorBody(Color color) {
		super();
		this.color = color;
	}

	@Override
	public void renderFixture(Graphics2D g, BodyFixture fixture, double scale) {
		ShapeRenderer.render(g, (Shape) fixture.getShape(), scale, color);
	}

	public Color getColor() {
		return color;
	}
}