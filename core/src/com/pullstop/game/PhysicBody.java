package com.pullstop.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PhysicBody extends Actor{
	final static protected float PIXELS_TO_METERS = 100f;
	
	protected Texture texture;
	protected Body body;

	public PhysicBody(float posX, float posY, Texture texture, World world, float density, float restitution, float friction, boolean rotation) {
		this.setPosition(posX, posY);
		this.texture = texture;
		setBounds(getX(), getY(), texture.getWidth(), texture.getHeight());
		setOrigin(texture.getWidth()/2, texture.getHeight()/2);
		
		createBody(posX, posY, world, density, restitution, friction, rotation);
	}
	
	public PhysicBody(float posX, float posY, Texture texture, World world) {
		this.setPosition(posX, posY);
		this.texture = texture;
		setBounds(getX(), getY(), texture.getWidth(), texture.getHeight());
		setOrigin(texture.getWidth()/2, texture.getHeight()/2);
		
		createBody(posX, posY, world, 0.15f, 0f, 0f, true);
	}
	
	protected void createBody(float posX, float posY, World world, float density, float restitution, float friction, boolean rotation){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(posX, posY);
		
		body = world.createBody(bodyDef);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(texture.getWidth()/2 / PIXELS_TO_METERS, texture.getHeight()/2 / PIXELS_TO_METERS);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = density;
		fixtureDef.restitution = restitution;
		fixtureDef.friction = friction;
		
		body.createFixture(fixtureDef);
		body.setFixedRotation(rotation);

		shape.dispose();	
	}
	
	public void move(){
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
}
