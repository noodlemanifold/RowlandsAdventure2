package tage;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES3.GL_STATIC_READ;
import static com.jogamp.opengl.GL3ES3.GL_SHADER_STORAGE_BUFFER;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Vector;

import javax.swing.JFrame;

import org.joml.AxisAngle4f;
import org.joml.Matrix4f;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLContext;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;

import tage.JmeBullet.JmeBulletPhysicsEngine;
import tage.TextureImage.TextureData;
import tage.objectRenderers.RenderObjectAnimation;
import tage.objectRenderers.RenderObjectAnimationVolume;
import tage.objectRenderers.RenderObjectLine;
import tage.objectRenderers.RenderObjectPlanet;
import tage.objectRenderers.RenderObjectSkyBox;
import tage.objectRenderers.RenderObjectStandard;
import tage.objectRenderers.RenderObjectVolume;
import tage.shapes.AnimatedShape;
import tage.shapes.ImportedModel;

/**
 * Manages the OpenGL setup and rendering for each frame.
 * Closely follows the method described in Computer Graphics Programming in
 * OpenGL with Java.
 * <p>
 * The only methods that the game application is likely to utilize are:
 * <ul>
 * <li>setTitle() to set the title in the bar at the top of the render window
 * <li>addViewport() if setting up multiple viewports
 * <li>getViewport() mainly to get that viewport's camera
 * </ul>
 * <p>
 * This class includes the init() and display() methods used by the JOGL
 * animator.
 * 
 * @author Scott Gordon
 */
public class RenderSystem extends JFrame implements GLEventListener {
	private GLCanvas myCanvas;
	private Animator animator;
	private Engine engine;
	private RenderQueue rq;
	private RenderObjectStandard objectRendererStandard;
	private RenderObjectSkyBox objectRendererSkyBox;
	private RenderObjectLine objectRendererLine;
	private RenderObjectAnimation objectRendererAnimation;
	private RenderObjectPlanet objectRendererPlanet;
	private RenderObjectVolume objectRendererVolume;
	private RenderObjectAnimationVolume objectRendererAnimationVolume;

	private float fov = 60.0f;
	private float nearClip = 0.1f;
	private float farClip = 1000.0f;

	private int renderingProgram, hudColorProgram, skyboxProgram, lineProgram, planetProgram, volumeProgram, volumeAnimationProgram;
	private ArrayList<Integer> customPrograms = new ArrayList<Integer>();
	private int heightProgram, heightMeshProgram, skelProgram;
	private int[] vao = new int[1];
	private int[] vbo = new int[3];

	private int defaultSkyBox;

	// allocate variables for display() function
	private Matrix4f pMat = new Matrix4f(); // perspective matrix
	private Matrix4f vMat = new Matrix4f(); // view matrix
	private int xLoc, zLoc;
	private float aspect;
	private int defaultTexture;
	private String defaultTitle = "default title", title;
	private int screenSizeX, screenSizeY, fullscreen;

	private ArrayList<TextureImage> textures = new ArrayList<TextureImage>();
	private ArrayList<ObjShape> shapes = new ArrayList<ObjShape>();
	private LinkedHashMap<String, Viewport> viewportList = new LinkedHashMap<String, Viewport>();

	private TextureImage shadingRamp;
	private TextureImage planetRamp;

	private int canvasWidth, canvasHeight;
	private boolean isInFullScreenMode = false;
	GraphicsEnvironment ge;
	GraphicsDevice gd;

	private int buffer[] = new int[3];
	private float res[] = new float[1];

	private boolean isFocused = false;

	protected RenderSystem(Engine e) {
		engine = e;

		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gd = ge.getDefaultScreenDevice();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// exit when window closed

		showDialoge();
	}

	protected void setUpCanvas() {
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(profile);
		capabilities.setSampleBuffers(true);// enable MSAA
		capabilities.setNumSamples(4);// set MSAA sample count
		capabilities.setStencilBits (8);// enable stencil buffer

		this.getContentPane().setBackground(Color.DARK_GRAY);//don't flashbang the user on startup

		myCanvas = new GLCanvas(capabilities);
		myCanvas.addGLEventListener(this);
		this.getContentPane().add(myCanvas, BorderLayout.CENTER);
		this.setVisible(true);
		(engine.getHUDmanager()).setGLcanvas(myCanvas);
	}

