package tage;

/**
 * Class that calculates elapsed and delta time per frame, and stores these
 * in static variables than can be accessed anywhere
 */
public class Time{

    private static double lastFrameTime_L, currFrameTime_L;
    private static double lastFrameTime_N, currFrameTime_N;
    public static double elapsedTime_L, deltaTime_L, serverElapsedTime_L;
    public static double deltaNanoseconds;
    public static float elapsedTime, deltaTime, serverElapsedTime;
    public static double startTime_L, serverStartTime_L;

    public static void setServerStart(long l){
        serverStartTime_L = l;
    }

    protected static void init(){
        lastFrameTime_L = System.currentTimeMillis();
		currFrameTime_L = System.currentTimeMillis();
        lastFrameTime_N = System.nanoTime();
        currFrameTime_N = System.nanoTime();
        startTime_L = System.currentTimeMillis();
        serverStartTime_L = startTime_L;
		deltaTime_L = 0.0;
		elapsedTime_L = 0.0;
        deltaNanoseconds = 0.0;
    }

    protected static void update(){
        lastFrameTime_L = currFrameTime_L;
		currFrameTime_L = System.currentTimeMillis();
        lastFrameTime_N = currFrameTime_N;
        currFrameTime_N = System.nanoTime();
		deltaTime_L = (currFrameTime_L - lastFrameTime_L) / 1000.0;
		elapsedTime_L = (currFrameTime_L - startTime_L) / 1000.0;
        serverElapsedTime_L = (currFrameTime_L - serverStartTime_L) / 1000.0;
        deltaNanoseconds = currFrameTime_N - lastFrameTime_N;
        elapsedTime = (float) elapsedTime_L;
        deltaTime = (float) deltaTime_L;
        serverElapsedTime = (float) serverElapsedTime_L;
    }
}