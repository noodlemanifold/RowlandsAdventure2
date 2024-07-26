package tage.JmeBullet;

import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;

/** If using TAGE, physics objects should be created using the methods in the TAGE Scenegraph class. */

public class JmeBulletStaticPlaneObject extends JmeBulletPhysicsObject {
	
    //private float[] up_vector;
    //private float plane_constant;

    public JmeBulletStaticPlaneObject(int uid, float[] transform, float[] up_vector, float plane_constant, int group, int mask) {

        super(uid, 0, transform, new PlaneCollisionShape(new Plane(new Vector3f(up_vector[0],up_vector[1],up_vector[2]), plane_constant)), group, mask);
        //this.up_vector = up_vector;
        //this.plane_constant = plane_constant;
    }

}
