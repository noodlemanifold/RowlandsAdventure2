package tage.JmeBullet;

import com.jme3.bullet.collision.shapes.SphereCollisionShape;

/** If using TAGE, physics objects should be created using the methods in the TAGE Scenegraph class. */

public class JmeBulletSphereObject extends JmeBulletPhysicsObject {
	
    
    public JmeBulletSphereObject(int uid, float mass, float[] transform, float radius, int group, int mask) {
   
    	super(uid, mass, transform, new SphereCollisionShape(radius), group, mask);
    
    }

}
