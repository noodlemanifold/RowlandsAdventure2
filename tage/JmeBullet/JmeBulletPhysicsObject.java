package tage.JmeBullet;

import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Math;
import org.joml.Vector4f;

import com.jme3.math.Vector3f;

import rowlandsAdventure2.builders.CommonResources;
import rowlandsAdventure2.character.CharacterSurface;
import rowlandsAdventure2.character.SurfaceInfo;
import rowlandsAdventure2.planets.Planet;
import tage.GameObject;
import tage.Time;
import tage.Utils;
import tage.audio.Sound;
import tage.audio.SoundType;

import com.jme3.math.Matrix4f;
import com.jme3.math.Transform;
import com.jme3.bullet.collision.PersistentManifolds;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;

/** If using TAGE, physics objects should be created using the methods in the TAGE Scenegraph class. */

public abstract class JmeBulletPhysicsObject implements SurfaceInfo{

	public static HashMap<Long, JmeBulletPhysicsObject> lookUpObject = new HashMap<>();
	public static JmeBulletPhysicsObject getJmeBulletPhysicsObject(Long r) { return lookUpObject.get(r); }

    private int uid;
    private float mass;
    protected Transform transform;
    private CollisionShape shape;
    private PhysicsRigidBody body;
    private boolean isDynamic;
    private ArrayList<Long> manifoldList;
    private Planet planet;
    private float planetSetTime = 0f;
    private boolean customPlanetPhys = false;

    private Sound slideSound;
    private Sound hitSound;

/** If using TAGE, physics objects should be created using the methods in the TAGE Scenegraph class, rather than this constructor. 
*/
    public JmeBulletPhysicsObject(int uid, float mass, float[] xform, CollisionShape shape, int group, int mask)
    {
        this.uid = uid;
        this.mass = mass;
        this.transform = new Transform();
        Matrix4f mat = new Matrix4f();
        JmeBulletUtils.setFromOpenGL(mat, xform);
        transform.fromTransformMatrix(mat);
        transform.setScale(new Vector3f(1f,1f,1f));//fix for floating point precision scaling
        this.isDynamic = (mass != 0f);
        this.shape = shape;

        body = new PhysicsRigidBody(shape, mass);
        body.setPhysicsTransform(transform);
        body.setCollisionGroup(group);
        body.setCollideWithGroups(mask);
        
        //set reasonable defaults
        //body.setSleepingThresholds(0.05f, 0.05f); //fix for objects stopping too soon
        //body.setDamping(0.1f, 0.1f);

        if (mass > 0){//dynamic
            slideSound = new Sound(CommonResources.slideResource, SoundType.SOUND_EFFECT, 100, true);
            slideSound.initialize(CommonResources.audioMgr);
            slideSound.setMaxDistance(50.0f);
            slideSound.setMinDistance(1f);
            slideSound.setRollOff(5.0f);
            slideSound.setVolume(0);
            slideSound.play();

            hitSound = new Sound(CommonResources.hitResource, SoundType.SOUND_EFFECT, 100, false);
            hitSound.initialize(CommonResources.audioMgr);
            hitSound.setMaxDistance(50.0f);
            hitSound.setMinDistance(0.5f);
            hitSound.setRollOff(5.0f);

            body.setCcdMotionThreshold(1f);
            body.setCcdSweptSphereRadius(0.4f);

        }

        manifoldList = new ArrayList<Long>();

	    JmeBulletPhysicsObject.lookUpObject.put(body.nativeId(),this);
    }

    public int getUID() {
        return uid;
    }

    public void setTransform(float[] xform) {
        synchronized(this)
        {
            Matrix4f trans = new Matrix4f();
            JmeBulletUtils.setFromOpenGL(trans, xform);
            transform.toTransformMatrix(trans);
            this.body.setPhysicsTransform(transform);
        }
    }

