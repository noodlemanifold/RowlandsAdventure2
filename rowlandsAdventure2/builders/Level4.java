package rowlandsAdventure2.builders;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.jme3.bullet.collision.shapes.SphereCollisionShape;

import rowlandsAdventure2.planets.PlanetManager;
import rowlandsAdventure2.planets.SpherePlanet;
import tage.Engine;
import tage.TextureImage;
import tage.Utils;
import tage.shapes.AnimatedShape;
import tage.shapes.ImportedModel;

//asteroids
public class Level4 extends LevelBuilder{

    private Engine engine;

    private TextureImage planetHeighttx;
    private TextureImage planetSurfacetx;
    private TextureImage planetColorstx;
    private AnimatedShape penguinS1;

    public Level4(Engine e) {
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
        planetHeighttx = new TextureImage("heightMap8.png");
        planetSurfacetx = new TextureImage("surfaceMap8.png", true);
        planetColorstx = new TextureImage("surfaceColors8.png", true);
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
        Vector3f planetLocation = new Vector3f(-0.11857773f,-0.61779732f,-0.77734536f);
        planetLocation.mul(72f);
        planetLocation.add(PlanetManager.getFromIndex(6).getLocation());
        Quaternionf planetRotation = new Quaternionf();
        planetRotation.rotateXYZ(0f, 0f, 0f);
        Vector3f planetScale = new Vector3f(3.5f,3.5f,3.5f);
        SphereCollisionShape shape = new SphereCollisionShape(15f);
        SpherePlanet p1 = new SpherePlanet(engine, planetLocation, planetRotation, planetScale, planetHeighttx, planetSurfacetx, planetColorstx, shape,
                (ImportedModel) CommonResources.planetSphereMedS, 10f);
        p1.setLightDirection(new Vector3f(0.34146798f,-0.35979864f,-0.86829978f));
        p1.setGravStrength(12f);

        Vector3f planet2Location = new Vector3f(0.68565792f,-0.72291237f,0.08526947f);
        planet2Location.mul(13.5f);
        planet2Location.add(planetLocation);
        Quaternionf planet2Rotation = new Quaternionf();
        planet2Rotation.rotateXYZ(1f, 2f, 0f);
        Vector3f planet2Scale = new Vector3f(3.5f,3.5f,3.5f);
        SphereCollisionShape shape2 = new SphereCollisionShape(15f);
        SpherePlanet p2 = new SpherePlanet(engine, planet2Location, planet2Rotation, planet2Scale, planetHeighttx, planetSurfacetx, planetColorstx, shape2,
                (ImportedModel) CommonResources.planetSphereMedS, 10f);
        p2.setLightDirection(new Vector3f(0.34146798f,-0.35979864f,-0.86829978f));
        p2.setGravStrength(12f);

        Vector3f planet3Location = new Vector3f(0.17217089f,-0.89018434f,0.42181629f);;
        planet3Location.mul(13.5f);
        planet3Location.add(planet2Location);
        Quaternionf planet3Rotation = new Quaternionf();
        planet3Rotation.rotateXYZ(-1f, 2f, 0f);
        Vector3f planet3Scale = new Vector3f(3.5f,3.5f,3.5f);
        SphereCollisionShape shape3 = new SphereCollisionShape(15f);
        SpherePlanet p3 = new SpherePlanet(engine, planet3Location, planet3Rotation, planet3Scale, planetHeighttx, planetSurfacetx, planetColorstx, shape3,
                (ImportedModel) CommonResources.planetSphereMedS, 10f);
        p3.setLightDirection(new Vector3f(0.34146798f,-0.35979864f,-0.86829978f));
        p3.setGravStrength(12f);

        Vector3f planet4Location = new Vector3f(-0.29118875f,-0.72301364f,-0.62646657f);
        planet4Location.mul(19f);
        planet4Location.add(planet3Location);
        Quaternionf planet4Rotation = new Quaternionf();
        planet4Rotation.rotateXYZ(-1f, -2f, 0f);
        Vector3f planet4Scale = new Vector3f(3.5f,3.5f,3.5f);
        SphereCollisionShape shape4 = new SphereCollisionShape(15f);
        SpherePlanet p4 = new SpherePlanet(engine, planet4Location, planet4Rotation, planet4Scale, planetHeighttx, planetSurfacetx, planetColorstx, shape4,
                (ImportedModel) CommonResources.planetSphereMedS, 10f);
        p4.setLightDirection(new Vector3f(0.34146798f,-0.35979864f,-0.86829978f));
        p4.setGravStrength(12f);

        Vector3f bump1pos = new Vector3f(47.47127914f,39.99760056f,-42.27896500f);
        Vector3f bump1up =  new Vector3f(-0.99525660f,-0.04894102f,-0.08407713f);
        Vector3f bump1fo = new Vector3f(-0.00081927f,-0.85999650f,0.51029927f);
        Utils.createBumper(engine, p4, bump1pos, bump1up, bump1fo, 80f, true);

        // DriverState[] driverStates = new DriverState[] {
        //         new DriverState()
        // };
        // driverStates[0].driverPosDir = new Vector3f(-0.11481366f,-0.96617651f,-0.23091312f);
        // driverStates[0].driverPosRadius = 25f;
        // driverStates[0].driverRadius = 3f;
        // driverStates[0].stateAdvance = StateAdvanceType.NONE;
        // driverStates[0].lockCharacter = false;

        // NpcPathStanding standing1 = new NpcPathStanding(new Vector3f(-0.11481366f,-0.96617651f,-0.23091312f), 25f, new Vector3f(-0.50484508f,0.25694519f,-0.82408172f), 1f);
        // Npc[] npcs = new Npc[] {
        //         new Npc(penguinS1, CommonResources.penguintx,
        //                 CommonResources.characterAnimatedShaderIndex, new NpcState[] {
        //                         new NpcState("HAPPY", 1f, popup1, CommonResources.penguinExpHappy, standing1)
        //                 }, 1f, 5f)
        // };
        // driver1 = new NpcEventDriver(driverStates, npcs, p1, CommonResources.characterController);

        

        // Vector3f planet2Location = new Vector3f(0.59664202f,0.58144522f,-0.55311823f);
        // planet2Location.mul(160f);
        // planet2Location.add(planetLocation);
        // Matrix4f planet2Rot = Utils.matLookTowards(bump5fo, new Vector3f(-0.59664202f,-0.58144522f,0.55311823f));
        // Quaternionf planet2Rotation = new Quaternionf();
        // planet2Rot.getNormalizedRotation(planet2Rotation);
        // Vector3f planet2Scale = new Vector3f(8f,8f,8f);
        // SphereCollisionShape shape2 = new SphereCollisionShape(30f);
        // CubePlanet p2 = new CubePlanet(engine, planet2Location, planet2Rotation, planet2Scale, planet2Heighttx, planet2Surfacetx, planet2Colorstx, shape2,
        //         (ImportedModel) CommonResources.planetCubeS, 10f, 6f);
        // p2.setLightDirection(new Vector3f(0.77916622f,0.62657613f,0.01739358f));

        
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
        // driver1.update(CommonResources.characterController.getLocation(),
        //         CommonResources.characterController.getCameraLocation());
    }
    
}
