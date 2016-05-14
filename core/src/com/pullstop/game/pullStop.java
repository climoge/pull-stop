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
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.pullstop.game.Character.MoveState;

public class pullStop extends ApplicationAdapter implements InputProcessor {
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
	Character characterOnFocus;
	Matrix4 debugMatrix;

	MoveByAction moveAction = new MoveByAction();

	private Stage stage;

	float torque = 0.0f;
	boolean drawSprite = true;

	final float PIXELS_TO_METERS = 100f;

	@Override
	public void create() {
		stage = new Stage();

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		world = new World(new Vector2(0, -9.8f), true);

		System.out.println(w + h);

		debugRenderer = new Box2DDebugRenderer();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, w, h);
		camera.update();

		tiledMap = new TmxMapLoader().load("map.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

		/* Array<Body> bodies = */MapBodyBuilder.buildShapes(tiledMap, 64f, world, stage);

		Gdx.input.setInputProcessor(this);

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		characterOnFocus = (Character) stage.getActors().first();

		camera.position.set(new Vector3(characterOnFocus.getX(), characterOnFocus.getY(), 0f));

		stage.getViewport().setCamera(camera);

		// sb = new SpriteBatch();
		// sprite = new Sprite(texture);
	}

	@Override
	public void render() {
		// Test de vitesse constante
		for (Actor actor : stage.getActors()) {
			if (actor.getClass() == Character.class) {
				((Character) actor).move();
			}
			if (actor.getClass() == Projectile.class) {
				((Projectile) actor).move();
			}
		}

		camera.position.set(new Vector3(characterOnFocus.getX(), characterOnFocus.getY(), 0f));
		camera.update();

		world.step(1f / 60f, 6, 2);

		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();

		debugMatrix = camera.combined.cpy().scale(PIXELS_TO_METERS, PIXELS_TO_METERS, 0);

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
		if (keycode == Input.Keys.LEFT) {
			((Character) stage.getActors().first()).body.setAwake(true);
			((Character) stage.getActors().first()).setMoveState(MoveState.LEFT);
			// body.setLinearVelocity(-character1.getSpeed(),body.getLinearVelocity().y);
			// body.applyForceToCenter(10f,0f,true);
		}
		if (keycode == Input.Keys.RIGHT) {
			((Character) stage.getActors().first()).body.setAwake(true);
			((Character) stage.getActors().first()).setMoveState(MoveState.RIGHT);
			// body.setLinearVelocity(character1.getSpeed(),body.getLinearVelocity().y);
		}
		if (keycode == Input.Keys.UP) {
			if (((Character) stage.getActors().first()).body.getLinearVelocity().y == 0) {
				((Character) stage.getActors().first()).body.applyForceToCenter(0f, 20f, true);
			}
		}

		if (keycode == Input.Keys.TAB) {

		}

		if (keycode == Input.Keys.D) {
			for (Actor actor : stage.getActors()) {
				if (actor.getClass() == Projectile.class) {
					if (((Projectile) actor).body.getType() == BodyType.StaticBody) {
						((Projectile) actor).body.setType(BodyType.DynamicBody);
						((Projectile) actor).body.setLinearVelocity(((Projectile) actor).getVelocity());
						((Projectile) actor).body.setAngularVelocity(((Projectile) actor).getAngularVelocity());
					}
				}
			}
		}

		if (keycode == Input.Keys.S) {
			for (Actor actor : stage.getActors()) {
				if (actor.getClass() == Projectile.class) {
					if (((Projectile) actor).body.getType() == BodyType.DynamicBody) {
						((Projectile) actor).setVelocity(((Projectile) actor).body.getLinearVelocity());
						((Projectile) actor).setAngularVelocity(((Projectile) actor).body.getAngularVelocity());
						((Projectile) actor).body.setType(BodyType.StaticBody);	
					}
				}
			}
			// character2.body.setType(BodyType.StaticBody);
		}

		if (keycode == Input.Keys.SPACE) {
			for (Actor actor : stage.getActors()) {
				if (actor.getClass() == Projectile.class) {
					if (((Projectile) actor).body.getType() == BodyType.DynamicBody) {
						float directionVector = ((Character)stage.getActors().first()).body.getPosition().sub(((Projectile) actor).body.getPosition()).angle();
						((Projectile) actor).body.applyForceToCenter(new Vector2(10f,0).rotate(directionVector), true);;
					}
				}
			}
		}

		if (keycode == Input.Keys.ESCAPE){
			((Character) stage.getActors().first()).body.setLinearVelocity(0f, 0f);
			((Character) stage.getActors().first()).body.setAngularVelocity(0f);
			torque = 0f;
			((Character) stage.getActors().first()).setPosition(0f, 500f);
			((Character) stage.getActors().first()).body.setTransform(0f, 5f, 0f);
		}

		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Input.Keys.LEFT && ((Character) stage.getActors().first()).getMoveState() == MoveState.LEFT) {
			((Character) stage.getActors().first()).setMoveState(MoveState.STOP);
			// body.setLinearVelocity(0f,body.getLinearVelocity().y);
		}
		if (keycode == Input.Keys.RIGHT && ((Character) stage.getActors().first()).getMoveState() == MoveState.RIGHT) {
			((Character) stage.getActors().first()).setMoveState(MoveState.STOP);
			// body.setLinearVelocity(0f,body.getLinearVelocity().y);
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
