package rowlandsAdventure2.builders;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.jme3.bullet.collision.shapes.SphereCollisionShape;

import rowlandsAdventure2.Enums.CollisionGroups;
import rowlandsAdventure2.ManualFridge;
import rowlandsAdventure2.character.BouncePadSurface;
import rowlandsAdventure2.character.CharacterSurface;
import rowlandsAdventure2.npcs.DriverState;
import rowlandsAdventure2.npcs.DriverState.StateAdvanceType;
import rowlandsAdventure2.npcs.Npc;
import rowlandsAdventure2.npcs.NpcEventDriver;
import rowlandsAdventure2.npcs.NpcPathElliptic;
import rowlandsAdventure2.npcs.NpcPathStanding;
import rowlandsAdventure2.npcs.NpcState;
import rowlandsAdventure2.planets.CubePlanet;
import rowlandsAdventure2.planets.SpherePlanet;
import tage.Engine;
import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;
import tage.JmeBullet.JmeBulletPhysicsObject;
import tage.shapes.Cube;
import tage.shapes.ImportedModel;
import tage.shapes.Line;
import tage.shapes.Plane;
import tage.shapes.Torus;

public class TestLevel extends LevelBuilder {

    private Engine engine;

    private GameObject dol;
    private GameObject cube;
    private GameObject xAxis;
    private GameObject yAxis;
    private GameObject zAxis;
    private GameObject fridge;
    private GameObject shuttle;
    private ObjShape dolS;
    private ObjShape cubeS;
    private ObjShape planeS;
    private ObjShape xAxisS;
    private ObjShape yAxisS;
    private ObjShape zAxisS;
    private ObjShape fridgeS;
    private ObjShape planetS;
    private ObjShape planet2S;
    private ObjShape shuttleS;
    private TextureImage doltx;
    private TextureImage cubetx;
    private TextureImage fridgetx;
    private TextureImage planetHeighttx;
    private TextureImage planetSurfacetx;
    private TextureImage planetMaptx;
    private TextureImage shuttletx;
    private TextureImage dialogueTest;
    private TextureImage popupTest;
    private JmeBulletPhysicsObject boxPhys;
    private NpcEventDriver testDriver;

    public TestLevel(Engine e) {
        engine = e;
    }

    @Override
    public void awake() {

    }

    @Override
    public void loadShapes() {
        dolS = new ImportedModel("dolphinHighPoly.obj");
        cubeS = new Cube();
        planeS = new Plane();
        planeS.setMatSpe(new float[] { 0.0f, 0.0f, 0.0f, 0.0f });
        xAxisS = new Line(new Vector3f(0, 0, 0), new Vector3f(2, 0, 0));
        yAxisS = new Line(new Vector3f(0, 0, 0), new Vector3f(0, 2, 0));
        zAxisS = new Line(new Vector3f(0, 0, 0), new Vector3f(0, 0, 2));
        fridgeS = new ManualFridge();
        planetS = new ImportedModel("planetHigh.obj");
        planet2S = new ImportedModel("planetCubeHigh.obj");
        shuttleS = new ImportedModel("shuttle.obj");
    }

    @Override
    public void loadTextures() {
        doltx = new TextureImage("Dolphin_HighPolyUV.png");
        cubetx = new TextureImage("cube.png");
        fridgetx = new TextureImage("fridgeTex.png");
        planetHeighttx = new TextureImage("testHeight.png");
        planetSurfacetx = new TextureImage("testMap.png", true);
        planetMaptx = new TextureImage("testRamp.png", true);
        shuttletx = new TextureImage("shuttle.png");
        popupTest = new TextureImage("popupTest.png");
        dialogueTest = new TextureImage("dialogueTest.png");
    }

    @Override
    public void loadSounds() {

    }

    @Override
    public void buildCustomRenderPrograms() {

    }

