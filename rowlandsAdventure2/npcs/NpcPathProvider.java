package rowlandsAdventure2.npcs;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import rowlandsAdventure2.planets.Planet;

public interface NpcPathProvider {

    public void SetPlanet(Planet p);

    public Vector3f GetPosition();

    public Matrix4f GetRotation();

    public Vector3f GetUp();

    public float GetAnimSpeed();

}
