package rowlandsAdventure2.npcs;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import rowlandsAdventure2.planets.Planet;
import tage.Utils;

public class NpcPathStanding implements NpcPathProvider{

    Vector3f posDir = new Vector3f();
    Vector3f posPlanet = new Vector3f();
    float posHeight = 0f;
    Vector3f rotForward = new Vector3f();
    float speed = 1f;

    public NpcPathStanding(Vector3f posDir, float posHeight, Vector3f forward, float speed){
        this.posDir.set(posDir);
        this.posDir.normalize();
        this.posHeight = posHeight;
        this.rotForward.set(forward);
        Utils.projectOntoPlane(rotForward, posDir, posDir);
        this.rotForward.normalize();
        this.speed = speed;
    }

    @Override
    public void SetPlanet(Planet p) {
        posPlanet.set(p.getLocation());
    }

    Vector3f newPos = new Vector3f();
    @Override
    public Vector3f GetPosition() {
        newPos.set(posDir);
        newPos.mul(posHeight);
        newPos.add(posPlanet);
        return newPos;
    }

    @Override
    public Matrix4f GetRotation() {
        return Utils.matLookTowards(rotForward, posDir);
    }

    @Override
    public Vector3f GetUp() {
        return posDir;
    }

    @Override
    public float GetAnimSpeed() {
        return speed;
    }

}
