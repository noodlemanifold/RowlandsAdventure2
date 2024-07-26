package rowlandsAdventure2.character;

import java.util.ArrayList;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.jme3.bullet.collision.ManifoldPoints;
import com.jme3.bullet.collision.PersistentManifolds;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.PhysicsSweepTestResult;
import com.jme3.bullet.collision.shapes.ConvexShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.objects.PhysicsRigidBody;

import rowlandsAdventure2.Enums.CollisionGroups;
import rowlandsAdventure2.character.CharacterAnimator.CharacterAvatar;
import rowlandsAdventure2.input.InputMapper;
import rowlandsAdventure2.input.RowlandInputs;
import rowlandsAdventure2.planets.Planet;
import tage.Camera;
import tage.Engine;
import tage.GameObject;
import tage.Light;
import tage.Light.LightType;
import tage.Time;
import tage.Utils;
import tage.JmeBullet.JmeBulletPhysicsEngine;
import tage.JmeBullet.JmeBulletPhysicsObject;
import tage.shapes.Line;

public class CharacterController {

    private Engine engine;
    private boolean debug = false;
    private boolean enabled = false;

    private JmeBulletPhysicsObject charPhys;
    private ConvexShape castShape;
    private Light light;
    private boolean lightOn = false;

    private CameraController cameraController;
    private CharacterStrafing strafer;
    private SoundController soundController;

    private CharacterAvatar avatar;
    private CharacterState currentState;
    private int textureIndex;

    // current state (rotation handled in strafe class)
    private Vector3f velocity = new Vector3f(0f, 0f, 0f);
    private PhysicsState physicsState = PhysicsState.falling;
    private PhysicsState lastPhysicsState = PhysicsState.falling;
    private float lastFreeJumpTime = -1f;
    private float lastGroundTime = -1f;
    private float lastStateTransitionTime = 0f;
    private float lastSurfaceImpulseTime = 0f;
    private float lastWallTime = 0f;

    private final float radius = 0.5f;
    private final float coyoteJumpTime = 0.08f;
    private final float coyoteGroundTime = 0.08f;
    private final float maximumGroundGrade = 65f;
    private final float groundSweepDistance = 0.2f;
    // private final float jumpTime = 0.2f;
    // private final float jumpSpeed = 1.1f;
    // private final float jumpSpeedInit = 1f;
    // private final float jumpTime = 0.2f;
    // private final float jumpSpeed = 0f;
    // private final float jumpSpeedInit = 10f;
    private final float jumpTime = 0.3f;
    private final float jumpSpeed = 40f;
    private final float jumpSpeedInit = 6f;
    private final float surfaceImpulseCooldown = 0.1f;
    private final float bonkThreshold = 3f;
    private final float wallCooldown = 0.2f;

    //level1
    private final Vector3f startPos = new Vector3f(0f,0f,0f);
    //level2
    //private final Vector3f startPos = new Vector3f(-93.27780151f,-114.59135437f,2.84822822f);
    //level3
    //private final Vector3f startPos = new Vector3f(-86.46704865f,-22.27980995f,123.23853302f);
    //level3.5
    //private final Vector3f startPos = new Vector3f(41.56801987f,109.64228821f,28.86394119f);
    //level4
    //private final Vector3f startPos = new Vector3f(45.28624344f,80.15290833f,-30.87259674f);
    //level end
    //private final Vector3f startPos = new Vector3f(51.63388824f,37.45057678f,-49.30218124f);

    private GameObject normalLine;
    private Line normalLineS;

    enum PhysicsState {
        grounded,
        falling,
        jumping,
        diving;
    }

    private class GroundData {
        boolean hitGround = false;
        boolean snapToGround = false;
        float offsetToGround = 0f;
        Vector3f groundNormal = new Vector3f();
        CharacterSurface surface = CharacterSurface.GroundSurface;
        Vector3f surfaceImpulse = new Vector3f();
        boolean surfaceImpluseOverwrite = false;
    }

    private class ContactData {
        boolean touchingGround = false;
        boolean touchingWall = false;
        boolean touchingCieling = false;
        JmeBulletPhysicsObject groundPo = null;
        JmeBulletPhysicsObject wallPo = null;
        JmeBulletPhysicsObject cielingPo = null;
        Vector3f groundPoint = new Vector3f();
        Vector3f wallPoint = new Vector3f();
        Vector3f cielingPoint = new Vector3f();
    }

