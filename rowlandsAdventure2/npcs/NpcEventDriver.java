package rowlandsAdventure2.npcs;

import org.joml.Vector3f;

import rowlandsAdventure2.character.CharacterController;
import rowlandsAdventure2.input.InputMapper;
import rowlandsAdventure2.npcs.DriverState.StateAdvanceType;
import rowlandsAdventure2.planets.Planet;
import tage.GameObject;
import tage.TextureImage;
import tage.Time;

public class NpcEventDriver {

    private DriverState[] states;
    private Npc[] npcs;
    protected int state = 0;
    private Billboard bill;
    private GameObject go;
    private Planet planet;
    protected CharacterController player;
    private boolean honk = true;

    private float stateTime = -1f;

    public static TextureImage dialoguePrompt;

    public NpcEventDriver (DriverState[] s, Npc[] n, Planet p, CharacterController c){
        states = s;
        npcs = n;
        planet = p;
        player = c;

        go = new GameObject(GameObject.root());
        go.setLocalLocation(calcPos());
        bill = new Billboard(go, 1.8f, dialoguePrompt, new Vector3f(1.8f,1f,1f).mul(0.5f));
    }

    Vector3f localUp = new Vector3f();
    public void update(Vector3f charPos, Vector3f cameraPos){
        Vector3f pos = calcPos();
        go.setLocalLocation(pos);

        boolean radiusActive = false;
        float charDistSqr = pos.distanceSquared(charPos);
        if (states[state].stateAdvance == StateAdvanceType.DISTANCE && charDistSqr < states[state].driverRadius * states[state].driverRadius){
            radiusActive = true;
        }

        if (states[state].stateAdvance == StateAdvanceType.DISTANCE && radiusActive && InputMapper.singleton().getCharacterInputs().interactFirstie){
            advanceState();
        }else if (states[state].stateAdvance == StateAdvanceType.DIALOGUE && InputMapper.singleton().getCharacterInputs().interactFirstie){
            advanceState();
        }

        if (stateTime < 0f){
            stateTime = Time.elapsedTime;
        }

        float timeDelta = Time.elapsedTime - stateTime;
        if (timeDelta == 0){
            ScreenBillboard.setImage(states[state].dialogueImage);
            initNpcs(planet);
            DriverState s = states[state];
            s.driverPosDir.normalize();
            player.setEnabled(!s.lockCharacter);
            if(s.lockCharacter){
                if (planet != null && s.lookTarget != null && s.characterPos != null && s.cameraPos != null){
                    player.setTransform(s.characterPos, s.lookTarget);
                    player.setCameraTransform(s.cameraPos, s.lookTarget);
                }
            }
        }

        localUp.set(0f,-1f,0f);
        if (planet != null)
            localUp.set(planet.getGravDir(new float[]{pos.x,pos.y,pos.z}));
        localUp.negate();
        bill.update(radiusActive, localUp, cameraPos);

        updateNpcs(localUp, charPos, cameraPos);
    }

    public void nextState(){
        if (states[state].stateAdvance == StateAdvanceType.EXTERNAL){
            advanceState();
        }
    }

    public void disableHonks(){
        honk = false;
    }

    private void advanceState(){
        int lastState = state;
        state++;
        if (state >= states.length){
            if (states.length > 1){
                state = states.length-2;//reopen last dialogue?
            }else{
                state = states.length-1;
            }
        }

        if (states[state].lockCharacter && honk){
            player.honk();
        }


        if (lastState != state){
            stateTime = -1;
        }
    }

    private void initNpcs(Planet p){
        if (npcs == null || p == null) return;
        for (Npc npc : npcs){
            npc.initState(state, p);
        }
    }

    private void updateNpcs(Vector3f up, Vector3f charPos, Vector3f cameraPos){
        if (npcs == null) return;
        for (Npc npc : npcs){
            npc.update(charPos, cameraPos, planet);
        }
    }

    Vector3f posObj = new Vector3f();
    private Vector3f calcPos(){
        posObj.set(states[state].driverPosDir);
        posObj.mul(states[state].driverPosRadius);
        if (planet != null){
            posObj.add(planet.getLocation());
        }
        return posObj;
    }

}