    @Override
    public void buildObjects() {
        dol = new GameObject(GameObject.root(), dolS, doltx);
        Matrix4f initialTranslation = (new Matrix4f()).translation(5, 0, 0);
        Matrix4f initialScale = (new Matrix4f()).scaling(3.0f);
        dol.setLocalTranslation(initialTranslation);
        dol.setLocalScale(initialScale);

        cube = new GameObject(GameObject.root(), cubeS, cubetx);
        Matrix4f initialTranslation2 = (new Matrix4f()).translation(6, 0, 10);
        Matrix4f initialScale2 = (new Matrix4f()).scaling(0.5f);
        cube.setLocalTranslation(initialTranslation2);
        cube.setLocalScale(initialScale2);

        if (engine.isDebugMode()) {
            xAxis = new GameObject(GameObject.root(), xAxisS);
            xAxis.getRenderStates().setColor(new Vector3f(1, 0, 0));
            xAxis.getRenderStates().hasDepthTesting(false);
            yAxis = new GameObject(GameObject.root(), yAxisS);
            yAxis.getRenderStates().setColor(new Vector3f(0, 1, 0));
            yAxis.getRenderStates().hasDepthTesting(false);
            zAxis = new GameObject(GameObject.root(), zAxisS);
            zAxis.getRenderStates().setColor(new Vector3f(0, 0, 1));
            zAxis.getRenderStates().hasDepthTesting(false);
        }

        fridge = new GameObject(GameObject.root(), fridgeS, fridgetx);
        Matrix4f initialTranslation5 = (new Matrix4f()).translation(-3, 0, 0);
        Matrix4f initialScale5 = (new Matrix4f()).scaling(1.0f);
        Matrix4f initialRotation5 = (new Matrix4f()).rotation(-3.14159f / 2f, 0f, 1f, 0f);
        fridge.setLocalTranslation(initialTranslation5);
        fridge.setLocalScale(initialScale5);
        fridge.setLocalRotation(initialRotation5);

        shuttle = new GameObject(GameObject.root(), shuttleS, shuttletx);
        Matrix4f shuttleTranslation = new Matrix4f().translation(-16, 5f, 16f);
        Vector3f shuttleAxis = new Vector3f(1f, 1f, 1f).normalize();
        Matrix4f shuttleRotation = new Matrix4f().rotation(-3f, shuttleAxis.x, shuttleAxis.y, shuttleAxis.z);
        Matrix4f shuttleScale = new Matrix4f().scaling(1f);
        shuttle.setLocalTranslation(shuttleTranslation);
        shuttle.setLocalScale(shuttleScale);
        shuttle.setLocalRotation(shuttleRotation);
    }

    @Override
    public void buildLights() {
    }

