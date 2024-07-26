package tage.JmeBullet;

import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.collision.shapes.infos.IndexedMesh;

public class JmeBulletMeshObject extends JmeBulletPhysicsObject {

    public JmeBulletMeshObject(int uid, float[] transform, IndexedMesh arr, int group, int mask) {
   
    	super(uid, 0f, transform, new MeshCollisionShape(true,arr), group, mask);
    
    }
    
}