    public CharacterController(Engine engine) {
        this.engine = engine;
        debug = engine.isDebugMode();
        strafer = new CharacterStrafing(debug);
        currentState = new CharacterState();
        soundController = new SoundController();
    }

    public void setTextureIndex(int i) {textureIndex = i;}

    public int getTextureIndex(){return textureIndex;}

    public void loadShapes() {
        if (debug) {
            normalLineS = new Line(new Vector3f(0, 0, 0), new Vector3f(0, -2f, 0));
        }
        strafer.loadShapes();// for debug lines
    }

    public void buildObjects() {
        Matrix4f sphereTransform = new Matrix4f().identity();
        sphereTransform.translate(startPos);
        charPhys = engine.getSceneGraph().addPhysicsSphere(1f, sphereTransform.get(new float[16]), radius,
                CollisionGroups.Character.value(), CollisionGroups.All.value());
        charPhys.setFriction(0f);
        charPhys.setAngularFactor(0f);
        charPhys.setCustomPlanetPhys(true);

        castShape = new SphereCollisionShape(radius);

        if (debug) {
            normalLine = new GameObject(GameObject.root(), normalLineS);
            normalLine.getRenderStates().setColor(new Vector3f(0.2f, 0, 0.5f));
            normalLine.getRenderStates().hasDepthTesting(false);
        }

        strafer.buildObjects();
    }

    public void buildLights(){
        light = new Light();
		engine.getSceneGraph().addLight(light);
		light.setType(LightType.SPOTLIGHT);
        light.setDiffuse(0f, 0f, 0f);
        light.setOffAxisExponent(2f);
		//light.setDiffuse(1f, 1f, 1f);
		light.setSpecular(1f, 1f, 1f);
		light.setConstantAttenuation(1f);
		light.setLinearAttenuation(0f);
		light.setQuadraticAttenuation(0.15f);
    }

    public void loadSounds(){
        soundController.loadSounds();
    }

    public void initialize() {
        Camera c = (engine.getRenderSystem()).getViewport("MAIN").getCamera();
        cameraController = new CameraController(charPhys, c);
    }

    public void initializeAvatar(CharacterAvatar avatar){
        this.avatar = avatar;
    }

    private Vector3f up = new Vector3f(0f, 1f, 0f);
    private Vector3f gravDir = new Vector3f(0f, -1f, 0f);
    private float gravStrength = 1f;
    private Vector3f groundNormal = new Vector3f(0f, 1f, 0f);
    private Vector3f groundSpeed = new Vector3f(0f, 0f, 0f);// for now
    private Vector3f jumpImpulse = new Vector3f(0f, 0f, 0f);

