package tage.input;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.ImageIcon;

import org.joml.Vector2i;

import com.jogamp.opengl.awt.GLCanvas;

import java.awt.Image;
import net.java.games.input.Component;
import net.java.games.input.Event;
import tage.Engine;
import tage.RenderSystem;
import tage.Utils;
import tage.Viewport;
import tage.input.action.AbstractInputAction;

/**
 * This class handles all the input binding for actions and Java AWT Keyboard and Mouse Listeners in one place.
 * It takes in all the data and exposes it in a {@link ControllerState} and a {@link MouseAndKeebState} object.
 * It is up to the game to decide how to reconcile and process these states.
 * All controller buttons are included int the controller state, but only keyboard keys I needed are included in
 * MouseAndKeebState.
 */

public class InputManagerManager implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

    private static InputManagerManager instance;

    private InputManager manager;
    private RenderSystem rs;
    private Robot robot;

    private ControllerState controllerState;
    private MouseAndKeebState mouseAndKeebState;

    IInputManager.INPUT_ACTION_TYPE actionType = InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE;
    private float deadzone = 0.3f;// high for those poor old logitechs :(

    private boolean scrollDirty = false;
    private Vector2i mousePosition = new Vector2i(0, 0);
    private Vector2i mousePositionLast = new Vector2i(0, 0);
    private Vector2i mouseDelta = new Vector2i(0, 0);
    private Vector2i screenCenter = new Vector2i(0, 0);
    private Vector2i mouseBounds = new Vector2i(0,0);
    private boolean lockMouse = true;
    private boolean recentering = false;

    private InputManagerManager(Engine engine) {
        manager = engine.getInputManager();
        rs = engine.getRenderSystem();
        controllerState = new ControllerState();
        mouseAndKeebState = new MouseAndKeebState();

        rs.getGLCanvas().addKeyListener(this);
        rs.getGLCanvas().addMouseListener(this);
        rs.getGLCanvas().addMouseMotionListener(this);
        rs.getGLCanvas().addMouseWheelListener(this);

        calcScreenStats();

        try {// note that some platforms may not support the Robot class
            robot = new Robot();
        } catch (AWTException ex) {
            throw new RuntimeException("Couldn't create Robot! :(");
        }

        Image faceImage = new ImageIcon("./assets/textures/cursor.png").getImage();
        Cursor faceCursor = Toolkit.getDefaultToolkit().
            createCustomCursor(faceImage, new Point(0,0), "FaceCursor");
        GLCanvas canvas = rs.getCanvas();
        canvas.setCursor(faceCursor);

        // code to change mouse cursor icon on canvas!!

        bindControllerConponents();
    }

    /**
     * Get the singleton reference
     */
    public static InputManagerManager singleton() {
        return instance;
    }

    /**
     * Special call to create the singleton object if it doesn't exist.
     * This pattern sucks, I switched to static abstract classes for the rest of the project
     */
    public static InputManagerManager createSingleton(Engine engine) {
        if (instance == null) {
            instance = new InputManagerManager(engine);
        }
        return instance;
    }

    /**
     * Update function. This needs to be called once a frame with the total elapsed time
     */
    public void update(float elapsedTime) {
        if (!scrollDirty)
            mouseAndKeebState.scrollDelta = 0;
        scrollDirty = false;

        mousePosition.sub(mousePositionLast, mouseDelta);
        mousePositionLast.set(mousePosition);
        if (rs.HasWindowFocus()){
            mouseAndKeebState.mousePosition.set(mousePosition);
            mouseAndKeebState.mouseDelta.set(mouseDelta);
        }

        manager.update(elapsedTime);

        if (lockMouse && rs.HasWindowFocus()){
            if (Math.abs(mousePosition.x-screenCenter.x) > mouseBounds.x ||
                Math.abs(mousePosition.y-screenCenter.y) > mouseBounds.y){
                recenterMouse();
            }
        }
        manager.update(elapsedTime+Utils.epsilon);
    }

    public void screenResized(){
        calcScreenStats();
    }

    private void recenterMouse() { // use the robot to move the mouse to the center point.
                                   // Note that this generates one MouseEvent.
        recentering = true;
        robot.mouseMove(screenCenter.x, screenCenter.y);
    }

    private void calcScreenStats(){
        Viewport vw = rs.getViewport("MAIN");
        float left = vw.getActualLeft();
        float bottom = vw.getActualBottom();
        float width = vw.getActualWidth();
        float height = vw.getActualHeight();
        screenCenter.x = (int) (left + width / 2);
        screenCenter.y = (int) (bottom - height / 2);
        mouseBounds.x = (int) (width * 0.25f);
        mouseBounds.y = (int) (height * 0.25f);
    }

    /**
     * Get current state of all gamepad devices
     */
    public ControllerState getControllerState() {
        return controllerState;
    }

    /**
     * Get current state of all Mouse and Keyboard buttons
     */
    public MouseAndKeebState getMouseAndKeebState() {
        return mouseAndKeebState;
    }

    public void SetMouseLock(boolean lock) {
        lockMouse = lock;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                mouseAndKeebState.wKey = true;
                break;
            case KeyEvent.VK_A:
                mouseAndKeebState.aKey = true;
                break;
            case KeyEvent.VK_S:
                mouseAndKeebState.sKey = true;
                break;
            case KeyEvent.VK_D:
                mouseAndKeebState.dKey = true;
                break;
            case KeyEvent.VK_E:
                mouseAndKeebState.eKey = true;
                break;
            case KeyEvent.VK_L:
                mouseAndKeebState.lKey = true;
                break;
            case KeyEvent.VK_U:
                mouseAndKeebState.uKey = true;
                break;
            case KeyEvent.VK_SPACE:
                mouseAndKeebState.spaceKey = true;
                break;
            case KeyEvent.VK_SHIFT:
                mouseAndKeebState.lShiftKey = true;
                break;
            case KeyEvent.VK_CONTROL:
                mouseAndKeebState.lControlKey = true;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                mouseAndKeebState.wKey = false;
                break;
            case KeyEvent.VK_A:
                mouseAndKeebState.aKey = false;
                break;
            case KeyEvent.VK_S:
                mouseAndKeebState.sKey = false;
                break;
            case KeyEvent.VK_D:
                mouseAndKeebState.dKey = false;
                break;
            case KeyEvent.VK_E:
                mouseAndKeebState.eKey = false;
                break;
            case KeyEvent.VK_L:
                mouseAndKeebState.lKey = false;
                break;
            case KeyEvent.VK_U:
                mouseAndKeebState.uKey = false;
                break;
            case KeyEvent.VK_SPACE:
                mouseAndKeebState.spaceKey = false;
                break;
            case KeyEvent.VK_SHIFT:
                mouseAndKeebState.lShiftKey = false;
                break;
            case KeyEvent.VK_CONTROL:
                mouseAndKeebState.lControlKey = false;
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        scrollDirty = true;
        mouseAndKeebState.scrollDelta = e.getWheelRotation() * e.getScrollAmount();
    }

    Vector2i movedDist = new Vector2i(0,0);
    @Override
    public void mouseDragged(MouseEvent e) {
        if (recentering){
            movedDist.x = e.getXOnScreen();
            movedDist.y = e.getYOnScreen();
            movedDist.sub(mousePosition);
            movedDist.mul(-1);
            mousePositionLast.sub(movedDist);
        }
        mousePosition.x = e.getXOnScreen();
        mousePosition.y = e.getYOnScreen();
        recentering = false;
    }
    @Override
    public void mouseMoved(MouseEvent e) {
        if (recentering){
            movedDist.x = e.getXOnScreen();
            movedDist.y = e.getYOnScreen();
            movedDist.sub(mousePosition);
            movedDist.mul(-1);
            mousePositionLast.sub(movedDist);
        }
        mousePosition.x = e.getXOnScreen();
        mousePosition.y = e.getYOnScreen();
        recentering = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // you can get double clicks and stuff here
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        calcScreenStats();
        mousePosition.x = e.getXOnScreen();
        mousePosition.y = e.getYOnScreen();
        mousePositionLast.x = e.getXOnScreen();
        mousePositionLast.y = e.getYOnScreen();
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public class LeftVerticalAction extends AbstractInputAction {
        @Override
        public void performAction(float time, Event evt) {
            float raw = -evt.getValue();
            if (Math.abs(raw) < deadzone)
                raw = 0f;
            controllerState.leftStick.y = raw;
        }
    }

    private class LeftHorizontalAction extends AbstractInputAction {
        @Override
        public void performAction(float time, Event evt) {
            float raw = evt.getValue();
            if (Math.abs(raw) < deadzone)
                raw = 0f;
            controllerState.leftStick.x = raw;
        }
    }

    public class RightVerticalAction extends AbstractInputAction {
        @Override
        public void performAction(float time, Event evt) {
            float raw = -evt.getValue();
            if (Math.abs(raw) < deadzone)
                raw = 0f;
            controllerState.rightStick.y = raw;
        }
    }

    private class RightHorizontalAction extends AbstractInputAction {
        @Override
        public void performAction(float time, Event evt) {
            float raw = evt.getValue();
            if (Math.abs(raw) < deadzone)
                raw = 0f;
            controllerState.rightStick.x = raw;
        }
    }

    private class TriggerAction extends AbstractInputAction {
        @Override
        public void performAction(float time, Event evt) {
            float raw = evt.getValue();
            if (Math.abs(raw) < deadzone)
                raw = 0f;
            controllerState.triggers = raw;
        }
    }

    private class LeftBumperAction extends AbstractInputAction {
        @Override
        public void performAction(float time, Event evt) {
            boolean value = evt.getValue() > 0.5f;
            controllerState.leftBumper = value;
        }
    }

    private class RightBumperAction extends AbstractInputAction {
        @Override
        public void performAction(float time, Event evt) {
            boolean value = evt.getValue() > 0.5f;
            controllerState.rightBumper = value;
        }
    }

    private class DPadAction extends AbstractInputAction {
        @Override
        public void performAction(float time, Event evt) {
            float raw = evt.getValue();
            int value = (int) (raw * 1000f);
            switch (value) {
                case 0:
                    controllerState.dPadState = ControllerState.DPadState.None;
                    break;
                case 125:
                    controllerState.dPadState = ControllerState.DPadState.UpLeft;
                    break;
                case 250:
                    controllerState.dPadState = ControllerState.DPadState.Up;
                    break;
                case 375:
                    controllerState.dPadState = ControllerState.DPadState.UpRight;
                    break;
                case 500:
                    controllerState.dPadState = ControllerState.DPadState.Right;
                    break;
                case 625:
                    controllerState.dPadState = ControllerState.DPadState.DownRight;
                    break;
                case 750:
                    controllerState.dPadState = ControllerState.DPadState.Down;
                    break;
                case 875:
                    controllerState.dPadState = ControllerState.DPadState.DownLeft;
                    break;
                case 1000:
                    controllerState.dPadState = ControllerState.DPadState.Left;
                    break;
            }
        }
    }

    private class NorthAction extends AbstractInputAction {
        @Override
        public void performAction(float time, Event evt) {
            boolean value = evt.getValue() > 0.5f;
            controllerState.northButton = value;
        }
    }

    private class SouthAction extends AbstractInputAction {
        @Override
        public void performAction(float time, Event evt) {
            boolean value = evt.getValue() > 0.5f;
            controllerState.southButton = value;
        }
    }

    private class EastAction extends AbstractInputAction {
        @Override
        public void performAction(float time, Event evt) {
            boolean value = evt.getValue() > 0.5f;
            controllerState.eastButton = value;
        }
    }

    private class WestAction extends AbstractInputAction {
        @Override
        public void performAction(float time, Event evt) {
            boolean value = evt.getValue() > 0.5f;
            controllerState.westButton = value;
        }
    }

    private void bindControllerConponents() {
        LeftVerticalAction lvAction = new LeftVerticalAction();
        manager.associateActionWithAllGamepads(Component.Identifier.Axis.Y, lvAction, actionType);

        LeftHorizontalAction lhAction = new LeftHorizontalAction();
        manager.associateActionWithAllGamepads(Component.Identifier.Axis.X, lhAction, actionType);

        RightVerticalAction rvAction = new RightVerticalAction();
        manager.associateActionWithAllGamepads(Component.Identifier.Axis.RY, rvAction, actionType);

        RightHorizontalAction rhAction = new RightHorizontalAction();
        manager.associateActionWithAllGamepads(Component.Identifier.Axis.RX, rhAction, actionType);

        TriggerAction tAction = new TriggerAction();
        manager.associateActionWithAllGamepads(Component.Identifier.Axis.Z, tAction, actionType);

        LeftBumperAction lbAction = new LeftBumperAction();
        manager.associateActionWithAllGamepads(Component.Identifier.Button._4, lbAction, actionType);

        RightBumperAction rbAction = new RightBumperAction();
        manager.associateActionWithAllGamepads(Component.Identifier.Button._5, rbAction, actionType);

        DPadAction dpAction = new DPadAction();
        manager.associateActionWithAllGamepads(Component.Identifier.Axis.POV, dpAction, actionType);

        SouthAction sAction = new SouthAction();
        manager.associateActionWithAllGamepads(Component.Identifier.Button._0, sAction, actionType);

        EastAction eAction = new EastAction();
        manager.associateActionWithAllGamepads(Component.Identifier.Button._1, eAction, actionType);

        WestAction wAction = new WestAction();
        manager.associateActionWithAllGamepads(Component.Identifier.Button._2, wAction, actionType);

        NorthAction nAction = new NorthAction();
        manager.associateActionWithAllGamepads(Component.Identifier.Button._3, nAction, actionType);

    }

}