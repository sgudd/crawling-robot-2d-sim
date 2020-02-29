package de.hfu.in.machinelearning.crawling_robot_2d_sim.models;

import java.awt.Color;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.LimitState;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.dynamics.joint.WheelJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import de.hfu.in.machinelearning.crawling_robot_2d_sim.utilities.Settings;

public class VehicleHandler {

	private final World world;
	private final double scale;
	private BasicBody wheel;
	private BasicBody body;
	private BasicBody pack;
	private BasicBody arm1;
	private BasicBody arm2;

	private RevoluteJoint rj1;
	private RevoluteJoint rj2;

	private double distanceAccumulator;

	private static final double MOTOR_SPEED = Settings.getDouble("robot.armSpeed", Math.PI / 4);

	public VehicleHandler(World world) {
		this(world, 1.0);
	}

	public VehicleHandler(World world, double scale) {
		this.world = world;
		this.scale = scale;
		initialize();
	}
	
	public void initialize() {
		distanceAccumulator = 0.0;

		body = createBody(scale);
		wheel = createWheel(scale);
		arm1 = createArm(scale, Settings.getDouble("robot.arm1Length", 2.0), false);
		arm2 = createArm(scale, Settings.getDouble("robot.arm2Length", 1.5), true);
		pack = createPack(scale);

		wheel.translate(-1.4, -0.3);
		WheelJoint j = new WheelJoint(body, wheel, new Vector2(-1.4, -0.3), new Vector2(0.0, -0.1));
		j.setFrequency(100);
		j.setMotorEnabled(true);
		j.setMaximumMotorTorque(Settings.getDouble("robot.wheelTorque", 13.0));
		world.addJoint(j);

		arm1.translate(1.75, 0.75);
		rj1 = new RevoluteJoint(body, arm1, new Vector2(1.75, 0.75));
		rj1.setMotorEnabled(true);
		rj1.setMaximumMotorTorque(Settings.getDouble("robot.arm1Torque", 160.0));
		rj1.setLimitEnabled(true);
		rj1.setLimits(-Math.PI / 4, Math.PI / 8);
		rj1.setMotorSpeed(0.0);
		world.addJoint(rj1);

		arm2.translate(3.75, 0.75);
		rj2 = new RevoluteJoint(arm1, arm2, new Vector2(3.75, 0.75));
		rj2.setMotorEnabled(true);
		rj2.setMaximumMotorTorque(Settings.getDouble("robot.arm2Torque", 100.0));
		rj2.setLimitEnabled(true);
		rj2.setLimits(0.0, 3 * Math.PI / 4);
		rj2.setMotorSpeed(0.0);
		world.addJoint(rj2);

		WeldJoint wj = new WeldJoint(body, pack, new Vector2(-1.0, 0.5));
		world.addJoint(wj);

		world.addBody(body);
		world.addBody(wheel);
		world.addBody(arm1);
		world.addBody(arm2);
		world.addBody(pack);

		body.translate(0.0, -3.7);
		wheel.translate(0.0, -3.7);
		arm1.translate(0.0, -3.7);
		arm2.translate(0.0, -3.7);
		pack.translate(0.0, -3.7);
	}

	public void setArmDirection(int arm1, int arm2) {
		setArmMotorSpeed(rj1, arm1);
		setArmMotorSpeed(rj2, arm2);
	}

	private static void setArmMotorSpeed(RevoluteJoint j, int direction) {
		if (Constants.DIRECTION_CLOCKWISE == direction && j.getLimitState() != LimitState.AT_UPPER)
			j.setMotorSpeed(MOTOR_SPEED);
		else if (Constants.DIRECTION_COUNTERCLOCKWISE == direction && j.getLimitState() != LimitState.AT_LOWER)
			j.setMotorSpeed(-MOTOR_SPEED);
		else
			j.setMotorSpeed(0.0);
	}

	public double getXPosition() {
		return body.getWorldCenter().x;
	}

	public void accumulateDistance() {
		distanceAccumulator += getXPosition();
	}

	/**
	 * Returns a state vector with the following components:
	 * 
	 * <ol>
	 * <li>Total distance traveled</li>
	 * <li>Angle between vehicle body and first arm in radiants</li>
	 * <li>Angle between both arms in radiants</li>
	 * <li>Vehicle body angle</li>
	 * <li>Current linear velocity (magnitude of the linear velocity vector)</li>
	 * </ol>
	 * 
	 * @return
	 */
	public double[] getStateVector() {
		return new double[] { getXPosition() + distanceAccumulator, rj1.getJointAngle(), rj2.getJointAngle(),
				body.getTransform().getRotation(), body.getLinearVelocity().getMagnitude() };
	}

	private static BasicBody createBody(double scale) {
		BasicBody body = new TextureBody(null, Color.lightGray);
		BodyFixture f;
		f = new BodyFixture( // base rectangle
				Geometry.createRectangle(4.0, 1.0));
		f.setDensity(1.0);
		body.addFixture(f);
		f = new BodyFixture( // arm holder
				Geometry.createRectangle(0.5, 0.5));
		f.getShape().translate(1.75, 0.75);
		f.setDensity(1.0);
		body.addFixture(f);
		f = new BodyFixture( // foot rectangle
				Geometry.createRectangle(0.4, 0.4));
		f.getShape().translate(1.8, -0.7);
		f.setDensity(1.0);
		body.addFixture(f);
		f = new BodyFixture( // foot tip
				Geometry.createSlice(0.2, Math.PI));
		f.getShape().rotate(-Math.PI / 2);
		f.getShape().translate(1.8, -0.9);
		f.setDensity(Settings.getDouble("robot.bodyDensity", 1.0));
		f.setFriction(15.0);
		body.addFixture(f);
		body.setMass(MassType.NORMAL);
		return body;
	}

	private static BasicBody createWheel(double scale) {
		BasicBody wheel = new TextureBody(null, Color.darkGray);
		BodyFixture f = new BodyFixture(Geometry.createCircle(0.8));
		f.setDensity(Settings.getDouble("robot.bodyDensity", 1.0));
		f.setFriction(Settings.getDouble("robot.wheelFriction", 10.0));
		wheel.addFixture(f);
		wheel.setMass(MassType.NORMAL);
		return wheel;
	}

	private static BasicBody createArm(double scale, double length, boolean isTip) {
		BasicBody arm = new TextureBody(null, Color.lightGray);
		double density = Settings.getDouble("robot.armDensity", 0.2);
		BodyFixture f = new BodyFixture(Geometry.createSlice(0.2, Math.PI));
		f.getShape().rotate(Math.PI);
		f.setDensity(density);
		arm.addFixture(f);
		f = new BodyFixture(Geometry.createRectangle(length, 0.4));
		f.getShape().translate(length / 2.0, 0.0);
		f.setDensity(density);
		arm.addFixture(f);
		f = new BodyFixture(Geometry.createSlice(0.2, Math.PI));
		f.getShape().translate(length, 0.0);
		f.setDensity(density);
		if (isTip)
			f.setFriction(Settings.getDouble("robot.armTipFriction", 18.0));
		arm.addFixture(f);
		arm.setMass(MassType.NORMAL);
		arm.rotate(0.0);
		return arm;
	}

	private static BasicBody createPack(double scale) {
		BasicBody pack = new TextureBody(null, Color.orange);
		BodyFixture f = new BodyFixture( // battery pack rectangle
				Geometry.createRectangle(2.0, 1.2));
		f.setDensity(0.05);
		f.getShape().translate(-1.0, 1.1);
		pack.addFixture(f);
		pack.setMass(MassType.NORMAL);
		return pack;
	}
}
