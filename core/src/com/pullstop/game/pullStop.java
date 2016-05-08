package com.pullstop.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class pullStop extends ApplicationAdapter {
	private OrthographicCamera camera;
	SpriteBatch batch;
	Texture texture;
	private BitmapFont font;
	private Sprite sprite;
	
	@Override
	public void create () {
		camera = new OrthographicCamera(1280, 720);
		float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        batch = new SpriteBatch();
        
        texture = new Texture(Gdx.files.internal("badlogic.jpg"));
        sprite = new Sprite(texture);
        sprite.setPosition(w/2 -sprite.getWidth()/2, h/2 - sprite.getHeight()/2);
        
		font = new BitmapFont();
        font.setColor(Color.GREEN);
	}
	
	@Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.setProjectionMatrix(camera.combined);
		
		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
            if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                sprite.translateX(-1f);
            else
                sprite.translateX(-10.0f);
        }
		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
            if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
                sprite.translateX(1f);
            else
                sprite.translateX(10.0f);
        }
		if(Gdx.input.isKeyPressed(Input.Keys.Q)){
            if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
            	camera.translate(-1f,0);
            else
            	camera.translate(-10.0f,0);
            camera.update();
        }
		if(Gdx.input.isKeyPressed(Input.Keys.D)){
            if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
            	camera.translate(1f,0);
            else
            	camera.translate(10.0f,0);
        }
		camera.position.set(sprite.getX(), sprite.getY(), 0);
        camera.update();
		batch.begin();
			sprite.draw(batch);
			font.draw(batch, "Hello World", Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		batch.end();
	}
	
	@Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
