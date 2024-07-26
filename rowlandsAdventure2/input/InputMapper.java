package rowlandsAdventure2.input;

import org.joml.Vector2f;

import tage.Utils;
import tage.input.ControllerState;
import tage.input.MouseAndKeebState;

public class InputMapper{

    private static InputMapper instance;

    private RowlandInputs characterInputs;

    private float mouseLookSens = 0.17f;
    private float padLookSens = 2f;

    private InputMapper() {
        characterInputs = new RowlandInputs();
    }

    public static InputMapper singleton() {
        return createSingleton();
    }

    public static InputMapper createSingleton() {
        if (instance == null) {
            instance = new InputMapper();
        }
        return instance;
    }

    public RowlandInputs getCharacterInputs(){
        return characterInputs;
    }

    private Vector2f keebMove = new Vector2f(0f,0f);
    private Vector2f keebLook = new Vector2f(0f,0f);
    private Vector2f padMove = new Vector2f(0f,0f);
    private Vector2f padLook = new Vector2f(0f,0f);
    private boolean lastJump = false;
    private boolean lastPull = false;
    private boolean lastInteract = false;
    private boolean lastLight = false;
    private boolean lastUI = false;
    public void ResolveInputs(ControllerState controller, MouseAndKeebState kb){
        //turn kb button presses into vectors
        keebMove.set(0f,0f);
        keebLook.set(0f,0f);
        if (kb.aKey && !kb.dKey){
            keebMove.x = -1f;
        }
        if (kb.dKey && !kb.aKey){
            keebMove.x = 1f;
        }
        if (kb.wKey && !kb.sKey){
            keebMove.y = 1f;
        }
        if (kb.sKey && !kb.wKey){
            keebMove.y = -1f;
        }
        Utils.clampMagnitude(0f, 1f, keebMove);//no strafe boosting!

        keebLook.x = kb.mouseDelta.x * mouseLookSens;
        keebLook.y = -kb.mouseDelta.y * mouseLookSens;

        //setup pad inputs
        padMove.set(controller.leftStick);
        Utils.clampMagnitude(0f, 1f, padMove);
        if (padMove.length() > 0.9f) padMove.normalize();//controllers are not perfect, this helps
        padLook.set(controller.rightStick.x,-controller.rightStick.y);
        padLook.mul(padLookSens);
        padLook.y = padLook.y * 0.5f;//this feels better

        //choose strongest input for movement
        if (keebMove.lengthSquared() > padMove.lengthSquared()){
            characterInputs.move.set(keebMove);
        }else{
            characterInputs.move.set(padMove);
        }

        //combine inputs for camera because why not
        characterInputs.look.set(0f,0f);
        characterInputs.look.add(keebLook);
        characterInputs.look.add(padLook);

        characterInputs.jump = controller.southButton || kb.spaceKey;
        if (characterInputs.jump && !lastJump){
            characterInputs.jumpFirstie = true;
        }else{
            characterInputs.jumpFirstie = false;
        }
        lastJump = characterInputs.jump;

        characterInputs.pull = controller.leftBumper || controller.triggers < 0f || kb.lShiftKey || kb.lControlKey;
        if (characterInputs.pull && !lastPull){
            characterInputs.pullFirstie = true;
        }else{
            characterInputs.pullFirstie = false;
        }
        lastPull = characterInputs.pull;

        characterInputs.interact = controller.westButton || kb.eKey;
        if (characterInputs.interact && !lastInteract){
            characterInputs.interactFirstie = true;
        }else{
            characterInputs.interactFirstie = false;
        }
        lastInteract = characterInputs.interact;

        characterInputs.light = controller.eastButton || kb.lKey;
        if (characterInputs.light && !lastLight){
            characterInputs.lightFirstie = true;
        }else{
            characterInputs.lightFirstie = false;
        }
        lastLight = characterInputs.light;

        characterInputs.ui = controller.northButton || kb.uKey;
        if (characterInputs.ui && !lastUI){
            characterInputs.uiFirstie = true;
        }else{
            characterInputs.uiFirstie = false;
        }
        lastUI = characterInputs.ui;

    }
}