	public void showDialoge(){
		if (!engine.isDebugMode()) {// skip dialog if debug it was annoying me
			DisplaySettingsDialog dsd = new DisplaySettingsDialog(gd);
			dsd.showIt();

			DisplayMode dm = dsd.getSelectedDisplayMode();
			screenSizeX = dm.getWidth();
			screenSizeY = dm.getHeight();
			//this.setWindowDimensions(dm.getWidth(), dm.getHeight());
			if (dsd.isFullScreenModeSelected())
				tryFullScreenMode(gd, dm);
		}
	}

	/**
	 * The game application can use this to set the window dimensions if in windowed
	 * mode.
	 */
	public void setWindowDimensions(int ssX, int ssY) {
		title = defaultTitle;
		screenSizeX = ssX;
		screenSizeY = ssY;
		if (!isInFullScreenMode)
			setSize(screenSizeX, screenSizeY);
	}

	//I modified this as well as tryFullscreen() to get the engine to use the selected resolution without crashing
	public void setWindowDimensions() {
		setWindowDimensions(screenSizeX,screenSizeY);
		if (fullscreen == 1){
			//this part crashes on my machine
			gd.setFullScreenWindow(this);
			if (gd.isDisplayChangeSupported()) {
				try {
					if (dm != null){
						gd.setDisplayMode(dm);
						screenSizeX = dm.getWidth();
						screenSizeY = dm.getHeight();
						this.setSize(dm.getWidth(), dm.getHeight());
					}
					//gd.setDisplayMode(dispMode);
					//screenSizeX = dispMode.getWidth();
					//screenSizeY = dispMode.getHeight();
					//this.setSize(dispMode.getWidth(), dispMode.getHeight());
					isInFullScreenMode = true;
				} catch (IllegalArgumentException e) {
					System.out.println(e.getLocalizedMessage());
					this.setUndecorated(false);
					this.setResizable(true);
				}
			} else {
				System.out.println("FSEM not supported");
			}
		}
	}

	/** gets a reference to the current OpenGL canvas used by the engine */
	public GLCanvas getGLCanvas() {
		return myCanvas;
	}

	/** sets the title at the top of the window if in windowed mode */
	public void setTitle(String t) {
		title = t;
	}

	private DisplayMode dm;
	private void tryFullScreenMode(GraphicsDevice gd, DisplayMode dispMode) {
		isInFullScreenMode = false;
		dm = dispMode;
		if (gd.isFullScreenSupported()) {
			this.setUndecorated(true);
			this.setResizable(false);
			this.setIgnoreRepaint(true); // AWT repaint events ignored for active rendering
			this.fullscreen = 1;
		} else {
			this.setUndecorated(false);
			this.setResizable(true);
			//this.setSize(dispMode.getWidth(), dispMode.getHeight());
			this.setLocationRelativeTo(null);
		}
	}

	/** incomplete - see comments in the code. */
	public void toggleFullScreenMode() { // Note that toggling out of fullscreen mode is incomplete.
											// It basically just makes the window smaller - it cannot be resized or
											// minimized.
											// The only way to completely exit fullscreen mode is to first dispose the
											// frame.
											// But that causes a new frame to be created, resulting in another call to
											// JOGL init().
											// So although this doesn't fully return to windowed mode, at least you can
											// access other windows.
											// And toggling back to fullscreen mode, if initially was in fullscreen
											// mode, should work.

		if (isInFullScreenMode) {
			gd.setFullScreenWindow(null);
			this.setSize(screenSizeX, screenSizeY);
			isInFullScreenMode = false;
		} else {
			gd.setFullScreenWindow(this);
			this.setExtendedState(JFrame.MAXIMIZED_BOTH);
			isInFullScreenMode = true;
		}
	}

	/** Adds a viewport at (bottom, left) with dimensions width and height. */
	public Viewport addViewport(String name, float bottom, float left, float width, float height) {
		Viewport vp = new Viewport(name, engine, bottom, left, width, height);
		viewportList.put(name, vp);
		return vp;
	}

	/** gets a reference to the viewport with the specified name. */
	public Viewport getViewport(String name) {
		return viewportList.get(name);
	}

	protected void startGameLoop() {
		setTitle(title);
		animator = new Animator(myCanvas);
		animator.start();
	}

