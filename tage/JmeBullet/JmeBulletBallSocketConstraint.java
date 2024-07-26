package tage.JmeBullet;

import com.jme3.math.Vector3f;

import com.jme3.bullet.joints.Point2PointJoint;
import com.jme3.bullet.objects.PhysicsRigidBody;

/**
 * Defines JBullet ball socket constraint
 * @author Oscar Solorzano
 *
 */

public class JmeBulletBallSocketConstraint extends JmeBulletConstraint{
	private Point2PointJoint p2pConstraint;
	public JmeBulletBallSocketConstraint(int uid, JmeBulletPhysicsObject bodyA, JmeBulletPhysicsObject bodyB) {
		super(uid, bodyA, bodyB);
		PhysicsRigidBody rbA = bodyA.getRigidBody();
		PhysicsRigidBody rbB = bodyB.getRigidBody();
		float[] pivotInA = new float[]{0, 0, 0};
		float[] pivotInB = new float[]{(float) (bodyA.getTransform()[12]-bodyB.getTransform()[12]),(float) (bodyA.getTransform()[13]-bodyB.getTransform()[13]),(float) (bodyA.getTransform()[14]-bodyB.getTransform()[14])};
		p2pConstraint = new Point2PointJoint(rbA, rbB, new Vector3f(pivotInA[0],pivotInA[1],pivotInA[2]), new Vector3f(pivotInB[0],pivotInB[1],pivotInB[2]));
		rbA.addJoint(p2pConstraint);
		rbB.addJoint(p2pConstraint);
	}
	/**
	 * Returns the JBullet specific ball socket constraint
	 * @return The ball socket constraint as a JBullet Point2PointConstraint
	 */
	public Point2PointJoint getConstraint(){
		return p2pConstraint;
	}
}
