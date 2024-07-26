package rowlandsAdventure2.npcs;

import org.joml.Vector3f;

import rowlandsAdventure2.character.CharacterController;
import rowlandsAdventure2.planets.Planet;

public class NpcEventDriverFlowerHack  extends NpcEventDriver{


    public NpcEventDriverFlowerHack (DriverState[] s, Npc[] n, Planet p, CharacterController c){
        super(s,n,p,c);
    }

    @Override
    public void update(Vector3f charPos, Vector3f cameraPos){
        if (state == 4){
            player.setAvatarState(1);
        }
        super.update(charPos, cameraPos);
    }
}
