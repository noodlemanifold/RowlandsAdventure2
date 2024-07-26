package tage.JmeBullet;

public abstract class JmeBulletConstraint{
	private int uid;
	private JmeBulletPhysicsObject bodyA;
	private JmeBulletPhysicsObject bodyB;
	
	public JmeBulletConstraint(int uid, JmeBulletPhysicsObject bodyA, JmeBulletPhysicsObject bodyB){
		this.uid=uid;
		this.bodyA=bodyA;
		this.bodyB=bodyB;
	}

	public JmeBulletPhysicsObject getBodyA() {
		return bodyA;
	}

	public JmeBulletPhysicsObject getBodyB() {
		return bodyB;
	}

	public int getUID() {
		return uid;
	}

}
