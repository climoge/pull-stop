package com.pullstop.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;

public class pullStop extends ApplicationAdapter {
	Level level;
	Body bodyPlatform;

	Matrix4 debugMatrix;

	MoveByAction moveAction = new MoveByAction();

	final float PIXELS_TO_METERS = 100f;

	@Override
	public void create() {
		level = new Level(false);
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0.5f, 0.9f, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		level.update();
	}

	@Override
	public void dispose() {
		level.getStage().dispose();
		level.getWorld().dispose();
	}

	 @Override
	 public void resize(int width, int height) {
		level.getCamera().setToOrtho(false, width, height);

		level.getCamera().setToOrtho(false, width, height);
	 }
}
