package rowlandsAdventure2.character;

import org.joml.Vector3f;

import rowlandsAdventure2.input.InputMapper;
import rowlandsAdventure2.input.RowlandInputs;
import tage.GameObject;
import tage.Time;
import tage.Utils;
import tage.shapes.Line;

public class CharacterStrafing {

    // Input state
    private RowlandInputs inputs;
    private float inputMagnitude;
    private Vector3f inputDirection = new Vector3f();

    // Velocity state
    private Vector3f charDirection = new Vector3f(0f, 0f, -1f);
    private Vector3f groundNormalLast = new Vector3f(0f, 0f, 0f);
    private float platonicSpeed = 0f;
    private float speedPercent = 0f;
    //Veclocity state cache (for animations)
    private float sideSpeedCache = 0f;

    // Surface physics parameters
    //private CharacterSurface activeSurf;

    // Debug lines
    private GameObject inputLine;
    private Line inputLineS;
    private GameObject directionLine;
    private Line directionLineS;
    private GameObject velocityLine;
    private Line velocityLineS;
    // Debug vectors
    private Vector3f worldPosition = new Vector3f();
    private Vector3f feetPosition = new Vector3f();
    private Vector3f inputRayEnd = new Vector3f();
    private Vector3f directionRayEnd = new Vector3f();
    private Vector3f velRayEnd = new Vector3f();
    private boolean isDebug;

    public CharacterStrafing(boolean isDebug) {
        this.isDebug = isDebug;
    }

    public void loadShapes() {
        if (isDebug) {
            inputLineS = new Line(new Vector3f(0, 0, 0), new Vector3f(0, -2f, 0));
            directionLineS = new Line(new Vector3f(0, 0, 0), new Vector3f(0, -2f, 0));
            velocityLineS = new Line(new Vector3f(0, 0, 0), new Vector3f(0, -2f, 0));
        }
    }

    public void buildObjects() {
        if (isDebug) {
            inputLine = new GameObject(GameObject.root(), inputLineS);
            inputLine.getRenderStates().setColor(new Vector3f(1, 0, 1));
            inputLine.getRenderStates().hasDepthTesting(false);

            directionLine = new GameObject(GameObject.root(), directionLineS);
            directionLine.getRenderStates().setColor(new Vector3f(0, 1, 1));
            directionLine.getRenderStates().hasDepthTesting(false);

            velocityLine = new GameObject(GameObject.root(), velocityLineS);
            velocityLine.getRenderStates().setColor(new Vector3f(1, 1, 0));
            velocityLine.getRenderStates().hasDepthTesting(false);
        }
    }

    public Vector3f getDirection(){
        return new Vector3f(charDirection);
    }

    public float getHorizontalSpeed(){
        return sideSpeedCache;
    }

    public float getTargetHorizontalSpeed(){
        return platonicSpeed;
    }

    public void setTargetHorizontalSpeed(float f){
        platonicSpeed = f;
    }

    private Vector3f impulseResult = new Vector3f();
    private Vector3f effectiveVelocity = new Vector3f();
    private Vector3f currentSidewaysVelocity = new Vector3f();
    private Vector3f newSidewaysVelocity = new Vector3f();

    public Vector3f update(Vector3f up, Vector3f groundNormal, Vector3f currentVelocity, Vector3f groundVelocity,
            Vector3f cameraU, Vector3f charPos, CharacterSurface surface) {
        speedPercent = Utils.clamp(0f, 1f, currentVelocity.length() / surface.maxSpeed);

        inputs = InputMapper.singleton().getCharacterInputs();
        inputMagnitude = inputs.move.length();
        inputDirection.set(orientInputs(up, cameraU));

        rotateCharacter(up, charDirection, surface);

        currentSidewaysVelocity.set(calculateSidewaysVelocity(up, groundNormal, currentVelocity));
        effectiveVelocity.set(calculateEffectiveVelocity(up, groundNormal, groundNormalLast, currentVelocity));
        newSidewaysVelocity.set(calculateNewSidewaysVelocity(up, groundNormal, effectiveVelocity, groundVelocity, surface));

        sideSpeedCache = newSidewaysVelocity.length();

        drawDebugLines(up, charPos, newSidewaysVelocity);

        groundNormalLast.set(groundNormal);

        newSidewaysVelocity.sub(currentSidewaysVelocity, impulseResult);
        if (inputs.pullFirstie && isDebug){
            impulseResult.add(charDirection);
        }
        return impulseResult;
    }

    private Vector3f inputDir = new Vector3f();

