package rowlandsAdventure2.npcs;

import org.joml.Vector3f;

import rowlandsAdventure2.MyGame;
import rowlandsAdventure2.character.CharacterController;
import rowlandsAdventure2.planets.Planet;
import tage.Engine;
import tage.GameObject;

public class NpcEventDriverEndHack  extends NpcEventDriver{

    Engine e;
    GameObject f;

    public NpcEventDriverEndHack (DriverState[] s, Npc[] n, Planet p, CharacterController c, Engine e, GameObject f){
        super(s,n,p,c);
        this.e = e;
        this.f = f;
    }

    @Override
    public void update(Vector3f charPos, Vector3f cameraPos){
        if (state == 1 && player.getAvatarState() == 1){
            player.setAvatarState(2);
            MyGame g = (MyGame)e.getGame();
            g.gameFinished();
            f.getRenderStates().enableRendering();
        }
        super.update(charPos, cameraPos);
    }
}