    public void physicsUpdate() {
        gravDir.set(charPhys.getPlanet().getGravDir(charPhys.getWorldLocation()));
        gravStrength = charPhys.getPlanet().getGravStrength(charPhys.getWorldLocation());
        gravDir.mul(-1f, up);
        groundNormal.set(up);

        //System.out.println(InputMapper.singleton().getCharacterInputs().move.length());

        if (!enabled){
            velocity.set(0f,0f,0f);
            strafer.setTargetHorizontalSpeed(0f);
            applyVeloToRb();
            updateNetworkState();
            return;
        }

        // input vectors depend on camera position, so update it here
        cameraController.update(up);

        // dont stack forces if multiple frames occur between physics ticks
        charPhys.getRigidBody().clearForces();

        float wallTime = Time.elapsedTime-lastWallTime;

        // get if jumping, if squish, if on ground, and if bonking
        // only ground cast if state=grounded
        ContactData contactData = detectTouchedSurfaceTypes(up);
        GroundData groundData = detectGround(gravDir, up, contactData);
        boolean coyoteJump = determineJump();
        boolean squished = isSquish();
        boolean bonking = isBonking(contactData);

        if ((physicsState == PhysicsState.falling || physicsState == PhysicsState.diving) && groundData.hitGround && wallTime>wallCooldown){
            soundController.bonk();
        }

        // transition states
        PhysicsState stateCache = physicsState;// so weirdness doesnt happen while switching states
        if (coyoteJump) {
            // jump regardless of state - coyote time!!
            physicsState = PhysicsState.jumping;
        } else {
            switch (stateCache) {
                case grounded:
                    if (!groundData.hitGround) {
                        physicsState = PhysicsState.falling;
                    }
                    break;
                case falling:
                    if (lastFreeJumpTime > 0f) {
                        lastFreeJumpTime = -1f;
                        physicsState = PhysicsState.diving;
                    } else if (groundData.hitGround) {
                        physicsState = PhysicsState.grounded;
                    }
                    break;
                case jumping:
                    // this might never get used but idk it helps me sleep
                    if (lastFreeJumpTime > 0f) {
                        lastFreeJumpTime = -1f;
                        physicsState = PhysicsState.diving;
                    }
                    break;
                case diving:
                    if (groundData.hitGround) {
                        physicsState = PhysicsState.grounded;
                    }
            }
        }

        //special case for bonking
        if (bonking){
            velocity.set(0f,0f,0f);//sure i guess
            if (wallTime>wallCooldown){
                soundController.bonk();
            }
        }

        //special case for bounce pads
        float it = Time.elapsedTime - lastSurfaceImpulseTime;
        if (it > surfaceImpulseCooldown){
            if (groundData.surfaceImpulse.lengthSquared() > 0){
                lastSurfaceImpulseTime = Time.elapsedTime;
                groundData.snapToGround = false;//disable surface snap
                groundData.offsetToGround = 0f;
                groundData.hitGround = false;
                physicsState = PhysicsState.falling;
                if (groundData.surfaceImpluseOverwrite) {
                    velocity.set(0f,0f,0f);
                }else{
                    Vector3f siN = new Vector3f(groundData.surfaceImpulse);
                    siN.normalize();
                    Utils.projectOntoPlane(velocity, siN, siN);
                }
                velocity.add(groundData.surfaceImpulse);
            }
        }else{
            groundData.snapToGround = false;//disable surface snap
            groundData.offsetToGround = 0f;
            groundData.hitGround = false;
            physicsState = PhysicsState.falling;
        }

        // for states that need to know how long they have existed
        if (physicsState != lastPhysicsState) {
            lastStateTransitionTime = Time.elapsedTime;
        }
        lastPhysicsState = physicsState;

        // execute state
        switch (physicsState) {
            case grounded:
                stateGrounded(gravDir, up, groundData);
                break;
            case falling:
                stateFalling(gravDir, up, groundData);
                break;
            case jumping:
                stateJumping(gravDir, up, groundData);
                break;
            case diving:
                stateDiving(gravDir, up, groundData);
                break;
        }

        applyVeloToRb();

        updateNetworkState();
    }

    private boolean determineJump() {
        boolean jumpInput = InputMapper.singleton().getCharacterInputs().jumpFirstie;
        if (jumpInput) {
            lastFreeJumpTime = Time.elapsedTime;
        }

        boolean validJump = (Time.elapsedTime - lastFreeJumpTime) <= coyoteJumpTime;
        boolean validGround = (Time.elapsedTime - lastGroundTime) <= coyoteGroundTime;
        boolean jumpHolding = InputMapper.singleton().getCharacterInputs().jump;

        boolean result = validJump && validGround && jumpHolding;
        if (result) {
            lastFreeJumpTime = -1f;
        }

        return result;
    }

    GroundData groundDataAir = new GroundData();

    private GroundData detectGround(Vector3f gravDir, Vector3f upDir, ContactData contacts) {
        // if on ground, do a spherecast down to detect ground
        // else normal ground collision detection
        GroundData result = null;
        if (physicsState == PhysicsState.grounded) {
            result = groundCast(gravDir, upDir);
        } else {
            groundDataAir.groundNormal.set(upDir);
            groundDataAir.offsetToGround = 0f;
            groundDataAir.snapToGround = false;
            groundDataAir.hitGround = contacts.touchingGround;
            if (contacts.groundPo != null){
                groundDataAir.surface = contacts.groundPo.getSurfaceImpl(new float[]{contacts.groundPoint.x,contacts.groundPoint.y,contacts.groundPoint.z});
            }
            groundDataAir.surfaceImpulse.set(0f,0f,0f);
            groundDataAir.surfaceImpluseOverwrite = false;
            //this isnt the best way of doing this but its good enough for last week crunch time!
            JmeBulletPhysicsObject surfaceImpulsePO = null;
            if (contacts.groundPo != null){
                surfaceImpulsePO = contacts.groundPo;
            }else if (contacts.wallPo != null){
                surfaceImpulsePO = contacts.wallPo;
            }else if (contacts.cielingPo != null){
                surfaceImpulsePO = contacts.cielingPo;
            }
            if (surfaceImpulsePO != null){
                float[] si = surfaceImpulsePO.getSurfaceImpulseImpl();
                groundDataAir.surfaceImpulse.set(si[0],si[1],si[2]);
                groundDataAir.surfaceImpluseOverwrite = (si[3] == 1);
            }

            result = groundDataAir;
        }
        if (result.hitGround) {
            lastGroundTime = Time.elapsedTime;
        }
        return result;
    }