    private Vector3f orientInputs(Vector3f up, Vector3f cameraU) {
        float angle = (float) Math.atan2(-inputs.move.y, -inputs.move.x) + ((float) Math.PI / 2f);
        if (inputMagnitude < 0.001f) {
            angle = 0f;// not that it matters but just in case
        }
        Vector3f camRight = cameraU;
        up.cross(camRight, inputDir);// get forward direction from camera
        inputDir.rotateAxis(angle, up.x, up.y, up.z);// rotate to input direction
        inputDir.normalize();
        return inputDir;
    }

    private void rotateCharacter(Vector3f up, Vector3f charDir, CharacterSurface surface) {
        Utils.projectOntoPlane(charDir, up);
        charDir.normalize();
        if (inputMagnitude > 0.001f) {
            float angleDiff = charDir.angleSigned(inputDirection, up);
            float winding = Math.signum(angleDiff);
            angleDiff = Math.abs(angleDiff);
            float rotSpeed = Utils.lerp(surface.rotationSpeedLowSpeed, surface.rotationSpeedHighSpeed,
                    speedPercent);
            float angleDelta = Math.min((float) Math.toRadians(rotSpeed) * Time.deltaTime, angleDiff);
            charDir.rotateAxis(angleDelta * winding, up.x, up.y, up.z);
        }
    }

    private Vector3f velDirectionTarget = new Vector3f();
    private Vector3f vel = new Vector3f();
    private Vector3f velTarget = new Vector3f();
    private Vector3f impulse = new Vector3f();

    private Vector3f calculateNewSidewaysVelocity(Vector3f up, Vector3f groundNormal, Vector3f currentEffectiveVel,
            Vector3f groundVelocity, CharacterSurface surface) {
        velDirectionTarget.set(charDirection);
        Utils.projectOntoPlane(velDirectionTarget, groundNormal, up);
        velDirectionTarget.normalize();

        float targetSpeed = calculateTargetSpeed(up, groundNormal, velDirectionTarget, currentEffectiveVel, surface);
        velDirectionTarget.mul(targetSpeed, velTarget);
        velTarget.add(groundVelocity);
        vel.set(currentEffectiveVel);
        velTarget.sub(vel, impulse);// get impulse between current and target velocities
        float impulseLimit = Utils.lerp(surface.impulseLimitLowSpeed, surface.impulseLimitHighSpeed, speedPercent)
                * Time.deltaTime;
        if (inputMagnitude < 0.001f){//make impulse limit smaller if releasing
            impulseLimit *= surface.impulseIdlePenaltyFactor;
        }
        if (impulse.lengthSquared() > impulseLimit * impulseLimit) {
            Utils.clampMagnitude(0f, impulseLimit, impulse);
            impulse.mul(surface.impulseBoundsPenaltyFactor);
        }
        //System.out.println(impulse.length()/Time.deltaTime);
        return impulse.add(vel);
    }

    private Vector3f velEffectiveDir = new Vector3f();
    private Vector3f velLastSideways = new Vector3f();

    private Vector3f calculateEffectiveVelocity(Vector3f up, Vector3f groundNormal, Vector3f groundNormalLast,
            Vector3f currentVelocity) {
        // TODO: add some horizontal speed for the vertical speed we are projecting away

        // In order to keep our "sideways" velocity as you would expect, we need to
        // project
        // the velocity direction to our current ground normal, but the speed to the
        // last
        // ground normal, and then combine the two. This way speed and angles are both
        // maintained.
        velEffectiveDir.set(currentVelocity);
        Utils.projectOntoPlane(velEffectiveDir, groundNormal, up);
        if (velEffectiveDir.lengthSquared() < 0.00001f) {
            velEffectiveDir.set(0f, 0f, 0f);
        } else {
            velEffectiveDir.normalize();
        }

        if (groundNormalLast.lengthSquared() < 0.1f) {
            groundNormalLast.set(groundNormal);
        }

        velLastSideways.set(currentVelocity);
        // this should maybe use last up vector, not current, but its probably fine
        Utils.projectOntoPlane(velLastSideways, groundNormalLast, up);
        float speedSideways = velLastSideways.length();
        velEffectiveDir.mul(speedSideways);
        return velEffectiveDir;
    }

    private Vector3f velSideways = new Vector3f();

    private Vector3f calculateSidewaysVelocity(Vector3f up, Vector3f groundNormal, Vector3f currentVelocity) {
        velSideways.set(currentVelocity);
        Utils.projectOntoPlane(velSideways, groundNormal, up);
        if (velSideways.lengthSquared() < 0.00001f) {
            velSideways.set(0f, 0f, 0f);
        }
        return velSideways;
    }

