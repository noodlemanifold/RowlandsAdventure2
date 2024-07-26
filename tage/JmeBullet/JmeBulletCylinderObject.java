package tage.JmeBullet;

import com.jme3.bullet.collision.shapes.CylinderCollisionShape;

import com.jme3.math.Vector3f;

/** If using TAGE, physics objects should be created using the methods in the TAGE Scenegraph class. */

public class JmeBulletCylinderObject extends JmeBulletPhysicsObject {
	
    private float[] halfExtents;

    public JmeBulletCylinderObject(int uid, float mass, float[] transform, float[] halfExtents, int group, int mask) {

        super(uid, mass, transform, new CylinderCollisionShape(new Vector3f(halfExtents[0],halfExtents[1],halfExtents[2]),1), group, mask);
        this.halfExtents = halfExtents;
    }

    public float[] halfExtents(){
        return halfExtents;
    }

}