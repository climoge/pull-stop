package com.pullstop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.pullstop.game.Character.MoveState;

public class Level implements InputProcessor {
	final float PIXELS_TO_METERS = 100f;

	private TiledMap tiledMap;

	private TiledMapRenderer tiledMapRenderer;

	private World world;
	private Stage stage;
	private OrthographicCamera camera;

	private Character characterOnFocus;

	private Box2DDebugRenderer debugRenderer;

	private Vector2 gravity;

	private Matrix4 debugMatrix;

	private boolean debugMode;

	public Level() {
		this(false);
	}

	public Level(boolean debugMode) {
		this(new Vector2(0, -9.8f), debugMode);
	}

	public Level(Vector2 gravity, boolean debugMode) {

		this.gravity = gravity;
		this.debugMode = debugMode;

		debugRenderer = new Box2DDebugRenderer();

		stage = new Stage();

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		world = new World(gravity, true);

		camera = new OrthographicCamera();
		camera.setToOrtho(false, w, h);
		camera.update();

		tiledMap = new TmxMapLoader().load("map.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);

		MapBodyBuilder.buildShapes(tiledMap, 64f, world, stage);

		Gdx.input.setInputProcessor(this);

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		characterOnFocus = (Character) stage.getActors().first();

		camera.position.set(new Vector3(characterOnFocus.getX(), characterOnFocus.getY(), 0f));

		stage.getViewport().setCamera(camera);
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
			characterOnFocus = (Character) stage.getActors().get(1);
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
						float directionVector = ((Character) stage.getActors().first()).body.getPosition()
								.sub(((Projectile) actor).body.getPosition()).angle();
						((Projectile) actor).body.applyForceToCenter(new Vector2(10f, 0).rotate(directionVector), true);
						;
					}
				}
			}
		}

		if (keycode == Input.Keys.ESCAPE) {
			((Character) stage.getActors().first()).body.setLinearVelocity(0f, 0f);
			((Character) stage.getActors().first()).body.setAngularVelocity(0f);
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

	public Vector2 getGravity() {
		return gravity;
	}

	public void setGravity(Vector2 gravity) {
		this.gravity = gravity;
	}

	public World getWorld() {
		return world;
	}

	public Stage getStage() {
		return stage;
	}

	public void update() {

		for (Actor actor : stage.getActors()) {
			((PhysicBody) actor).move();
		}
		camera.position.interpolate(new Vector3(characterOnFocus.getX(), characterOnFocus.getY(), 0f), 0.2f, Interpolation.linear);
		//camera.position.set(new Vector3(characterOnFocus.getX(), characterOnFocus.getY(), 0f));
		camera.update();

		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();

		world.step(1f / 60f, 6, 2);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

		if (debugMode) {
			debugMatrix = camera.combined.cpy().scale(PIXELS_TO_METERS, PIXELS_TO_METERS, 0);
			debugRenderer.render(world, debugMatrix);
		}

	}
}