    private boolean isSquish() {
        return false;
    }

    private boolean isBonking(ContactData data) {
        if (data.touchingWall && !data.wallPo.isDynamic()){
            Vector3f wallDir = new Vector3f();
            data.wallPoint.sub(getLocation(),wallDir);
            wallDir.normalize();
            if (wallDir.dot(velocity) > bonkThreshold){
                return true;
            }
        }
        
        return false;
    }

    private Vector3f impulseStrafe = new Vector3f(0f, 0f, 0f);
    private Vector3f veloCopy = new Vector3f(0f, 0f, 0f);
    private Vector3f snapOffset = new Vector3f(0f, 0f, 0f);

    private void stateGrounded(Vector3f gravDir, Vector3f upDir, GroundData groundData) {

        if (groundData.snapToGround) {
            upDir.mul(-groundData.offsetToGround, snapOffset);
            nudgeRb(snapOffset);
            Utils.projectOntoPlane(velocity, groundData.groundNormal, upDir);
        } else {
            if (groundData.offsetToGround > 0.01f) {
                applyGravity(gravDir, gravStrength);// im too tired rn global variable pog
            } else {
                Utils.projectOntoPlane(velocity, upDir, upDir);
            }
        }

        veloCopy.set(velocity);
        impulseStrafe.set(
                strafer.update(upDir, groundData.groundNormal, veloCopy, groundSpeed, cameraController.getCameraU(),
                        getLocation(), groundData.surface));
        soundController.setSurface(groundData.surface);
        velocity.add(impulseStrafe);
    }

    private void stateFalling(Vector3f gravDir, Vector3f upDir, GroundData groundData) {
        applyGravity(gravDir, gravStrength);

        veloCopy.set(velocity);
        impulseStrafe.set(
                strafer.update(upDir, groundData.groundNormal, veloCopy, groundSpeed, cameraController.getCameraU(),
                        getLocation(), CharacterSurface.AirSurface));
        soundController.setSurface(CharacterSurface.AirSurface);
        velocity.add(impulseStrafe);
    }

    private void stateJumping(Vector3f gravDir, Vector3f upDir, GroundData groundData) {
        float jt = Time.elapsedTime - lastStateTransitionTime;
        if (jt == 0) {
            Utils.projectOntoPlane(velocity, up);
            up.mul(jumpSpeedInit, jumpImpulse);
            velocity.add(jumpImpulse);
            soundController.jump();;
        }
        if (jt <= jumpTime) {
            float jf = 1-Utils.clamp(0f, 1f, jt/jumpTime);
            if (InputMapper.singleton().getCharacterInputs().jump){
                up.mul(jumpSpeed *jf*jf*Time.deltaTime, jumpImpulse);
                velocity.add(jumpImpulse);
            }
        }else{
            physicsState = PhysicsState.falling;
        }
        applyGravity(gravDir, gravStrength);

        veloCopy.set(velocity);
        impulseStrafe.set(
                strafer.update(upDir, groundData.groundNormal, veloCopy, groundSpeed, cameraController.getCameraU(),
                        getLocation(), CharacterSurface.AirSurface));
        soundController.setSurface(CharacterSurface.AirSurface);
        velocity.add(impulseStrafe);
    }

