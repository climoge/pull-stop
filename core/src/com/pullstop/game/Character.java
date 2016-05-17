package com.pullstop.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Character extends PhysicBody{
	public enum MoveState {
		LEFT,
		RIGHT,
		STOP;	
	}
	
	private MoveState moveState = MoveState.STOP;

	public Character(float posX, float posY, Texture texture, World world, float density, float restitution, float friction, boolean rotation) {
		super(posX, posY, texture, world, density, restitution, friction, rotation);
	}
	
	public Character(float posX, float posY, Texture texture, World world) {
		super(posX, posY, texture, world);
	}
	
	public void move(){
		Vector2 vel = body.getLinearVelocity();
	    float desiredVel = 0;
	    switch ( moveState )
	    {
	      case LEFT:  desiredVel = -5; break;
	      case STOP:  desiredVel =  0; break;
	      case RIGHT: desiredVel =  5; break;
	    }
	    float velChange = desiredVel - vel.x;
	    float impulse = body.getMass() * velChange; //disregard time factor
	    body.applyLinearImpulse(new Vector2(impulse,0), body.getWorldCenter(), false);
	    
	    this.setPosition((body.getPosition().x * PIXELS_TO_METERS) - getWidth()/2, (body.getPosition().y * PIXELS_TO_METERS) - this.getHeight()/2);
	}

	public MoveState getMoveState() {
		return moveState;
	}

	public void setMoveState(MoveState movestate) {
		this.moveState = movestate;
	}

	/*
	 * @Override public void act(float delta){ System.out.println("acting");
	 * if(started){ System.out.println("started"); actorX+=5; } }
	 */
}
