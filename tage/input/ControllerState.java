package tage.input;

import org.joml.Vector2f;

/**
 * Data class to hold state of the controller.
 */
public class ControllerState{
    enum DPadState{
        None, Left, UpLeft, Up, UpRight, Right, DownRight, Down, DownLeft
    }

    public Vector2f leftStick = new Vector2f(0f,0f);
    public Vector2f rightStick = new Vector2f(0f,0f);
    public float triggers = 0f;
    public DPadState dPadState = DPadState.None;
    public boolean leftBumper = false;
    public boolean rightBumper = false;
    public boolean southButton = false;
    public boolean eastButton = false;
    public boolean westButton = false;
    public boolean northButton = false;

    public void print(){
        System.out.println("Controller State:");
        System.out.println("Left Stick: " + leftStick.x + ", " + leftStick.y);
        System.out.println("Right Stick: " + rightStick.x + ", " + rightStick.y);
        System.out.println("Tiggers: " + triggers);
        System.out.println("D-Pad: " + dPadState);
        System.out.println("Left Bumper: " + leftBumper);
        System.out.println("Right Bumper: " + rightBumper);
        System.out.println("South Button: " + southButton);
        System.out.println("East Button: " + eastButton);
        System.out.println("West Button: " + westButton);
        System.out.println("North Button: " + northButton);
        System.out.println("");
    }
}