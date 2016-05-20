package com.pullstop.game;

import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class Level{
	final float PIXELS_TO_METERS = 100f;
	
	public static long startTime;
	public static long elapsedTime;

	private TiledMap tiledMap;

	private TiledMapRenderer tiledMapRenderer;

	private World world;
	private Stage stage;
	private OrthographicCamera camera;

	private Box2DDebugRenderer debugRenderer;

	private Vector2 gravity;

	private Matrix4 debugMatrix;

	private boolean debugMode;

	private Character pullCharacter;
	private Character stopCharacter;

	boolean hasControllers = true;
	String message = "Please install a controller";
	BitmapFont font;
	
	private boolean timeStopped;

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
			if (actor instanceof Character) {
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

		if (Controllers.getControllers().size == 0) {
			hasControllers = false;
		} else
			controller = Controllers.getControllers().first();

		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		camera.position.set(new Vector3((pullCharacter.getX() + stopCharacter.getX())*0.5f, (pullCharacter.getY() + stopCharacter.getY())*0.5f, 0f));

		stage.getViewport().setCamera(camera);
		
		startTime = System.currentTimeMillis();
	}

	public void update() {
		elapsedTime = System.currentTimeMillis() - startTime;
		for (Actor actor : stage.getActors()) {
			((Moveable) actor).move();
			if(actor instanceof Enemy && !timeStopped && elapsedTime > 3000){
				startTime = System.currentTimeMillis();
				Enemy enemy = (Enemy) actor;
				stage.addActor(new Projectile(enemy.getX() / PIXELS_TO_METERS, enemy.getY() / PIXELS_TO_METERS, new Texture(Gdx.files.internal("cannonball.png")), world, 3, 0, 0, false));
				enemy.body.setGravityScale(0f);
				Filter filter = enemy.body.getFixtureList().first().getFilterData();
				filter.categoryBits = PhysicBody.PROJECTILE;
				filter.maskBits = PhysicBody.WORLD;
				enemy.body.getFixtureList().first().setFilterData(filter);
				enemy.body.setLinearVelocity(new Vector2(-200f, 0f));
				
			}
		}
		
		camera.position.interpolate(new Vector3((pullCharacter.getX() + stopCharacter.getX())*0.5f, (pullCharacter.getY() + stopCharacter.getY())*0.5f, 0f), 0.2f, Interpolation.linear);
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
		
		if (controller.getAxis(XBox360Pad.AXIS_LEFT_Y) > 0.95f) {
			if(pullCharacter.isColliding(stopCharacter))
				stopCharacter.grabbedBy(pullCharacter);
		}

		if (controller.getAxis(XBox360Pad.AXIS_LEFT_TRIGGER) == 1.f) {
			pullProjectilesTo(pullCharacter.getPosition());
		}

		if (controller.getButton(XBox360Pad.BUTTON_LB)) {
			if(stopCharacter.isGrabbed()){
				stopCharacter.free();
			}
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
			timeStopped = true;
		} else {
			unFreezeProjectiles();
			timeStopped = false;
		}

		if (controller.getButton(XBox360Pad.BUTTON_RB)) {
			stopCharacter.jump();
		}
		
		/*Other inputs*/
		
		if (controller.getButton(XBox360Pad.BUTTON_START)){
			restart();
		}
	}

	private void restart() {
		stage = new Stage();
		world = new World(gravity, true);
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
		
		stage.getViewport().setCamera(camera);
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
					((Projectile) actor).pullTo(p, 0.25f);
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
