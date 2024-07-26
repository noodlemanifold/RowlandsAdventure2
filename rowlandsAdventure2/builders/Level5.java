package rowlandsAdventure2.builders;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.jme3.bullet.collision.shapes.SphereCollisionShape;

import rowlandsAdventure2.ManualFridge;
import rowlandsAdventure2.npcs.Billboard;
import rowlandsAdventure2.npcs.DriverState;
import rowlandsAdventure2.npcs.DriverState.StateAdvanceType;
import rowlandsAdventure2.npcs.NpcEventDriverEndHack;
import rowlandsAdventure2.planets.PlanetManager;
import rowlandsAdventure2.planets.SpherePlanet;
import tage.Engine;
import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;
import tage.Utils;
import tage.shapes.AnimatedShape;
import tage.shapes.ImportedModel;

//booster planets
public class Level5 extends LevelBuilder{

    private Engine engine;

    private TextureImage planetHeighttx;
    private TextureImage planetSurfacetx;
    private TextureImage planetColorstx;
    private TextureImage planet2Heighttx;
    private TextureImage planet2Surfacetx;
    private TextureImage planet2Colorstx;
    private TextureImage doltx;
    private TextureImage popup1;
    private AnimatedShape penguinS1;
    private TextureImage flowerTx;
    private TextureImage stoneTx;
    private ImportedModel flowers;
    private ImportedModel flower;
    private ImportedModel stone;
    private ImportedModel dolS;
    private TextureImage dialogue1;
    private NpcEventDriverEndHack driver1;
    private Billboard bill;
    private Vector3f billUp = new Vector3f();

    private ObjShape fridgeS;
    private TextureImage fridgetx;

    public Level5(Engine e) {
        engine = e;
    }

    @Override
    public void awake() {

    }

    @Override
    public void loadShapes() {
        penguinS1 = new AnimatedShape("penguin.rkm", "penguin.rks", 60f);
        penguinS1.loadAnimation("HAPPY", "penguinHappy.rka");

        flowers = new ImportedModel("flowers.obj");
        flower = new ImportedModel("rowland/RowlandFlower.obj");
        stone = new ImportedModel("stone.obj");

        dolS = new ImportedModel("dolphinHighPoly.obj");
        fridgeS = new ManualFridge();
    }

