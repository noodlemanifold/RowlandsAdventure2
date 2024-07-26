package rowlandsAdventure2.npcs;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;

public class Billboard {

    private GameObject me;
    private float offset;
    public static ObjShape shapeShared;
    public static TextureImage defaultTex;
    private boolean textureExists = false;

    public Billboard(GameObject p, float o, TextureImage i, Vector3f scale){
        offset = o;
        //ObjShape shape = new ImportedModel("billboard.obj");
        me = new GameObject(GameObject.root(),shapeShared,defaultTex);
        me.setParent(p);
        me.propagateTranslation(true);
        me.propagateRotation(false);
        me.propagateScale(false);
        me.applyParentRotationToPosition(false);
        me.applyParentScaleToPosition(false);
        me.getRenderStates().disableRendering();
        me.getRenderStates().hasLighting(false);
        //me.getRenderStates().hasDepthTesting(false);
        me.setLocalScale(new Matrix4f().scale(scale));

        setTexture(i);
    }

    Vector3f offsetVector = new Vector3f();
    public void update(boolean enabled, Vector3f up, Vector3f cameraPos){
        if (enabled && textureExists){
            me.getRenderStates().enableRendering();
            offsetVector.set(up);
            offsetVector.mul(offset);
            me.setLocalLocation(offsetVector);
            me.lookAt(cameraPos,up);
        }else{
            me.getRenderStates().disableRendering();
        }
    }

    public void setTexture(TextureImage i){
        if (i != null){
            me.setTextureImage(i);
            textureExists = true;
        }else{
            textureExists = false;
        }
    }

}
