package de.hfu.in.machinelearning.crawling_robot_2d_sim.models;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;

/**
 * Provides basic rendering capabilities for Dyn4j bodies. All bodies in this
 * project inherit from this class and can safely be casted to it when received
 * from a {@link World}-Object.
 */
public abstract class BasicBody extends org.dyn4j.dynamics.Body {

	public BasicBody() {
		super();
	}

	public abstract void renderFixture(Graphics2D g, BodyFixture fixture, double scale);

	/**
	 * Renders all fixtures of this body using the
	 * {@link #renderFixture(Graphics2D, BodyFixture, double)}-Method that has to be
	 * implemented by the subclasses.
	 * 
	 * @param g
	 * @param scale Scale-factor at which the fixtures should be rendered. Any
	 *              coordinates will be multiplied by this factor before they are
	 *              drawn to the {@link Graphics2D}-Instance.
	 */
	public void render(Graphics2D g, double scale) {
		// point radius
//		final int pr = 4;

		// save the original transform
		AffineTransform ot = g.getTransform();

		// transform the coordinate system from world coordinates to local coordinates
		AffineTransform lt = new AffineTransform();
		lt.translate(transform.getTranslationX() * scale, transform.getTranslationY() * scale);
		lt.rotate(transform.getRotation());

		// apply the transform
		g.transform(lt);

		// loop over all the body fixtures for this body
		for (BodyFixture fixture : fixtures)
			renderFixture(g, fixture, scale);

		// draw a center point
//		Ellipse2D.Double ce = new Ellipse2D.Double(getLocalCenter().x * scale - pr * 0.5,
//				this.getLocalCenter().y * scale - pr * 0.5, pr, pr);
//		g.setColor(Color.WHITE);
//		g.fill(ce);
//		g.setColor(Color.DARK_GRAY);
//		g.draw(ce);

		// set the original transform
		g.setTransform(ot);
	}
}
