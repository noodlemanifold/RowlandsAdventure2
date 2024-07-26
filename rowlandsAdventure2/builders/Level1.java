package rowlandsAdventure2.builders;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.jme3.bullet.collision.shapes.SphereCollisionShape;

import rowlandsAdventure2.character.CharacterSurface;
import rowlandsAdventure2.npcs.DriverState;
import rowlandsAdventure2.npcs.DriverState.StateAdvanceType;
import rowlandsAdventure2.npcs.Npc;
import rowlandsAdventure2.npcs.NpcEventDriver;
import rowlandsAdventure2.npcs.NpcEventDriverFlowerHack;
import rowlandsAdventure2.npcs.NpcPathElliptic;
import rowlandsAdventure2.npcs.NpcPathStanding;
import rowlandsAdventure2.npcs.NpcState;
import rowlandsAdventure2.planets.SpherePlanet;
import tage.Engine;
import tage.TextureImage;
import tage.Utils;
import tage.shapes.AnimatedShape;
import tage.shapes.ImportedModel;

//starting planet and stone exposition planet
public class Level1 extends LevelBuilder {

    private Engine engine;

    private TextureImage planetHeighttx;
    private TextureImage planetSurfacetx;
    private TextureImage planetColorstx;
    private TextureImage planet2Heighttx;
    private TextureImage planet2Surfacetx;
    private TextureImage planet2Colorstx;
    private TextureImage dialogue1;
    private TextureImage dialogue2;
    private TextureImage dialogue3;
    private TextureImage dialogue4;
    private TextureImage dialogue5;
    private TextureImage dialogue6;
    private TextureImage dialogue7;
    private TextureImage dialogue8;
    private TextureImage dialogue9;
    private TextureImage popup1;
    private TextureImage popup2;
    private TextureImage popup3;
    private TextureImage popupT;
    private NpcEventDriver driver1;
    private NpcEventDriver driver2;
    private NpcEventDriver driver3;
    private AnimatedShape penguinS1;
    private AnimatedShape penguinS2;
    private AnimatedShape penguinS3;
    private AnimatedShape penguinS4;

    public Level1(Engine e) {
        engine = e;
    }

    @Override
    public void awake() {

    }

    @Override
    public void loadShapes() {

        penguinS1 = new AnimatedShape("penguin.rkm", "penguin.rks", 60f);
        penguinS1.loadAnimation("WAVE", "penguinWave.rka");

        penguinS2 = new AnimatedShape("penguin.rkm", "penguin.rks", 60f);
        penguinS2.loadAnimation("HAPPY", "penguinHappy.rka");

        penguinS3 = new AnimatedShape("penguin.rkm", "penguin.rks", 60f);
        penguinS3.loadAnimation("SAD", "penguinSad.rka");
        penguinS3.loadAnimation("IDLE", "penguinIdle.rka");
        penguinS3.loadAnimation("HAPPY", "penguinHappy.rka");
        penguinS3.loadAnimation("WAVE", "penguinWave.rka");

        penguinS4 = new AnimatedShape("penguin.rkm", "penguin.rks", 60f);
        penguinS4.loadAnimation("SAD", "penguinSad.rka");
        penguinS4.loadAnimation("WADDLE", "penguinWaddle.rka");
        penguinS4.loadAnimation("HAPPY", "penguinHappy.rka");
        penguinS4.loadAnimation("WAVE", "penguinWave.rka");
        penguinS4.loadAnimation("IDLE", "penguinIdle.rka");
    }