	/**
	 * Displays the current frame - for Engine use only.
	 * This method is called automatically by the JOGL Animator, once per frame.
	 * It renders every object in the scene, considering all factors such as lights,
	 * etc.
	 * The game application should NOT call this function directly.
	 */
	Component lastFocus = null;

	public void display(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		gl.glClear(GL_STENCIL_BUFFER_BIT);

		// handle window focus
		if (getFocusOwner() == null && lastFocus != null) {
			requestFocusInWindow(); // make sure we are always the default selected component
									// otherwise the canvas is always first selected and its impossible to move the window without the cursor getting recentered
		}
		if (getFocusOwner() == myCanvas) {
			isFocused = true;
		} else {
			isFocused = false;
		}
		lastFocus = getFocusOwner();

		Time.update();
		(engine.getGame()).update();
		(engine.getSceneGraph()).applyNodeControllers();

		engine.getLightManager().updateSSBO();

		canvasWidth = myCanvas.getWidth();
		canvasHeight = myCanvas.getHeight();

		for (Viewport vp : viewportList.values()) {
			vMat = vp.getCamera().getViewMatrix();

			aspect = ((float) myCanvas.getWidth() * vp.getRelativeWidth())
					/ ((float) myCanvas.getHeight() * vp.getRelativeHeight());
			pMat.setPerspective((float) Math.toRadians(fov), aspect, nearClip, farClip);

			constructViewport(vp);

			rq = new RenderQueue((engine.getSceneGraph()).getRoot());
			Vector<GameObject> q = rq.createStandardQueue();
			Vector<GameObject> vq = rq.createVolumeQueue();

			if ((engine.getSceneGraph()).isSkyboxEnabled()) {
				objectRendererSkyBox.render((engine.getSceneGraph()).getSkyBoxObject(), skyboxProgram, pMat, vMat);
			}

			// render the graphics objects unless this has been disabled
			if (engine.willRenderGraphicsObjects()) {
				//render depth pass
				gl.glDrawBuffer(GL_NONE);
				gl.glDepthMask(true);
				renderGraphicsWorld(q, 1f);

				////render shadow volumes to stencil buffer
				gl.glEnable(GL_STENCIL_TEST);
				gl.glDepthMask(false);
				gl.glEnable(GL_DEPTH_TEST);
				gl.glDepthFunc(GL_LEQUAL);
				gl.glDisable(GL_CULL_FACE);
				//gl.glEnable(GL_DEPTH_CLAMP);
				gl.glStencilFunc(GL_ALWAYS, 0, 0xff);
				gl.glStencilOpSeparate(GL_BACK, GL_KEEP, GL_INCR_WRAP, GL_KEEP);
				gl.glStencilOpSeparate(GL_FRONT, GL_KEEP, GL_DECR_WRAP, GL_KEEP);
				renderShadowVolumes(vq, vp.getCamera());

				////render fragments in shadow
				gl.glDrawBuffer(GL_BACK);
				gl.glClear(GL_DEPTH_BUFFER_BIT);
				gl.glDepthMask(true);
				gl.glEnable(GL_CULL_FACE);
				gl.glStencilFunc(GL_EQUAL, 0, 0xff);
				gl.glStencilOpSeparate(GL_BACK, GL_KEEP, GL_KEEP, GL_KEEP);
				gl.glStencilOpSeparate(GL_FRONT, GL_KEEP, GL_KEEP, GL_KEEP);
				gl.glDisable(GL_STENCIL_TEST);
				renderGraphicsWorld(q, 0f);

				////render fragments in light
				gl.glClear(GL_DEPTH_BUFFER_BIT);
				gl.glEnable(GL_STENCIL_TEST);
				renderGraphicsWorld(q, 1f);

				//reset stencil
				gl.glStencilFunc(GL_ALWAYS, 0, 0xff);
			}

			// render the physics world if this is enabled
			if (engine.willRenderPhysicsObjects()) {
				renderPhyicsWorld();
			}

			(engine.getHUDmanager()).drawHUDs(hudColorProgram);
		}
	}

