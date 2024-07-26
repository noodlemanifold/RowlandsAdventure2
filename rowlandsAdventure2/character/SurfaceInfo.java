package rowlandsAdventure2.character;

public interface SurfaceInfo {

    public CharacterSurface getSurface(float[] pos);

    public float[] getSurfaceSpeed(float[] pos);

    public float[] getSurfaceImpulse();

}