    private void stateDiving(Vector3f gravDir, Vector3f upDir, GroundData groundData) {
        if (Time.elapsedTime - lastStateTransitionTime == 0) {
            Utils.projectOntoPlane(velocity, up);
            float speed = velocity.length();
            jumpImpulse.set(strafer.getDirection());
            jumpImpulse.mul(Math.max(speed, 7f));
            velocity.set(jumpImpulse);
            up.mul(10f, jumpImpulse);
            velocity.add(jumpImpulse);
            soundController.doubleJump();
        }

        applyGravity(gravDir, gravStrength);

        veloCopy.set(velocity);
        impulseStrafe.set(
                strafer.update(upDir, groundData.groundNormal, veloCopy, groundSpeed, cameraController.getCameraU(),
                        getLocation(), CharacterSurface.DiveSurface));
        soundController.setSurface(CharacterSurface.DiveSurface);
        velocity.add(impulseStrafe);
    }

    private Vector3f tempGroundCast = new Vector3f(0f, 0f, 0f);
    private Vector3f castFrom = new Vector3f(0f, 0f, 0f);
    private Vector3f castTo = new Vector3f(0f, 0f, 0f);
    private Vector3f castTwo = new Vector3f(0f, 0f, 0f);
    private Vector3f hitPos = new Vector3f(0f, 0f, 0f);
    private Vector3f hitNorm = new Vector3f(0f, 0f, 0f);
    private com.jme3.math.Vector3f n = new com.jme3.math.Vector3f();

    private float cosGrade = (float) Math.cos(Math.toRadians(maximumGroundGrade));
    private float raycastDistance = radius / (float) Math.sin(Math.toRadians(90 - maximumGroundGrade));
    private float testStartUpDistance = 0.45f;
    private GroundData groundDataCast = new GroundData();

    private GroundData groundCast(Vector3f gravDir, Vector3f upDir) {
        JmeBulletPhysicsEngine pe = engine.getSceneGraph().getPhysicsEngine();

        int mask = CollisionGroups.Character.value() | CollisionGroups.Sensor.value();

        groundDataCast.hitGround = false;
        groundDataCast.snapToGround = false;
        groundDataCast.offsetToGround = 0f;
        groundDataCast.groundNormal.set(upDir.x, upDir.y, upDir.z);
        groundDataCast.surface = CharacterSurface.GroundSurface;
        groundDataCast.surfaceImpulse.set(0f,0f,0f);
        groundDataCast.surfaceImpluseOverwrite = false;

        castFrom.set(charPhys.getWorldLocation());
        tempGroundCast.set(upDir);
        tempGroundCast.mul(testStartUpDistance);
        castFrom.add(tempGroundCast);

        castTo.set(charPhys.getWorldLocation());
        tempGroundCast.set(gravDir);
        tempGroundCast.mul(groundSweepDistance);
        castTo.add(tempGroundCast);

        castTwo.set(charPhys.getWorldLocation());
        tempGroundCast.set(gravDir);
        tempGroundCast.mul(raycastDistance);// on very steep edges this wont be enough, may want to add a small fudge.
                                            // up to u future roxy!!
        castTwo.add(tempGroundCast);

        ArrayList<PhysicsSweepTestResult> results = pe.ConvexCast(castShape,
                new float[] { castFrom.x(), castFrom.y(), castFrom.z() },
                new float[] { castTo.x(), castTo.y(), castTo.z() });
        float minFraction = 1f;
        float minFractionWall = 1f;
        PhysicsSweepTestResult closest = null;
        for (PhysicsSweepTestResult result : results) {
            if ((result.getCollisionObject().getCollisionGroup() & mask) == 0) {// if not us
                result.getHitNormalLocal(n);
                hitNorm.x = n.x;
                hitNorm.y = n.y;
                hitNorm.z = n.z;
                hitNorm.normalize();
                if (upDir.dot(hitNorm) >= cosGrade) {// if not too steep
                    if (result.getHitFraction() < minFraction) {// if closer than last best point
                        minFraction = result.getHitFraction();
                        closest = result;
                    }
                } else {
                    if (result.getHitFraction() < minFractionWall) {
                        minFractionWall = result.getHitFraction();
                    }
                }
            }

        }
        if (minFractionWall < (minFraction-0.1f)) {
            // no snapping down thru walls!
            closest = null;
        }

        if (closest != null) {// sweep test found the ground!!

            //calculate hit pos cuz we aren't given it for whatever reason :/
            closest.getHitNormalLocal(n);
            hitNorm.set(gravDir);
            hitNorm.mul(closest.getHitFraction() * (testStartUpDistance + groundSweepDistance));
            hitPos.set(castFrom);
            hitPos.add(hitNorm);
            hitNorm.x = n.x;
            hitNorm.y = n.y;
            hitNorm.z = n.z;
            hitNorm.normalize();
            hitNorm.mul(0.5f);
            hitPos.sub(hitNorm);
            hitNorm.mul(2f);

            groundDataCast.hitGround = true;
            closest.getHitNormalLocal(n);
            groundDataCast.groundNormal.set(n.x, n.y, n.z);
            groundDataCast.offsetToGround = (closest.getHitFraction() * (testStartUpDistance + groundSweepDistance))
                    - testStartUpDistance;
            PhysicsCollisionObject co = closest.getCollisionObject();
            if (co instanceof PhysicsRigidBody){
                PhysicsRigidBody rb = (PhysicsRigidBody)co;
                long id = rb.nativeId();
                JmeBulletPhysicsObject po = JmeBulletPhysicsObject.getJmeBulletPhysicsObject(id);
                groundDataCast.surface = po.getSurfaceImpl(new float[]{hitPos.x,hitPos.y,hitPos.z});
                float[] si = po.getSurfaceImpulseImpl();
                groundDataCast.surfaceImpulse.set(si[0],si[1],si[2]);
                groundDataCast.surfaceImpluseOverwrite = (si[3] == 1);
            }

            // just because we found the ground does not mean we want to snap to it
            // if we are on the edge of a cliff, we do not want to snap
            // this lets the player smoothly run off an edge
            // this raycast detects cliffs by shooting a ray straight down at the maximum
            // ground distance
            // if it doesnt hit anything, we are on a cliff. otherwise, its just a drop and
            // we can snap.

            float groundDist = -1f;
            // unlike sweeptest, these are returned in order closest to furthest!
            ArrayList<PhysicsRayTestResult> rayResults = pe.RayCast(
                    new float[] { castFrom.x(), castFrom.y(), castFrom.z() },
                    new float[] { castTwo.x(), castTwo.y(), castTwo.z() });
            for (PhysicsRayTestResult rayResult : rayResults) {
                if ((rayResult.getCollisionObject().getCollisionGroup() & mask) == 0) {
                    groundDist = (rayResult.getHitFraction() * (testStartUpDistance + raycastDistance))
                            - testStartUpDistance;
                    break;
                }
            }
            if (groundDist > 0) {// we hit something!
                groundDataCast.snapToGround = true;
            }
        }

        drawDebugLines(up, gravDir, closest);

        return groundDataCast;
    }

