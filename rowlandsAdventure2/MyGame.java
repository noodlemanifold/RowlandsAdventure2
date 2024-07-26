package rowlandsAdventure2;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.joml.Vector3f;

import rowlandsAdventure2.builders.*;
import rowlandsAdventure2.input.InputMapper;
import rowlandsAdventure2.networking.GhostManager;
import rowlandsAdventure2.networking.ProtocolClient;
import rowlandsAdventure2.npcs.DriverState;
import rowlandsAdventure2.npcs.DriverState.StateAdvanceType;
import rowlandsAdventure2.npcs.NpcEventDriver;
import rowlandsAdventure2.npcs.NpcEventDriverTitleHack;
import rowlandsAdventure2.planets.PlanetManager;
import tage.Camera;
import tage.Engine;
import tage.RenderSystem;
import tage.TextureImage;
import tage.Time;
import tage.VariableFrameRateGame;
import tage.JmeBullet.JmeBulletPhysicsObject;
import tage.input.InputManagerManager;
import tage.networking.IGameConnection.ProtocolType;

public class MyGame extends VariableFrameRateGame {
	private static Engine engine;

	private int starBox;

	private GhostManager gm;
	private String serverAddress;
	private int serverPort;
	private ProtocolType serverProtocol;
	private ProtocolClient protClient;

	private InputManagerManager imm;
	private InputMapper inputMapper;

	private LevelBuilder[] levels;

	private NpcEventDriver titleDriver;
	private TextureImage titleImage;
	private TextureImage selectImage;

	public MyGame(String serverAddress, int serverPort, String protocol) {
		super();
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		if (protocol.toUpperCase().compareTo("TCP") == 0)
			this.serverProtocol = ProtocolType.TCP;
		else
			this.serverProtocol = ProtocolType.UDP;
	}

	public static void main(String[] args) {
		MyGame game = new MyGame(args[0], Integer.parseInt(args[1]), args[2]);
		boolean debug = false;
		if (args.length >= 4 && args[3].equals("-debug")) {
			debug = true;
		}
		engine = new Engine(game, debug);

		System.setProperty("net.java.games.input.useDefaultPlugin", "false");
		game.initializeSystem();
		game.game_loop();
	}

	@Override
	public void awake() {
		levels = new LevelBuilder[]{new CommonResources(engine),
									new Level1(engine),
									new Level2(engine),
									new Level3(engine),
									new Level4(engine),
									new Level5(engine)
		};
		for(LevelBuilder level : levels) level.awake();
	}

	@Override
	public void loadShapes() {
		for(LevelBuilder level : levels) level.loadShapes();
	}

	@Override
	public void loadTextures() {
		titleImage = new TextureImage("titleScreen.png");
		selectImage = new TextureImage("selectScreen.png");
		for(LevelBuilder level : levels) level.loadTextures();
	}

	@Override
	public void loadSkyBoxes() {
		starBox = engine.getSceneGraph().loadCubeMap("stars2");
		engine.getSceneGraph().setActiveSkyBoxTexture(starBox);
		engine.getSceneGraph().setSkyBoxEnabled(true);
	}

	@Override
	public void loadSounds(){
		for(LevelBuilder level : levels) level.loadSounds();
	}

	@Override
	public void buildCustomRenderPrograms(RenderSystem rs) {
		for(LevelBuilder level : levels) level.buildCustomRenderPrograms();
	}

	@Override
	public void buildObjects() {
		for(LevelBuilder level : levels) level.buildObjects();
	}

	@Override
	public void buildPhysicsObjects(RenderSystem rs) {
		for(LevelBuilder level : levels) level.buildPhysicsObjects();

		DriverState[] titleStates = new DriverState[] {
                new DriverState(),
                new DriverState(),
                new DriverState()
        };
        titleStates[0].driverPosDir = new Vector3f(0f, 1f, 0f);
        titleStates[0].driverPosRadius = 20f;
        titleStates[0].driverRadius = 0f;
        titleStates[0].stateAdvance = StateAdvanceType.DIALOGUE;
		titleStates[0].dialogueImage = titleImage;
        titleStates[0].lockCharacter = true;
        titleStates[1].driverPosDir = new Vector3f(0f, 1f, 0f);
        titleStates[1].driverPosRadius = 20f;
        titleStates[1].driverRadius = 0f;
        titleStates[1].stateAdvance = StateAdvanceType.EXTERNAL;
        titleStates[1].dialogueImage = selectImage;
        titleStates[1].lockCharacter = true;
        titleStates[2].driverPosDir = new Vector3f(0f, 1f, 0f);
        titleStates[2].driverPosRadius = 20f;
        titleStates[2].driverRadius = 0f;
        titleStates[2].stateAdvance = StateAdvanceType.NONE;
        titleStates[2].lockCharacter = false;

		titleDriver = new NpcEventDriverTitleHack(titleStates, null, null, CommonResources.characterController, this);
		titleDriver.disableHonks();
	}

