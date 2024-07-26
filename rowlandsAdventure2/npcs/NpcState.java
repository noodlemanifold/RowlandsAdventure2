package rowlandsAdventure2.npcs;

import tage.TextureImage;

public class NpcState {

    public String animation;
    public float animationSpeed;
    public TextureImage popUpText;
    public TextureImage expression;
    public NpcPathProvider pathProvider;

    public NpcState(String animation, float animationSpeed, TextureImage popUpText, TextureImage expression, NpcPathProvider pathProvider){
        this.animation = animation;
        this.animationSpeed = animationSpeed;
        this.popUpText = popUpText;
        this.expression = expression;
        this.pathProvider = pathProvider;
    }
}
