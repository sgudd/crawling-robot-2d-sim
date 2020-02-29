package de.hfu.in.machinelearning.crawling_robot_2d_sim.models;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Shape;

/**
 * Represents a body that will be rendered by a provided texture-image. If no
 * image is available the color will be used instead.
 */
public class TextureBody extends SolidColorBody {

	private final Image texture;

	public TextureBody(Image texture, Color color) {
		super(color);
		this.texture = texture;
	}

	@Override
	public void renderFixture(Graphics2D g, BodyFixture fixture, double scale) {
		TextureRenderer.render(g, (Shape) fixture.getShape(), scale, texture, getColor());
	}
}