	private void renderGraphicsWorld(Vector<GameObject> q, float shadow) {
		for (int i = 0; i < q.size(); i++) {
			GameObject go = q.get(i);
			if (go.getRenderStates().renderingEnabled()) {
				if ((go.getShape()).getPrimitiveType() < 3) {
					objectRendererLine.render(go, lineProgram, pMat, vMat);
				} else if (go.getRenderStates().getIsPlanet()) {
					objectRendererPlanet.render(go, planetProgram, pMat, vMat, shadow);
				} else if (go.getRenderStates().hasCustomProgram()) {
					int programIndex = go.getRenderStates().customProgramIndex();
					if (go.getShape() instanceof AnimatedShape) {
						objectRendererAnimation.render(go, customPrograms.get(programIndex), pMat, vMat, shadow);
					} else {
						objectRendererStandard.render(go, customPrograms.get(programIndex), pMat, vMat, shadow);
					}
				} else if (go.getShape() instanceof AnimatedShape) {
					objectRendererAnimation.render(go, skelProgram, pMat, vMat, shadow);
				} else {
					objectRendererStandard.render(go, renderingProgram, pMat, vMat, shadow);
					// if hidden faces are rendered, render a second time with opposite winding
					// order
					if ((go.getRenderStates()).willRenderHiddenFaces()) {
						(go.getShape()).toggleWindingOrder();
						objectRendererStandard.render(go, renderingProgram, pMat, vMat, shadow);
						(go.getShape()).toggleWindingOrder();
					}
				}
			}
		}
	}

	private void renderShadowVolumes(Vector<GameObject> q, Camera camera){
		for (int i = 0; i < q.size(); i++) {
			GameObject go = q.get(i);
			if (go.getRenderStates().renderingEnabled() && go.getRenderStates().hasLighting() && !go.getRenderStates().getIsPlanet()) {
				if (go.getShape() instanceof AnimatedShape) {
					objectRendererAnimationVolume.render(go, volumeAnimationProgram, pMat, vMat, camera);
				} else {
					objectRendererVolume.render(go, volumeProgram, pMat, vMat, camera);
				}
			}
		}
	}

	private void renderPhyicsWorld() {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		Vector<GameObject> physicsQueue = (engine.getSceneGraph()).getPhysicsRenderables();
		gl.glClear(GL_DEPTH_BUFFER_BIT);
		for (int i = 0; i < physicsQueue.size(); i++) {
			GameObject go = physicsQueue.get(i);

			Matrix4f mat = new Matrix4f();
			Matrix4f mat2 = new Matrix4f().identity();
			Matrix4f mat3 = new Matrix4f().identity();
			AxisAngle4f aa = new AxisAngle4f();

			// set translation
			mat.set(go.getPhysicsObject().getTransform());
			mat2.set(3, 0, mat.m30());
			mat2.set(3, 1, mat.m31());
			mat2.set(3, 2, mat.m32());
			go.setLocalTranslation(mat2);

			// set rotation
			mat.getRotation(aa);
			mat3.rotation(aa);
			go.setLocalRotation(mat3);

			objectRendererStandard.render(go, renderingProgram, pMat, vMat, 1f);
		}
	}

	private void constructViewport(Viewport vp) {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		gl.glEnable(GL_SCISSOR_TEST);
		gl.glScissor((int) (vp.getRelativeLeft() * canvasWidth),
				(int) (vp.getRelativeBottom() * canvasHeight),
				(int) vp.getActualWidth(),
				(int) vp.getActualHeight());
		gl.glClear(GL_COLOR_BUFFER_BIT);
		gl.glClear(GL_DEPTH_BUFFER_BIT);

		if (vp.getHasBorder()) {
			int borderWidth = vp.getBorderWidth();
			float[] borderColor = vp.getBorderColor();
			float[] clearColor = vp.getClearColor();
			gl.glEnable(GL_SCISSOR_TEST);
			gl.glScissor((int) (vp.getRelativeLeft() * canvasWidth),
					(int) (vp.getRelativeBottom() * canvasHeight),
					(int) vp.getActualWidth(),
					(int) vp.getActualHeight());
			gl.glClearColor(borderColor[0], borderColor[1], borderColor[2], 1.0f);
			gl.glClear(GL_COLOR_BUFFER_BIT);
			gl.glScissor((int) (vp.getRelativeLeft() * canvasWidth) + borderWidth,
					(int) (vp.getRelativeBottom() * canvasHeight) + borderWidth,
					(int) vp.getActualWidth() - borderWidth * 2,
					(int) vp.getActualHeight() - borderWidth * 2);
			gl.glClearColor(clearColor[0], clearColor[1], clearColor[2], 1.0f);
			gl.glClear(GL_COLOR_BUFFER_BIT);
			gl.glDisable(GL_SCISSOR_TEST);

			gl.glViewport((int) (vp.getRelativeLeft() * canvasWidth) + borderWidth,
					(int) (vp.getRelativeBottom() * canvasHeight) + borderWidth,
					(int) (vp.getRelativeWidth() * canvasWidth) - borderWidth * 2,
					(int) (vp.getRelativeHeight() * canvasHeight) - borderWidth * 2);
		} else {
			gl.glViewport((int) (vp.getRelativeLeft() * canvasWidth),
					(int) (vp.getRelativeBottom() * canvasHeight),
					(int) (vp.getRelativeWidth() * canvasWidth),
					(int) (vp.getRelativeHeight() * canvasHeight));
		}
	}