    private Vector3f collPointWorld = new Vector3f(0f, 0f, 0f);
    private Vector3f collPointRelative = new Vector3f(0f, 0f, 0f);
    private Vector3f posWorld = new Vector3f(0f, 0f, 0f);
    private com.jme3.math.Vector3f storeColl = new com.jme3.math.Vector3f();
    private ContactData result = new ContactData();

    // this doesnt perfectly line up with what groundCast counts as a floor or
    // cieling
    // this is just for bonking and landing!!
    private ContactData detectTouchedSurfaceTypes(Vector3f upDir) {
        result.touchingGround = false;
        result.touchingWall = false;
        result.touchingCieling = false;
        result.groundPo = null;
        result.wallPo = null;
        result.cielingPo = null;

        int mask = CollisionGroups.Character.value() | CollisionGroups.Sensor.value();

        long rbId = charPhys.getRigidBody().nativeId();
        posWorld.set(getLocation());

        int count = charPhys.GetManifoldCount();
        for (int i = 0; i < count; i++) {
            long manifoldId = charPhys.GetManifold(i);
            boolean isA = PersistentManifolds.getBodyAId(manifoldId) == rbId;
            long points[] = PersistentManifolds.listPointIds(manifoldId);
            for (int j = 0; j < points.length; j++) {
                long pointId = points[j];
                int surfaceFlags = -1;
                JmeBulletPhysicsObject po = null;
                if (isA) {
                    ManifoldPoints.getPositionWorldOnA(pointId, storeColl);
                    po = JmeBulletPhysicsObject
                            .getJmeBulletPhysicsObject(PersistentManifolds.getBodyBId(manifoldId));
                    if (po != null) {
                        surfaceFlags = po.getRigidBody().getCollisionGroup();
                    }
                } else {
                    ManifoldPoints.getPositionWorldOnB(pointId, storeColl);
                    po = JmeBulletPhysicsObject
                            .getJmeBulletPhysicsObject(PersistentManifolds.getBodyAId(manifoldId));
                    if (po != null) {
                        surfaceFlags = po.getRigidBody().getCollisionGroup();
                    }
                }
                if ((surfaceFlags & mask) != 0) {
                    continue;
                }
                collPointWorld.x = storeColl.x;
                collPointWorld.y = storeColl.y;
                collPointWorld.z = storeColl.z;
                collPointWorld.sub(posWorld, collPointRelative);
                collPointRelative.normalize();
                float cosAngle = upDir.dot(collPointRelative);
                if (Math.abs(cosAngle) < cosGrade) {
                    result.touchingWall = true;
                    result.wallPo = po;
                    result.wallPoint.set(collPointWorld);
                    lastWallTime = Time.elapsedTime;
                } else if (cosAngle > 0) {
                    result.touchingCieling = true;
                    result.cielingPo = po;
                    result.cielingPoint.set(collPointWorld);
                } else {
                    result.touchingGround = true;
                    result.groundPo = po;
                    result.groundPoint.set(collPointWorld);
                }
            }
        }

        return result;
    }