    @Override
    public void buildPhysicsObjects() {
        Vector3f planetLocation = new Vector3f(0f,-21.5f,0f);
        Quaternionf planetRotation = new Quaternionf();
        planetRotation.rotateXYZ(1f, 2f, 0f);
        Vector3f planetScale = new Vector3f(20f,20f,20f);
        SphereCollisionShape shape = new SphereCollisionShape(30f);
        SpherePlanet p1 = new SpherePlanet(engine, planetLocation, planetRotation, planetScale, planetHeighttx, planetSurfacetx, planetMaptx, shape,
                (ImportedModel) planetS, -10f);

        Vector3f planetLocation2 = new Vector3f(0f,40f,0f);
        Quaternionf planetRotation2 = new Quaternionf();
        planetRotation2.rotateXYZ(-1f, -2f, 0f);
        Vector3f planetScale2 = new Vector3f(6f,6f,6f);
        SphereCollisionShape shape2 = new SphereCollisionShape(30f);
        CubePlanet p2 = new CubePlanet(engine, planetLocation2, planetRotation2, planetScale2, planetHeighttx, planetSurfacetx, planetMaptx, shape2,
                (ImportedModel) planet2S, 0f,6f);

        Matrix4f boxTransform = new Matrix4f().identity();
        boxTransform.translate(1.5f, 0f, 0f);
        // boxTransform.rotateX((float)Math.toRadians(45f));
        boxPhys = engine.getSceneGraph().addPhysicsBox(1f, boxTransform.get(new float[16]), new float[] { 1f, 1f, 1f },
                CollisionGroups.Prop.value(), CollisionGroups.All.value());
        boxPhys.setSurface(CharacterSurface.GroundSurface);
        boxPhys.setGameObject(cube);

        boxTransform = new Matrix4f().identity();
        boxTransform.translate(-1.5f, -1f, 0f);
        // boxTransform.rotateX((float)Math.toRadians(45f));
        JmeBulletPhysicsObject box2 = engine.getSceneGraph().addPhysicsBox(0f, boxTransform.get(new float[16]),
                new float[] { 1f, 1f, 1f }, CollisionGroups.Prop.value(), CollisionGroups.All.value());
        box2.setSurfaceProvider(new BouncePadSurface(new Vector3f(0f, 30f, 0f), false));

        DriverState[] driverStates = new DriverState[] {
                new DriverState(),
                new DriverState(),
                new DriverState(),
                new DriverState(),
                new DriverState()
        };
        driverStates[0].driverPosDir = new Vector3f(0.6f, 1f, 0f);
        driverStates[0].driverPosRadius = 20f;
        driverStates[0].driverRadius = 3f;
        driverStates[0].stateAdvance = StateAdvanceType.DISTANCE;
        driverStates[0].lockCharacter = false;
        driverStates[1].driverPosDir = new Vector3f(0.6f, 1f, 0f);
        driverStates[1].driverPosRadius = 20f;
        driverStates[1].driverRadius = 0f;
        driverStates[1].stateAdvance = StateAdvanceType.DIALOGUE;
        driverStates[1].dialogueImage = dialogueTest;
        driverStates[1].lockCharacter = true;
        driverStates[1].characterPos = new Vector3f(11.12625408f,-4.25118876f,0.72525078f);
        driverStates[1].cameraPos = new Vector3f(11.73058510f,-1.91288257f,-3.65277433f);
        driverStates[1].lookTarget = new Vector3f(12.79000664f,-3.97099614f,0.22301844f);
        driverStates[2].driverPosDir = new Vector3f(0.6f, 1f, 0f);
        driverStates[2].driverPosRadius = 20f;
        driverStates[2].driverRadius = 0f;
        driverStates[2].stateAdvance = StateAdvanceType.DIALOGUE;
        driverStates[2].dialogueImage = dialogueTest;
        driverStates[2].lockCharacter = true;
        driverStates[2].characterPos = new Vector3f(11.12625408f,-4.25118876f,0.72525078f);
        driverStates[2].cameraPos = new Vector3f(11.73058510f,-1.91288257f,-3.65277433f);
        driverStates[2].lookTarget = new Vector3f(12.79000664f,-3.97099614f,0.22301844f);
        driverStates[3].driverPosDir = new Vector3f(0.6f, 1f, 0f);
        driverStates[3].driverPosRadius = 20f;
        driverStates[3].driverRadius = 0f;
        driverStates[3].stateAdvance = StateAdvanceType.DIALOGUE;
        driverStates[3].dialogueImage = dialogueTest;
        driverStates[3].lockCharacter = true;
        driverStates[3].characterPos = new Vector3f(11.12625408f,-4.25118876f,0.72525078f);
        driverStates[3].cameraPos = new Vector3f(11.73058510f,-1.91288257f,-3.65277433f);
        driverStates[3].lookTarget = new Vector3f(12.79000664f,-3.97099614f,0.22301844f);
        driverStates[4].driverPosDir = new Vector3f(0.6f, 1f, 0f);
        driverStates[4].driverPosRadius = 20f;
        driverStates[4].driverRadius = 0f;
        driverStates[4].stateAdvance = StateAdvanceType.NONE;
        driverStates[4].lockCharacter = false;

        NpcPathStanding standing1 = new NpcPathStanding(new Vector3f(0.8f, 1f, 0f), 20f, new Vector3f(-1f, 0f, 0f), 1f);
        NpcPathElliptic elliptic1 = new NpcPathElliptic(new Vector3f(0.8f, 1f, 0f), 20f, new Vector3f(-1f, 0f, 0f), 1f,
                0.16f, 0.16f, 1f, 1f);
        Npc[] npcs = new Npc[] {
                new Npc(CommonResources.penguinS, CommonResources.penguintx,
                        CommonResources.characterAnimatedShaderIndex, new NpcState[] {
                                new NpcState("WADDLE", 1f, null, CommonResources.penguinExpDefault, elliptic1),
                                new NpcState("WAVE", 1f, null, CommonResources.penguinExpHappy, standing1),
                                new NpcState("HAPPY", 1f, null, CommonResources.penguinExpHappy, standing1),
                                new NpcState("SAD", 1f, null, CommonResources.penguinExpHappy, standing1),
                                new NpcState("IDLE", 1f, null, CommonResources.penguinExpHappy, standing1)
                        }, 1f, 3f)
        };
        testDriver = new NpcEventDriver(driverStates, npcs, p1, CommonResources.characterController);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void physicsUpdate() {

    }

    @Override
    public void visualUpdate() {
        testDriver.update(CommonResources.characterController.getLocation(),
                CommonResources.characterController.getCameraLocation());
    }

}