	/**
	 * Initializes the elements needed for rendering - for Engine use only.
	 * This method is called one time, automatically, by the JOGL Animator.
	 * The game application should NOT call this function directly.
	 */
	public void init(GLAutoDrawable drawable) {

		Time.init();

		JmeBulletPhysicsEngine pe = new JmeBulletPhysicsEngine();
		pe.initSystem();
		engine.getSceneGraph().setPhysicsEngine(pe);

		GL4 gl = (GL4) GLContext.getCurrentGL();

		gl.glEnable(GL_MULTISAMPLE);// maybe needed for msaa not 100% sure

		renderingProgram = Utils.createShaderProgram("assets/shaders/StandardVert.glsl",
				"assets/shaders/StandardFrag.glsl");

		hudColorProgram = Utils.createShaderProgram("assets/shaders/HUDcolorVert.glsl",
				"assets/shaders/HUDcolorFrag.glsl");

		skyboxProgram = Utils.createShaderProgram("assets/shaders/skyboxVert.glsl",
				"assets/shaders/skyboxFrag.glsl");

		lineProgram = Utils.createShaderProgram("assets/shaders/LineVert.glsl",
				"assets/shaders/LineFrag.glsl");

		skelProgram = Utils.createShaderProgram("assets/shaders/skeletalVert.glsl",
				"assets/shaders/StandardFrag.glsl");

		planetProgram = Utils.createShaderProgram("assets/shaders/PlanetVert.glsl",
		"assets/shaders/PlanetGeom.glsl","assets/shaders/PlanetFrag.glsl");

		volumeProgram = Utils.createShaderProgram("assets/shaders/VolumesVert.glsl",
		"assets/shaders/VolumesGeom.glsl", "assets/shaders/VolumesFrag.glsl");

		volumeAnimationProgram = Utils.createShaderProgram("assets/shaders/VolumesSkeletalVert.glsl",
		"assets/shaders/VolumesGeom.glsl", "assets/shaders/VolumesFrag.glsl");

		shadingRamp = new TextureImage("shadingRamp.png");
		planetRamp = new TextureImage("planetRamp.png");

		objectRendererStandard = new RenderObjectStandard(engine, shadingRamp, planetRamp);
		objectRendererSkyBox = new RenderObjectSkyBox(engine);
		objectRendererLine = new RenderObjectLine();
		objectRendererAnimation = new RenderObjectAnimation(engine, shadingRamp, planetRamp);
		objectRendererPlanet = new RenderObjectPlanet(engine, planetRamp);
		objectRendererVolume = new RenderObjectVolume(engine);
		objectRendererAnimationVolume = new RenderObjectAnimationVolume(engine);

		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.setPerspective((float) Math.toRadians(fov), aspect, nearClip, farClip);

		System.out.println("loading skyboxes");
		defaultTexture = Utils.loadTexture("assets/defaultAssets/checkerboardSmall.JPG");
		defaultSkyBox = Utils.loadCubeMap("assets/defaultAssets/lakeIslands");

		loadVBOs();

		loadTexturesIntoOpenGL();
		(engine.getGame()).loadSkyBoxes();

		// prepare buffer for extracting height from height map
		heightProgram = Utils.createShaderProgram("assets/shaders/heightCompute.glsl");
		gl.glGenBuffers(3, buffer, 0);
		gl.glBindBuffer(GL_SHADER_STORAGE_BUFFER, buffer[0]);
		FloatBuffer resBuf = Buffers.newDirectFloatBuffer(res.length);
		gl.glBufferData(GL_SHADER_STORAGE_BUFFER, resBuf.limit() * 4, null, GL_STATIC_READ);

		heightMeshProgram = Utils.createShaderProgram("assets/shaders/heightMeshCompute.glsl");

		// engine.getGame().initializeGame(); //;-;
		// this seems like the least terrible way of doing this but it still feels bad
		engine.getGame().buildCustomRenderPrograms(this);
		engine.getGame().buildPhysicsObjects(this);

		(engine.getLightManager()).loadLightsSSBOinitial();
	}