    @Override
    public void loadTextures() {
        planetHeighttx = new TextureImage("heightMap9.png");
        planetSurfacetx = new TextureImage("surfaceMap9.png", true);
        planetColorstx = new TextureImage("surfaceColors9.png", true);
        planet2Heighttx = new TextureImage("heightMap10.png");
        planet2Surfacetx = new TextureImage("surfaceMap10.png", true);
        planet2Colorstx = new TextureImage("surfaceColors10.png", true);
        flowerTx = new TextureImage("flowerTex.png");
        stoneTx = new TextureImage("stoneTex.png");
        popup1 = new TextureImage("popup5.png");
        dialogue1 = new TextureImage("dialogue10.png");
        doltx = new TextureImage("Dolphin_HighPolyUV.png");
        fridgetx = new TextureImage("fridgeTex.png");
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
        Vector3f planetLocation = new Vector3f(-0.99525660f,-0.04894102f,-0.08407713f);
        planetLocation.mul(300f);
        planetLocation.add(PlanetManager.getFromIndex(10).getLocation());
        Quaternionf planetRotation = new Quaternionf();
        planetRotation.rotateXYZ(0f, 0f, 0f);
        Vector3f planetScale = new Vector3f(9f,9f,9f);
        SphereCollisionShape shape = new SphereCollisionShape(90f);
        SpherePlanet p1 = new SpherePlanet(engine, planetLocation, planetRotation, planetScale, planetHeighttx, planetSurfacetx, planetColorstx, shape,
                (ImportedModel) CommonResources.planetSphereS, 10f);
        p1.setLightDirection(new Vector3f(0.34146798f,-0.35979864f,-0.86829978f));
        //p1.setGravStrength(12f);

        GameObject fs = new GameObject(GameObject.root(), flowers, flowerTx);
        fs.setLocalLocation(planetLocation);
        Vector3f fsPos = new Vector3f(-1f,0f,0f);
        fsPos.mul(11f);
        fsPos.add(planetLocation);
        float[] fslightDir = p1.getLightDir(new float[]{fsPos.x,fsPos.y,fsPos.z});
        fs.getRenderStates().setShadowDirection(new Vector4f(fslightDir[0],fslightDir[1],fslightDir[2],0f));
        float fspl = p1.getLight(new float[]{fsPos.x,fsPos.y,fsPos.z});
        fs.getRenderStates().setPlanetLight(fspl);
        float fssd = 2f;//p1.getShadowDistance();
        fs.getRenderStates().setShadowDistance(fssd);

        GameObject s = new GameObject(GameObject.root(), stone, stoneTx);
        Vector3f stoneLocation = new Vector3f(-257.38757324f,25.28616142f,-65.78925323f);
        Vector3f stoneUp = new Vector3f(-0.98953432f,-0.02042214f,0.14284541f);
        Vector3f stoneFor = new Vector3f(0.14197123f,0.03926273f,0.98909187f);
        s.setLocalLocation(stoneLocation);
        Matrix4f stoneRot = Utils.matLookTowards(stoneFor, stoneUp);
        s.setLocalRotation(stoneRot);
        float[] slightDir = p1.getLightDir(new float[]{stoneLocation.x,stoneLocation.y,stoneLocation.z});
        s.getRenderStates().setShadowDirection(new Vector4f(slightDir[0],slightDir[1],slightDir[2],0f));
        float spl = p1.getLight(new float[]{stoneLocation.x,stoneLocation.y,stoneLocation.z});
        s.getRenderStates().setPlanetLight(spl);
        float ssd = 2f;//p1.getShadowDistance();
        s.getRenderStates().setShadowDistance(ssd);

        GameObject f = new GameObject(GameObject.root(), flower, flowerTx);
        Vector3f flowerLocation = new Vector3f(-257.55996704f,25.56312561f,-67.45226288f);
        Vector3f flowerUp = new Vector3f(-0.99966395f,0.00747750f,-0.02482197f);
        Vector3f flowerFor = new Vector3f(0.02501720f,0.02725631f,-0.99931538f);
        f.setLocalLocation(flowerLocation);
        Matrix4f flowerRot = Utils.matLookTowards(flowerFor, flowerUp);
        f.setLocalRotation(flowerRot);
        float[] flightDir = p1.getLightDir(new float[]{flowerLocation.x,flowerLocation.y,flowerLocation.z});
        f.getRenderStates().setShadowDirection(new Vector4f(flightDir[0],flightDir[1],flightDir[2],0f));
        float fpl = p1.getLight(new float[]{flowerLocation.x,flowerLocation.y,flowerLocation.z});
        f.getRenderStates().setPlanetLight(fpl);
        float fsd = 2f;//p1.getShadowDistance();
        f.getRenderStates().setShadowDistance(fsd);
        f.getRenderStates().disableRendering();

        Vector3f bump1pos = new Vector3f(-254.75421143f,25.18264771f,-61.71021652f);
        Vector3f bump1up =  new Vector3f(-0.79332775f,-0.03383734f,0.60785371f);
        Vector3f bump1fo = new Vector3f(0.60824525f,-0.00163979f,0.79374748f);
        Utils.createBumper(engine, p1, bump1pos, bump1up, bump1fo, 25f, true);

        Vector3f planet2Location = new Vector3f(-0.79332775f,-0.03383734f,0.60785371f);
        planet2Location.mul(40f);
        planet2Location.add(planetLocation);
        Quaternionf planet2Rotation = new Quaternionf();
        planet2Rotation.rotateXYZ(0f, 0f, 0f);
        Vector3f planet2Scale = new Vector3f(9f,9f,9f);
        SphereCollisionShape shape2 = new SphereCollisionShape(30f);
        SpherePlanet p2 = new SpherePlanet(engine, planet2Location, planet2Rotation, planet2Scale, planet2Heighttx, planet2Surfacetx, planet2Colorstx, shape2,
                (ImportedModel) CommonResources.planetSphereS, 10f);
        p2.setLightDirection(new Vector3f(-0.44318089f,0.32693321f,0.83468872f));
        p2.setGravStrength(7f);

        Vector3f dolPos = new Vector3f(-287.36291504f,26.66853523f,-38.43739700f);
        Vector3f dolUp = new Vector3f(-0.84352612f,0.26559725f,0.46682087f);
        Vector3f dolFor = new Vector3f(0.46515954f,-0.07323688f,0.88219225f);
        GameObject dol = new GameObject(GameObject.root(),dolS,doltx);
        dol.setLocalLocation(dolPos);
        Matrix4f dolRot = Utils.matLookTowards(dolFor, dolUp);
        dol.setLocalRotation(dolRot);
        Matrix4f dolScale = new Matrix4f();
        dolScale.scale(2f);
        dol.setLocalScale(dolScale);
        float[] dlightDir = p2.getLightDir(new float[]{dolPos.x,dolPos.y,dolPos.z});
        dol.getRenderStates().setShadowDirection(new Vector4f(dlightDir[0],dlightDir[1],dlightDir[2],0f));
        float dpl = p2.getLight(new float[]{dolPos.x,dolPos.y,dolPos.z});
        dol.getRenderStates().setPlanetLight(dpl);
        float dsd = 4f;//p1.getShadowDistance();
        dol.getRenderStates().setShadowDistance(dsd);

        GameObject bgo = new GameObject(GameObject.root());
        bgo.setLocalLocation(dolPos);
        billUp.set(dolUp);
        bill = new Billboard(bgo, 1.2f, popup1, new Vector3f(1.8f,1f,1f).mul(0.5f));

        Vector3f fridgePos = new Vector3f(-286.30221558f,28.12553978f,-38.76873779f);
        Vector3f fridgeUp = new Vector3f(-0.77279788f,0.44145545f,0.45596126f);
        Vector3f fridgeFor = new Vector3f(-0.43289343f,0.15871193f,-0.88736349f);
        GameObject fridge = new GameObject(GameObject.root(), fridgeS, fridgetx);
        fridge.setLocalLocation(fridgePos);
        Matrix4f fridgeRot = Utils.matLookTowards(fridgeFor, fridgeUp);
        fridge.setLocalRotation(fridgeRot);
        Matrix4f fridgeScale = new Matrix4f();
        fridgeScale.scale(1f);
        fridge.setLocalScale(fridgeScale);
        float[] rlightDir = p2.getLightDir(new float[]{fridgePos.x,fridgePos.y,fridgePos.z});
        fridge.getRenderStates().setShadowDirection(new Vector4f(rlightDir[0],rlightDir[1],rlightDir[2],0f));
        float rpl = p2.getLight(new float[]{fridgePos.x,fridgePos.y,fridgePos.z});
        fridge.getRenderStates().setPlanetLight(rpl);
        float rsd = 4f;//p1.getShadowDistance();
        fridge.getRenderStates().setShadowDistance(rsd);
        

        DriverState[] driverStates = new DriverState[] {
                new DriverState(),
                new DriverState(),
                new DriverState()
        };
        driverStates[0].driverPosDir = new Vector3f(-0.99966395f,0.00747750f,-0.02482197f);
        driverStates[0].driverPosRadius = 10f;
        driverStates[0].driverRadius = 4f;
        driverStates[0].stateAdvance = StateAdvanceType.DISTANCE;
        driverStates[0].lockCharacter = false;
        driverStates[1].driverPosDir = new Vector3f(-0.99966395f,0.00747750f,-0.02482197f);
        driverStates[1].driverPosRadius = 10f;
        driverStates[1].driverRadius = 4f;
        driverStates[1].stateAdvance = StateAdvanceType.DIALOGUE;
        driverStates[1].dialogueImage = dialogue1;
        driverStates[1].lockCharacter = true;
        driverStates[2].driverPosDir = new Vector3f(-0.99966395f,0.00747750f,-0.02482197f);
        driverStates[2].driverPosRadius = 10f;
        driverStates[2].driverRadius = 4f;
        driverStates[2].stateAdvance = StateAdvanceType.NONE;
        driverStates[2].lockCharacter = false;

        driver1 = new NpcEventDriverEndHack(driverStates, null, p1, CommonResources.characterController, engine,f);
        driver1.disableHonks();

        
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

        bill.update(true, billUp, CommonResources.characterController.getCameraLocation());
    }
    
}
