package rowlandsAdventure2.builders;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.jme3.bullet.collision.shapes.SphereCollisionShape;

import rowlandsAdventure2.Enums.CollisionGroups;
import rowlandsAdventure2.character.CharacterSurface;
import rowlandsAdventure2.npcs.DriverState;
import rowlandsAdventure2.npcs.DriverState.StateAdvanceType;
import rowlandsAdventure2.npcs.Npc;
import rowlandsAdventure2.npcs.NpcEventDriver;
import rowlandsAdventure2.npcs.NpcPathStanding;
import rowlandsAdventure2.npcs.NpcState;
import rowlandsAdventure2.planets.CubePlanet;
import rowlandsAdventure2.planets.PlanetManager;
import rowlandsAdventure2.planets.SpherePlanet;
import tage.Engine;
import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;
import tage.Utils;
import tage.JmeBullet.JmeBulletPhysicsObject;
import tage.shapes.AnimatedShape;
import tage.shapes.ImportedModel;

//double jump planet, small box planet, and box  parkour planet
public class Level3 extends LevelBuilder{

    private Engine engine;

    private TextureImage planetHeighttx;
    private TextureImage planetSurfacetx;
    private TextureImage planetColorstx;
    private TextureImage planet2Heighttx;
    private TextureImage planet2Surfacetx;
    private TextureImage planet2Colorstx;
    private TextureImage planet3Heighttx;
    private TextureImage planet3Surfacetx;
    private TextureImage planet3Colorstx;
    private TextureImage popup1;
    private AnimatedShape penguinS1;
    private NpcEventDriver driver1;

    public Level3(Engine e) {
        engine = e;
    }

    @Override
    public void awake() {

    }

    @Override
    public void loadShapes() {
        penguinS1 = new AnimatedShape("penguin.rkm", "penguin.rks", 60f);
        penguinS1.loadAnimation("HAPPY", "penguinHappy.rka");
    }

