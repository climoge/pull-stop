package com.pullstop.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Character extends PhysicBody {
	public enum MoveState {
		LEFT,
		RIGHT,
		STOP;	
	}
	
	private String name;
	
	public Character(String name, float posX, float posY, Texture texture, World world, float density, float restitution, float friction, boolean rotation) {
		super(posX, posY, texture, world, density, restitution, friction, rotation);
		this.name = name;
	}
	
	public Character(float posX, float posY, Texture texture, World world, float density, float restitution, float friction, boolean rotation) {
		super(posX, posY, texture, world, density, restitution, friction, rotation);
	}
	
	public Character(String name, float posX, float posY, Texture texture, World world) {
		super(posX, posY, texture, world);
		this.name = name;
	}
	
	public Character(float posX, float posY, Texture texture, World world) {
		super(posX, posY, texture, world);
	}
	
	
	@Override
	public String toString() {
		return "Character [texture=" + texture + ", body=" + body + "]";
	}

	public void moveAt(float speed){
		body.setAwake(true);
		Vector2 vel = body.getLinearVelocity();
	    float desiredVel = 0;
	    
	    desiredVel = speed;
	    float velChange = desiredVel - vel.x;
	    float impulse = body.getMass() * velChange; //disregard time factor
	    body.applyLinearImpulse(new Vector2(impulse,0), body.getWorldCenter(), false);
	    
	    this.setPosition((body.getPosition().x * PIXELS_TO_METERS) - getWidth()/2, (body.getPosition().y * PIXELS_TO_METERS) - this.getHeight()/2);
	}
	
	public void jump(){
		body.setAwake(true);
		if (body.getLinearVelocity().y == 0) {
			body.applyForceToCenter(0f, 20f, true);
		}
	}

	public String getName() {
		return name;
	}

	/*
	 * @Override public void act(float delta){ System.out.println("acting");
	 * if(started){ System.out.println("started"); actorX+=5; } }
	 */
}
