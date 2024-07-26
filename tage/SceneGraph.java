package tage;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Vector;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.jme3.bullet.collision.shapes.infos.IndexedMesh;

import tage.JmeBullet.JmeBulletPhysicsEngine;
import tage.JmeBullet.JmeBulletPhysicsObject;
import tage.shapes.Cube;
import tage.shapes.ImportedModel;
import tage.shapes.SkyBoxShape;
import tage.shapes.Sphere;

/**
* Tools for building a scene graph tree, and building and applying the associated node controllers.
* A game application should use the tools here for adding game objects, lights, and node controllers.
* The game objects and node controllers are stored in ArrayLists.
* The renderer also uses methods here for applying node controllers at each frame.
* <p>
* The functions here that are useful for the game application are:
* <ul>
* <li> addLight()
* <li> addNodeController()
* <li> getRoot()
* <li> loadCubeMap()
* <li> removeGameObject()
* <li> getNumGameObjects()
* <li> addPhysicsXXX() - where XXX is the desired physics object shape
* <li> removePhysicsObject()
* </ul>
* <p>
* It is important to understand that adding a game object doesn't require calling addGameObject().
* That function is called by the engine.  All that is necessary is to use one of the GameObject constructors.
* They will call addGameObject().  Similarly it isn't necessary to call buildSkyBox(), that happens automatically.
* However, adding a Light or a Node Controller does necessitate calling addLight() and addNodeController().
* <p>
* Loading a skybox texture into an OpenGL CubeMap using loadCubeMap() is done as follows.
* The game application needs to supply the name of a folder containing the 6 textures.
* The folder is assumed to be in the assets/skyboxes folder.
* The resulting OpenGL CubeMap is referenced by the returned integer.
* The game application should store that integer to refer to the cubemap if it wishes to swap
* between multiple cubemaps.  That can be done by calling setActiveSkyBoxTexture() with the integer
* skybox reference provided as a parameter. All cubemaps should be loaded before starting the game loop.
* @author Scott Gordon
*/

public class SceneGraph
{	private static GameObject root;
	private ArrayList<GameObject> gameObjects = new ArrayList<GameObject>();
	private ArrayList<NodeController> nodeControllers = new ArrayList<NodeController>();
	private Vector<GameObject> physicsRenderables = new Vector<GameObject>();
	private Engine engine;

	private RenderSystem rs;
	private JmeBulletPhysicsEngine pe;
	private JmeBulletPhysicsObject po;
	private GameObject go;
	private NodeController nci;
	private GameObject skybox;
	private boolean skyboxEnabled = false;
	private int activeSkyBoxTexture;
	
	private GameObject physicsRoot;
	private ObjShape physicsBox, physicsSphere, physicsCylinder, physicsCone, physicsCapsule, physicsPlane;
	private float halfExtents[] = new float[3];

	protected SceneGraph(Engine e)
	{	engine = e;
		rs = e.getRenderSystem();
		root = GameObject.createRoot();
		preparePhysicsDisplayObjects();
	}

	// -------------- LIGHT SECTION ------------------------

	/** adds the specified Light object to the LightManager for rendering. */
	public void addLight(Light light) { (engine.getLightManager()).addLight(light); }

	// -------------- NODE CONTROLLER SECTION -------------------

	/** adds the specified node controller for use in the game. */
	public void addNodeController(NodeController nc) { nodeControllers.add(nc); }

	// Apply the node controllers to their attached objects - for engine use only.
	// Called by RenderSystem, should not be called by the game application directly.

	protected void applyNodeControllers()
	{	for (int i = 0; i < nodeControllers.size(); i++)
		{	nci = nodeControllers.get(i);
			if (nci.isEnabled()) nci.applyController();
		}
	}

	// -------------- GAME OBJECT SECTION ---------------------

	/** returns the current number of GameObjects. */
	public int getNumGameObjects() { return gameObjects.size(); }

	/** returns a reference to the entire ArrayList of GameObjects - not likely to be useful to the game application. */
	public ArrayList<GameObject> getGameObjects() { return gameObjects; }

