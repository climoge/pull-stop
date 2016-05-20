package com.pullstop.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;

public class Character extends PhysicBody {
	public enum MoveState {
		LEFT,
		RIGHT,
		STOP;	
	}
	
	private String name;
	
	private boolean grabbed;
	private Character grabber;	
	
	public Character(String name, float posX, float posY, Texture texture, World world, float density, float restitution, float friction, boolean fixedRotation) {
		super(posX, posY, texture, world, density, restitution, friction, fixedRotation);
		this.name = name;
		updateFilter();
	}
	
	public Character(float posX, float posY, Texture texture, World world, float density, float restitution, float friction, boolean fixedRotation) {
		super(posX, posY, texture, world, density, restitution, friction, fixedRotation);
		updateFilter();
	}
	
	public Character(String name, float posX, float posY, Texture texture, World world) {
		super(posX, posY, texture, world);
		this.name = name;
		updateFilter();
	}
	
	
	public Character(float posX, float posY, Texture texture, World world) {
		super(posX, posY, texture, world);
		updateFilter();
	}
	
	public void updateFilter(){
		Filter filter = this.body.getFixtureList().first().getFilterData();
		filter.categoryBits = CHARACTER;
		filter.maskBits = WORLD | PROJECTILE;
		//filter.groupIndex = -1;
		this.body.getFixtureList().first().setFilterData(filter);
	}
	
	@Override
	public String toString() {
		return "Character [texture=" + texture + ", body=" + body + "]";
	}

	public void moveAt(float speed){
		if(!grabbed){
			body.setAwake(true);
			Vector2 vel = body.getLinearVelocity();
		    float desiredVel = 0;
		    
		    desiredVel = speed;
		    float velChange = desiredVel - vel.x;
		    float impulse = body.getMass() * velChange; //disregard time factor
		    body.applyLinearImpulse(new Vector2(impulse,0), body.getWorldCenter(), false);
		    
		    this.setPosition((body.getPosition().x * PIXELS_TO_METERS) - getWidth()/2, (body.getPosition().y * PIXELS_TO_METERS) - this.getHeight()/2);
		}
		else{
			body.setTransform(grabber.getPosition().x, grabber.getPosition().y + (grabber.getHeight())/PIXELS_TO_METERS, 0);
		    this.setPosition((body.getPosition().x * PIXELS_TO_METERS) - getWidth()/2, (body.getPosition().y * PIXELS_TO_METERS) - this.getHeight()/2);
		}
	}
	
	public void jump(){
		body.setAwake(true);
		if (body.getLinearVelocity().y == 0) {
			body.applyForceToCenter(0f, 20f, true);
		}
	}
	
	public boolean isColliding(Character character){
		for(Contact contact : body.getWorld().getContactList()){
			if(contact.getFixtureA().getBody() == body && contact.getFixtureB().getBody() == character.body)
				return true;
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void grabbedBy(Character character) {
		grabbed = true;
		grabber = character;
	}
	
	public boolean isGrabbed(){
		return grabbed;
	}
	
	public void free(){
		grabbed = false;
		grabber = null;
	}
}
