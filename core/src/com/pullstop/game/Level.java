package com.pullstop.game;

import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

	private Character pullCharacter;
	private Character stopCharacter;

	boolean hasControllers = true;
	String message = "Please install a controller";
	BitmapFont font;

	private Controller controller;

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

		for (Actor actor : stage.getActors()) {
			if (actor.getClass() == Character.class) {
				if (((Character) actor).getName().compareTo("pull") == 0) {
					pullCharacter = (Character) actor;
				} else if (((Character) actor).getName().compareTo("stop") == 0) {
					stopCharacter = (Character) actor;
				} else {
					Objects.requireNonNull(pullCharacter, "You have to create a character named \"pull\"");
					Objects.requireNonNull(stopCharacter, "You have to create a character named \"stop\"");
				}
			}
		}

		Gdx.input.setInputProcessor(this);

		if (Controllers.getControllers().size == 0) {
			hasControllers = false;
		} else
			controller = Controllers.getControllers().first();

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		characterOnFocus = (Character) stage.getActors().first();

		camera.position.set(new Vector3(characterOnFocus.getX(), characterOnFocus.getY(), 0f));

		stage.getViewport().setCamera(camera);
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Input.Keys.LEFT) {
		}
		if (keycode == Input.Keys.RIGHT) {
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
			freezeProjectiles();
		}

		if (keycode == Input.Keys.S) {
			unFreezeProjectiles();
		}

		if (keycode == Input.Keys.SPACE) {
			pullProjectilesTo(pullCharacter.getPosition());
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
		if (keycode == Input.Keys.LEFT) {
		}
		if (keycode == Input.Keys.RIGHT) {
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

	public void update() {

		for (Actor actor : stage.getActors()) {
			((PhysicBody) actor).move();
		}
		camera.position.interpolate(new Vector3(characterOnFocus.getX(), characterOnFocus.getY(), 0f), 0.2f,
				Interpolation.linear);
		// camera.position.set(new Vector3(characterOnFocus.getX(),
		// characterOnFocus.getY(), 0f));
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

		/* pullCharacter inputs */

		if (controller.getAxis(XBox360Pad.AXIS_LEFT_X) > 0.3f || controller.getAxis(XBox360Pad.AXIS_LEFT_X) < -0.3f) {
			pullCharacter.moveAt(controller.getAxis(XBox360Pad.AXIS_LEFT_X) * 5);
		} else {
			pullCharacter.moveAt(0);
		}

		if (controller.getAxis(XBox360Pad.AXIS_LEFT_TRIGGER) == 1.f) {
			pullProjectilesTo(pullCharacter.getPosition());
		}

		if (controller.getButton(XBox360Pad.BUTTON_LB)) {
			pullCharacter.jump();
		}

		/* stopCharacter inputs */

		if (controller.getAxis(XBox360Pad.AXIS_RIGHT_X) > 0.3f || controller.getAxis(XBox360Pad.AXIS_RIGHT_X) < -0.3f) {
			stopCharacter.moveAt(controller.getAxis(XBox360Pad.AXIS_RIGHT_X) * 5);
		} else {
			stopCharacter.moveAt(0);
		}

		if (controller.getAxis(XBox360Pad.AXIS_RIGHT_TRIGGER) > 0.f) {
			freezeProjectiles();
		} else {
			unFreezeProjectiles();
		}

		if (controller.getButton(XBox360Pad.BUTTON_RB)) {
			stopCharacter.jump();
		}
	}

	public void freezePhysicBodies(PhysicBody physicBodies[]) {
		for (PhysicBody physicBody : physicBodies) {
			physicBody.freeze();
		}
	}

	public void unFreezePhysicBodies(PhysicBody physicBodies[]) {
		for (PhysicBody physicBody : physicBodies) {
			physicBody.unFreeze();
		}
	}

	public void freezeProjectiles() {
		for (Actor actor : stage.getActors()) {
			if (actor instanceof Projectile) {
				((PhysicBody) actor).freeze();
			}
		}
	}

	public void unFreezeProjectiles() {
		for (Actor actor : stage.getActors()) {
			if (actor instanceof Projectile) {
				((PhysicBody) actor).unFreeze();
			}
		}
	}
	
	public void pullProjectilesTo(final Vector2 p){
		for (Actor actor : stage.getActors()) {
			if (actor instanceof Projectile) {
				if (((Projectile) actor).body.getType() == BodyType.DynamicBody) {
					((Projectile) actor).pullTo(p, 0.5f);
				}
			}
		}
	}

	public OrthographicCamera getCamera() {
		return camera;
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
}
