package com.pullstop.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Enemy extends Actor implements Moveable{
	public static final short WORLD = 0x1;
	public static final short PROJECTILE = 0x2;
	public static final short CHARACTER = 0x3;
	
	final static protected float PIXELS_TO_METERS = 100f;

	protected Texture texture;
	protected Body body;

	public Enemy(float posX, float posY, Texture texture, World world) {
		this.setPosition(posX, posY);
		this.texture = texture;
		setBounds(getX(), getY(), texture.getWidth(), texture.getHeight());
		setOrigin(texture.getWidth() / 2, texture.getHeight() / 2);

		createBody(posX + texture.getWidth() / 2 / PIXELS_TO_METERS, posY + texture.getHeight() / 2 / PIXELS_TO_METERS, world);
	}

	protected void createBody(float posX, float posY, World world) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(posX, posY);

		body = world.createBody(bodyDef);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(texture.getWidth() / 2 / PIXELS_TO_METERS, texture.getHeight() / 2 / PIXELS_TO_METERS);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;

		body.createFixture(fixtureDef);

		shape.dispose();
	}
	
	@Override
	public void move() {
		this.setPosition((body.getPosition().x * PIXELS_TO_METERS) - getWidth() / 2,
				(body.getPosition().y * PIXELS_TO_METERS) - this.getHeight() / 2);
	}
	
	@Override
	public String toString() {
		return "Enemy [texture=" + texture + ", body=" + body + "]";
	}

	@Override
	public void draw(Batch batch, float alpha) {
		batch.draw(texture, this.getX(), getY(), this.getOriginX(), this.getOriginY(), this.getWidth(),
				this.getHeight(), this.getScaleX(), this.getScaleY(), this.getRotation(), 0, 0, texture.getWidth(),
				texture.getHeight(), false, false);
	}

	public Vector2 getPosition(){
		return body.getPosition();
	}
}
