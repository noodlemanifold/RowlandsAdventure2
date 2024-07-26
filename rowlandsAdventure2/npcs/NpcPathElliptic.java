package rowlandsAdventure2.npcs;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import rowlandsAdventure2.planets.Planet;
import tage.Time;
import tage.Utils;

public class NpcPathElliptic implements NpcPathProvider {

    private Vector3f centerPosDir = new Vector3f();
    private float centerPosDist;
    private Vector3f cosDir = new Vector3f();
    private Vector3f sinDir = new Vector3f();
    private float speed;
    private float sinF;
    private float sinR;
    private float cosF;
    private float cosR;

    private Vector3f planetPos = new Vector3f();

    public NpcPathElliptic(Vector3f centerPosDir, float centerPosDist, Vector3f startOffset, float speed, float sinF, float cosF, float sinR, float cosR){
        this.centerPosDir.set(centerPosDir);
        this.centerPosDir.normalize();
        this.centerPosDist = centerPosDist;
        this.cosDir.set(startOffset);
        Utils.projectOntoPlane(cosDir, this.centerPosDir, this.centerPosDir);
        this.cosDir.normalize();
        this.centerPosDir.cross(cosDir, sinDir);
        this.sinDir.normalize();
        this.speed = speed;
        this.sinF = sinF;
        this.cosF = cosF;
        this.sinR = sinR;
        this.cosR = cosR;
    }

    @Override
    public void SetPlanet(Planet p) {
        planetPos.set(p.getLocation());
    }

    Vector3f upCache = new Vector3f();
    Vector3f newPos = new Vector3f();
    Vector3f posOffset = new Vector3f();
    Vector3f cosComp = new Vector3f();
    Vector3f sinComp = new Vector3f();
    @Override
    public Vector3f GetPosition() {
        cosComp.set(cosDir);
        cosComp.mul(cosR * (float)Math.cos(Time.serverElapsedTime * 2 * Math.PI * cosF));
        sinComp.set(sinDir);
        sinComp.mul(sinR * (float)Math.sin(Time.serverElapsedTime * 2 * Math.PI * sinF));
        newPos.set(centerPosDir);
        newPos.mul(centerPosDist);
        posOffset.set(0f,0f,0f);
        posOffset.add(cosComp);
        posOffset.add(sinComp);
        newPos.add(posOffset);
        newPos.normalize(centerPosDist);
        newPos.normalize(upCache);
        newPos.add(planetPos);
        return newPos;
    }


    Vector3f forwardDir = new Vector3f();
    Vector3f deltaCos = new Vector3f();
    Vector3f deltaSin = new Vector3f();
    float ellipseSpeed = 0f;
    @Override
    public Matrix4f GetRotation() {//also assume this is called after position. a bit jank I know Im sorry
        //upCache.cross(posOffset,forwardDir);
        deltaCos.set(cosDir);
        deltaCos.mul(cosR * (float)(2 * Math.PI * cosF) * (float)-Math.sin(Time.serverElapsedTime * 2 * Math.PI * cosF));
        deltaSin.set(sinDir);
        deltaSin.mul(sinR * (float)(2 * Math.PI * sinF) * (float)Math.cos(Time.serverElapsedTime * 2 * Math.PI * sinF));
        ellipseSpeed = 0f;
        if (deltaCos.lengthSquared()>0.000000001f && deltaSin.lengthSquared()>0.000000001f){
            forwardDir.set(0f,0f,0f);
            forwardDir.add(deltaCos);
            forwardDir.add(deltaSin);
            ellipseSpeed = forwardDir.length();
            forwardDir.normalize();
        }
        return Utils.matLookTowards(forwardDir, upCache);
    }

    @Override
    public Vector3f GetUp() {//assuming this is called later, just return cache from GetPosition() call to save performance!
        return upCache;
    }

    @Override
    public float GetAnimSpeed() {//same here lol
        return speed * ellipseSpeed;
    }

    
    
    
}
