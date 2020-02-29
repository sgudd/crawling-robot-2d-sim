package de.hfu.in.machinelearning.crawling_robot_2d_sim.models;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;

import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Vector2;

public class TextureRenderer {
	public static final void render(Graphics2D g, Shape shape, double scale, Image texture, Color color) {
		// no-op
		if (shape == null) return;
		if (texture == null) {
			ShapeRenderer.render(g, shape, scale, color);
			return;
		}
		
		if (shape instanceof Circle) {
			render(g, (Circle)shape, scale, texture);
		} else if (shape instanceof Polygon) {
			render(g, (Polygon)shape, scale, texture);
		} else {
			// shape not supported for texture rendering, fall back to shape rendering
			ShapeRenderer.render(g, shape, scale, color);
		}
	}
	
	public static final void render(Graphics2D g, Circle circle, double scale, Image texture) {
		double r = circle.getRadius();
		Vector2 cc = circle.getCenter();
		int x = (int)Math.ceil((cc.x - r) * scale);
		int y = (int)Math.ceil((cc.y - r) * scale);
		int w = (int)Math.ceil(r * 2 * scale);
		g.drawImage(texture, x, y, w, w, null);
	}
	
	public static final void render(Graphics2D g, Polygon polygon, double scale, Image texture) {
		Vector2[] vertices = polygon.getVertices();
		int l = vertices.length;
		
		double xMin = Double.POSITIVE_INFINITY;
		double yMin = Double.POSITIVE_INFINITY;
		double xMax = Double.NEGATIVE_INFINITY;
		double yMax = Double.NEGATIVE_INFINITY;
		
		for (int i = 0; i < l; i++) {
			if (vertices[i].x < xMin)
				xMin = vertices[i].x;
			if (vertices[i].y < yMin)
				yMin = vertices[i].y;
			if (vertices[i].x > xMax)
				xMax = vertices[i].x;
			if (vertices[i].y > yMax)
				yMax = vertices[i].y;
		}
		
		int w = (int)Math.ceil((xMax - xMin) * scale);
		int h = (int)Math.ceil((yMax - yMin) * scale);
		int x = (int)Math.ceil(xMin * scale);
		int y = (int)Math.ceil(yMax * scale);
		
		g.drawImage(texture, x, y, w, -h, null);
	}
}
