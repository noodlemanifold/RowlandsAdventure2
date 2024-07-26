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

public class SpherePlanet extends Planet{

    private float radius = 0f;
    private float gravStrength = 20f;

    public SpherePlanet(Engine engine, org.joml.Vector3f position, Quaternionf rotation, org.joml.Vector3f scale, TextureImage heightMap, TextureImage surfaceMap, TextureImage surfaceRamp,CollisionShape ghostShape, ImportedModel surfaceShape, float terrainHeight){
        super(engine,position,rotation,scale,heightMap,surfaceMap,surfaceRamp,ghostShape,surfaceShape,terrainHeight);
        radius = Math.max(scale.x, scale.y);
        radius = Math.max(scale.z, radius);
    }

    @Override
    public float[] getGravDir(float[] worldPos) {
        float[] pos = po.getWorldLocation();
        Vector3f gravDir = new Vector3f();
        gravDir.x = pos[0]-worldPos[0];
        gravDir.y = pos[1]-worldPos[1];
        gravDir.z = pos[2]-worldPos[2];
        gravDir = gravDir.normalize();
        return new float[]{gravDir.x,gravDir.y,gravDir.z};
    }

    org.joml.Vector3f gld_ld = new org.joml.Vector3f();
    org.joml.Vector3f gld_dd = new org.joml.Vector3f();
    org.joml.Vector3f gld_axis = new org.joml.Vector3f();
    @Override
    public float[] getLightDir(float[] worldPos) {
        float[] g = getGravDir(worldPos);
        gld_ld.set(-g[0],-g[1],-g[2]);
        Vector4f d = go.getRenderStates().getShadowDirection();
        gld_dd.set(d.x,d.y,d.z);
        gld_dd.cross(gld_ld, gld_axis);
        if (gld_axis.lengthSquared() > 0.00001f){
            gld_axis.normalize();
            gld_axis.cross(gld_ld);
            gld_ld.rotateAxis((float)Math.toRadians(30f),gld_axis.z,gld_axis.y,gld_axis.z);
        }
        return new float[]{gld_ld.x,gld_ld.y,gld_ld.z};
    }

    @Override
    public float getGravStrength(float[] worldPos) {
        return gravStrength;
    }

    public void setGravStrength(float g){
        gravStrength = g;
    }

    @Override
    public float getSurfaceDistance(float[] worldPos) {
        float[] pos = po.getWorldLocation();
        Vector3f dist = new Vector3f();
        dist.x = pos[0]-worldPos[0];
        dist.y = pos[1]-worldPos[1];
        dist.z = pos[2]-worldPos[2];
        return dist.length() - radius;
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
        float va = upGS.angle(lpGS);
        float v = (float)(1f-(va/Math.PI));
        float u = 0f;
        Utils.projectOntoPlane(lpGS, upGS);
        if (lpGS.lengthSquared() > 0.000000001f){
            lpGS.normalize();
            float ha = forGS.angleSigned(lpGS, upGS);
            if (ha < 0){
                ha = (float)((Math.PI + ha) + Math.PI);
            }
            u = (float)(ha / (2*Math.PI));
        }

        //determine surface type from color
        Vector4f surfCol = surfaceMap.getNearestPixel(u, v);
        surfCol.x *= surfCol.w;
        surfCol.y *= surfCol.w;
        surfCol.z *= surfCol.w;
        surfCol.w = 1-surfCol.w;
        int surfI = 0;
        int surfIa = 1;
        int surfIb = 3;
        float surfVala = surfCol.y;
        float surfValb = surfCol.w;
        if (surfCol.x > surfCol.y){
            surfIa = 0;
            surfVala = surfCol.x;
        } 
        if (surfCol.z > surfCol.w){
            surfIb = 2;
            surfValb = surfCol.z;
        }
        if (surfVala > surfValb){
            surfI = surfIa;
        }else{
            surfI = surfIb;
        }

        return surfaceList[surfI];
    }

}
