package rowlandsAdventure2.character;

public class CharacterSurface{

    public float rotationSpeedHighSpeed = 360f;
    public float rotationSpeedLowSpeed = 2000f;
    public float accelerationHighSpeed = 6f;
    public float accelerationLowSpeed = 4f;
    public float deccelerationHighSpeed = 18f;
    public float deccelerationLowSpeed = 24f;
    public float deccelerationOverspeed = 0.5f;
    public float minSpeed = 0f;
    public float maxSpeed = 6f;
    public float impulseLimitHighSpeed = 40f;//1f;
    public float impulseLimitLowSpeed = 40f;//0.3f;
    public float impulseBoundsPenaltyFactor = 1f;
    public float impulseIdlePenaltyFactor = 1f;
    public float slopeAccelerationCapFactor=1f;
    public float slopeSpeedCapFactor=1f;

    public static CharacterSurface GrassSurface = GrassSurface();
    public static CharacterSurface GroundSurface = GroundSurface();
    public static CharacterSurface AirSurface = AirSurface();
    public static CharacterSurface DiveSurface = DiveSurface();
    public static CharacterSurface DirtSurface = DirtSurface();
    public static CharacterSurface IceSurface = IceSurface();
    public static CharacterSurface Booster1Surface = Booster1Surface();

    private CharacterSurface(){

    }

    private static CharacterSurface GroundSurface(){
        CharacterSurface surf = new CharacterSurface();
        return surf;
    }

    private static CharacterSurface GrassSurface(){
        CharacterSurface surf = new CharacterSurface();
        return surf;
    }

    private static CharacterSurface AirSurface(){
        CharacterSurface surf = new CharacterSurface();
        surf.rotationSpeedHighSpeed = 2000f;
        surf.rotationSpeedLowSpeed = 2000f;
        surf.accelerationHighSpeed = 10f;
        surf.accelerationLowSpeed = 10f;
        surf.deccelerationHighSpeed = 50f;
        surf.deccelerationLowSpeed = 50f;
        surf.deccelerationOverspeed = 1f;
        surf.minSpeed = 0f;
        //surf.maxSpeed = 
        surf.impulseLimitHighSpeed = 10f;
        surf.impulseLimitLowSpeed = 7f;
        surf.impulseBoundsPenaltyFactor = 1f;
        surf.impulseIdlePenaltyFactor = 0.5f;
        surf.slopeAccelerationCapFactor = 0f;
        surf.slopeSpeedCapFactor = 0f;
        return surf;
    }

    private static CharacterSurface DiveSurface(){
        CharacterSurface surf = AirSurface();
        surf.rotationSpeedHighSpeed = 30f;
        surf.rotationSpeedLowSpeed = 30f;
        surf.deccelerationOverspeed = 0.5f;
        surf.impulseLimitHighSpeed = 4f;
        surf.impulseLimitLowSpeed = 4f;
        return surf;
    }

    private static CharacterSurface DirtSurface(){
        CharacterSurface surf = new CharacterSurface();
        surf.rotationSpeedHighSpeed = 360f;
        surf.rotationSpeedLowSpeed = 2000f;
        surf.accelerationHighSpeed = 5f;
        surf.accelerationLowSpeed = 3f;
        surf.deccelerationHighSpeed = 18f;
        surf.deccelerationLowSpeed = 12f;
        surf.deccelerationOverspeed = 1f;
        surf.minSpeed = 0f;
        //surf.maxSpeed = 
        surf.impulseLimitHighSpeed = 30f;
        surf.impulseLimitLowSpeed = 30f;
        surf.impulseBoundsPenaltyFactor = 0.5f;
        surf.impulseIdlePenaltyFactor = 1f;
        //surf.slopeAccelerationCapFactor = 
        //surf.slopeSpeedCapFactor = 
        return surf;
    }

    private static CharacterSurface IceSurface(){
        CharacterSurface surf = new CharacterSurface();
        surf.rotationSpeedHighSpeed = 2000f;
        surf.rotationSpeedLowSpeed = 2000f;
        surf.accelerationHighSpeed = 10f;
        surf.accelerationLowSpeed = 10f;
        surf.deccelerationHighSpeed = 50f;
        surf.deccelerationLowSpeed = 50f;
        surf.deccelerationOverspeed = 1f;
        surf.minSpeed = 0f;
        //surf.maxSpeed = 
        surf.impulseLimitHighSpeed = 2f;
        surf.impulseLimitLowSpeed = 3f;
        surf.impulseBoundsPenaltyFactor = 1f;
        surf.impulseIdlePenaltyFactor = 0.5f;
        //surf.slopeAccelerationCapFactor = 
        //surf.slopeSpeedCapFactor = 
        return surf;
    }

    private static CharacterSurface Booster1Surface(){
        CharacterSurface surf = new CharacterSurface();
        surf.rotationSpeedHighSpeed = 180f;
        surf.rotationSpeedLowSpeed = 1000f;
        surf.accelerationHighSpeed = 20f;
        surf.accelerationLowSpeed = 15f;
        surf.deccelerationHighSpeed = 10f;
        surf.deccelerationLowSpeed = 14f;
        surf.deccelerationOverspeed = 0f;
        surf.minSpeed = 15f;
        surf.maxSpeed = 15f;
        surf.impulseLimitHighSpeed = 400f;//1f;
        surf.impulseLimitLowSpeed = 400f;//0.3f;
        surf.impulseBoundsPenaltyFactor = 1f;
        surf.impulseIdlePenaltyFactor = 1f;
        return surf;
    }

}