    @Override
    public void loadTextures() {
        planetHeighttx = new TextureImage("heightMap1.png");
        planetSurfacetx = new TextureImage("surfaceMap1.png", true);
        planetColorstx = new TextureImage("surfaceColors1.png", true);
        planet2Heighttx = new TextureImage("heightMap2.png");
        planet2Surfacetx = new TextureImage("surfaceMap2.png", true);
        planet2Colorstx = new TextureImage("surfaceColors2.png", true);
        popup1 = new TextureImage("popup1.png");
        popup2 = new TextureImage("popup2.png");
        popup3 = new TextureImage("popup3.png");
        popupT = new TextureImage("popupTest.png");
        dialogue1 = new TextureImage("dialogue1.png");
        dialogue2 = new TextureImage("dialogue2.png");
        dialogue3 = new TextureImage("dialogue3.png");
        dialogue4 = new TextureImage("dialogue4.png");
        dialogue5 = new TextureImage("dialogue5.png");
        dialogue6 = new TextureImage("dialogue6.png");
        dialogue7 = new TextureImage("dialogue7.png");
        dialogue8 = new TextureImage("dialogue8.png");
        dialogue9 = new TextureImage("dialogue9.png");
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
    public void buildLights() {
    }

    @Override
    public void buildPhysicsObjects() {
        Vector3f planetLocation = new Vector3f(0f,-21.5f,0f);
        Quaternionf planetRotation = new Quaternionf();
        planetRotation.rotateXYZ(0f, 1.57f, 0f);
        Vector3f planetScale = new Vector3f(20f,20f,20f);
        SphereCollisionShape shape = new SphereCollisionShape(40f);
        SpherePlanet p1 = new SpherePlanet(engine, planetLocation, planetRotation, planetScale, planetHeighttx, planetSurfacetx, planetColorstx, shape,
                (ImportedModel) CommonResources.planetSphereS, 7f);
        p1.setLightDirection(new Vector3f(-0.81859279f,0.55548257f,0.14609860f));

        
        Vector3f bump1pos = new Vector3f(-18.42391586f,-30.13127899f,2.31818867f);
        Vector3f bump1up = new Vector3f(-0.89973050f,-0.42150787f,0.11320857f);
        Vector3f bump1fo = new Vector3f(-0.40411201f,0.88067752f,0.24718554f);
        Utils.createBumper(engine, p1, bump1pos, bump1up, bump1fo, 23f, false);

        Vector3f bump2pos = new Vector3f(-17.43017578f,-42.11539078f,1.49227178f);
        Vector3f bump2up = new Vector3f(-0.64466411f,-0.76247096f,0.05519245f);
        Vector3f bump2fo = new Vector3f(0.75216079f,-0.64553380f,-0.13244025f);
        Utils.createBumper(engine, p1, bump2pos, bump2up, bump2fo, 23f, true);

        DriverState[] driverStates = new DriverState[] {
                new DriverState()
        };
        driverStates[0].driverPosDir = new Vector3f(0.6f, 1f, 0f);
        driverStates[0].driverPosRadius = 20f;
        driverStates[0].driverRadius = 3f;
        driverStates[0].stateAdvance = StateAdvanceType.NONE;
        driverStates[0].lockCharacter = false;

        NpcPathStanding standing1 = new NpcPathStanding(new Vector3f(0.12947182f,0.95257318f,0.27539301f), 20.24f, new Vector3f(-0.60554087f,0.29588392f,-1f), 1f);
        NpcPathStanding standing2 = new NpcPathStanding(new Vector3f(-0.91760206f,-0.39748555f,0.00340126f), 20.5f, new Vector3f(-0.30450803f,0.70841038f,0.63673365f), 1f);
        Npc[] npcs = new Npc[] {
                new Npc(penguinS1, CommonResources.penguintx,
                        CommonResources.characterAnimatedShaderIndex, new NpcState[] {
                                new NpcState("WAVE", 1f, popup1, CommonResources.penguinExpHappy, standing1)
                        }, 1f, 5f),
                new Npc(penguinS2, CommonResources.penguintx,
                        CommonResources.characterAnimatedShaderIndex, new NpcState[] {
                                new NpcState("HAPPY", 1f, popup2, CommonResources.penguinExpHappy, standing2)
                        }, 1f, 5f)
        };
        driver1 = new NpcEventDriver(driverStates, npcs, p1, CommonResources.characterController);


        Vector3f planet2Location = new Vector3f(-0.64044511f,-0.76617962f,0.05290418f);
        planet2Location.mul(70f);
        planet2Location.add(planetLocation);
        Quaternionf planet2Rotation = new Quaternionf();
        planet2Rotation.rotateXYZ(0f, 0f, 0f);
        Vector3f planet2Scale = new Vector3f(10f,10f,10f);
        SphereCollisionShape shape2 = new SphereCollisionShape(40f);
        SpherePlanet p2 = new SpherePlanet(engine, planet2Location, planet2Rotation, planet2Scale, planet2Heighttx, planet2Surfacetx, planet2Colorstx, shape2,
                (ImportedModel) CommonResources.planetSphereS, 10f);
        p2.setLightDirection(new Vector3f(-0.81859279f,0.55548257f,0.14609860f));
        p2.setSurfaceList(new CharacterSurface[]{
            CharacterSurface.GrassSurface,
            CharacterSurface.GroundSurface,
            CharacterSurface.GroundSurface,
            CharacterSurface.GroundSurface
        });

        Vector3f bump3pos = new Vector3f(-58.09560394f,-85.85192871f,3.31058550f);
        Vector3f bump3up = new Vector3f(-0.77756977f,-0.62837511f,-0.02302072f);
        Vector3f bump3fo = new Vector3f(-0.17196144f,0.24772003f,-0.95344859f);
        Utils.createBumper(engine, p2, bump3pos, bump3up, bump3fo, 30f, true);


        DriverState[] driverStates2 = new DriverState[] {
            new DriverState(),
            new DriverState(),
            new DriverState(),
            new DriverState(),
            new DriverState(),
            new DriverState()
        };
        driverStates2[0].driverPosDir = new Vector3f(0.74639994f,0.62050360f,0.24054609f);
        driverStates2[0].driverPosRadius = 10f;
        driverStates2[0].driverRadius = 3f;
        driverStates2[0].stateAdvance = StateAdvanceType.DISTANCE;
        driverStates2[0].lockCharacter = false;
        driverStates2[1].driverPosDir = new Vector3f(0.74639994f,0.62050360f,0.24054609f);
        driverStates2[1].driverPosRadius = 10f;
        driverStates2[1].driverRadius = 3f;
        driverStates2[1].stateAdvance = StateAdvanceType.DIALOGUE;
        driverStates2[1].dialogueImage = dialogue1;
        driverStates2[1].characterPos = new Vector3f(-36.80451965f,-69.42628479f,7.45615387f);
        driverStates2[1].cameraPos = new Vector3f(-37.13444519f,-65.95247650f,6.31712818f);
        driverStates2[1].lookTarget = new Vector3f(-36.88227463f,-68.51251221f,8.60742950f);
        driverStates2[1].lockCharacter = true;
        driverStates2[2].driverPosDir = new Vector3f(0.74639994f,0.62050360f,0.24054609f);
        driverStates2[2].driverPosRadius = 10f;
        driverStates2[2].driverRadius = 3f;
        driverStates2[2].stateAdvance = StateAdvanceType.DIALOGUE;
        driverStates2[2].dialogueImage = dialogue2;
        driverStates2[2].characterPos = new Vector3f(-36.80451965f,-69.42628479f,7.45615387f);
        driverStates2[2].cameraPos = new Vector3f(-37.13444519f,-65.95247650f,6.31712818f);
        driverStates2[2].lookTarget = new Vector3f(-36.88227463f,-68.51251221f,8.60742950f);
        driverStates2[2].lockCharacter = true;
        driverStates2[3].driverPosDir = new Vector3f(0.74639994f,0.62050360f,0.24054609f);
        driverStates2[3].driverPosRadius = 10f;
        driverStates2[3].driverRadius = 3f;
        driverStates2[3].stateAdvance = StateAdvanceType.DIALOGUE;
        driverStates2[3].dialogueImage = dialogue3;
        driverStates2[3].characterPos = new Vector3f(-36.80451965f,-69.42628479f,7.45615387f);
        driverStates2[3].cameraPos = new Vector3f(-37.13444519f,-65.95247650f,6.31712818f);
        driverStates2[3].lookTarget = new Vector3f(-36.88227463f,-68.51251221f,8.60742950f);
        driverStates2[3].lockCharacter = true;
        driverStates2[4].driverPosDir = new Vector3f(0.74639994f,0.62050360f,0.24054609f);
        driverStates2[4].driverPosRadius = 10f;
        driverStates2[4].driverRadius = 3f;
        driverStates2[4].stateAdvance = StateAdvanceType.DIALOGUE;
        driverStates2[4].dialogueImage = dialogue4;
        driverStates2[4].characterPos = new Vector3f(-36.80451965f,-69.42628479f,7.45615387f);
        driverStates2[4].cameraPos = new Vector3f(-37.13444519f,-65.95247650f,6.31712818f);
        driverStates2[4].lookTarget = new Vector3f(-36.88227463f,-68.51251221f,8.60742950f);
        driverStates2[4].lockCharacter = true;
        driverStates2[5].driverPosDir = new Vector3f(0.74639994f,0.62050360f,0.24054609f);
        driverStates2[5].driverPosRadius = 10f;
        driverStates2[5].driverRadius = 3f;
        driverStates2[5].stateAdvance = StateAdvanceType.NONE;
        driverStates2[5].lockCharacter = false;

        NpcPathStanding standing3 = new NpcPathStanding(new Vector3f(0.62403673f,0.60544741f,0.49397516f), 10f, new Vector3f(0.53493029f,0.12979989f,-0.83486617f), 1f);
        Npc[] npcs2 = new Npc[] {
                new Npc(penguinS3, CommonResources.penguintx,
                        CommonResources.characterAnimatedShaderIndex, new NpcState[] {
                                new NpcState("IDLE", 1f, null, CommonResources.penguinExpDefault, standing3),
                                new NpcState("WAVE", 1f, null, CommonResources.penguinExpHappy, standing3),
                                new NpcState("SAD", 1f, null, CommonResources.penguinExpSad, standing3),
                                new NpcState("HAPPY", 1f, null, CommonResources.penguinExpHappy, standing3),
                                new NpcState("HAPPY", 1f, null, CommonResources.penguinExpHappy, standing3),
                                new NpcState("WAVE", 1f, popupT, CommonResources.penguinExpHappy, standing3)
                        }, 1f, 5f)
        };
        driver2 = new NpcEventDriver(driverStates2, npcs2, p2, CommonResources.characterController);



        DriverState[] driverStates3 = new DriverState[] {
            new DriverState(),
            new DriverState(),
            new DriverState(),
            new DriverState(),
            new DriverState(),
            new DriverState(),
            new DriverState()
        };
        driverStates3[0].driverPosDir = new Vector3f(-0.41747680f,-0.72596812f,0.54651928f);
        driverStates3[0].driverPosRadius = 16f;
        driverStates3[0].driverRadius = 3f;
        driverStates3[0].stateAdvance = StateAdvanceType.DISTANCE;
        driverStates3[0].lockCharacter = false;
        driverStates3[1].driverPosDir = new Vector3f(-0.41747680f,-0.72596812f,0.54651928f);
        driverStates3[1].driverPosRadius = 16f;
        driverStates3[1].driverRadius = 3f;
        driverStates3[1].stateAdvance = StateAdvanceType.DIALOGUE;
        driverStates3[1].dialogueImage = dialogue5;
        driverStates3[1].characterPos = new Vector3f(-52.91583252f,-88.09984589f,10.02972507f);
        driverStates3[1].cameraPos = new Vector3f(-54.16000748f,-86.88261414f,13.59378815f);
        driverStates3[1].lookTarget = new Vector3f(-54.21213150f,-88.13192749f,10.23289967f);
        driverStates3[1].lockCharacter = true;
        driverStates3[2].driverPosDir = new Vector3f(-0.41747680f,-0.72596812f,0.54651928f);
        driverStates3[2].driverPosRadius = 16f;
        driverStates3[2].driverRadius = 3f;
        driverStates3[2].stateAdvance = StateAdvanceType.DIALOGUE;
        driverStates3[2].dialogueImage = dialogue6;
        driverStates3[2].characterPos = new Vector3f(-52.91583252f,-88.09984589f,10.02972507f);
        driverStates3[2].cameraPos = new Vector3f(-54.16000748f,-86.88261414f,13.59378815f);
        driverStates3[2].lookTarget = new Vector3f(-54.21213150f,-88.13192749f,10.23289967f);
        driverStates3[2].lockCharacter = true;
        driverStates3[3].driverPosDir = new Vector3f(-0.41747680f,-0.72596812f,0.54651928f);
        driverStates3[3].driverPosRadius = 16f;
        driverStates3[3].driverRadius = 3f;
        driverStates3[3].stateAdvance = StateAdvanceType.DIALOGUE;
        driverStates3[3].dialogueImage = dialogue7;
        driverStates3[3].characterPos = new Vector3f(-52.91583252f,-88.09984589f,10.02972507f);
        driverStates3[3].cameraPos = new Vector3f(-54.16000748f,-86.88261414f,13.59378815f);
        driverStates3[3].lookTarget = new Vector3f(-54.21213150f,-88.13192749f,10.23289967f);
        driverStates3[3].lockCharacter = true;
        driverStates3[4].driverPosDir = new Vector3f(-0.41747680f,-0.72596812f,0.54651928f);
        driverStates3[4].driverPosRadius = 16f;
        driverStates3[4].driverRadius = 3f;
        driverStates3[4].stateAdvance = StateAdvanceType.DIALOGUE;
        driverStates3[4].dialogueImage = dialogue8;
        driverStates3[4].characterPos = new Vector3f(-52.91583252f,-88.09984589f,10.02972507f);
        driverStates3[4].cameraPos = new Vector3f(-54.16000748f,-86.88261414f,13.59378815f);
        driverStates3[4].lookTarget = new Vector3f(-54.21213150f,-88.13192749f,10.23289967f);
        driverStates3[4].lockCharacter = true;
        driverStates3[5].driverPosDir = new Vector3f(-0.41747680f,-0.72596812f,0.54651928f);
        driverStates3[5].driverPosRadius = 16f;
        driverStates3[5].driverRadius = 3f;
        driverStates3[5].stateAdvance = StateAdvanceType.DIALOGUE;
        driverStates3[5].dialogueImage = dialogue9;
        driverStates3[5].characterPos = new Vector3f(-52.91583252f,-88.09984589f,10.02972507f);
        driverStates3[5].cameraPos = new Vector3f(-54.16000748f,-86.88261414f,13.59378815f);
        driverStates3[5].lookTarget = new Vector3f(-54.21213150f,-88.13192749f,10.23289967f);
        driverStates3[5].lockCharacter = true;
        driverStates3[6].driverPosDir = new Vector3f(-0.41747680f,-0.72596812f,0.54651928f);
        driverStates3[6].driverPosRadius = 16f;
        driverStates3[6].driverRadius = 3f;
        driverStates3[6].stateAdvance = StateAdvanceType.NONE;
        driverStates3[6].lockCharacter = false;

        NpcPathStanding standing4 = new NpcPathStanding(new Vector3f(-0.58189231f,-0.71980250f,0.37853083f), 16f, new Vector3f(0.67927670f,-0.17422976f,0.71290052f), 1f);
        NpcPathElliptic elliptic = new NpcPathElliptic(new Vector3f(-0.58189231f,-0.71980250f,0.37853083f), 16f, new Vector3f(0.67927670f,-0.17422976f,0.71290052f), 1f, 0.10616f*0.8f, 0.10616f*0.8f, 1.5f, 1.5f);
        Npc[] npcs3 = new Npc[] {
                new Npc(penguinS4, CommonResources.penguintx,
                        CommonResources.characterAnimatedShaderIndex, new NpcState[] {
                                new NpcState("WADDLE", 0.8f, null, CommonResources.penguinExpSad, elliptic),
                                new NpcState("SAD", 1f, null, CommonResources.penguinExpSad, standing4),
                                new NpcState("HAPPY", 1f, null, CommonResources.penguinExpHappy, standing4),
                                new NpcState("IDLE", 1f, null, CommonResources.penguinExpDefault, standing4),
                                new NpcState("IDLE", 1f, null, CommonResources.penguinExpDefault, standing4),
                                new NpcState("HAPPY", 1f, null, CommonResources.penguinExpHappy, standing4),
                                new NpcState("WAVE", 1f, popup3, CommonResources.penguinExpDefault, standing4)
                        }, 1f, 5f)
        };
        driver3 = new NpcEventDriverFlowerHack(driverStates3, npcs3, p2, CommonResources.characterController);
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
        driver2.update(CommonResources.characterController.getLocation(),
                CommonResources.characterController.getCameraLocation());
        driver3.update(CommonResources.characterController.getLocation(),
                CommonResources.characterController.getCameraLocation());
    }

}
