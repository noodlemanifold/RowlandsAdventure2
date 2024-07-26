package tage.JmeBullet;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.math.Vector3f;

/** If using TAGE, physics objects should be created using the methods in the TAGE Scenegraph class. */

public class JmeBulletBoxObject extends JmeBulletPhysicsObject {
    private float[] size;

    public JmeBulletBoxObject(int uid, float mass, float[] transform, float[] size, int group, int mask)
    {

        super(uid, mass, transform, new BoxCollisionShape(new Vector3f(size[0],size[1],size[2])), group, mask);
        this.size = size;
    }

    public float[] size(){
        return size;
    }

}
