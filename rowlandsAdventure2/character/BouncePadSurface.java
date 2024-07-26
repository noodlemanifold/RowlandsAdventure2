package rowlandsAdventure2.character;

import org.joml.Vector3f;

public class BouncePadSurface implements SurfaceInfo{

    Vector3f velocity;
    boolean overwrite;

    public BouncePadSurface(Vector3f velocity, boolean overwrite){
        this.velocity = velocity;
        this.overwrite = overwrite;
    }

    @Override
    public CharacterSurface getSurface(float[] pos) {
        return CharacterSurface.AirSurface;
    }

    @Override
    public float[] getSurfaceSpeed(float[] pos) {
        return new float[]{0f,0f,0f};
    }

    @Override
    public float[] getSurfaceImpulse() {
        //if 4th component is one, speed is overwritten, else it is added.
        return new float[]{velocity.x,velocity.y,velocity.z,overwrite?1f:0f};
    }

}
