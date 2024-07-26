package rowlandsAdventure2.planets;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.joml.Quaternionf;

import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;

import rowlandsAdventure2.Enums.CollisionGroups;
import rowlandsAdventure2.character.CharacterSurface;
import rowlandsAdventure2.character.SurfaceInfo;
import tage.Engine;
import tage.GameObject;
import tage.TextureImage;
import tage.JmeBullet.JmeBulletPhysicsObject;
import tage.shapes.ImportedModel;

public abstract class Planet implements SurfaceInfo {

    protected TextureImage heightMap;
    protected TextureImage surfaceMap;
    protected PhysicsGhostObject ghost;
    protected ImportedModel surfaceShape;
    protected GameObject go;
    protected JmeBulletPhysicsObject po;
    protected Engine engine;
    //protected Matrix4f transform;
    protected float shadowDistance;
    private int listIndex;

    protected CharacterSurface[] surfaceList = new CharacterSurface[] {
            CharacterSurface.GrassSurface,
            CharacterSurface.GroundSurface,
            CharacterSurface.DirtSurface,
            CharacterSurface.IceSurface };

    public Planet(Engine engine, Vector3f position, Quaternionf rotation, Vector3f scale, TextureImage heightMap, TextureImage surfaceMap,
        TextureImage surfaceRamp,CollisionShape ghostShape, ImportedModel surfaceShape, float terrainHeight) {
        this.listIndex = PlanetManager.addPlanet(this);
        this.heightMap = heightMap;
        this.surfaceMap = surfaceMap;
        this.surfaceShape = surfaceShape;
        this.engine = engine;
        //this.transform = transform;

        go = new GameObject(GameObject.root(), surfaceShape, surfaceMap);
        go.setLocalLocation(position);
        Matrix4f r = new Matrix4f();
        r.rotate(rotation);
        go.setLocalRotation(r);
        Matrix4f s = new Matrix4f();
        s.scale(scale);
        go.setLocalScale(s);
        Matrix4f posRot = new Matrix4f();
        posRot.translate(position);
        posRot.rotate(rotation);
        po = engine.getSceneGraph().addPhysicsStaticMesh(posRot.get(new float[16]), scale, surfaceShape,
                heightMap, terrainHeight, CollisionGroups.Static.value(),
                (CollisionGroups.All.value() ^ CollisionGroups.Sensor.value()));
        po.setAngularFactor(0f);
        po.setSurfaceProvider(this);
        go.setPhysicsObject(po);
        go.setHeightMap(heightMap);
        go.setTerrainScale(terrainHeight);
        go.getRenderStates().setIsPlanet(true);
        go.setTextureImage2(surfaceRamp);
        // go.getRenderStates().setColor(new Vector3f(0f, 0.9f, 0.3f));
        // go.getRenderStates().setHasSolidColor(true);

        shadowDistance = Math.min(scale.x, Math.min(scale.y,scale.z));

        ghost = new PhysicsGhostObject(ghostShape);
        ghost.setCollisionGroup(CollisionGroups.Sensor.value());
        ghost.setCollideWithGroups((CollisionGroups.All.value() ^ CollisionGroups.Static.value()));
        ghost.setPhysicsLocation(new com.jme3.math.Vector3f(position.x, position.y, position.z));
        engine.getSceneGraph().getPhysicsEngine().getDynamicsWorld().addCollisionObject(ghost);
    }

    public abstract float[] getGravDir(float[] worldPos);

    public abstract float[] getLightDir(float[] worldPos);

    public abstract float getGravStrength(float[] worldPos);

    public abstract float getSurfaceDistance(float[] worldPos);

    public float getLight(float[] worldPos){
        Vector4f ld = go.getRenderStates().getShadowDirection();
        float[] sd = getGravDir(worldPos);
        Vector3f lightDir = new Vector3f(ld.x,ld.y,ld.z);
        Vector3f surfaceDir = new Vector3f(-sd[0],-sd[1],-sd[2]);
        float angle = (float)Math.acos(lightDir.dot(surfaceDir));
        return (1-(angle/(float)Math.PI));
    }

    public float getShadowDistance(){
        return shadowDistance;
    }

    public void setLightDirection(Vector3f dir){
        go.getRenderStates().setShadowDirection(new Vector4f(dir.x,dir.y,dir.z,0f));
    }

    public void checkGhostOverlaps() {
        List<PhysicsCollisionObject> objects = ghost.getOverlappingObjects();
        for (PhysicsCollisionObject object : objects) {
            if (object instanceof PhysicsRigidBody){
                PhysicsRigidBody body = (PhysicsRigidBody) object;
                if (body != null) {
                    JmeBulletPhysicsObject po = JmeBulletPhysicsObject.getJmeBulletPhysicsObject(body.nativeId());
                    if (po != null) {
                        po.setPlanet(this);
                    }
                }
            }
        }
    }

    public void setSurfaceList(CharacterSurface[] surfs){
        surfaceList = surfs;
    }

    public Vector3f getLocation(){
        Vector3f huh = go.getLocalLocation();
        return huh;
    }

    public int getIndex(){
        return listIndex;
    }

    @Override
    public CharacterSurface getSurface(float[] pos) {
        return surfaceList[0];
    }

    @Override
    public float[] getSurfaceSpeed(float[] pos) {
        // no planetary rotation
        return new float[] { 0f, 0f, 0f };
    }

    @Override
    public float[] getSurfaceImpulse(){
        return new float[]{0f,0f,0f,0f};
    }

}