    public float[] getTransform() {
        synchronized(this)
        {
            float[] new_xform = new float[16];
            //this.transform.getOpenGLMatrix(new_xform);
            this.body.getTransform(transform);
            new_xform = JmeBulletUtils.getOpenGLMatrix(transform.toTransformMatrix(), new_xform);
            return new_xform;
        }
    }
    public float getMass()
    {
        return this.mass;
    }
    public void setMass(float mass)
    {
        this.mass = mass;
        this.isDynamic = mass != 0;
    }
    public PhysicsRigidBody getRigidBody()
    {
        return this.body;
    }
    public boolean isDynamic()
    {
        return isDynamic;
    }
    public float getFriction()
    {
        return this.body.getFriction();
    }
    public void setFriction(float friction)
    {
        this.body.setFriction(friction);
    }
    /**
     * Returns the restitution coefficient
     * 
     * @return the bounciness (restitution coefficient) of this object
     */
    public float getBounciness()
    {
        return this.body.getRestitution();
    }
    /**
     * Set the bounciness (restitution) coefficient. The value should be kept
     * between 0 and 1. Anything above 1 will make bouncing objects bounce further
     * on each bounce. The true bouncieness value of a collision between any two
     * objects in the physics world is the muplication of the two object's bounciness
     * coefficient.
     *
     * @param bounciness
     */
    public void setBounciness(float bounciness)
    {
        this.body.setRestitution(bounciness);
    }
    public float[] getLinearVelocity()
    {
        
        Vector3f out = new Vector3f();
        this.body.getLinearVelocity(out);
        float[] velocity = {out.x, out.y, out.z};
        return velocity;
    }
    public void setLinearVelocity(float[] velocity)
    {
        this.body.setLinearVelocity(new Vector3f(velocity[0],velocity[1],velocity[2]));
    }
    public float[] getAngularVelocity()
    {

        Vector3f out = new Vector3f();
        this.body.getAngularVelocity(out);
        float[] velocity = {out.x, out.y, out.z};
        return velocity;
    }
    public void setAngularVelocity(float[] velocity)
    {
        this.body.setAngularVelocity(new Vector3f(velocity[0],velocity[1],velocity[2]));
    }
    
	public void setSleepThresholds(float linearThreshold, float angularThreshold) 
    {
    	body.setSleepingThresholds(linearThreshold, angularThreshold);
	}

	public float getLinearSleepThreshold() 
	{
		return body.getLinearSleepingThreshold();
	}

	public float getAngularSleepThreshold() 
	{
		return body.getAngularSleepingThreshold();
	}

	public void setDamping(float linearDamping, float angularDamping) 
	{
		body.setDamping(linearDamping, angularDamping);
	}

	public float getLinearDamping() 
	{
		return body.getLinearDamping();
	}

	public float getAngularDamping() 
	{
		return body.getAngularDamping();
	}
	
	public void applyForce(float fx, float fy, float fz, float px, float py, float pz){
        if (!body.isActive()){
            body.activate();
        }
		body.applyForce(new Vector3f(fx, fy, fz), new Vector3f(px, py, pz));
	}
	
	public void applyTorque(float fx, float fy, float fz){
        if (!body.isActive()){
            body.activate();
        }
		body.applyTorque(new Vector3f(fx, fy, fz));
	}

    public void applyImpulse(float fx, float fy, float fz, float px, float py, float pz){
        if (!body.isActive()){
            body.activate();
        }
        body.applyImpulse(new Vector3f(fx, fy, fz), new Vector3f(px, py, pz));
    }

    public void setAngularFactor(float f){
        body.setAngularFactor(0f);
    }

    public void setLocation(float[] location){
        body.setPhysicsLocation(new Vector3f(location[0],location[1],location[2]));
    }

    public void AddManifold(long c){
        manifoldList.add(c);
    }

    public int GetManifoldCount(){
        return manifoldList.size();
    }

    public long GetManifold(int i){
        return manifoldList.get(i);
    }