	protected GameObject getGameObject(int i) { return gameObjects.get(i); }
	protected GameObject getRoot() { return root; }
	protected void updateAllObjectTransforms() { root.update(); }

	/** removes the specified GameObject from the scenegraph. */
	public void removeGameObject(GameObject go)
	{	if (go.hasChildren())
		{	System.out.println("attempted deletion of game object with children");
		}
		else
		{	// first delete any NodeController references to the game object
			for (int i = 0; i < nodeControllers.size(); i++)
			{	nci = nodeControllers.get(i);
				if (nci.hasTarget(go)) nci.removeTarget(go);
			}
			// then remove the object, also removing the parent reference
			if (go.getParent() != null) (go.getParent()).removeChild(go);
			if (gameObjects.contains(go)) gameObjects.remove(go);
		}
	}

	protected void addGameObject(GameObject g) { gameObjects.add(g); }

	//------------- SKYBOX SECTION ---------------------

	/** loads a set of six skybox images into an OpenGL cubemap so that it can be used in an OpenGL skybox. */
	public int loadCubeMap(String foldername)
	{	int skyboxTexture = Utils.loadCubeMap("assets/skyboxes/"+foldername);
		return skyboxTexture;
	}

	/** returns a boolean that is true if skybox rendering has been enabled */
	public boolean isSkyboxEnabled() { return skyboxEnabled; }

	/** sets whether or not to render a skybox */
	public void setSkyBoxEnabled(boolean sbe) { skyboxEnabled = sbe; }

	/** specifies which loaded skybox should be rendered */
	public void setActiveSkyBoxTexture(int tex) { activeSkyBoxTexture = tex; }

	/** returns an integer reference to the current active skybox */
	public int getActiveSkyBoxTexture() { return activeSkyBoxTexture; }

	protected GameObject getSkyBoxObject() { return skybox; }

	protected void buildSkyBox()
	{	skybox = new GameObject(new SkyBoxShape());
	}

	//------------------- PHYSICS SECTION -----------------------------

	protected void setPhysicsEngine(JmeBulletPhysicsEngine peng) { pe = peng; }

	/** returns the physics engine object */
	public JmeBulletPhysicsEngine getPhysicsEngine() { return pe; }
	
	// returns the GameObjects that are physics renderables - used by the renderer.
	protected Vector<GameObject> getPhysicsRenderables() { return physicsRenderables; }
	
	// prepares the physics objects and shapes for displaying the physics world if enabled
	protected void preparePhysicsDisplayObjects()
	{	physicsRoot = new GameObject();
		physicsBox = new Cube();
		physicsSphere = new Sphere(8);
		physicsCone = new ImportedModel("cone.obj", "assets/defaultAssets/");
		physicsCylinder = new ImportedModel("cylinder.obj", "assets/defaultAssets/");
		physicsCapsule = new ImportedModel("capsule.obj", "assets/defaultAssets/");
		physicsPlane = new ImportedModel("plane.obj", "assets/defaultAssets/");
	}	

	/** Adds a box physics object in the physics world, with specified mass, matrix transform, and size.
	*  <br>
	*  The transform is a 4x4 homogeneous matrix, stored in an array of type double, which should only contain translation and/or rotation.<br>
	*  The size is specified using a 3-element array of type double, with desired dimensions in X, Y, and Z.<br>
	*  Also adds a corresponding renderable object if displaying the physics world has been enabled.
	*/
	public JmeBulletPhysicsObject addPhysicsBox(float mass, float[] transform, float[] size, int group, int mask)
	{	po = pe.addBoxObject(pe.nextUID(), mass, transform, size, group, mask);
		go = new GameObject();
		go.setFirstParent(physicsRoot);
		go.setShape(physicsBox);
		go.setLocalScale((new Matrix4f()).scaling(size[0]/2f, size[1]/2f, size[2]/2f));
		go.setPhysicsObject(po);
		go.getRenderStates().setWireframe(true);
		physicsRenderables.add(go);
		return po;
	}
	
