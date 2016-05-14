package com.pullstop.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Projectile extends Actor {
	final static float PIXELS_TO_METERS = 100f;

	private Texture texture;
	private Vector2 velocity;
	private Vector2 angularVelocity;

	public Body body;

	public Projectile(float posX, float posY, Texture texture, World world) {
		this.setPosition(posX, posY);
		this.texture = texture;
		setBounds(getX(), getY(), texture.getWidth(), texture.getHeight());
		setOrigin(texture.getWidth() / 2, texture.getHeight() / 2);

		createBody(posX, posY, world);
	}

	private void createBody(float posX, float posY, World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(posX, posY);

		body = world.createBody(bodyDef);

		CircleShape shape = new CircleShape();

		shape.setRadius((texture.getWidth() / 2) / PIXELS_TO_METERS);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.1f;
		fixtureDef.restitution = 0.2f;
		fixtureDef.friction = 0.5f;

		body.createFixture(fixtureDef);

		shape.dispose();

	}

	public void move() {
		this.setPosition((body.getPosition().x * PIXELS_TO_METERS) - getWidth() / 2,
				(body.getPosition().y * PIXELS_TO_METERS) - this.getHeight() / 2);
		this.setRotation((float) Math.toDegrees(body.getAngle()));
	}

	@Override
	public void draw(Batch batch, float alpha) {
		batch.draw(texture, this.getX(), getY(), this.getOriginX(), this.getOriginY(), this.getWidth(),
				this.getHeight(), this.getScaleX(), this.getScaleY(), this.getRotation(), 0, 0, texture.getWidth(),
				texture.getHeight(), false, false);
	}
	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public Vector2 getAngularVelocity() {
		return angularVelocity;
	}

	public void setAngularVelocity(Vector2 angularVelocity) {
		this.angularVelocity = angularVelocity;
	}

	/*
	 * @Override public void act(float delta){ System.out.println("acting");
	 * if(started){ System.out.println("started"); actorX+=5; } }
	 */
}