    public void ClearManifolds(){
        manifoldList.clear();
    }

    float[] worldPos = new float[3];
    public float[] getWorldLocation(){
        float[] transform = getTransform();
        worldPos[0] = transform[12];
        worldPos[1] = transform[13];
        worldPos[2] = transform[14];
        return worldPos;
    }

    public CollisionShape getShape(){
        return shape;
    }

    Vector3f vapLocation = new Vector3f();
    Vector3f vapAngularVelocity = new Vector3f();
    Vector3f vapVelocity = new Vector3f();
    Vector3f vapPoint = new Vector3f();
    public float[] GetLinearVelocityAtPoint(float[] point){
        vapPoint.set(point[0], point[1], point[2]);
        body.getPhysicsLocation(vapLocation);
        body.getAngularVelocity(vapAngularVelocity);
        body.getLinearVelocity(vapVelocity);
        vapPoint.subtract(vapLocation, vapPoint);
        vapAngularVelocity.cross(vapPoint, vapPoint);
        vapPoint.add(vapVelocity);
        float[] res = new float[]{vapPoint.x,vapPoint.y,vapPoint.z};
        return res;
    }

    //-------------Custom Planet Stuff----------------------

    private SurfaceInfo surfProvider = this;
    private CharacterSurface surfaceType = CharacterSurface.GroundSurface;
    private GameObject myGo;
    private float lastSurfaceImpulseTime = 0f;
    private final float surfaceImpulseCooldown = 0.1f;

    public void setPlanet(Planet p){
        if (Time.elapsedTime != planetSetTime){//if from a previous frame, overwrite!
            planet = p;
            p.getSurfaceDistance(getWorldLocation());
            planetSetTime = Time.elapsedTime;
        }else{//only keep closest planet this frame
            float[] pos = getWorldLocation();
            if (p.getSurfaceDistance(pos) < planet.getSurfaceDistance(pos)){
                planet = p;
            }
        }
    }

    public Planet getPlanet(){
        return planet;
    }

    public void setCustomPlanetPhys(boolean b){
        customPlanetPhys = b;
    }

