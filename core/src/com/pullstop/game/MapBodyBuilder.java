package com.pullstop.game;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class MapBodyBuilder {
	final static float PIXELS_TO_METERS = 100f;

    public static Array<Body> buildShapes(TiledMap map, float pixels, World world, Stage stage) {
        System.out.println(map);
        
        TextureMapObject mapObject;
        
        MapObjects objects = map.getLayers().get("Obstacles").getObjects();

        Array<Body> bodies = new Array<Body>();
        
        MapObjects characters = map.getLayers().get("Character").getObjects();
        
        for(MapObject object : characters) {
        	if (object instanceof TextureMapObject) {
        		mapObject = (TextureMapObject) object;
        		System.out.println(mapObject.getName());
        		Character character = new Character(mapObject.getName(), mapObject.getX() / PIXELS_TO_METERS, mapObject.getY() / PIXELS_TO_METERS, mapObject.getTextureRegion().getTexture(), world);
        		character.setTouchable(Touchable.enabled);
        		bodies.add(character.body);
        		stage.addActor(character);
            }
        }
        
        MapObjects projectiles = map.getLayers().get("Projectiles").getObjects();
        
        for(MapObject object : projectiles) {
        	if (object instanceof TextureMapObject) {
            	System.out.println("projectile generated");
        		mapObject = (TextureMapObject) object;
        		Projectile projectile;
        		if(mapObject.getProperties().containsKey("fixedRotation")){
        			projectile = new Projectile(mapObject.getX() / PIXELS_TO_METERS, mapObject.getY() / PIXELS_TO_METERS, mapObject.getTextureRegion().getTexture(), world, 0.1f, 0.5f, 0.5f, true);
        		}
        		else{
        			projectile = new Projectile(mapObject.getX() / PIXELS_TO_METERS, mapObject.getY() / PIXELS_TO_METERS, mapObject.getTextureRegion().getTexture(), world, 0.1f, 0.5f, 0.5f, false);
        		}
        		
        		projectile.setTouchable(Touchable.enabled);
        		bodies.add(projectile.body);
        		stage.addActor(projectile);
            }
        }
        
        MapObjects ennemies = map.getLayers().get("Cannon").getObjects();
        
        for(MapObject object : ennemies) {
        	if (object instanceof TextureMapObject) {
            	System.out.println("ennemy generated");
        		mapObject = (TextureMapObject) object;
        		if(mapObject.getTextureRegion().isFlipX())
        			mapObject.getTextureRegion().flip(true, false);
        		Enemy enemy = new Enemy(mapObject.getX() / PIXELS_TO_METERS, mapObject.getY() / PIXELS_TO_METERS, mapObject.getTextureRegion().getTexture(), world);
        		enemy.setTouchable(Touchable.enabled);
        		bodies.add(enemy.body);
        		stage.addActor(enemy);
            }
        }
        
        mapObject = null;

        for(MapObject object : objects) {
        	System.out.println("obstacle generated");
            if (object instanceof TextureMapObject) {
                continue;
            }

            Shape shape;

            if (object instanceof RectangleMapObject) {
                shape = getRectangle((RectangleMapObject)object);
            }
            else if (object instanceof PolygonMapObject) {
                shape = getPolygon((PolygonMapObject)object);
            }
            else if (object instanceof PolylineMapObject) {
                shape = getPolyline((PolylineMapObject)object);
            }
            else if (object instanceof CircleMapObject) {
                shape = getCircle((CircleMapObject)object);
            }
            else {
                continue;
            }

            BodyDef bd = new BodyDef();
            bd.type = BodyType.StaticBody;
            
            Body body = world.createBody(bd);
            body.createFixture(shape, 1);

            bodies.add(body);

            shape.dispose();
        }
        return bodies;
    }

    private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle = rectangleObject.getRectangle();
        PolygonShape polygon = new PolygonShape();
        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) / PIXELS_TO_METERS,
                                   (rectangle.y + rectangle.height * 0.5f ) / PIXELS_TO_METERS);
        polygon.setAsBox(rectangle.width * 0.5f / PIXELS_TO_METERS,
                         rectangle.height * 0.5f / PIXELS_TO_METERS,
                         size,
                         0.0f);
        return polygon;
    }

    private static CircleShape getCircle(CircleMapObject circleObject) {
        Circle circle = circleObject.getCircle();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circle.radius / PIXELS_TO_METERS);
        circleShape.setPosition(new Vector2(circle.x / PIXELS_TO_METERS, circle.y / PIXELS_TO_METERS));
        return circleShape;
    }

    private static PolygonShape getPolygon(PolygonMapObject polygonObject) {
        PolygonShape polygon = new PolygonShape();
        float[] vertices = polygonObject.getPolygon().getTransformedVertices();

        float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i) {
            System.out.println(vertices[i]);
            worldVertices[i] = vertices[i] / PIXELS_TO_METERS;
        }

        polygon.set(worldVertices);
        return polygon;
    }

    private static ChainShape getPolyline(PolylineMapObject polylineObject) {
        float[] vertices = polylineObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] / PIXELS_TO_METERS;
            worldVertices[i].y = vertices[i * 2 + 1] / PIXELS_TO_METERS;
        }

        ChainShape chain = new ChainShape(); 
        chain.createChain(worldVertices);
        return chain;
    }
}
