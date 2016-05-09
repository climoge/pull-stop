package com.pullstop.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Character extends Actor{
	private Texture texture;
	private float _speed = 1.f;

	public Character(float posX, float posY, Texture texture) {
		this.setPosition(posX, posY);
		this.texture = texture;
		setBounds(getX(), getY(), texture.getWidth(), texture.getHeight());
		setOrigin(texture.getWidth()/2, texture.getHeight()/2);
	}

	@Override
	public void draw(Batch batch, float alpha) {
		batch.draw(texture, this.getX(), getY(), this.getOriginX(), this.getOriginY(), this.getWidth(),
				this.getHeight(), this.getScaleX(), this.getScaleY(), this.getRotation(), 0, 0, texture.getWidth(),
				texture.getHeight(), false, false);
	}

	public float getSpeed() {
		return _speed;
	}

	/*
	 * @Override public void act(float delta){ System.out.println("acting");
	 * if(started){ System.out.println("started"); actorX+=5; } }
	 */
}