    private float calculateTargetSpeed(Vector3f up, Vector3f groundNormal, Vector3f velDirTarget,
            Vector3f effectiveVelocity, CharacterSurface surface) {
        Vector3f uphill = calculateUphillVector(up, groundNormal);
        float normalFactor = -velDirTarget.dot(uphill);
        // factor in how steep the slope is
        normalFactor *= (1f - groundNormal.dot(up));

        float minSpeed = Utils.clamp(0, 100f,
                surface.minSpeed + surface.minSpeed * normalFactor * surface.slopeSpeedCapFactor);
        float maxSpeed = Utils.clamp(0, 100f,
                surface.maxSpeed + surface.maxSpeed * normalFactor * surface.slopeSpeedCapFactor);

        float speedFromInputs = Utils.lerp(minSpeed, maxSpeed, inputMagnitude);
        float acceleration = calculateAcceleration(speedFromInputs, platonicSpeed, normalFactor, surface);
        float speedDelta = acceleration * Time.deltaTime;
        if (Math.abs(speedDelta) > Math.abs(speedFromInputs - platonicSpeed)) {
            speedDelta = speedFromInputs - platonicSpeed;
        }
        platonicSpeed = platonicSpeed + speedDelta;

        float effectiveSpeed = effectiveVelocity.length();
        if (effectiveSpeed > maxSpeed){
            effectiveSpeed -= (Math.abs(surface.deccelerationOverspeed) * Time.deltaTime);
            float overspeedFromInputs = Utils.lerp(/*minSpeed*/ Utils.clamp(0, effectiveSpeed, effectiveSpeed-(maxSpeed*0.3f)), effectiveSpeed, inputMagnitude);
            float overspeedAccel = calculateAcceleration(overspeedFromInputs, effectiveSpeed, normalFactor, surface);
            float overspeedDelta = overspeedAccel * Time.deltaTime;
            if (Math.abs(overspeedDelta) > Math.abs(overspeedFromInputs - effectiveSpeed)) {
                overspeedDelta = overspeedFromInputs - effectiveSpeed;
            }
            return effectiveSpeed + overspeedDelta;
        }else{
            return platonicSpeed;
        }
    }

    private float calculateAcceleration(float targetSpeed, float currentSpeed, float normalFactor, CharacterSurface surface) {
        float acceleration = 0f;

        if (targetSpeed > currentSpeed) {
            acceleration = Math
                    .abs(Utils.lerp(surface.accelerationLowSpeed, surface.accelerationHighSpeed, speedPercent));
        } else {
            acceleration = -Math
                    .abs(Utils.lerp(surface.deccelerationLowSpeed, surface.deccelerationHighSpeed, speedPercent));
        }

        float accelerationSlope = acceleration
                + Math.abs(acceleration) * normalFactor * surface.slopeAccelerationCapFactor;
        if (accelerationSlope * acceleration < 0f) {// no switching signs that would be very bad
            accelerationSlope = 0f;
        }

        return accelerationSlope;
    }

    private Vector3f normalUpCross = new Vector3f();
    private Vector3f groundUphill = new Vector3f();

    private Vector3f calculateUphillVector(Vector3f up, Vector3f groundNormal) {
        up.cross(groundNormal, normalUpCross);
        if (normalUpCross.lengthSquared() < 0.00001f) {
            normalUpCross.set(0f, 0f, 0f);
            groundUphill.set(0f, 0f, 0f);
            groundNormal.set(up);
        } else {
            normalUpCross.normalize();
            groundNormal.cross(normalUpCross, groundUphill);
        }
        return groundUphill;
    }

    private void drawDebugLines(Vector3f up, Vector3f charPos, Vector3f newVel) {
        if (isDebug) {
            worldPosition.set(charPos);
            up.mul(-0.5f);
            worldPosition.add(up, feetPosition);
            up.mul(-2f);
            inputDirection.mul(inputMagnitude);
            feetPosition.add(inputDirection, inputRayEnd);
            feetPosition.add(charDirection, directionRayEnd);
            feetPosition.add(newVel, velRayEnd);

            inputLineS.setRay(feetPosition, inputRayEnd);
            directionLineS.setRay(feetPosition, directionRayEnd);
            velocityLineS.setRay(feetPosition, velRayEnd);
        }
    }

    //for use in cutscenes
    public void setDirection(Vector3f dir, Vector3f up){
        charDirection.set(dir);
        Utils.projectOntoPlane(charDirection, up);
        charDirection.normalize();
    }
}