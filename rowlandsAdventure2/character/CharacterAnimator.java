package rowlandsAdventure2.character;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import rowlandsAdventure2.builders.CommonResources;
import rowlandsAdventure2.planets.Planet;
import rowlandsAdventure2.planets.PlanetManager;
import tage.GameObject;
import tage.ObjShape;
import tage.TextureImage;
import tage.shapes.ImportedModel;

public class CharacterAnimator {

    private ObjShape coreS;
    private ObjShape eyeS;
    private ObjShape gyroS;
    private ObjShape headS;
    private ObjShape headGearS;
    private ObjShape headMountS;
    private ObjShape shellS;
    private ObjShape sliderS;
    private ObjShape flowerS;
    private TextureImage tex1;
    private TextureImage tex2;
    private TextureImage tex3;
    private TextureImage tex4;
    private TextureImage flowerTex;
    private TextureImage exp1;

    //private TextureImage rowlandTex;

    private static final float scale = 0.8f;

    public class CharacterAvatar {
        public GameObject rootGO;
        public GameObject coreGO;
        public GameObject eyeGO;
        public GameObject gyroGO;
        public GameObject headGO;
        public GameObject headGearGO;
        public GameObject headMountGO;
        public GameObject shellGO;
        public GameObject sliderGO;
        public GameObject flowerGO;
    }

    public void loadShapes() {
        coreS = new ImportedModel("rowland/RowlandCore.obj");
        eyeS = new ImportedModel("rowland/RowlandEye.obj");
        gyroS = new ImportedModel("rowland/RowlandGyro.obj");
        headS = new ImportedModel("rowland/RowlandHead.obj");
        headGearS = new ImportedModel("rowland/RowlandHeadGear.obj");
        headMountS = new ImportedModel("rowland/RowlandHeadMount.obj");
        shellS = new ImportedModel("rowland/RowlandShell.obj");
        sliderS = new ImportedModel("rowland/RowlandSlider.obj");
        flowerS = new ImportedModel("rowland/RowlandFlower.obj");
    }

    public void loadTextures() {
        tex1 = new TextureImage("rowlandTex1.png");
        tex2 = new TextureImage("rowlandTex2.png");
        tex3 = new TextureImage("rowlandTex3.png");
        tex4 = new TextureImage("rowlandTex4.png");
        flowerTex = new TextureImage("flowerTex.png");
        exp1 = new TextureImage("rowlandExp1.png");
    }

    public static void move(CharacterAvatar avatar, Vector3f pos) {
        Matrix4f trans = new Matrix4f().translate(pos);
        avatar.rootGO.setLocalTranslation(trans);
    }

    public static void faceTowards(CharacterAvatar avatar, Vector3f dir, Vector3f up){
        avatar.rootGO.lookTowards(dir.mul(-1), up);
    }

    public static void roll(CharacterAvatar avatar, float distance){
        float rotation = distance * (2f / scale);
        avatar.shellGO.pitch(rotation);
    }

    public static void state(CharacterAvatar avatar, int state){
        switch(state){
            case 0:
                avatar.flowerGO.getRenderStates().disableRendering();
                break;
            case 1:
                avatar.flowerGO.getRenderStates().enableRendering();
                break;
            case 2:
                avatar.flowerGO.getRenderStates().disableRendering();
                break;
        }
    }