	/** Adds a sphere physics object in the physics world, with specified mass, matrix transform, and radius.
	*  <br>
	*  The transform is a 4x4 homogeneous matrix, stored in an array of type double, which should only contain translation and/or rotation.<br>
	*  The radius is specified as a float.<br>
	*  Also adds a corresponding renderable object if displaying the physics world has been enabled.
	*/
	public JmeBulletPhysicsObject addPhysicsSphere(float mass, float[] transform, float radius, int group, int mask)
	{	po = pe.addSphereObject(pe.nextUID(), mass, transform, radius, group, mask);
		go = new GameObject();
		go.setFirstParent(physicsRoot);
		go.setShape(physicsSphere);
		go.setLocalScale((new Matrix4f()).scaling(radius));
		go.setPhysicsObject(po);
		go.getRenderStates().setWireframe(true);
		physicsRenderables.add(go);
		return po;
	}

	/** Adds a cone physics object (pointing upward in +Y direction) in the physics world, with specified mass, matrix transform, and radius.
	*  <br>
	*  The transform is a 4x4 homogeneous matrix, stored in an array of type double, which should only contain translation and/or rotation.<br>
	*  The radius and height are specified as floats.<br>
	*  Also adds a corresponding renderable object if displaying the physics world has been enabled.
	*/
	public JmeBulletPhysicsObject addPhysicsCone(float mass, float[] transform, float radius, float height, int group, int mask)
	{	po = pe.addConeObject(pe.nextUID(), mass, transform, radius, height, group, mask);
		go = new GameObject();
		go.setFirstParent(physicsRoot);
		go.setShape(physicsCone);
		go.setLocalScale((new Matrix4f()).scaling(radius, height/2f, radius));
		go.setPhysicsObject(po);
		go.getRenderStates().setWireframe(true);
		physicsRenderables.add(go);
		return po;
	}
	
	/** Adds a cone physics object (pointing right in +X direction) in the physics world, with specified mass, matrix transform, and radius.
	*  <br>
	*  The transform is a 4x4 homogeneous matrix, stored in an array of type double, which should only contain translation and/or rotation.<br>
	*  The radius and height are specified as floats.<br>
	*  Also adds a corresponding renderable object if displaying the physics world has been enabled.
	*/
	public JmeBulletPhysicsObject addPhysicsConeX(float mass, float[] transform, float radius, float height, int group, int mask)
	{	po = pe.addConeXObject(pe.nextUID(), mass, transform, radius, height, group, mask);
		go = new GameObject();
		go.setFirstParent(physicsRoot);
		go.setShape(physicsCone);
		go.setLocalScale((new Matrix4f()).scaling(radius, height/2f, radius));
		go.getRenderStates().setModelOrientationCorrection(
		   (new Matrix4f()).rotationZ((float)java.lang.Math.toRadians(270.0f)));
		go.setPhysicsObject(po);
		go.getRenderStates().setWireframe(true);
		physicsRenderables.add(go);
		return po;
	}
	
	/** Adds a cone physics object (pointing forward in +Z direction) in the physics world, with specified mass, matrix transform, and radius.
	*  <br>
	*  The transform is a 4x4 homogeneous matrix, stored in an array of type double, which should only contain translation and/or rotation.<br>
	*  The radius and height are specified as floats.<br>
	*  Also adds a corresponding renderable object if displaying the physics world has been enabled.
	*/
	public JmeBulletPhysicsObject addPhysicsConeZ(float mass, float[] transform, float radius, float height, int group, int mask)
	{	po = pe.addConeZObject(pe.nextUID(), mass, transform, radius, height, group, mask);
		go = new GameObject();
		go.setFirstParent(physicsRoot);
		go.setShape(physicsCone);
		go.setLocalScale((new Matrix4f()).scaling(radius, height/2f, radius));
		go.getRenderStates().setModelOrientationCorrection(
		   (new Matrix4f()).rotationX((float)java.lang.Math.toRadians(90.0f)));
		go.setPhysicsObject(po);
		go.getRenderStates().setWireframe(true);
		physicsRenderables.add(go);
		return po;
	}

