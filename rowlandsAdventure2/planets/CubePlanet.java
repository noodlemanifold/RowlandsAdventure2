package rowlandsAdventure2.planets;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.math.Vector3f;

import rowlandsAdventure2.character.CharacterSurface;
import tage.Engine;
import tage.TextureImage;
import tage.Utils;
import tage.shapes.ImportedModel;

public class CubePlanet extends Planet {

    private float maxRadius;
    private float power;

    public CubePlanet(Engine engine, org.joml.Vector3f position, Quaternionf rotation, org.joml.Vector3f scale, TextureImage heightMap, TextureImage surfaceMap, TextureImage surfaceRamp,CollisionShape ghostShape, ImportedModel surfaceShape, float terrainHeight, float power){
        super(engine,position,rotation,scale,heightMap,surfaceMap,surfaceRamp,ghostShape,surfaceShape,terrainHeight);
        maxRadius = Math.max(scale.x, scale.y);
        maxRadius = Math.max(scale.z, maxRadius);
        this.power = power;
    }

    Matrix4f transGD = new Matrix4f();
    org.joml.Vector4f pGD = new org.joml.Vector4f();
    org.joml.Vector3f lpGD = new org.joml.Vector3f();
    @Override
    public float[] getGravDir(float[] worldPos) {
        transGD = new Matrix4f();
        transGD.mul(go.getWorldTranslation());
        transGD.mul(go.getWorldRotation());
        transGD.mul(go.getWorldScale());
        transGD.invert();
        pGD.set(worldPos[0],worldPos[1],worldPos[2],1);
        pGD.mul(transGD);
        lpGD.set(pGD.x,pGD.y,pGD.z);
        //lpGD.normalize();
        lpGD.x = (float)Math.pow(Math.abs(lpGD.x), power)*Math.signum(lpGD.x);
        lpGD.y = (float)Math.pow(Math.abs(lpGD.y), power)*Math.signum(lpGD.y);
        lpGD.z = (float)Math.pow(Math.abs(lpGD.z), power)*Math.signum(lpGD.z);
        transGD.invert();
        pGD.set(lpGD.x,lpGD.y,lpGD.z,1f);
        pGD.mul(transGD);
        lpGD.set(pGD.x,pGD.y,pGD.z);
        lpGD.sub(go.getWorldLocation());
        lpGD.normalize();
        lpGD.mul(-1f);
        

        return new float[]{lpGD.x,lpGD.y,lpGD.z};
        //return new float[]{0f,1f,0f};
    }

    @Override
    public float[] getLightDir(float[] worldPos) {
        float[] g = getGravDir(worldPos);
        return new float[]{-g[0],-g[1],-g[2]};
    }

    @Override
    public float getGravStrength(float[] worldPos) {
        return 20f;
    }

    @Override
    public float getSurfaceDistance(float[] worldPos) {
        //approximating as a sphere is good enough 
        float[] pos = po.getWorldLocation();
        Vector3f dist = new Vector3f();
        dist.x = pos[0]-worldPos[0];
        dist.y = pos[1]-worldPos[1];
        dist.z = pos[2]-worldPos[2];
        return dist.length() - maxRadius;
    }

    Matrix4f transGS = new Matrix4f();
    org.joml.Vector4f pGS = new org.joml.Vector4f();
    org.joml.Vector3f lpGS = new org.joml.Vector3f();
    org.joml.Vector3f upGS = new org.joml.Vector3f(0f,1f,0f);
    org.joml.Vector3f forGS = new org.joml.Vector3f(0f,0f,-1f);
    @Override
    public CharacterSurface getSurface(float[] pos) {
        //calculate UV coord
        transGS = new Matrix4f();
        transGS.mul(go.getWorldTranslation());
        transGS.mul(go.getWorldRotation());
        transGS.mul(go.getWorldScale());
        transGS.invert();
        pGS.set(pos[0],pos[1],pos[2],1);
        pGS.mul(transGS);
        lpGS.set(pGS.x,pGS.y,pGS.z);
        lpGS.normalize();
        //this is possible but im tired and its the last week and this is good enough
        return surfaceList[0];
    }
    
}
