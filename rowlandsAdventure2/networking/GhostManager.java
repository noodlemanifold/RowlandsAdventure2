package rowlandsAdventure2.networking;

import java.util.HashMap;
import java.util.UUID;

import rowlandsAdventure2.character.CharacterAnimator;
import rowlandsAdventure2.character.CharacterAnimator.CharacterAvatar;
import tage.Time;
import rowlandsAdventure2.character.CharacterState;

public class GhostManager {

    private CharacterAnimator animator;
    //private ArrayList<CharacterAvatar> ghostAvatars;
    private HashMap<UUID,CharacterAvatar> ghostAvatars;

    public GhostManager(CharacterAnimator anim){
    	animator = anim;
        ghostAvatars = new HashMap<UUID,CharacterAvatar>();
	}

    public void createGhostAvatar(UUID id, CharacterState state, int texIndex){
        System.out.println("adding ghost with ID --> " + id + " and texture ID: " + texIndex);
		CharacterAvatar newAvatar = animator.createAvatar(texIndex);
		ghostAvatars.put(id, newAvatar);
        CharacterAnimator.move(newAvatar, state.position);
        CharacterAnimator.faceTowards(newAvatar, state.dir, state.up);
    }

    public void removeGhostAvatar(UUID id){
        CharacterAvatar removed = ghostAvatars.remove(id);
        if (removed == null){
            System.out.println("tried to remove, but unable to find ghost in list");
        }
    }

    public void updateGhostAvatar(UUID id, CharacterState state){
        CharacterAvatar ghost = ghostAvatars.get(id);
        if (ghost == null){
            System.out.println("tried to update ghost avatar position, but unable to find ghost in list");
        }else{
            CharacterAnimator.move(ghost, state.position);
            CharacterAnimator.faceTowards(ghost, state.dir, state.up);
            CharacterAnimator.roll(ghost, state.horizontalSpeed*Time.deltaTime);
            CharacterAnimator.state(ghost, state.avatarState);
            CharacterAnimator.planetLight(ghost, state.position, state.planetIndex);
        }
    }

}