	/** Adds a capsule physics object (oriented vertically on Y axis) in the physics world, with specified mass, matrix transform, radius, and height.
	*  <br>
	*  A capsule has cylindrical sides, but with rounded semi-spherical ends.<br>
	*  The dimensions of a capsule are specified with radius and height, where radius is for the semi-spheres on each end, and height is the connecting cylinder.<br>
	*  The total length of the capsule is height + 2 * radius.<br>
	*  The transform is a 4x4 homogeneous matrix, stored in an array of type double, which should only contain translation and/or rotation.<br>
	*  The radius and height are specified as floats.<br>
	*  Also adds a corresponding renderable object if displaying the physics world has been enabled.
	*/
	public JmeBulletPhysicsObject addPhysicsCapsule(float mass, float[] transform, float radius, float height, int group, int mask)
	{	po = pe.addCapsuleObject(pe.nextUID(), mass, transform, radius, height, group, mask);
		go = new GameObject();
		go.setFirstParent(physicsRoot);
		go.setShape(physicsCapsule);
		go.setLocalScale((new Matrix4f()).scaling(radius, (height+radius*2f)/4f, radius));
		go.setPhysicsObject(po);
		go.getRenderStates().setWireframe(true);
		physicsRenderables.add(go);
		return po;
	}

	/** Adds a capsule physics object (oriented horizontally on X axis) in the physics world, with specified mass, matrix transform, radius, and height.
	*  <br>
	*  A capsule has cylindrical sides, but with rounded semi-spherical ends.<br>
	*  The dimensions of a capsule are specified with radius and height, where radius is for the semi-spheres on each end, and height is the connecting cylinder.<br>
	*  The total length of the capsule is height + 2 * radius.<br>
	*  The transform is a 4x4 homogeneous matrix, stored in an array of type double, which should only contain translation and/or rotation.<br>
	*  The radius and height are specified as floats.<br>
	*  Also adds a corresponding renderable object if displaying the physics world has been enabled.
	*/
	public JmeBulletPhysicsObject addPhysicsCapsuleX(float mass, float[] transform, float radius, float height, int group, int mask)
	{	po = pe.addCapsuleXObject(pe.nextUID(), mass, transform, radius, height, group, mask);
		go = new GameObject();
		go.setFirstParent(physicsRoot);
		go.setShape(physicsCapsule);
		go.setLocalScale((new Matrix4f()).scaling(radius, (height+radius*2f)/4f, radius));
		go.getRenderStates().setModelOrientationCorrection(
		   (new Matrix4f()).rotationZ((float)java.lang.Math.toRadians(270.0f)));
		go.setPhysicsObject(po);
		go.getRenderStates().setWireframe(true);
		physicsRenderables.add(go);
		return po;
	}

	/** Adds a capsule physics object (oriented horizontally on Z axis) in the physics world, with specified mass, matrix transform, radius, and height.
	*  <br>
	*  A capsule has cylindrical sides, but with rounded semi-spherical ends.<br>
	*  The dimensions of a capsule are specified with radius and height, where radius is for the semi-spheres on each end, and height is the connecting cylinder.<br>
	*  The total length of the capsule is height + 2 * radius.<br>
	*  The transform is a 4x4 homogeneous matrix, stored in an array of type double, which should only contain translation and/or rotation.<br>
	*  The radius and height are specified as floats.<br>
	*  Also adds a corresponding renderable object if displaying the physics world has been enabled.
	*/
	public JmeBulletPhysicsObject addPhysicsCapsuleZ(float mass, float[] transform, float radius, float height, int group, int mask)
	{	po = pe.addCapsuleZObject(pe.nextUID(), mass, transform, radius, height, group, mask);
		go = new GameObject();
		go.setFirstParent(physicsRoot);
		go.setShape(physicsCapsule);
		go.setLocalScale((new Matrix4f()).scaling(radius, (height+radius*2f)/4f, radius));
		go.getRenderStates().setModelOrientationCorrection(
		   (new Matrix4f()).rotationX((float)java.lang.Math.toRadians(90.0f)));
		go.setPhysicsObject(po);
		go.getRenderStates().setWireframe(true);
		physicsRenderables.add(go);
		return po;
	}

