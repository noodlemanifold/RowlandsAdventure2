package tage.input;

import org.joml.Vector2i;

/**
 * Data class to hold state of the mouse and keyboard.
 */
public class MouseAndKeebState{
    
    public Vector2i mousePosition = new Vector2i(0,0);
    public Vector2i mouseDelta = new Vector2i(0,0);
    public int scrollDelta = 0;
    public boolean leftClick = false;
    public boolean rightClick = false;

    public boolean wKey = false;
    public boolean aKey = false;
    public boolean sKey = false;
    public boolean dKey = false;
    public boolean eKey = false;
    public boolean lKey = false;
    public boolean uKey = false;
    public boolean spaceKey = false;
    public boolean lShiftKey = false;
    public boolean lControlKey = false;

    public void print(){
        System.out.println("Mouse and Keeb State:");
        System.out.println("Mouse Position: " + mousePosition.x + ", " + mousePosition.y);
        System.out.println("Mouse Delta: " +  mouseDelta.x + ", " + mouseDelta.y);
        System.out.println("Scroll: " + scrollDelta);
        System.out.println("Left Click: " + leftClick);
        System.out.println("Right Click: " + rightClick);
        System.out.println("---");
        System.out.println("W Key: " + wKey);
        System.out.println("A Key: " + aKey);
        System.out.println("S Key: " + sKey);
        System.out.println("D Key: " + dKey);
        System.out.println("E Key: " + eKey);
        System.out.println("L Key: " + lKey);
        System.out.println("U Key: " + uKey);
        System.out.println("Space Key: " + spaceKey);
        System.out.println("Shift Key: " + lShiftKey);
        System.out.println("Control Key: " + lControlKey);
        System.out.println("");
    }
}