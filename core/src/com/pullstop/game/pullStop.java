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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.MoveByAction;
import com.badlogic.gdx.utils.Array;

public class pullStop extends ApplicationAdapter implements InputProcessor{
	/*
	 * private OrthographicCamera camera; SpriteBatch batch; Texture texture;
	 * private BitmapFont font; private Sprite sprite;
	 * 
	 * @Override public void create () { camera = new OrthographicCamera(1280,
	 * 720); float w = Gdx.graphics.getWidth(); float h =
	 * Gdx.graphics.getHeight(); batch = new SpriteBatch();
	 * 
	 * texture = new Texture(Gdx.files.internal("badlogic.jpg")); sprite = new
	 * Sprite(texture); sprite.setPosition(w/2 -sprite.getWidth()/2, h/2 -
	 * sprite.getHeight()/2);
	 * 
	 * font = new BitmapFont(); font.setColor(Color.GREEN); }
	 * 
	 * @Override public void dispose() { batch.dispose(); font.dispose(); }
	 * 
	 * @Override public void render () { Gdx.gl.glClearColor(1, 0, 0, 1);
	 * Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	 * 
	 * batch.setProjectionMatrix(camera.combined);
	 * 
	 * if(Gdx.input.isKeyPressed(Input.Keys.LEFT)){
	 * if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
	 * sprite.translateX(-1f); else sprite.translateX(-10.0f); }
	 * if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
	 * if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
	 * sprite.translateX(1f); else sprite.translateX(10.0f); }
	 * if(Gdx.input.isKeyPressed(Input.Keys.Q)){
	 * if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
	 * camera.translate(-1f,0); else camera.translate(-10.0f,0);
	 * camera.update(); } if(Gdx.input.isKeyPressed(Input.Keys.D)){
	 * if(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
	 * camera.translate(1f,0); else camera.translate(10.0f,0); }
	 * camera.position.set(sprite.getX(), sprite.getY(), 0); camera.update();
	 * batch.begin(); sprite.draw(batch); font.draw(batch, "Hello World",
	 * Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2); batch.end(); }
	 * 
	 * @Override public void resize(int width, int height) { }
	 * 
	 * @Override public void pause() { }
	 * 
	 * @Override public void resume() { }
	 */

	/*private Stage stage;
	private Group group;

	@Override
	public void create() {
		stage = new Stage(new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		Gdx.input.setInputProcessor(stage);

		MyActor myActor = new MyActor();
		myActor.setTouchable(Touchable.enabled);

		MoveToAction moveAction = new MoveToAction();
		moveAction.setPosition(300f, 0f);
		moveAction.setDuration(10f);
		myActor.addAction(moveAction);

		stage.addActor(myActor);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	 */

	Texture img;
	TiledMap tiledMap;
	OrthographicCamera camera;
	TiledMapRenderer tiledMapRenderer;
	SpriteBatch sb;
	Texture texture;
	Sprite sprite;
	World world;
	Body body;
	Body bodyPlatform;
	Box2DDebugRenderer debugRenderer;
	Matrix4 debugMatrix;

	Character character1;
	MoveByAction moveAction = new MoveByAction();

	private Stage stage;

	float torque = 0.0f;
	boolean drawSprite = true;

	final float PIXELS_TO_METERS = 100f;

	@Override
	public void create () {
		stage = new Stage(/*new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight())*/);

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();

		character1 = new Character(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2, new Texture(Gdx.files.internal("pika.png")));
		character1.setTouchable(Touchable.enabled);

		System.out.println(w + h);

		world = new World(new Vector2(0, 0), true);

		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;

		bodyDef.position.set((character1.getX() - character1.getWidth()/2) / PIXELS_TO_METERS, 
				(character1.getY() - character1.getHeight()/2) / PIXELS_TO_METERS);

		body = world.createBody(bodyDef);

		PolygonShape shape = new PolygonShape();

		shape.setAsBox(character1.getWidth()/2 / PIXELS_TO_METERS, character1.getHeight()/2 / PIXELS_TO_METERS);
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.density = 0.1f;
		fixtureDef.restitution = 0f;

		body.createFixture(fixtureDef);

		shape.dispose();
		
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


		//sb = new SpriteBatch();
		//sprite = new Sprite(texture);
	}
	
	private float elapsed = 0;
	
	@Override
	public void render () {
		camera.position.set(new Vector3(character1.getX(), character1.getY(), 0f));
		camera.update();
		
		world.step(1f/60f, 6, 2);
		
		body.applyTorque(torque,true);
		
		character1.setPosition((body.getPosition().x * PIXELS_TO_METERS) - character1.getWidth()/2, (body.getPosition().y * PIXELS_TO_METERS) - character1.getHeight()/2);
		
		character1.setRotation((float)Math.toDegrees(body.getAngle()));
		
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		camera.update();
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();
		
		debugMatrix = camera.combined.cpy().scale(PIXELS_TO_METERS, 
                PIXELS_TO_METERS, 0);
		
		/*sb.begin();
        sprite.draw(sb);
        sb.end();*/
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
			System.out.println("Left");
			body.setLinearVelocity(-1f,0f);
		}
		if(keycode == Input.Keys.RIGHT)
			body.setLinearVelocity(1f,0f);
		if(keycode == Input.Keys.UP)
			body.applyForceToCenter(0f,10f,true);
		if(keycode == Input.Keys.DOWN)
			body.applyForceToCenter(0f,-10f,true);
		
		if(keycode == Input.Keys.Q)
            torque += 0.1f;
        if(keycode == Input.Keys.D)
            torque -= 0.1f;
        
        if(keycode == Input.Keys.S)
            torque = 0.0f;
        
        if(keycode == Input.Keys.SPACE) {
            body.setLinearVelocity(0f, 0f);
            body.setAngularVelocity(0f);
            torque = 0f;
            character1.setPosition(0f,0f);
            body.setTransform(0f,0f,0f);
        }
		
        if(keycode == Input.Keys.ESCAPE)
            drawSprite = !drawSprite;
        
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Input.Keys.LEFT){
			System.out.println("Left");
			body.setLinearVelocity(0f,0f);
		}
		if(keycode == Input.Keys.RIGHT)
			body.setLinearVelocity(0f,0f);
		return true;
	}

	@Override
	public boolean keyTyped(char character) {

		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		System.out.println("X"+screenX+" Y"+screenY);
		body.applyForce(1f,1f,screenX,screenY,true);
        //body.applyTorque(0.4f,true);
        return true;
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