    private Vector3f rbVel = new Vector3f(0f, 0f, 0f);
    private Vector3f rbImpulse = new Vector3f(0f, 0f, 0f);

    private void applyVeloToRb() {
        rbVel.set(charPhys.getLinearVelocity());
        velocity.sub(rbVel, rbImpulse);
        charPhys.applyImpulse(rbImpulse.x, rbImpulse.y, rbImpulse.z, 0f, 0f, 0f);
    }

    private Vector3f newNudgePos = new Vector3f(0f, 0f, 0f);

    private void nudgeRb(Vector3f offset) {
        getLocation().add(offset, newNudgePos);
        charPhys.setLocation(new float[] { newNudgePos.x, newNudgePos.y, newNudgePos.z });
    }

    private Vector3f gravTick = new Vector3f(0f, 0f, 0f);

    private void applyGravity(Vector3f gravDir, float gravStrength) {
        float downSpeed = gravDir.dot(velocity);
        if (downSpeed < 15f){
            gravDir.mul((gravStrength * Time.deltaTime), gravTick);
            velocity.add(gravTick);
        }
    }

    private void updateNetworkState() {
        currentState.position = getLocation();
        currentState.dir = strafer.getDirection();
        currentState.up = up;
        currentState.velocity = velocity;
        currentState.horizontalSpeed = strafer.getTargetHorizontalSpeed();
        if (charPhys.getPlanet() != null){
            currentState.planetIndex = charPhys.getPlanet().getIndex();
        }else{
            currentState.planetIndex = -1;
        }
    }

    private Vector3f physPos = new Vector3f();

    public void visualUpdate() {
        physPos.set(charPhys.getWorldLocation());

        if (enabled){
            soundController.setRoving(InputMapper.singleton().getCharacterInputs().move.lengthSquared() > 0.00001f);
        }else{
            soundController.setRoving(false);
        }
        soundController.update(physPos, cameraController.getCameraN(), cameraController.getCameraV());

        if (avatar != null){
            Vector3f lookDir = strafer.getDirection();
            CharacterAnimator.faceTowards(avatar, lookDir, up);
            CharacterAnimator.move(avatar, new Vector3f(charPhys.getWorldLocation()));
            float rollDist = strafer.getTargetHorizontalSpeed() * Time.deltaTime;
            CharacterAnimator.roll(avatar, rollDist);
            int pi = -1;
            if (charPhys.getPlanet() != null) {
                pi = charPhys.getPlanet().getIndex();
                // if (pi > 1 && pi < 10){
                //     currentState.avatarState = 1;//if u skip pengu u still get a flower
                // }
                if (pi == 12){
                    cameraController.setDistance(6f);
                }
            }
            CharacterAnimator.state(avatar, currentState.avatarState);
            CharacterAnimator.planetLight(avatar, physPos, pi);

            Vector3f lightPos = new Vector3f(up);
            lightPos.mul(0.75f);
            lightPos.add(physPos);
            light.setLocation(lightPos);
            Vector3f lightDir = new Vector3f(lookDir);
            Vector3f sideDir = new Vector3f();
            lookDir.cross(up,sideDir);
            lightDir.rotateAxis((float)Math.toRadians(20f), sideDir.x, sideDir.y, sideDir.z);
            light.setDirection(new Vector3f(lightDir));
            if (InputMapper.singleton().getCharacterInputs().lightFirstie){
                lightOn = !lightOn;
            }
            if (lightOn){//we just need to set diffuse, my cartoon lighting model doesnt use much else
                light.setDiffuse(5f, 5f, 5f);
            }else{
                light.setDiffuse(0f, 0f, 0f);
            }

            if (engine.isDebugMode() && InputMapper.singleton().getCharacterInputs().interactFirstie){
                printTransform();
            }
        }

    }

