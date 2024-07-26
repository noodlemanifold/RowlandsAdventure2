package tage.JmeBullet;

import java.io.File;
import java.util.ArrayList;

import com.jme3.math.Matrix4f;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;


import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.SolverType;
import com.jme3.bullet.collision.PersistentManifolds;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.PhysicsSweepTestResult;
import com.jme3.bullet.collision.shapes.ConvexShape;
import com.jme3.bullet.collision.shapes.infos.IndexedMesh;
import com.jme3.bullet.PhysicsSpace.BroadphaseType;
import com.jme3.system.NativeLibraryLoader;

/**
 * This class provides an implementation of the PhysicsEngine interface using
 * the JBullet physics engine.
 * <br>
 * If using TAGE, this physics engine is automatically instantiated.
 * 
 * @author Russell Bolles
 * @author John Clevenger (JavaDoc)
 * @author Oscar Solorzano
 */

public class JmeBulletPhysicsEngine {


	// maximum number of objects (and allow user to shoot additional boxes)
	//private static final int MAX_PHYSICS_OBJECTS = 1024;
	private static int nextUID;

	private PhysicsSpace physicsSpace;

	// keep the collision shapes, for deletion/cleanup
	private ArrayList<JmeBulletPhysicsObject> objects;

	/**
	 * {@code #initSystem()} initializes the underlying physics engine, providing an
	 * (empty)
	 * "physics world" along with a default collision handler, collision dispatcher,
	 * and constraint
	 * solver. The default physics world extents are {-10,000 ... 10,000} in each of
	 * X, Y, and Z;
	 * object locations should be constrained to these bounds for proper physics
	 * calculations. The
	 * implementation's default gravity vector in the physics world is [0,0,0]
	 * (meaning gravity is turned off by default). Note that this means the
	 * implementation
	 * <I>does not use the DEFAULT_GRAVITY constants defined in
	 * PhysicsEngine</i>.
	 */
	public void initSystem() {

		//load physics library
		NativeLibraryLoader.loadLibbulletjme(true, new File(System.getProperty("user.dir")+"\\bullet"), "Release", "Sp");

		Vector3f worldAabbMin = new Vector3f(-10000, -10000, -10000);
		Vector3f worldAabbMax = new Vector3f(10000, 10000, 10000);

		BroadphaseType broadphaseType = BroadphaseType.AXIS_SWEEP_3;
		SolverType solverType = SolverType.SI;

		physicsSpace = new PhysicsSpace(worldAabbMin, worldAabbMax, broadphaseType);
		physicsSpace.setAccuracy(1/100f);
		physicsSpace.setMaxSubSteps(5);

		// float[] gravity_vector =
		// {PhysicsEngine.DEFAULT_GRAVITY_X,PhysicsEngine.DEFAULT_GRAVITY_Y,PhysicsEngine.DEFAULT_GRAVITY_Z};
		float[] gravity_vector = { 0, 0, 0 };
		setGravity(gravity_vector);

		objects = new ArrayList<JmeBulletPhysicsObject>(/* 50, 25 */);
	}

	/**
	 * Sets the gravity vector for the physics world to the specified [x,y,z]
	 * vector.
	 */
	public void setGravity(float[] grav) {
		physicsSpace.setGravity(new Vector3f(grav[0], grav[1], grav[2]));
	}

	/**
	 * Adds a PhysicsObject object of type Box to the physics world.
	 */
	public JmeBulletPhysicsObject addBoxObject(int uid, float mass, float[] transform, float[] size, int group,
			int mask) {
		// PhysicsEngine asks for dimensions, JBullet uses halfExtents
		float[] temp = new float[size.length];
		for (int i = 0; i < size.length; i++) {
			temp[i] = size[i] / 2f;
		}
		JmeBulletBoxObject boxObject = new JmeBulletBoxObject(uid, mass, transform, temp, group, mask);
		this.physicsSpace.addCollisionObject(boxObject.getRigidBody());
		this.objects.add(boxObject);
		return boxObject;
	}

