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

public class Character extends Actor{
	final static float PIXELS_TO_METERS = 100f;

	public enum MoveState {
		LEFT,
		RIGHT,
		STOP;	
	}
	
	private Texture texture;
	public Body body;
	private MoveState moveState = MoveState.STOP;

	public Character(float posX, float posY, Texture texture, World world) {
		this.setPosition(posX, posY);
		this.texture = texture;
		setBounds(getX(), getY(), texture.getWidth(), texture.getHeight());
		setOrigin(texture.getWidth()/2, texture.getHeight()/2);
		
		createBody(posX, posY, world);
	}
	
	private void createBody(float posX, float posY, World world){
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(posX, posY);
		
		body = world.createBody(bodyDef);

		PolygonShape shape = new PolygonShape();
		shape.setAsBox(texture.getWidth()/2 / PIXELS_TO_METERS, texture.getHeight()/2 / PIXELS_TO_METERS);
		/*CircleShape shape = new CircleShape();

		shape.setRadius((texture.getWidth() / 2) / PIXELS_TO_METERS);*/
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.1f;
		fixtureDef.restitution = 0f;
		fixtureDef.friction = 0f;
		
		body.createFixture(fixtureDef);
		body.setFixedRotation(true);

		shape.dispose();
		
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

	@Override
	public void draw(Batch batch, float alpha) {
		batch.draw(texture, this.getX(), getY(), this.getOriginX(), this.getOriginY(), this.getWidth(),
				this.getHeight(), this.getScaleX(), this.getScaleY(), this.getRotation(), 0, 0, texture.getWidth(),
				texture.getHeight(), false, false);
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