	/** Adds a cylinder physics object (oriented vertically on Y axis) in the physics world, with specified mass, matrix transform, radius, and height.
	*  <br>
	*  The dimensions of a cylinder are specified with radius and height.<br>
	*  The transform is a 4x4 homogeneous matrix, stored in an array of type double, which should only contain translation and/or rotation.<br>
	*  Also adds a corresponding renderable GameObject if displaying the physics world has been enabled.
	*/
	public JmeBulletPhysicsObject addPhysicsCylinder(float mass, float[] transform, float radius, float height, int group, int mask)
	{	halfExtents[0] = radius; halfExtents[1] = height; halfExtents[2] = radius;
		po = pe.addCylinderObject(pe.nextUID(), mass, transform, halfExtents, group, mask);
		go = new GameObject();
		go.setFirstParent(physicsRoot);
		go.setShape(physicsCylinder);
		go.setLocalScale((new Matrix4f()).scaling(radius, height, radius));
		go.setPhysicsObject(po);
		go.getRenderStates().setWireframe(true);
		physicsRenderables.add(go);
		return po;
	}

	/** Adds a cylinder physics object (oriented horizontally on X axis) in the physics world, with specified mass, matrix transform, radius, and height.
	*  <br>
	*  The dimensions of a cylinder are specified with radius and height.<br>
	*  The transform is a 4x4 homogeneous matrix, stored in an array of type double, which should only contain translation and/or rotation.<br>
	*  Also adds a corresponding renderable GameObject if displaying the physics world has been enabled.
	*/
	public JmeBulletPhysicsObject addPhysicsCylinderX(float mass, float[] transform, float radius, float height, int group, int mask)
	{	halfExtents[0] = height; halfExtents[1] = radius; halfExtents[2] = radius;
		po = pe.addCylinderXObject(pe.nextUID(), mass, transform, halfExtents, group, mask);
		go = new GameObject();
		go.setFirstParent(physicsRoot);
		go.setShape(physicsCylinder);
		go.setLocalScale((new Matrix4f()).scaling(radius, height, radius));
		go.getRenderStates().setModelOrientationCorrection(
		   (new Matrix4f()).rotationZ((float)java.lang.Math.toRadians(270.0f)));
		go.setPhysicsObject(po);
		go.getRenderStates().setWireframe(true);
		physicsRenderables.add(go);
		return po;
	}

	/** Adds a cylinder physics object (oriented horizontally on Z axis) in the physics world, with specified mass, matrix transform, radius, and height.
	*  <br>
	*  The dimensions of a cylinder are specified with radius and height.<br>
	*  The transform is a 4x4 homogeneous matrix, stored in an array of type double, which should only contain translation and/or rotation.<br>
	*  Also adds a corresponding renderable GameObject if displaying the physics world has been enabled.
	*/
	public JmeBulletPhysicsObject addPhysicsCylinderZ(float mass, float[] transform, float radius, float height, int group, int mask)
	{	halfExtents[0] = radius; halfExtents[1] = radius; halfExtents[2] = height;
		po = pe.addCylinderZObject(pe.nextUID(), mass, transform, halfExtents, group, mask);
		go = new GameObject();
		go.setFirstParent(physicsRoot);
		go.setShape(physicsCylinder);
		go.setLocalScale((new Matrix4f()).scaling(radius, height, radius));
		go.getRenderStates().setModelOrientationCorrection(
		   (new Matrix4f()).rotationX((float)java.lang.Math.toRadians(90.0f)));
		go.setPhysicsObject(po);
		go.getRenderStates().setWireframe(true);
		physicsRenderables.add(go);
		return po;
	}

