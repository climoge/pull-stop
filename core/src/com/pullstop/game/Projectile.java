package com.pullstop.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Projectile extends PhysicBody {

	private Vector2 velocity;
	private float angularVelocity;

	public Projectile(float posX, float posY, Texture texture, World world, float density, float restitution, float friction, boolean rotation) {
		super(posX, posY, texture, world, density, restitution, friction, rotation);
	}
	
	@Override
	protected void createBody(float posX, float posY, World world, float density, float restitution, float friction, boolean rotation) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(posX, posY);

		body = world.createBody(bodyDef);

		CircleShape shape = new CircleShape();
		shape.setRadius((texture.getWidth() / 2) / PIXELS_TO_METERS);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = density;
		fixtureDef.restitution = restitution;
		fixtureDef.friction = friction;

		body.createFixture(fixtureDef);
		body.setFixedRotation(rotation);

		shape.dispose();
	}

	public void move() {
		this.setPosition((body.getPosition().x * PIXELS_TO_METERS) - getWidth() / 2,
				(body.getPosition().y * PIXELS_TO_METERS) - this.getHeight() / 2);
		this.setRotation((float) Math.toDegrees(body.getAngle()));
	}

	@Override
	public String toString() {
		return "Projectile [velocity=" + velocity + ", angularVelocity=" + angularVelocity + ", texture=" + texture
				+ ", body=" + body + "]";
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public float getAngularVelocity() {
		return angularVelocity;
	}

	public void setAngularVelocity(float angularVelocity) {
		this.angularVelocity = angularVelocity;
	}

	/*
	 * @Override public void act(float delta){ System.out.println("acting");
	 * if(started){ System.out.println("started"); actorX+=5; } }
	 */
}