	@Override
	public void initializeLights() {
		for(LevelBuilder level : levels) level.buildLights();
	}

	// called after everything is built
	@Override
	public void initializeGame() {
		if (engine.isDebugMode()) {
			engine.getRenderSystem().setWindowDimensions(1900, 1000);
			//engine.enablePhysicsWorldRender();
		}else{
			engine.getRenderSystem().setWindowDimensions();
		}

		imm = InputManagerManager.createSingleton(engine);
		inputMapper = InputMapper.createSingleton();

		Camera c = (engine.getRenderSystem()).getViewport("MAIN").getCamera();
		c.setLocation(new Vector3f(0f, 0f, -3f));

		for(LevelBuilder level : levels) level.initialize();

		//setupNetworking(2);
	}

	private int frameCount = 0;
	private float physStartTime = -1f;
	@Override
	public void update() {
		frameCount++;
		imm.update(Time.elapsedTime);
		inputMapper.ResolveInputs(imm.getControllerState(), imm.getMouseAndKeebState());

		if (frameCount > 5){
			if (physStartTime < 0) physStartTime = Time.elapsedTime;
			physicsUpdate();//simple physics easing to prevent zipping thru the ground
		}

		visualUpdate();

		processNetworking(Time.elapsedTime);
	}

	private void physicsUpdate() {

		engine.getSceneGraph().getPhysicsEngine().update(Time.deltaTime);

		engine.getSceneGraph().getPhysicsEngine().detectCollisions();

		PlanetManager.processPlanets();

		JmeBulletPhysicsObject.updateObjects();

		for(LevelBuilder level : levels) level.physicsUpdate();
		
		if (protClient != null) {
			protClient.sendMoveMessage(CommonResources.characterController.getState());
		}

	}

	private float timer = 0f;
	private boolean gameFinished = false;
	private boolean showUI = false;
	private Vector3f uiColor = new Vector3f(0.5f,0.5f,1f);
	private float timerStart = -1f;
	private void visualUpdate() {
		if (InputMapper.singleton().getCharacterInputs().uiFirstie) showUI = !showUI;
		if (physStartTime > 0){
			if (timerStart < 0 && CommonResources.characterController.getEnabled()){
				timerStart = Time.elapsedTime;
			}
			if (!gameFinished && timerStart > 0){
				timer = Time.elapsedTime - timerStart;
			}
			if (showUI){
				int h = Math.round(engine.getRenderSystem().getViewport("MAIN").getActualHeight()) - 30;
				int w = Math.round(engine.getRenderSystem().getViewport("MAIN").getActualWidth()) - 230;
				String frac = ""+(timer%1);
				if (frac.length() >= 5){
					frac = frac.substring(2,5);
				}
				engine.getHUDmanager().setHUD1( "Game Time: "+((int)timer) + ":" + frac, uiColor, 15, h);

				float speed = CommonResources.characterController.getState().velocity.length();
				engine.getHUDmanager().setHUD2( "Current Speed: "+String.format("%3.2f", speed), uiColor, w, h);
			}else{
				engine.getHUDmanager().setHUD1("", uiColor, 0, 0);
				engine.getHUDmanager().setHUD2("", uiColor, 0, 0);
			}
		}
		for(LevelBuilder level : levels) level.visualUpdate();

		titleDriver.update(CommonResources.characterController.getLocation(),CommonResources.characterController.getCameraLocation());
	}

	public void gameFinished(){
		gameFinished = true;
	}

	@Override
	public void resized() {
		imm.screenResized();
	}

	public void setupNetworking(int texI) {
		CommonResources.characterController.setTextureIndex(texI);
		CommonResources.characterController.initializeAvatar(CommonResources.characterAnimator.createAvatar(texI));
		try {
			gm = new GhostManager(CommonResources.characterAnimator);
			protClient = new ProtocolClient(InetAddress.getByName(serverAddress), serverPort, serverProtocol, gm,
				CommonResources.characterController);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (protClient == null) {
			System.out.println("missing protocol host");
		} else { // Send the initial join message with a unique identifier for this client
			System.out.println("sending join message to protocol host");
			protClient.sendJoinMessage();
		}
	}

	protected void processNetworking(float elapsTime) { // Process packets received by the client from the server
		if (protClient != null)
			protClient.processPackets();
	}
}