package rowlandsAdventure2.character;

import org.joml.Vector3f;

import rowlandsAdventure2.input.InputMapper;
import rowlandsAdventure2.input.RowlandInputs;
import tage.Camera;
import tage.Time;
import tage.Utils;
import tage.JmeBullet.JmeBulletPhysicsObject;

public class CameraController{

    private JmeBulletPhysicsObject target;
    private Camera camera;

    private Vector3f targetPoint;

    //modify device sensitivities instead of changing this future me
    private float cameraSpeed = 90f;
    private float minElevation = -80f;
    private float maxElevation = 80f;
    private float minDistance = 3.5f;
    private float vertOffset = 0.7f;

    private float elevation = 40f;

    private float flipSpeed = (float)Math.toRadians(180f);

    public CameraController(JmeBulletPhysicsObject target, Camera camera){
        this.target = target;
        this.camera = camera;
        targetPoint = new Vector3f(target.getWorldLocation());
    }

    //Vector3f up = new Vector3f(0f,1f,0f);
    public void update(Vector3f up){
        updatePosition(up);
    }

    private Vector3f targetToCamera = new Vector3f(0f,0f,0f);
    private Vector3f cameraPitchAxis = new Vector3f(0f,0f,0f);
    private Vector3f cameraPos = new Vector3f(0f,0f,0f);
    private Vector3f lastUp = new Vector3f(0f,1f,0f);
    private Vector3f axis = new Vector3f(0f,0f,0f);
    private Vector3f tieaxis = new Vector3f(0f,0f,0f);
    private Vector3f upOffset = new Vector3f(0f,0f,0f);
    private Vector3f upDirection = new Vector3f(0f,0f,0f);
    private float yawAmount = 0f;
    private void updatePosition(Vector3f up){
        upDirection.set(up);
        //limit flip rate
        if (lastUp.angle(upDirection) > flipSpeed * Time.deltaTime){
            lastUp.cross(upDirection,axis);
            if (axis.lengthSquared() <= 0.0000001f){
                tieaxis.set(0f,1f,0f);
                if (upDirection.dot(tieaxis) > 0.97f){
                    tieaxis.set(1f,0f,0f);
                }
                axis.set(lastUp);
                axis.rotateAxis((float)Math.toRadians(90f), tieaxis.x, tieaxis.y, tieaxis.z);
            }
            axis.normalize();
            upDirection.set(lastUp);
            upDirection.rotateAxis(flipSpeed*Time.deltaTime, axis.x, axis.y, axis.z);
        }


        RowlandInputs inputs = InputMapper.singleton().getCharacterInputs();

        targetPoint.set(target.getWorldLocation());//for now
        upOffset.set(upDirection);
        upOffset.mul(vertOffset);
        targetPoint.add(upOffset);

        //update rotation amounts based on current inputs
        yawAmount = Time.deltaTime * -inputs.look.x * cameraSpeed;
        elevation += Time.deltaTime * -inputs.look.y * cameraSpeed;
        elevation = Utils.clamp(minElevation, maxElevation, elevation);

        //current vector from target to camera
        camera.getLocation().sub(targetPoint,targetToCamera);
        //flatten vector onto plane
        Utils.projectOntoPlane(targetToCamera, upDirection);
        targetToCamera.normalize();
        //rotate vector along vertical axis depending on inputs
        targetToCamera.rotateAxis((float)Math.toRadians(yawAmount), upDirection.x, upDirection.y, upDirection.z);
        //get axis to pitch camera on
        targetToCamera.cross(upDirection,cameraPitchAxis);
        //pitch vector along that axis by angle
        targetToCamera.rotateAxis((float)Math.toRadians(elevation), cameraPitchAxis.x, cameraPitchAxis.y, cameraPitchAxis.z);
        //set camera distance from target
        targetToCamera.mul(minDistance);
        //add new camera vector to target position
        targetPoint.add(targetToCamera,cameraPos);
        
        camera.setLocation(cameraPos);
        camera.lookAt(targetPoint,upDirection);

        lastUp.set(upDirection);
    }

    public Vector3f getCameraU(){
        return camera.getU();
    }

    public Vector3f getCameraV(){
        return camera.getV();
    }

    public Vector3f getCameraN(){
        return camera.getN();
    }

    public Vector3f getCameraPos(){
        return camera.getLocation();
    }

    public Camera getCamera(){
        return camera;
    }

    public void setDistance(float d){
        minDistance = d;
    }

    public void setTransform(Vector3f position, Vector3f target, Vector3f up){
        camera.setLocation(position);
        camera.lookAt(target, up);
    }
}