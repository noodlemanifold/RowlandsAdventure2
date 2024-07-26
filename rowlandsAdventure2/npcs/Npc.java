package rowlandsAdventure2.npcs;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import rowlandsAdventure2.planets.Planet;
import tage.GameObject;
import tage.TextureImage;
import tage.Time;
import tage.shapes.AnimatedShape;

public class Npc {

    private NpcState[] states;
    private int state = 0;
    private GameObject go;
    private AnimatedShape shape;
    private Billboard bill;
    private float popUpRadiusSqr;

    public Npc(AnimatedShape shape, TextureImage tex, int shaderIndex, NpcState[] states, float scale, float popUpRadius){
        this.states = states;
        this.shape = shape;
        this.popUpRadiusSqr = popUpRadius * popUpRadius;
        go = new GameObject(GameObject.root(),shape,tex);
        go.setLocalLocation(this.states[0].pathProvider.GetPosition());
        go.setLocalRotation(this.states[0].pathProvider.GetRotation());
        go.setLocalScale(new Matrix4f().scale(scale));
        //go.getRenderStates().hasLighting(false);
        if (shaderIndex >= 0){
            go.getRenderStates().setCustomProgram(shaderIndex);
        }

        bill = new Billboard(go, 2f*scale + 0.25f, this.states[0].popUpText, new Vector3f(2f,1f,1f).mul(0.7f));
    }

    public void initState(int s, Planet p){
        state = s;
        shape.stopAnimation();
        shape.playAnimation(states[state].animation, states[state].animationSpeed, AnimatedShape.EndType.LOOP, 0);
        bill.setTexture(states[state].popUpText);
        go.setTextureImage2(states[state].expression);
        states[state].pathProvider.SetPlanet(p);//this is done here for my own sanity making all the npc objects, this is awful for modularity
    }

    public void update(Vector3f charPos, Vector3f cameraPos, Planet p){
        shape.updateAnimation(Time.deltaTime, states[state].pathProvider.GetAnimSpeed());
        //TODO: wait for path provider location to get close
        Vector3f pos = states[state].pathProvider.GetPosition();
        go.setLocalLocation(pos);
        go.setLocalRotation(states[state].pathProvider.GetRotation());
        boolean inRadius = go.getLocalLocation().distanceSquared(charPos) < popUpRadiusSqr;
        bill.update(inRadius, states[state].pathProvider.GetUp(), cameraPos);

        float[] lightDir = p.getLightDir(new float[]{pos.x,pos.y,pos.z});
        go.getRenderStates().setShadowDirection(new Vector4f(lightDir[0],lightDir[1],lightDir[2],0f));

        float pl = p.getLight(new float[]{pos.x,pos.y,pos.z});
        go.getRenderStates().setPlanetLight(pl);

        float sd = p.getShadowDistance();
        go.getRenderStates().setShadowDistance(sd);
    }

}
