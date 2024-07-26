package rowlandsAdventure2.builders;

public abstract class LevelBuilder {

    public abstract void awake();

    public abstract void loadShapes();

    public abstract void loadTextures();

    public abstract void loadSounds();

    public abstract void buildCustomRenderPrograms();

    public abstract void buildObjects();

    public abstract void buildPhysicsObjects();

    public abstract void buildLights();

    public abstract void initialize();

    public abstract void physicsUpdate();

    public abstract void visualUpdate();
    
}
