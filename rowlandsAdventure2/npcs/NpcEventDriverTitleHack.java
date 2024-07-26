package rowlandsAdventure2.npcs;

import org.joml.Vector2f;
import org.joml.Vector3f;

import rowlandsAdventure2.MyGame;
import rowlandsAdventure2.character.CharacterController;
import rowlandsAdventure2.input.InputMapper;
import rowlandsAdventure2.planets.Planet;

public class NpcEventDriverTitleHack  extends NpcEventDriver{

    private MyGame game;

    public NpcEventDriverTitleHack (DriverState[] s, Npc[] n, Planet p, CharacterController c, MyGame g){
        super(s,n,p,c);
        game = g;
    }

    int texI = -1;
    @Override
    public void update(Vector3f charPos, Vector3f cameraPos){
        if (state == 1){
            Vector2f i = InputMapper.singleton().getCharacterInputs().move;
            if (i.length() > 0.8f){
                if (i.dot(new Vector2f(-1f,0f)) > 0.8f){
                    texI = 0;
                }else
                if (i.dot(new Vector2f(0f,1f)) > 0.8f){
                    texI = 1;
                }else
                if (i.dot(new Vector2f(1f,0f)) > 0.8f){
                    texI = 2;
                }else
                if (i.dot(new Vector2f(0f,-1f)) > 0.8f){
                    texI = 3;
                }
            }

            if (texI >= 0){
                game.setupNetworking(texI);
                super.nextState();
            }
        }
        super.update(charPos, cameraPos);
    }
}
