package rowlandsAdventure2.npcs;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import tage.Camera;
import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;

public abstract class ScreenBillboard {

    public static ObjShape shape;
    public static TextureImage tex;
    public static GameObject go;
    public static Camera cam;

    public static void init(Camera c){
        cam = c;
        go = new GameObject(GameObject.root(),shape,tex);
        go.getRenderStates().hasLighting(false);
        go.getRenderStates().hasDepthTesting(false);
        go.setLocalScale(new Matrix4f().scale(1.15f));
        go.getRenderStates().disableRendering();
        go.getRenderStates().isTransparent(true);
    }

    public static void setImage(TextureImage i){
        if (i == null){
            go.getRenderStates().disableRendering();
        }else{
            go.getRenderStates().enableRendering();
            go.setTextureImage(i);
        }
    }

    private static Vector3f pos = new Vector3f();
    private static Vector3f forward = new Vector3f();
    private static Vector3f up = new Vector3f();
    public static void update(){
        pos.set(cam.getLocation());
        forward.set(cam.getN());
        up.set(cam.getV());
        pos.add(forward);
        forward.negate();
        go.setLocalLocation(pos);
        go.lookTowards(forward,up);
    }

}