	/**
	 * Adds a PhysicsObject object of type Sphere to the physics world.
	 */
	public JmeBulletPhysicsObject addSphereObject(int uid, float mass, float[] transform, float radius, int group,
			int mask) {
		JmeBulletSphereObject sphereObject = new JmeBulletSphereObject(uid, mass, transform, radius, group, mask);
		this.physicsSpace.addCollisionObject(sphereObject.getRigidBody());
		this.objects.add(sphereObject);
		return sphereObject;
	}

	/**
	 * Add a Cone object to the physics worlds, with the tip at (0,0,0)
	 * 
	 * @param mass
	 * @param radius
	 * @param height
	 */
	public JmeBulletPhysicsObject addConeObject(int uid, float mass, float[] transform, float radius,
			float height, int group, int mask) {
		JmeBulletConeObject coneObject = new JmeBulletConeObject(uid, mass, transform, radius, height, group, mask);
		this.physicsSpace.addCollisionObject(coneObject.getRigidBody());
		this.objects.add(coneObject);
		return coneObject;
	}

	public JmeBulletPhysicsObject addConeXObject(int uid, float mass, float[] transform, float radius,
			float height, int group, int mask) {
		JmeBulletConeXObject coneObject = new JmeBulletConeXObject(uid, mass, transform, radius, height, group, mask);
		this.physicsSpace.addCollisionObject(coneObject.getRigidBody());
		this.objects.add(coneObject);
		return coneObject;
	}

	public JmeBulletPhysicsObject addConeZObject(int uid, float mass, float[] transform, float radius,
			float height, int group, int mask) {
		JmeBulletConeZObject coneObject = new JmeBulletConeZObject(uid, mass, transform, radius, height, group, mask);
		this.physicsSpace.addCollisionObject(coneObject.getRigidBody());
		this.objects.add(coneObject);
		return coneObject;
	}

	/**
	 * Add a cylinder object to the physics simulation
	 * 
	 * @param mass
	 * @param transform
	 * @param halfExtents
	 *                    the dimensions of the height, width, and length
	 */
	public JmeBulletPhysicsObject addCylinderObject(int uid, float mass, float[] transform,
			float[] halfExtents, int group, int mask) {
		JmeBulletCylinderObject cylinderObject = new JmeBulletCylinderObject(uid, mass, transform,
				halfExtents, group, mask);
		this.physicsSpace.addCollisionObject(cylinderObject.getRigidBody());
		this.objects.add(cylinderObject);
		return cylinderObject;
	}

	/**
	 * Add a cylinder object to the physics simulation
	 * 
	 * @param mass
	 * @param transform
	 * @param halfExtents
	 *                    the dimensions of the height, width, and length
	 */
	public JmeBulletPhysicsObject addCylinderXObject(int uid, float mass, float[] transform,
			float[] halfExtents, int group, int mask) {
		JmeBulletCylinderXObject cylinderObject = new JmeBulletCylinderXObject(uid, mass, transform,
				halfExtents, group, mask);
		this.physicsSpace.addCollisionObject(cylinderObject.getRigidBody());
		this.objects.add(cylinderObject);
		return cylinderObject;
	}

	/**
	 * Add a cylinder object to the physics simulation
	 * 
	 * @param mass
	 * @param transform
	 * @param halfExtents
	 *                    the dimensions of the height, width, and length
	 */
	public JmeBulletPhysicsObject addCylinderZObject(int uid, float mass, float[] transform,
			float[] halfExtents, int group, int mask) {
		JmeBulletCylinderZObject cylinderObject = new JmeBulletCylinderZObject(uid, mass, transform,
				halfExtents, group, mask);
		this.physicsSpace.addCollisionObject(cylinderObject.getRigidBody());
		this.objects.add(cylinderObject);
		return cylinderObject;
	}

