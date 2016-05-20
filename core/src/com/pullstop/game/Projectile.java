package com.pullstop.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class Projectile extends PhysicBody {
	enum ProjectileShape{
		SQUARE,
		CIRCLE
	}

	public Projectile(float posX, float posY, Texture texture, World world, float density, float restitution, float friction, boolean fixedRotation) {
		super(posX, posY, texture, world, density, restitution, friction, fixedRotation);
	}
	
	@Override
	protected void createBody(float posX, float posY, World world, float density, float restitution, float friction, boolean fixedRotation) {
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(posX, posY);

		body = world.createBody(bodyDef);
		
		Shape shape;
		
		if(!fixedRotation){
			shape = new CircleShape();
			shape.setRadius((texture.getWidth() / 2) / PIXELS_TO_METERS);
		}
		else{
			shape = new PolygonShape();
			((PolygonShape) shape).setAsBox(texture.getWidth() / 2 / PIXELS_TO_METERS, texture.getHeight() / 2 / PIXELS_TO_METERS);
		}
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = density;
		fixtureDef.restitution = restitution;
		fixtureDef.friction = friction;
		fixtureDef.filter.categoryBits = PROJECTILE;
		fixtureDef.filter.maskBits = WORLD | PROJECTILE;

		body.createFixture(fixtureDef);
		body.setFixedRotation(fixedRotation);

		shape.dispose();
	}

	public void move() {
		this.setPosition((body.getPosition().x * PIXELS_TO_METERS) - getWidth() / 2,
				(body.getPosition().y * PIXELS_TO_METERS) - this.getHeight() / 2);
		this.setRotation((float) Math.toDegrees(body.getAngle()));
	}

	@Override
	public String toString() {
		return "Projectile [texture=" + texture + ", body=" + body + "]";
	}

	/*
	 * @Override public void act(float delta){ System.out.println("acting");
	 * if(started){ System.out.println("started"); actorX+=5; } }
	 */
}
