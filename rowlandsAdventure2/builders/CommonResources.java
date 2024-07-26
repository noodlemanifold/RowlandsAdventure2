package rowlandsAdventure2.builders;

import rowlandsAdventure2.character.CharacterAnimator;
import rowlandsAdventure2.character.CharacterController;
import rowlandsAdventure2.npcs.Billboard;
import rowlandsAdventure2.npcs.NpcEventDriver;
import rowlandsAdventure2.npcs.ScreenBillboard;
import tage.Engine;
import tage.Light;
import tage.Light.LightType;
import tage.ObjShape;
import tage.RenderSystem;
import tage.TextureImage;
import tage.audio.AudioResource;
import tage.audio.AudioResourceType;
import tage.audio.IAudioManager;
import tage.audio.Sound;
import tage.audio.SoundType;
import tage.shapes.AnimatedShape;
import tage.shapes.ImportedModel;

public class CommonResources extends LevelBuilder {

    //idk if this is a good way to do this but I'm trying it out

    private static Engine engine;

    public static CharacterController characterController;
    public static CharacterAnimator characterAnimator;

    protected static AnimatedShape penguinS;
    protected static TextureImage penguintx;
    protected static TextureImage penguinExpDefault;
    protected static TextureImage penguinExpHappy;
    protected static TextureImage penguinExpSad;

    public static int characterShaderIndex;
	protected static int characterAnimatedShaderIndex;

    public static IAudioManager audioMgr;
    protected static Light directionalLight;

    public static AudioResource slideResource;
    public static AudioResource hitResource;

    protected static ObjShape planetSphereS;
    protected static ObjShape planetSphereMedS;
    protected static ObjShape planetCubeS;

    public static ImportedModel effectCylinder;
    public static ImportedModel effectBlock;

    public static TextureImage effectBumper;
    public static TextureImage effectBooster;

    public CommonResources(Engine e){
        engine = e;
    }

    @Override
    public void awake() {
        characterController = new CharacterController(engine);
		characterAnimator = new CharacterAnimator();
    }

    @Override
    public void loadShapes() {
        Billboard.shapeShared = new ImportedModel("billboard.obj");
		ScreenBillboard.shape = new ImportedModel("screenBillboard.obj");

        planetSphereS = new ImportedModel("planetHigh.obj");
        planetSphereMedS = new ImportedModel("planetMed.obj");
        planetCubeS = new ImportedModel("planetCubeHigh.obj");

        effectCylinder = new ImportedModel("effectCylinder.obj");
        effectBlock = new ImportedModel("effectBlock.obj");

        penguinS = new AnimatedShape("penguin.rkm", "penguin.rks", 60f);
        penguinS.loadAnimation("WAVE", "penguinWave.rka");
        penguinS.loadAnimation("TPOSE", "penguinTPose.rka");
        penguinS.loadAnimation("IDLE", "penguinIdle.rka");
        penguinS.loadAnimation("HAPPY", "penguinHappy.rka");
        penguinS.loadAnimation("SAD", "penguinSad.rka");
        penguinS.loadAnimation("WADDLE", "penguinWaddle.rka");

        characterController.loadShapes();
		characterAnimator.loadShapes();
    }

    @Override
    public void loadTextures() {
        NpcEventDriver.dialoguePrompt = new TextureImage("dialoguePrompt.png");
		ScreenBillboard.tex = new TextureImage("fridgeTex.png");
		Billboard.defaultTex = new TextureImage("fridgeTex.png");

        effectBumper = new TextureImage("bumper.png");
        effectBooster = new TextureImage("booster.png");

        penguintx = new TextureImage("penguinTex.png");
        penguinExpDefault = new TextureImage("penguinExpDefault.png");
        penguinExpHappy = new TextureImage("penguinExpHappy.png");
        penguinExpSad = new TextureImage("penguinExpSad.png");

        characterAnimator.loadTextures();
    }

    @Override
    public void loadSounds() {
		audioMgr = engine.getAudioManager();
		audioMgr.setMasterVolume(100);
        slideResource = audioMgr.createAudioResource("assets/sounds/metalSlide.wav", AudioResourceType.AUDIO_SAMPLE);
        hitResource = audioMgr.createAudioResource("assets/sounds/metalBonk.wav", AudioResourceType.AUDIO_SAMPLE);
        characterController.loadSounds();
    }

    @Override
    public void buildCustomRenderPrograms() {
        RenderSystem rs = engine.getRenderSystem();
        characterShaderIndex = rs.AddShaderProgram("assets/shaders/CharacterVert.glsl", "assets/shaders/CharacterFrag.glsl");
		characterAnimatedShaderIndex = rs.AddShaderProgram("assets/shaders/CharacterSkeletalVert.glsl", "assets/shaders/CharacterFrag.glsl");
    }

    @Override
    public void buildObjects() {

    }

    @Override
    public void buildPhysicsObjects() {
        characterController.buildObjects();

        //what a line
        characterController.initialize();

        // not a physics object, but relies on a physics object!
        ScreenBillboard.init(characterController.getCamera());
    }

    @Override
    public void buildLights(){
        directionalLight = new Light();
		engine.getSceneGraph().addLight(directionalLight);
		directionalLight.setType(LightType.DIRECTIONAL);
		directionalLight.setDiffuse(1f, 1f, 1f);
		directionalLight.setSpecular(1f, 1f, 1f);
		//disable attentuation
		directionalLight.setConstantAttenuation(1f);
		directionalLight.setLinearAttenuation(0f);
		directionalLight.setQuadraticAttenuation(0f);

        characterController.buildLights();
    }

    @Override
    public void initialize() {
        // slideSound.play();
		// slideSound.setVolume(0);
    }

    @Override
    public void physicsUpdate() {
        characterController.physicsUpdate();
        // Vector3f vel = new Vector3f(boxPhys.getLinearVelocity());
		// float fac = Utils.clamp(0f, 1f, vel.length()/5f);
		// slideSound.setVolume(Math.round(fac*100));
		// slideSound.setLocation(new Vector3f(boxPhys.getWorldLocation()));
    }

    @Override
    public void visualUpdate() {
        characterController.visualUpdate();

		ScreenBillboard.update();
    }

}