    //this function ended up being responsible for a lot more than I first intended O_o
    public void applyPlanetPhysics(){
        if (!customPlanetPhys && planet != null && this.isDynamic()){
            float[] worldPos = getWorldLocation();
            float[] dir = planet.getGravDir(worldPos);
            float mag = planet.getGravStrength(worldPos);
            Vector3f gravityVector = new Vector3f();
            gravityVector.x = dir[0];
            gravityVector.y = dir[1];
            gravityVector.z = dir[2];
            Vector3f velo = new Vector3f();
            body.getLinearVelocity(velo);
            float downSpeed = gravityVector.dot(velo);
            if (downSpeed < 15f){
                gravityVector = gravityVector.mult(mag);
                body.setGravity(gravityVector);
            }else{
                body.setGravity(new Vector3f(0f,0f,0f));
            }

            org.joml.Vector3f p = new org.joml.Vector3f(worldPos);
            slideSound.setLocation(p);
            hitSound.setLocation(p);

            if (myGo != null){
                org.joml.Matrix4f mat = new org.joml.Matrix4f();
                org.joml.Matrix4f mat2 = new org.joml.Matrix4f().identity();
                org.joml.Matrix4f mat3 = new org.joml.Matrix4f().identity();
                org.joml.AxisAngle4f aa = new org.joml.AxisAngle4f();

                // set translation
                mat.set(getTransform());
                mat2.set(3, 0, mat.m30());
                mat2.set(3, 1, mat.m31());
                mat2.set(3, 2, mat.m32());
                myGo.setLocalTranslation(mat2);

                // set rotation
                mat.getRotation(aa);
                mat3.rotation(aa);
                myGo.setLocalRotation(mat3);

                float[] lightDir = planet.getLightDir(worldPos);
                myGo.getRenderStates().setShadowDirection(new Vector4f(lightDir[0],lightDir[1],lightDir[2],0f));
                float pl = planet.getLight(worldPos);
                myGo.getRenderStates().setPlanetLight(pl);
                float sd = planet.getShadowDistance();
                myGo.getRenderStates().setShadowDistance(sd);
            }

            //lower friction when on ice or dirt
            float friction = 0.5f;
            body.setFriction(0f);
            for (long manifold : manifoldList){
                long collId = PersistentManifolds.getBodyAId(manifold);
                if (collId == body.nativeId()){
                    collId = PersistentManifolds.getBodyBId(manifold);
                }
                JmeBulletPhysicsObject collPo = JmeBulletPhysicsObject.getJmeBulletPhysicsObject(collId);
                if (collPo != null){
                    CharacterSurface surf = collPo.getSurfaceImpl(worldPos);
                    if (surf.equals(CharacterSurface.IceSurface)){
                        body.setFriction(Math.max(body.getFriction(), friction*0.2f));
                    }else if (surf.equals(CharacterSurface.DirtSurface)){
                        body.setFriction(Math.max(body.getFriction(), friction*0.8f));
                    }else{
                        body.setFriction(friction);
                    }
                    float[] impulse = collPo.getSurfaceImpulseImpl();
                    Vector3f impulsesv = new Vector3f(impulse[0],impulse[1],impulse[2]);
                    float it = Time.elapsedTime - lastSurfaceImpulseTime;
                    if (it > surfaceImpulseCooldown && impulsesv.lengthSquared() > 0.00001f){
                        lastSurfaceImpulseTime = Time.elapsedTime;
                        Vector3f veloCurrent = new Vector3f();
                        body.getLinearVelocity(veloCurrent);
                        if (impulse[3] == 1) {
                            veloCurrent.set(0f,0f,0f);
                        }else{
                            //look at this super elegant code
                            org.joml.Vector3f siN = new org.joml.Vector3f(impulsesv.x,impulsesv.y,impulsesv.z);
                            org.joml.Vector3f veloJoml = new org.joml.Vector3f(veloCurrent.x,veloCurrent.y,veloCurrent.z);
                            siN.normalize();
                            Utils.projectOntoPlane(veloJoml, siN, siN);
                            veloCurrent = new Vector3f(veloJoml.x, veloJoml.y, veloJoml.z);
                        }
                        veloCurrent = veloCurrent.add(impulsesv);
                        body.setLinearVelocity(veloCurrent);
                    }
                }
            }
            if (body.getFriction() == 0f){
                body.setFriction(friction);
            }

            if (manifoldList.size() > 0){
                int vol = Math.round(Math.clamp(0, 1, velo.length()/6f)*100);
                slideSound.setVolume(vol);
            }else{
                slideSound.setVolume(0);
            }
        }
    }

    public void setGameObject(GameObject go){
        myGo = go;
    }

    public static void updateObjects(){
        lookUpObject.forEach((id, object) -> {
            object.body.clearForces();
            object.applyPlanetPhysics();
        }); 
    }

    public void setSurface(CharacterSurface surf){
        surfaceType = surf;
    }

    public void setSurfaceProvider(SurfaceInfo si){
        surfProvider = si;
    }

    @Override
    public CharacterSurface getSurface(float[] pos){
        return surfaceType;
    }

    @Override
    public float[] getSurfaceSpeed(float[] pos){
        return GetLinearVelocityAtPoint(pos);
    }

    @Override
    public float[] getSurfaceImpulse(){
        return new float[]{0f,0f,0f,0f};
    }


    public CharacterSurface getSurfaceImpl(float[] pos){
        return surfProvider.getSurface(pos);
    }

    public float[] getSurfaceSpeedImpl(float[] pos){
        return surfProvider.getSurfaceSpeed(pos);
    }

    public float[] getSurfaceImpulseImpl(){
        return surfProvider.getSurfaceImpulse();
    }
}
