package tage.JmeBullet;

import com.jme3.bullet.collision.shapes.CylinderCollisionShape;

import com.jme3.math.Vector3f;

/** If using TAGE, physics objects should be created using the methods in the TAGE Scenegraph class. */

public class JmeBulletCylinderXObject extends JmeBulletPhysicsObject {
	
    private float[] halfExtents;

    public JmeBulletCylinderXObject(int uid, float mass, float[] transform, float[] halfExtents, int group, int mask) {

        super(uid, mass, transform, new CylinderCollisionShape(new Vector3f(halfExtents[0],halfExtents[1],halfExtents[2]),0), group, mask);
        this.halfExtents = halfExtents;
    }

    public float[] halfExtents(){
        return halfExtents;
    }

}