    public Vector3f getLocation() {
        physPos.set(charPhys.getWorldLocation());
        return physPos;
    }

    public Vector3f getCameraLocation() {
        return cameraController.getCameraPos();
    }

    public CharacterState getState() {
        return currentState;
    }

    public void setAvatarState(int s){
        currentState.avatarState = s;
    }

    public Camera getCamera(){
        return cameraController.getCamera();
    }

    public void setEnabled(boolean e){
        enabled = e;
    }

    public boolean getEnabled(){
        return enabled;
    }

    private void drawDebugLines(Vector3f up, Vector3f gravDir, PhysicsSweepTestResult closest) {
        if (debug) {
            hitNorm.set(0f, 0f, 0f);
            hitPos.set(0f, 0f, 0f);
            if (closest != null) {
                closest.getHitNormalLocal(n);
                hitNorm.set(gravDir);
                hitNorm.mul(closest.getHitFraction() * (testStartUpDistance + groundSweepDistance));
                hitPos = new Vector3f(castFrom);
                hitPos.add(hitNorm);
                hitNorm.x = n.x;
                hitNorm.y = n.y;
                hitNorm.z = n.z;
                hitNorm.normalize();
                hitNorm.mul(0.5f);
                hitPos.sub(hitNorm);
                hitNorm.mul(2f);
            }

            Vector3f hitEnd = new Vector3f(hitPos);
            hitEnd.add(hitNorm);
            normalLineS.setRay(hitPos, hitEnd);
        }
    }

    //meant for use with dialogue system, only works when disabled
    public void setTransform (Vector3f position, Vector3f target){
        Planet p = charPhys.getPlanet();
        if (charPhys.getPlanet() != null){
            Vector3f up = new Vector3f(p.getGravDir(new float[]{position.x,position.y,position.z}));
            up.mul(-1f);
            Vector3f dir = new Vector3f();
            target.sub(position, dir);
            //strafer normalizes and projects this no worries
            strafer.setDirection(dir, up);
            charPhys.setLocation(new float[]{position.x,position.y,position.z});
        }
    }

    //meant for use with dialogue system, only works when disabled
    public void setCameraTransform(Vector3f position, Vector3f target){
        Planet p = charPhys.getPlanet();
        if (charPhys.getPlanet() != null){
            Vector3f up = new Vector3f(p.getGravDir(new float[]{position.x,position.y,position.z}));
            up.mul(-1f);
            cameraController.setTransform(position, target, up);
        }
    }

    public void honk(){
        soundController.honk();
    }

    public int getAvatarState(){
        return currentState.avatarState;
    }

    public void printTransform(){
        Planet p = charPhys.getPlanet();
        if (charPhys.getPlanet() != null){
            Vector3f down = new Vector3f(p.getGravDir(charPhys.getWorldLocation()));
            Vector3f pos = new Vector3f(charPhys.getWorldLocation());
            Vector3f downOffset = new Vector3f(down);
            downOffset.mul(radius);
            Vector3f feetPos = new Vector3f(pos);
            feetPos.add(downOffset);
            Vector3f forward = strafer.getDirection();
            Vector3f camPos = cameraController.getCameraPos();
            Vector3f camForward = cameraController.getCameraN();
            System.out.println("--------------------------");
            System.out.println("Current Stats:");
            System.out.println("Grav Dir: " + Utils.formatVector(down));
            System.out.println("Char Pos: " + Utils.formatVector(pos));
            System.out.println("Feet Pos: " + Utils.formatVector(feetPos));
            System.out.println("Char Dir: " + Utils.formatVector(forward));
            System.out.println("Cam  Pos: " + Utils.formatVector(camPos));
            System.out.println("Cam  Dir: " + Utils.formatVector(camForward));
            System.out.println("--------------------------");
        }
    }
}
