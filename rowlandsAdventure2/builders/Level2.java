package rowlandsAdventure2.builders;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.jme3.bullet.collision.shapes.SphereCollisionShape;

import rowlandsAdventure2.Enums.CollisionGroups;
import rowlandsAdventure2.character.CharacterSurface;
import rowlandsAdventure2.planets.PlanetManager;
import rowlandsAdventure2.planets.SpherePlanet;
import tage.Engine;
import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;
import tage.Utils;
import tage.JmeBullet.JmeBulletPhysicsObject;
import tage.shapes.ImportedModel;

//small ice planet and box ice planet
public class Level2 extends LevelBuilder{

    private Engine engine;

    private TextureImage planetHeighttx;
    private TextureImage planetSurfacetx;
    private TextureImage planetColorstx;
    private TextureImage planet2Heighttx;
    private TextureImage planet2Surfacetx;
    private TextureImage planet2Colorstx;
    private TextureImage boxTex;
    private ObjShape boxS;

    public Level2(Engine e) {
        engine = e;
    }

    @Override
    public void awake() {

    }

    @Override
    public void loadShapes() {
        boxS = new ImportedModel("box.obj");
    }

    @Override
    public void loadTextures() {
        planetHeighttx = new TextureImage("heightMap3.png");
        planetSurfacetx = new TextureImage("surfaceMap3.png", true);
        planetColorstx = new TextureImage("surfaceColors3.png", true);
        planet2Heighttx = new TextureImage("heightMap4.png");
        planet2Surfacetx = new TextureImage("surfaceMap4.png", true);
        planet2Colorstx = new TextureImage("surfaceColors4.png", true);
        boxTex = new TextureImage("box.png");
    }

    @Override
    public void loadSounds() {

    }

    @Override
    public void buildCustomRenderPrograms() {

    }

    @Override
    public void buildObjects() {

    }

    @Override
    public void buildPhysicsObjects() {
        Vector3f planetLocation = new Vector3f(-0.77916622f,-0.62657613f,-0.01739358f);
        planetLocation.mul(70f);
        planetLocation.add(PlanetManager.getFromIndex(1).getLocation());
        Quaternionf planetRotation = new Quaternionf();
        planetRotation.rotateXYZ(0f, 0f, 0f);
        Vector3f planetScale = new Vector3f(5f,5f,5f);
        SphereCollisionShape shape = new SphereCollisionShape(40f);
        SpherePlanet p1 = new SpherePlanet(engine, planetLocation, planetRotation, planetScale, planetHeighttx, planetSurfacetx, planetColorstx, shape,
                (ImportedModel) CommonResources.planetSphereS, 10f);
        p1.setLightDirection(new Vector3f(0.77916622f,0.62657613f,0.01739358f));

        Vector3f bump1pos = new Vector3f(-101.40359497f,-117.25804901f,7.66070127f);
        Vector3f bump1up = new Vector3f(-0.34872177f,0.29790038f,0.88862175f);
        Vector3f bump1fo = new Vector3f(-0.75880319f,-0.64624733f,-0.08112997f);
        Utils.createBumper(engine, p1, bump1pos, bump1up, bump1fo, 50f, true);

        Vector3f planet2Location = new Vector3f(-0.34872177f,0.29790038f,0.88862175f);
        planet2Location.mul(90f);
        planet2Location.add(planetLocation);
        Quaternionf planet2Rotation = new Quaternionf();
        planet2Rotation.rotateXYZ(0f, 0f, 0f);
        Vector3f planet2Scale = new Vector3f(15f,15f,15f);
        SphereCollisionShape shape2 = new SphereCollisionShape(30f);
        SpherePlanet p2 = new SpherePlanet(engine, planet2Location, planet2Rotation, planet2Scale, planet2Heighttx, planet2Surfacetx, planet2Colorstx, shape2,
                (ImportedModel) CommonResources.planetSphereS, 10f);
        p2.setLightDirection(new Vector3f(0.77916622f,0.62657613f,0.01739358f));

        Vector3f boxPos = new Vector3f(-123.10037994f,-100.76918793f,67.70124054f);
        Matrix4f boxTransform = new Matrix4f().identity();
        boxTransform.translate(boxPos);
        // boxTransform.rotateX((float)Math.toRadians(45f));
        GameObject box = new GameObject(GameObject.root(), boxS, boxTex);
        Matrix4f initialTranslation = (new Matrix4f()).translation(boxPos);
        Matrix4f initialScale = (new Matrix4f()).scaling(1.7f);
        box.setLocalTranslation(initialTranslation);
        box.setLocalScale(initialScale);
        JmeBulletPhysicsObject boxPhys = engine.getSceneGraph().addPhysicsBox(1.5f, boxTransform.get(new float[16]), new float[] { 1.7f, 1.7f, 1.7f },
                CollisionGroups.Prop.value(), CollisionGroups.All.value());
        boxPhys.setSurface(CharacterSurface.GroundSurface);
        boxPhys.setGameObject(box);
        box.setPhysicsObject(boxPhys);

        Vector3f boxPos2 = new Vector3f(-145.90225220f,-93.38951874f,97.37502289f);
        Matrix4f boxTransform2 = new Matrix4f().identity();
        boxTransform2.translate(boxPos2);
        GameObject box2 = new GameObject(GameObject.root(), boxS, boxTex);
        Matrix4f initialTranslation2 = (new Matrix4f()).translation(boxPos2);
        Matrix4f initialScale2 = (new Matrix4f()).scaling(1.7f);
        box2.setLocalTranslation(initialTranslation2);
        box2.setLocalScale(initialScale2);
        JmeBulletPhysicsObject boxPhys2 = engine.getSceneGraph().addPhysicsBox(1.5f, boxTransform2.get(new float[16]), new float[] { 1.7f, 1.7f, 1.7f },
                CollisionGroups.Prop.value(), CollisionGroups.All.value());
        boxPhys2.setSurface(CharacterSurface.GroundSurface);
        boxPhys2.setGameObject(box2);
        box2.setPhysicsObject(boxPhys2);

        Vector3f bump2pos = new Vector3f(-119.72992706f,-75.29859161f,92.32806396f);
        Vector3f bump2up = new Vector3f(0.49121681f,0.75203866f,0.43948156f);
        Vector3f bump2fo = new Vector3f(0.84060210f,-0.54149848f,-0.01294730f);
        Utils.createBumper(engine, p2, bump2pos, bump2up, bump2fo, 50f, true);
    }

    @Override
    public void buildLights() {

    }

    @Override
    public void initialize() {

    }

    @Override
    public void physicsUpdate() {

    }

    @Override
    public void visualUpdate() {

    }
    
}