    public static void planetLight(CharacterAvatar avatar, Vector3f pos, int planetIndex){
        float planetLight = 1f;
        float shadowDistance = 5f;
        Vector4f dir = new Vector4f(0f,1f,0f,0f);
        if (planetIndex >= 0){
            Planet p = PlanetManager.getFromIndex(planetIndex);
            if (p != null){
                float[] d = p.getGravDir(new float[]{pos.x,pos.y,pos.z});
                dir.set(-d[0],-d[1],-d[2],0f);
                planetLight = p.getLight(new float[]{pos.x,pos.y,pos.z});
                shadowDistance = p.getShadowDistance();
            }
        }

        avatar.coreGO.getRenderStates().setShadowDirection(dir);
        avatar.gyroGO.getRenderStates().setShadowDirection(dir);
        avatar.shellGO.getRenderStates().setShadowDirection(dir);
        avatar.sliderGO.getRenderStates().setShadowDirection(dir);
        avatar.headMountGO.getRenderStates().setShadowDirection(dir);
        avatar.headGearGO.getRenderStates().setShadowDirection(dir);
        avatar.headGO.getRenderStates().setShadowDirection(dir);
        avatar.eyeGO.getRenderStates().setShadowDirection(dir);
        avatar.flowerGO.getRenderStates().setShadowDirection(dir);

        avatar.coreGO.getRenderStates().setPlanetLight(planetLight);
        avatar.gyroGO.getRenderStates().setPlanetLight(planetLight);
        avatar.shellGO.getRenderStates().setPlanetLight(planetLight);
        avatar.sliderGO.getRenderStates().setPlanetLight(planetLight);
        avatar.headMountGO.getRenderStates().setPlanetLight(planetLight);
        avatar.headGearGO.getRenderStates().setPlanetLight(planetLight);
        avatar.headGO.getRenderStates().setPlanetLight(planetLight);
        avatar.eyeGO.getRenderStates().setPlanetLight(planetLight);
        avatar.flowerGO.getRenderStates().setPlanetLight(planetLight);

        avatar.coreGO.getRenderStates().setShadowDistance(shadowDistance);
        avatar.gyroGO.getRenderStates().setShadowDistance(shadowDistance);
        avatar.shellGO.getRenderStates().setShadowDistance(shadowDistance);
        avatar.sliderGO.getRenderStates().setShadowDistance(shadowDistance);
        avatar.headMountGO.getRenderStates().setShadowDistance(shadowDistance);
        avatar.headGearGO.getRenderStates().setShadowDistance(shadowDistance);
        avatar.headGO.getRenderStates().setShadowDistance(shadowDistance);
        avatar.eyeGO.getRenderStates().setShadowDistance(shadowDistance);
        avatar.flowerGO.getRenderStates().setShadowDistance(shadowDistance);
    }