	/**
	 * Add a capsule object to the physics simulation
	 * 
	 * @param mass
	 * @param transform
	 * @param radius
	 * @param height
	 */
	public JmeBulletPhysicsObject addCapsuleObject(int uid, float mass, float[] transform, float radius,
			float height, int group, int mask) {
		JmeBulletCapsuleObject cylinderObject = new JmeBulletCapsuleObject(uid, mass, transform,
				radius, height, group, mask);
		this.physicsSpace.addCollisionObject(cylinderObject.getRigidBody());
		this.objects.add(cylinderObject);
		return cylinderObject;

	}

	/**
	 * Add a capsule object to the physics simulation
	 * 
	 * @param mass
	 * @param transform
	 * @param radius
	 * @param height
	 */
	public JmeBulletPhysicsObject addCapsuleXObject(int uid, float mass, float[] transform, float radius,
			float height, int group, int mask) {
		JmeBulletCapsuleXObject cylinderObject = new JmeBulletCapsuleXObject(uid, mass, transform,
				radius, height, group, mask);
		this.physicsSpace.addCollisionObject(cylinderObject.getRigidBody());
		this.objects.add(cylinderObject);
		return cylinderObject;

	}

	/**
	 * Add a capsule object to the physics simulation
	 * 
	 * @param mass
	 * @param transform
	 * @param radius
	 * @param height
	 */
	public JmeBulletPhysicsObject addCapsuleZObject(int uid, float mass, float[] transform, float radius,
			float height, int group, int mask) {
		JmeBulletCapsuleZObject cylinderObject = new JmeBulletCapsuleZObject(uid, mass, transform,
				radius, height, group, mask);
		this.physicsSpace.addCollisionObject(cylinderObject.getRigidBody());
		this.objects.add(cylinderObject);
		return cylinderObject;
	}

	public JmeBulletPhysicsObject addStaticPlaneObject(int uid, float[] transform, float[] up_vector,
			float plane_constant, int group, int mask) {
		JmeBulletStaticPlaneObject planeObject = new JmeBulletStaticPlaneObject(uid, transform,
				up_vector, plane_constant, group, mask);
		this.physicsSpace.addCollisionObject(planeObject.getRigidBody());
		this.objects.add(planeObject);
		return planeObject;
	}

	/**
	 * Convert a mesh into a collider and add it to the scene
	 * MUST BE STATIC
	 */
	public JmeBulletPhysicsObject addStaticMeshObject(int uid, float[] transform,
			IndexedMesh mesh, int group, int mask) {
		JmeBulletMeshObject meshObject = new JmeBulletMeshObject(uid, transform, mesh, group, mask);
		this.physicsSpace.addCollisionObject(meshObject.getRigidBody());
		this.objects.add(meshObject);
		return meshObject;
	}

	/**
	 * Removes the PhysicsObject with the specified UID from the physics
	 * world, if it
	 * exists in the world. This method has no effect if no object with the
	 * specified UID
	 * exists in the physics world.
	 */
	public void removeObject(int uid) {
		JmeBulletPhysicsObject target_object = null;
		for (JmeBulletPhysicsObject object : objects) {
			if (object.getUID() == uid) {
				target_object = object;
			}
		}
		if (target_object != null) {
			physicsSpace.removeCollisionObject(target_object.getRigidBody());
		}
	}

	/**
	 * Forces the physics world to advance (that is, steps the physics simulation)
	 * by the
	 * specified amount of time, given in nanoseconds.
	 */
	public void update(float seconds) {
		if (physicsSpace != null) {
			physicsSpace.update(seconds);
		}
	}

	/**
	 * Returns a unique ID used to identify physics objects.
	 */
	public int nextUID() {
		int temp = JmeBulletPhysicsEngine.nextUID;
		JmeBulletPhysicsEngine.nextUID++;
		return temp;

	}

