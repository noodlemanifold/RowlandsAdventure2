package tage.JmeBullet;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;

/** If using TAGE, physics objects should be created using the methods in the TAGE Scenegraph class. */

public class JmeBulletCapsuleObject extends JmeBulletPhysicsObject {
	private float radius;
	private float height;

    public JmeBulletCapsuleObject(int uid, float mass, float[] transform, float radius, float height, int group, int mask) {

        super(uid, mass, transform, new CapsuleCollisionShape(radius, height, 1), group, mask);
        this.radius = radius;
        this.height = height;
    }

    public float radius(){
        return radius;
    }

    public float height(){
        return height;
    }
}