    public CharacterAvatar createAvatar(int texIndex) {
        CharacterAvatar avatar = new CharacterAvatar();
        TextureImage img = tex1;
        if (texIndex == 1) img = tex2;
        if (texIndex == 2) img = tex3;
        if (texIndex == 3) img = tex4;

        Matrix4f defaultLocation = new Matrix4f();
        defaultLocation.translation(0f, 0f, 0f);
        avatar.rootGO = new GameObject(GameObject.root());
        avatar.rootGO.setLocalTranslation(defaultLocation);

        Matrix4f defaultScale = new Matrix4f();
        defaultScale.scaling(scale);
        avatar.coreGO = new GameObject(GameObject.root(), coreS, img);
        avatar.coreGO.setParent(avatar.rootGO);
        avatar.coreGO.setLocalLocation(new Vector3f(0f, -0.5f+(scale*0.5f), 0f));
        avatar.coreGO.setLocalScale(defaultScale);
        avatar.coreGO.propagateTranslation(true);
        avatar.coreGO.propagateRotation(true);
        avatar.coreGO.propagateScale(true);
        avatar.coreGO.applyParentRotationToPosition(true);
        avatar.coreGO.applyParentScaleToPosition(true);

        avatar.gyroGO = new GameObject(GameObject.root(), gyroS, img);
        avatar.gyroGO.setParent(avatar.coreGO);
        avatar.gyroGO.setLocalLocation(new Vector3f(0f, 0f, 0f));
        avatar.gyroGO.propagateTranslation(true);
        avatar.gyroGO.propagateRotation(true);
        avatar.gyroGO.propagateScale(true);
        avatar.gyroGO.applyParentRotationToPosition(true);
        avatar.gyroGO.applyParentScaleToPosition(true);

        avatar.shellGO = new GameObject(GameObject.root(), shellS, img);
        avatar.shellGO.setParent(avatar.coreGO);
        avatar.shellGO.setLocalLocation(new Vector3f(0f, 0f, 0f));
        avatar.shellGO.propagateTranslation(true);
        avatar.shellGO.propagateRotation(true);
        avatar.shellGO.propagateScale(true);
        avatar.shellGO.applyParentRotationToPosition(true);
        avatar.shellGO.applyParentScaleToPosition(true);

        avatar.sliderGO = new GameObject(GameObject.root(), sliderS, img);
        avatar.sliderGO.setParent(avatar.coreGO);
        avatar.sliderGO.setLocalLocation(new Vector3f(0f, 0f, 0f));
        avatar.sliderGO.propagateTranslation(true);
        avatar.sliderGO.propagateRotation(true);
        avatar.sliderGO.propagateScale(true);
        avatar.sliderGO.applyParentRotationToPosition(true);
        avatar.sliderGO.applyParentScaleToPosition(true);

        avatar.headMountGO = new GameObject(GameObject.root(), headMountS, img);
        avatar.headMountGO.setParent(avatar.sliderGO);
        avatar.headMountGO.setLocalLocation(new Vector3f(0f, 0.62f, 0f));
        avatar.headMountGO.propagateTranslation(true);
        avatar.headMountGO.propagateRotation(true);
        avatar.headMountGO.propagateScale(true);
        avatar.headMountGO.applyParentRotationToPosition(true);
        avatar.headMountGO.applyParentScaleToPosition(true);

        avatar.headGearGO = new GameObject(GameObject.root(), headGearS, img);
        avatar.headGearGO.setParent(avatar.headMountGO);
        avatar.headGearGO.setLocalLocation(new Vector3f(0f, 0f, 0f));
        avatar.headGearGO.propagateTranslation(true);
        avatar.headGearGO.propagateRotation(true);
        avatar.headGearGO.propagateScale(true);
        avatar.headGearGO.applyParentRotationToPosition(true);
        avatar.headGearGO.applyParentScaleToPosition(true);

        avatar.headGO = new GameObject(GameObject.root(), headS, img);
        avatar.headGO.setParent(avatar.headMountGO);
        avatar.headGO.setLocalLocation(new Vector3f(0f, 0f, 0f));
        avatar.headGO.propagateTranslation(true);
        avatar.headGO.propagateRotation(true);
        avatar.headGO.propagateScale(true);
        avatar.headGO.applyParentRotationToPosition(true);
        avatar.headGO.applyParentScaleToPosition(true);

        avatar.eyeGO = new GameObject(GameObject.root(), eyeS, img);
        avatar.eyeGO.setParent(avatar.headGO);
        avatar.eyeGO.setLocalLocation(new Vector3f(0f, 0.13f, 0f));
        avatar.eyeGO.propagateTranslation(true);
        avatar.eyeGO.propagateRotation(true);
        avatar.eyeGO.propagateScale(true);
        avatar.eyeGO.applyParentRotationToPosition(true);
        avatar.eyeGO.applyParentScaleToPosition(true);
        avatar.eyeGO.getRenderStates().setCustomProgram(CommonResources.characterShaderIndex);
        avatar.eyeGO.setTextureImage2(exp1);

        avatar.flowerGO = new GameObject(GameObject.root(), flowerS, flowerTex);
        avatar.flowerGO.setParent(avatar.headGO);
        avatar.flowerGO.setLocalLocation(new Vector3f(0f, 0.32f, 0f));
        avatar.flowerGO.propagateTranslation(true);
        avatar.flowerGO.propagateRotation(true);
        avatar.flowerGO.propagateScale(true);
        avatar.flowerGO.applyParentRotationToPosition(true);
        avatar.flowerGO.applyParentScaleToPosition(true);

        return avatar;
    }
}