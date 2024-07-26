package tage.JmeBullet;

import com.jme3.math.Vector3f;

import com.jme3.bullet.joints.HingeJoint;
import com.jme3.bullet.objects.PhysicsRigidBody;

public class JmeBulletHingeConstraint extends JmeBulletConstraint{
	private HingeJoint hingeConstraint;
	private float[] axis;

	public JmeBulletHingeConstraint(int uid, JmeBulletPhysicsObject bodyA, JmeBulletPhysicsObject bodyB, float axisX, float axisY, float axisZ) {
		super(uid, bodyA, bodyB);
		PhysicsRigidBody rigidA = bodyA.getRigidBody();
		PhysicsRigidBody rigidB = bodyB.getRigidBody();
		float []pivotInA = new float[]{0, 0, 0};
		float []pivotInB = new float[]{(float) (bodyA.getTransform()[12]-bodyB.getTransform()[12]),(float) (bodyA.getTransform()[13]-bodyB.getTransform()[13]),(float) (bodyA.getTransform()[14]-bodyB.getTransform()[14])};
		axis = new float[]{axisX, axisY, axisZ};
		hingeConstraint = new HingeJoint(rigidA, rigidB, new Vector3f(pivotInA[0],pivotInA[1],pivotInA[2]), new Vector3f(pivotInB[0],pivotInB[1],pivotInB[2]), new Vector3f(axisX, axisY, axisZ), new Vector3f(axisX, axisY, axisZ));
		rigidA.addJoint(hingeConstraint);
		rigidB.addJoint(hingeConstraint);
	}
	
	/**
	 * Returns the JBullet specific hinge constraint
	 * @return The hinge constraint as a JBullet HingeConstraint
	 */
	public HingeJoint getConstraint(){
		return hingeConstraint;
	}

	public float getAngle() {
		return hingeConstraint.getHingeAngle();
	}

	public float[] getAxis() {
		return axis;
	}

}
