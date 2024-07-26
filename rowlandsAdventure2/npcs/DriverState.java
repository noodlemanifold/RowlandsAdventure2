package rowlandsAdventure2.npcs;

import org.joml.Vector3f;

import tage.TextureImage;

public class DriverState {

    public enum StateAdvanceType{
        DIALOGUE,
        DISTANCE,
        EXTERNAL,
        NONE;
    }

    public Vector3f driverPosDir;
    public float driverPosRadius;
    public float driverRadius;//set to 0 to disable

    public boolean lockCharacter = false;
    //if locked, specify camera and avatar position
    public Vector3f lookTarget;
    public Vector3f cameraPos;
    public float cameraLerpTime;
    public Vector3f characterPos;
    public float characterLerpTime;

    public TextureImage dialogueImage;//can be null
    public StateAdvanceType stateAdvance;
}