    @Override
    public void loadTextures() {
        planetHeighttx = new TextureImage("heightMap5.png");
        planetSurfacetx = new TextureImage("surfaceMap5.png", true);
        planetColorstx = new TextureImage("surfaceColors5.png", true);
        planet2Heighttx = new TextureImage("heightMap6.png");
        planet2Surfacetx = new TextureImage("surfaceMap6.png", true);
        planet2Colorstx = new TextureImage("surfaceColors6.png", true);
        planet3Heighttx = new TextureImage("heightMap7.png");
        planet3Surfacetx = new TextureImage("surfaceMap7.png", true);
        planet3Colorstx = new TextureImage("surfaceColors7.png", true);
        popup1 = new TextureImage("popup4.png");
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
        Vector3f planetLocation = new Vector3f(0.49121681f,0.75203866f,0.43948156f);
        planetLocation.mul(120f);
        planetLocation.add(PlanetManager.getFromIndex(3).getLocation());
        Quaternionf planetRotation = new Quaternionf();
        planetRotation.rotateXYZ(0f, 0f, 0f);
        Vector3f planetScale = new Vector3f(25f,25f,25f);
        SphereCollisionShape shape = new SphereCollisionShape(30f);
        SpherePlanet p1 = new SpherePlanet(engine, planetLocation, planetRotation, planetScale, planetHeighttx, planetSurfacetx, planetColorstx, shape,
                (ImportedModel) CommonResources.planetSphereS, 10f);
        p1.setLightDirection(new Vector3f(0.34146798f,-0.35979864f,-0.86829978f));

        Vector3f bump1pos = new Vector3f(-101.40359497f,-117.25804901f,7.66070127f);
        Vector3f bump1up = new Vector3f(-0.34872177f,0.29790038f,0.88862175f);
        Vector3f bump1fo = new Vector3f(-0.75880319f,-0.64624733f,-0.08112997f);
        Utils.createBumper(engine, p1, bump1pos, bump1up, bump1fo, 50f, true);

        DriverState[] driverStates = new DriverState[] {
                new DriverState()
        };
        driverStates[0].driverPosDir = new Vector3f(-0.11481366f,-0.96617651f,-0.23091312f);
        driverStates[0].driverPosRadius = 25f;
        driverStates[0].driverRadius = 3f;
        driverStates[0].stateAdvance = StateAdvanceType.NONE;
        driverStates[0].lockCharacter = false;

        NpcPathStanding standing1 = new NpcPathStanding(new Vector3f(-0.11481366f,-0.96617651f,-0.23091312f), 25f, new Vector3f(-0.50484508f,0.25694519f,-0.82408172f), 1f);
        Npc[] npcs = new Npc[] {
                new Npc(penguinS1, CommonResources.penguintx,
                        CommonResources.characterAnimatedShaderIndex, new NpcState[] {
                                new NpcState("HAPPY", 1f, popup1, CommonResources.penguinExpHappy, standing1)
                        }, 1f, 5f)
        };
        driver1 = new NpcEventDriver(driverStates, npcs, p1, CommonResources.characterController);

        Vector3f bump2pos = new Vector3f(-81.58760071f,11.26002598f,107.67927551f);
        Vector3f bump2up = new Vector3f(-0.28662336f,0.87834787f,-0.38255975f);
        Vector3f bump2fo = new Vector3f(-0.07670554f,0.88812917f,0.45314786f);
        Utils.createBumper(engine, p1, bump2pos, bump2up, bump2fo, 30f, true);

        Vector3f bump3pos = new Vector3f(-72.30606079f,29.08837891f,146.73136902f);
        Vector3f bump3up = new Vector3f(0.49941370f,0.59192175f,0.63262510f);
        Vector3f bump3fo = new Vector3f(0.79614055f,-0.19963589f,0.57123190f);
        Utils.createBumper(engine, p1, bump3pos, bump3up, bump3fo, 25f, true);

        Vector3f bump4pos = new Vector3f(-46.50187683f,4.61357021f,152.83625793f);
        Vector3f bump4up = new Vector3f(0.97325379f,0.22961132f,-0.00747245f);
        Vector3f bump4fo = new Vector3f(0.55002660f,0.10539655f,-0.82846987f);
        Utils.createBumper(engine, p1, bump4pos, bump4up, bump4fo, 45f, true);
        
        Vector3f bump5pos = new Vector3f(-50.90576172f,18.43626404f,115.81856537f);
        Vector3f bump5up = new Vector3f(0.59664202f,0.58144522f,-0.55311823f);
        Vector3f bump5fo = new Vector3f(-0.63810343f,-0.07424580f,-0.76636273f);
        Utils.createBumper(engine, p1, bump5pos, bump5up, bump5fo, 60f, true);

        Vector3f planet2Location = new Vector3f(0.59664202f,0.58144522f,-0.55311823f);
        planet2Location.mul(160f);
        planet2Location.add(planetLocation);
        Matrix4f planet2Rot = Utils.matLookTowards(bump5fo, new Vector3f(-0.59664202f,-0.58144522f,0.55311823f));
        Quaternionf planet2Rotation = new Quaternionf();
        planet2Rot.getNormalizedRotation(planet2Rotation);
        Vector3f planet2Scale = new Vector3f(8f,8f,8f);
        SphereCollisionShape shape2 = new SphereCollisionShape(30f);
        CubePlanet p2 = new CubePlanet(engine, planet2Location, planet2Rotation, planet2Scale, planet2Heighttx, planet2Surfacetx, planet2Colorstx, shape2,
                (ImportedModel) CommonResources.planetCubeS, 10f, 6f);
        p2.setLightDirection(new Vector3f(0.77916622f,0.62657613f,0.01739358f));

        Vector3f bump6pos = new Vector3f(29.60216141f,97.30263519f,41.35319901f);
        Vector3f bump6up = new Vector3f(0.59664190f,0.58144516f,-0.55311835f);
        Vector3f bump6fo = new Vector3f(-0.48640010f,-0.28620142f,-0.82553238f);
        Utils.createBumper(engine, p2, bump6pos, bump6up, bump6fo, 25f, true);

        Vector3f planet3Location = new Vector3f(0.59664202f,0.58144522f,-0.55311823f);
        planet3Location.mul(50f);
        planet3Location.add(planet2Location);
        Matrix4f planet3Rot = Utils.matLookTowards(new Vector3f(0.79369867f,-0.52938437f,0.29965729f), new Vector3f(-0.59664202f,-0.58144522f,0.55311823f));
        Quaternionf planet3Rotation = new Quaternionf();
        planet3Rot.getNormalizedRotation(planet3Rotation);
        Vector3f planet3Scale = new Vector3f(16f,16f,16f);
        SphereCollisionShape shape3 = new SphereCollisionShape(30f);
        CubePlanet p3 = new CubePlanet(engine, planet3Location, planet3Rotation, planet3Scale, planet3Heighttx, planet3Surfacetx, planet3Colorstx, shape3,
                (ImportedModel) CommonResources.planetCubeS, 10f, 8f);
        p3.setLightDirection(new Vector3f(0.21821503f,-0.94983393f,0.22404820f));

        Vector3f bump7pos = new Vector3f(49.68838882f,105.04946136f,0.81277907f);
        Vector3f bump7up = new Vector3f(-0.11857773f,-0.61779732f,-0.77734536f);
        Vector3f bump7fo = new Vector3f(-0.57048863f,-0.59837013f,0.56257969f);
        Utils.createBumper(engine, p3, bump7pos, bump7up, bump7fo, 35f, true);
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
        driver1.update(CommonResources.characterController.getLocation(),
                CommonResources.characterController.getCameraLocation());
    }
    
}
