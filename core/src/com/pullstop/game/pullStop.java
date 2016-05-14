package com.pullstop.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.utils.Array;
import com.pullstop.game.Character.MoveState;

public class pullStop extends ApplicationAdapter implements InputProcessor{
	Texture img;
	TiledMap tiledMap;
	OrthographicCamera camera;
	TiledMapRenderer tiledMapRenderer;
	SpriteBatch sb;
	Texture texture;
	Sprite sprite;
	World world;
	Body bodyPlatform;
	Box2DDebugRenderer debugRenderer;
	Matrix4 debugMatrix;

	Character character1;
	Character character2;
	MoveByAction moveAction = new MoveByAction();

	private Stage stage;

	float torque = 0.0f;
	boolean drawSprite = true;

	final float PIXELS_TO_METERS = 100f;

	@Override
	public void create () {
		stage = new Stage();

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		
		world = new World(new Vector2(0, -9.8f), true);

		character1 = new Character(0, 5f, new Texture(Gdx.files.internal("pika.png")), world);
		character1.setTouchable(Touchable.enabled);
		
		character2 = new Character(10f, 5f, new Texture(Gdx.files.internal("pika.png")), world);
		character2.setTouchable(Touchable.enabled);

		System.out.println(w + h);

		
				
		
		debugRenderer = new Box2DDebugRenderer();

		camera = new OrthographicCamera();
		camera.setToOrtho(false,w,h);
		camera.update();

		tiledMap = new TmxMapLoader().load("map.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		
		Array<Body> bodies = MapBodyBuilder.buildShapes(tiledMap, 64f, world);
		
		Gdx.input.setInputProcessor(this);        
		
		camera = new OrthographicCamera(Gdx.graphics.getWidth(),Gdx.graphics.
                getHeight());
		
		stage.getViewport().setCamera(camera);
		
		stage.addActor(character1);
		stage.addActor(character2);


		//sb = new SpriteBatch();
		//sprite = new Sprite(texture);
	}
	
	@Override
	public void render () {
		// Test de vitesse constante
		character1.move();
		character2.move();
	    // Fin test
	    
	    
		camera.position.set(new Vector3(character1.getX(), character1.getY(), 0f));
		camera.update();
		
		world.step(1f/60f, 6, 2);
		
		/*character1.setPosition((character1.body.getPosition().x * PIXELS_TO_METERS) - character1.getWidth()/2, (character1.body.getPosition().y * PIXELS_TO_METERS) - character1.getHeight()/2);
		System.out.println("Stage height : " + stage.getHeight() + " character Y : " + character1.getY() + " body Y : " + (character1.body.getPosition().y * PIXELS_TO_METERS));
		*/
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();
		
		debugMatrix = camera.combined.cpy().scale(PIXELS_TO_METERS, 
                PIXELS_TO_METERS, 0);
		
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		
		debugRenderer.render(world, debugMatrix);
	}

	@Override
	public void dispose() {
		stage.dispose();
		world.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.LEFT){
			character1.body.setAwake(true);
			character1.setMoveState(MoveState.LEFT);
			//body.setLinearVelocity(-character1.getSpeed(),body.getLinearVelocity().y);
			//body.applyForceToCenter(10f,0f,true);
		}
		if(keycode == Input.Keys.RIGHT){
			character1.body.setAwake(true);
			character1.setMoveState(MoveState.RIGHT);
			//body.setLinearVelocity(character1.getSpeed(),body.getLinearVelocity().y);
		}
		if(keycode == Input.Keys.UP){
			if(character1.body.getLinearVelocity().y == 0){
				character1.body.applyForceToCenter(0f,20f,true);
			}
		}
		
		if(keycode == Input.Keys.Q){
			
		}
			
        if(keycode == Input.Keys.D){
        	character2.body.setType(BodyType.DynamicBody);
        }
        
        if(keycode == Input.Keys.S){
        	character2.body.setType(BodyType.StaticBody);
        }
        
        if(keycode == Input.Keys.SPACE) {
        	character1.body.setLinearVelocity(0f, 0f);
        	character1.body.setAngularVelocity(0f);
            torque = 0f;
            character1.setPosition(0f,500f);
            character1.body.setTransform(0f,5f,0f);
        }
		
        if(keycode == Input.Keys.ESCAPE)
            drawSprite = !drawSprite;
        
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Input.Keys.LEFT && character1.getMoveState() == MoveState.LEFT){
			character1.setMoveState(MoveState.STOP);
			//body.setLinearVelocity(0f,body.getLinearVelocity().y);
		}
		if(keycode == Input.Keys.RIGHT && character1.getMoveState() == MoveState.RIGHT){
			character1.setMoveState(MoveState.STOP);
			//body.setLinearVelocity(0f,body.getLinearVelocity().y);
		}
		return true;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
}