	/** Adds a a static plane object in the physics world, with specified up vector and location offset.
	*  <br>
	*  The up vector, usually set to (0,1,0), defines a normal to the plane, and is opposite the pull of gravity.<br>
	*  The plane_constant, usually set to 0, is an offset (positive or negative) for moving the plane up and down along its up vector.<br>
	*  Also adds a corresponding renderable object if displaying the physics world has been enabled.<br>
	*  The renderable object is a 100x100 rectangle, althought the actual physics object is infinite.
	*/
	public JmeBulletPhysicsObject addPhysicsStaticPlane(float[] transform, float[] up_vector, float plane_constant, int group, int mask)
	{	po = pe.addStaticPlaneObject(pe.nextUID(), transform, up_vector, plane_constant, group, mask);
		go = new GameObject();
		go.setFirstParent(physicsRoot);
		go.setShape(physicsPlane);
		go.setLocalScale((new Matrix4f()).scaling(100));
		go.setPhysicsObject(po);
		go.getRenderStates().setWireframe(true);
		physicsRenderables.add(go);
		return po;
	}

	//this method assumes triangle_adjacency primitives.
	//if you want this to work with normal imported meshes, don't skip every other vertex
	//(or just email me I still have the old code)
	public JmeBulletPhysicsObject addPhysicsStaticMesh(float[] transform, Vector3f scale, ObjShape shape, TextureImage heightTex, float height, int group, int mask)
	{	
		int numVerts = shape.getNumVertices()/2;
		float[] vertices = shape.getVertices();
		float[] uvs = shape.getTexCoords();
		float[] normals = shape.getNormals();
		int blockSize = 12;
		float data[] = new float[numVerts*blockSize];
		for(int i = 0; i < numVerts; i++){
            data[i*blockSize+0] = vertices[i*3*2+0];
			data[i*blockSize+1] = vertices[i*3*2+1];
			data[i*blockSize+2] = vertices[i*3*2+2];
			data[i*blockSize+3] = 1f;
			data[i*blockSize+4] = normals[i*3*2+0];
			data[i*blockSize+5] = normals[i*3*2+1];
			data[i*blockSize+6] = normals[i*3*2+2];
			data[i*blockSize+7] = 1f;
			data[i*blockSize+8] = uvs[i*2*2+0];
			data[i*blockSize+9] = uvs[i*2*2+1];
			data[i*blockSize+10] = 1f;
			data[i*blockSize+11] = 1f;
		}
		Matrix4f scaleMat = new Matrix4f();
		scaleMat.scale(scale);
		float[] vertsRaised = rs.applyHeightMapToMesh(data, numVerts, height, heightTex.getTexture(),scaleMat);

		//https://github.com/jbullet-maven/jbullet-demos/blob/master/src/main/java/com/bulletphysics/demos/concave/ConcaveDemo.java
		FloatBuffer verts = FloatBuffer.allocate(numVerts*3);
		for (int i = 0; i < numVerts; i++){
			verts.put(vertsRaised[i*3+0]);
			verts.put(vertsRaised[i*3+1]);
			verts.put(vertsRaised[i*3+2]);
		}
		IndexedMesh mesh = new IndexedMesh(verts);
		po = pe.addStaticMeshObject(pe.nextUID(), transform, mesh, group, mask);

		go = new GameObject();
		go.setFirstParent(physicsRoot);
		go.setShape(shape);
		go.setLocalScale((new Matrix4f()).scaling(scale));
		go.setPhysicsObject(po);
		go.getRenderStates().setWireframe(true);
		if (heightTex != null){
			go.setHeightMap(heightTex);
			go.setTerrainScale(height);
		}
		physicsRenderables.add(go);
		return po;
	}
	
	/** removes the specified PhysicsObject from the physics world, and its associated renderable, if one exists. */
	public void removePhysicsObject(JmeBulletPhysicsObject po)
	{	GameObject toRemove = null;
	
		// look for it in the renderables queue
		for (int i = 0; i < physicsRenderables.size(); i++)
		{	GameObject go = physicsRenderables.get(i);
			if (go.getPhysicsObject() == po) toRemove = go;
		}
		// if it is found in the renderables queue, remove it from the root list and the renderables list
		if (toRemove != null)
		{	if (toRemove.getParent() != null) (toRemove.getParent()).removeChild(toRemove);
			physicsRenderables.remove(toRemove);
		}
		// finally, tell the physics engine to remove the physics object itself
		pe.removeObject(po.getUID());
	}
}

