	protected int getDefaultSkyBox() {
		return defaultSkyBox;
	}

	/** for engine use only. */
	public int getDefaultTexture() {
		return defaultTexture;
	}

	public GLCanvas getCanvas(){
		return myCanvas;
	}

	// ----------------------- SHAPES SECTION ----------------------

	protected void addShape(ObjShape s) {
		shapes.add(s);
	}

	// loads the vertices, tex coords, and normals into three VBOs.
	private void loadVBOs() {
		GL4 gl = (GL4) GLContext.getCurrentGL();

		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);

		for (ObjShape shape : shapes) {
			gl.glGenBuffers(3, vbo, 0);

			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
			FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(shape.getVertices());
			gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit() * 4, vertBuf, GL_STATIC_DRAW);

			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
			FloatBuffer texBuf = Buffers.newDirectFloatBuffer(shape.getTexCoords());
			gl.glBufferData(GL_ARRAY_BUFFER, texBuf.limit() * 4, texBuf, GL_STATIC_DRAW);

			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[2]);
			FloatBuffer norBuf = Buffers.newDirectFloatBuffer(shape.getNormals());
			gl.glBufferData(GL_ARRAY_BUFFER, norBuf.limit() * 4, norBuf, GL_STATIC_DRAW);

			shape.setVertexBuffer(vbo[0]);
			shape.setTexCoordBuffer(vbo[1]);
			shape.setNormalBuffer(vbo[2]);

			if (shape instanceof AnimatedShape) {
				gl.glGenBuffers(2, vbo, 0);

				gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
				FloatBuffer boneBuf = Buffers.newDirectFloatBuffer(shape.getBoneWeights());
				gl.glBufferData(GL_ARRAY_BUFFER, boneBuf.limit() * 4, boneBuf, GL_STATIC_DRAW);

				gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
				FloatBuffer binBuf = Buffers.newDirectFloatBuffer(shape.getBoneIndices());
				gl.glBufferData(GL_ARRAY_BUFFER, binBuf.limit() * 4, binBuf, GL_STATIC_DRAW);

				shape.setBoneWeightBuffer(vbo[0]);
				shape.setBoneIndicesBuffer(vbo[1]);
			}

			if (shape instanceof ImportedModel) {
				gl.glGenBuffers(1, vbo, 0);

				gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
				FloatBuffer tangentBuf = Buffers.newDirectFloatBuffer(shape.getTangents());
				gl.glBufferData(GL_ARRAY_BUFFER, tangentBuf.limit() * 4, tangentBuf, GL_STATIC_DRAW);

				shape.setTangentBuffer(vbo[0]);
			}
		}

		// load skybox into vbo
		gl.glGenBuffers(1, vbo, 0);
		GameObject go = (engine.getSceneGraph()).getSkyBoxObject();
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer vertBuf = Buffers.newDirectFloatBuffer(go.getShape().getVertices());
		gl.glBufferData(GL_ARRAY_BUFFER, vertBuf.limit() * 4, vertBuf, GL_STATIC_DRAW);
		go.getShape().setVertexBuffer(vbo[0]);
	}

	// ------------------ TEXTURE SECTION ---------------------

	protected void addTexture(TextureImage t) {
		textures.add(t);
	}

	private void loadTexturesIntoOpenGL() {
		int thisTexture;
		for (int i = 0; i < textures.size(); i++) {
			TextureImage t = textures.get(i);
			if (!t.getWantsCache()) {
				thisTexture = Utils.loadTexture(t.getTextureFile());
			} else {
				TextureData dataOut = new TextureData();
				thisTexture = Utils.loadTextureAWT(t.getTextureFile(), dataOut);
				t.setTextureData(dataOut);
			}
			t.setTexture(thisTexture);
		}
		engine.getSceneGraph().setActiveSkyBoxTexture(defaultSkyBox);
	}

	/** get height map height at the specified texture coordinate (x,z). */
	public float getHeightAt(int texture, float x, float z) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glUseProgram(heightProgram);
		gl.glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, buffer[0]);

		xLoc = gl.glGetUniformLocation(heightProgram, "x");
		zLoc = gl.glGetUniformLocation(heightProgram, "z");
		gl.glUniform1f(xLoc, x);
		gl.glUniform1f(zLoc, z);

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, texture);

		gl.glDispatchCompute(1, 1, 1);
		gl.glFinish();

		gl.glBindBuffer(GL_SHADER_STORAGE_BUFFER, buffer[0]);
		FloatBuffer resBuf = Buffers.newDirectFloatBuffer(res.length);
		gl.glGetBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, resBuf.limit() * 4, resBuf);

		float res = resBuf.get();
		return res;
	}

	private FloatBuffer vals = Buffers.newDirectFloatBuffer(16);

	public float[] applyHeightMapToMesh(float[] meshData, int vertCount, float height, int heightTex,
			Matrix4f scaleMat) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glBindBuffer(GL_SHADER_STORAGE_BUFFER, buffer[1]);
		FloatBuffer dataBuf = Buffers.newDirectFloatBuffer(meshData);
		gl.glBufferData(GL_SHADER_STORAGE_BUFFER, dataBuf.limit() * 4, dataBuf, GL_STATIC_DRAW);

		gl.glBindBuffer(GL_SHADER_STORAGE_BUFFER, buffer[2]);
		FloatBuffer resBuf = Buffers.newDirectFloatBuffer(vertCount * 4);
		gl.glBufferData(GL_SHADER_STORAGE_BUFFER, resBuf.limit() * 4, null, GL_STATIC_READ);

		gl.glUseProgram(heightMeshProgram);
		gl.glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 0, buffer[1]);
		gl.glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 1, buffer[2]);

		int heightLoc = gl.glGetUniformLocation(heightMeshProgram, "height");
		gl.glUniform1f(heightLoc, height);
		int scaleLoc = gl.glGetUniformLocation(heightMeshProgram, "s_matrix");
		gl.glUniformMatrix4fv(scaleLoc, 1, false, scaleMat.get(vals));

		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, heightTex);

		gl.glDispatchCompute(vertCount, 1, 1);
		gl.glFinish();

		gl.glBindBuffer(GL_SHADER_STORAGE_BUFFER, buffer[2]);
		FloatBuffer resBuf2 = Buffers.newDirectFloatBuffer(vertCount * 4);
		gl.glGetBufferSubData(GL_SHADER_STORAGE_BUFFER, 0, resBuf2.limit() * 4, resBuf2);

		float result[] = new float[vertCount * 3];
		for (int i = 0; i < vertCount; i++) {
			result[i * 3 + 0] = resBuf2.get();
			result[i * 3 + 1] = resBuf2.get();
			result[i * 3 + 2] = resBuf2.get();
			// System.out.println(result[i*3+0] + " " + result[i*3+1] + " " +
			// result[i*3+2]);
			resBuf2.get();// burn 4th component of vec4
		}
		return result;
	}

	// ------------------ SHADERS SECTION ---------------------

	/**
	 * create a shader program with the specified vertex and fragment shaders.
	 * Returns the custom program's index.
	 */
	public int AddShaderProgram(String vS, String fS) {
		int newProgram = Utils.createShaderProgram(vS, fS);
		customPrograms.add(newProgram);
		return customPrograms.size() - 1;
	}

	public boolean HasWindowFocus() {
		return isFocused;
	}

	// ---------------------------------------------------------
	/** for engine use, called by JOGL. */
	public void dispose(GLAutoDrawable drawable) {

	}

	/** for engine use, called by JOGL. */
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		pMat.setPerspective((float) Math.toRadians(fov), aspect, nearClip, farClip);

		engine.getGame().resized();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	public void exit() {
		animator.stop();
		dispose();
		System.exit(0);
	}
}