	// manifolds actually seem to persist for a lot longer than I thought
	// so it would be more efficient to check for changes in manifolds and assign
	// them accordingly
	// instead of regenerating the list every frame
	// but alas I have 1 month to make a game from scratch and I don't want to die
	// on this hill
	/**
	 * Call once every frame to detect collisions for all objects
	 * Results are stored in an arraylist per physics object
	 */
	public void detectCollisions() {
		clearManifoldLists();
		long[] manifolds = physicsSpace.listManifoldIds();
		for (long manifold : manifolds) {
			if (PersistentManifolds.countPoints(manifold) < 1) {
				continue;// only want narrow phase
			}

			JmeBulletPhysicsObject obj0 = JmeBulletPhysicsObject.getJmeBulletPhysicsObject(PersistentManifolds.getBodyAId(manifold));
			JmeBulletPhysicsObject obj1 = JmeBulletPhysicsObject.getJmeBulletPhysicsObject(PersistentManifolds.getBodyBId(manifold));

			if (obj0 != null) {
				obj0.AddManifold(manifold);
			}
			if (obj1 != null) {
				obj1.AddManifold(manifold);
			}
		}
	}

	private void clearManifoldLists() {
		for (JmeBulletPhysicsObject obj : objects) {
			obj.ClearManifolds();
		}
	}

	/**
	 * Cast a convex collider through the scene
	 * returns all intersecting objects in a random order
	 */
	public ArrayList<PhysicsSweepTestResult> ConvexCast(ConvexShape shape, float[] startPos, float[] endPos) {
		Vector3f start = new Vector3f(startPos[0],startPos[1],startPos[2]);
		Vector3f end = new Vector3f(endPos[0],endPos[1],endPos[2]);

		Transform from = new Transform();
		Matrix4f fromMat = new Matrix4f();
		fromMat.setTranslation(start);
		from.fromTransformMatrix(fromMat);

		Transform to = new Transform();
		Matrix4f toMat = new Matrix4f();
		toMat.setTranslation(end);
		to.fromTransformMatrix(toMat);

		// callback.collisionFilterGroup = group;
		// callback.collisionFilterMask = mask;

		ArrayList<PhysicsSweepTestResult> results = new ArrayList<PhysicsSweepTestResult>();

		physicsSpace.sweepTest(shape, from, to, results,0f);

		return results;

	}

	/**
	 * Cast a ray through the scene
	 * returns all intersecting objects in nearest to furthest order
	 */
	public ArrayList<PhysicsRayTestResult> RayCast(float[] startPos, float[] endPos) {
		Vector3f start = new Vector3f(startPos[0],startPos[1],startPos[2]);
		Vector3f end = new Vector3f(endPos[0],endPos[1],endPos[2]);
		ArrayList<PhysicsRayTestResult> results = new ArrayList<PhysicsRayTestResult>();;
		physicsSpace.rayTest(start, end, results);
		return results;

	}

	public JmeBulletHingeConstraint addHingeConstraint(int uid, JmeBulletPhysicsObject bodyA, JmeBulletPhysicsObject bodyB,
			float axisX,
			float axisY, float axisZ) {
		JmeBulletHingeConstraint hingeConstraint = new JmeBulletHingeConstraint(uid, (JmeBulletPhysicsObject) bodyA,
				(JmeBulletPhysicsObject) bodyB, axisX, axisY, axisZ);
		physicsSpace.addJoint(hingeConstraint.getConstraint());
		return hingeConstraint;
	}

	public JmeBulletBallSocketConstraint addBallSocketConstraint(int uid, JmeBulletPhysicsObject bodyA,
			JmeBulletPhysicsObject bodyB) {
		JmeBulletBallSocketConstraint ballSocketConstraint = new JmeBulletBallSocketConstraint(uid,
				(JmeBulletPhysicsObject) bodyA, (JmeBulletPhysicsObject) bodyB);
		physicsSpace.addJoint(ballSocketConstraint.getConstraint());
		return ballSocketConstraint;
	}

	public PhysicsSpace getDynamicsWorld() {
		return physicsSpace;
	}

}
