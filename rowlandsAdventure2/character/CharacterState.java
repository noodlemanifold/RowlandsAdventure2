package rowlandsAdventure2.character;

import org.joml.Vector3f;

public class CharacterState {

    public Vector3f position;
    public Vector3f dir;
    public Vector3f up;
    public Vector3f velocity;
    public float horizontalSpeed;
    public int planetIndex;
    public int avatarState = 0;

    public String encode(){
        String e = "";
        e += position.x() + "|";
        e += position.y() + "|";
        e += position.z() + "|";
        e += dir.x() + "|";
        e += dir.y() + "|";
        e += dir.z() + "|";
        e += up.x() + "|";
        e += up.y() + "|";
        e += up.z() + "|";
        e += velocity.x() + "|";
        e += velocity.y() + "|";
        e += velocity.z() + "|";
        e += horizontalSpeed + "|";
        e += planetIndex + "|";
        e += avatarState;
        return e;
    }

    public CharacterState(){
        position = new Vector3f(0f,0f,0f);
        dir = new Vector3f(0f,0f,0f);
        up = new Vector3f(0f,0f,0f);
        velocity = new Vector3f(0f,0f,0f);
    }

    public CharacterState(String s){
        position = new Vector3f(0f,0f,0f);
        dir = new Vector3f(0f,0f,0f);
        up = new Vector3f(0f,0f,0f);
        velocity = new Vector3f(0f,0f,0f);
        decode(s);
    }

    public void decode(String e){
        String[] tokens = e.split("\\|");
        position.x = Float.parseFloat(tokens[0]);
        position.y = Float.parseFloat(tokens[1]);
        position.z = Float.parseFloat(tokens[2]);
        dir.x = Float.parseFloat(tokens[3]);
        dir.y = Float.parseFloat(tokens[4]);
        dir.z = Float.parseFloat(tokens[5]);
        up.x = Float.parseFloat(tokens[6]);
        up.y = Float.parseFloat(tokens[7]);
        up.z = Float.parseFloat(tokens[8]);
        velocity.x = Float.parseFloat(tokens[9]);
        velocity.y = Float.parseFloat(tokens[10]);
        velocity.z = Float.parseFloat(tokens[11]);
        horizontalSpeed = Float.parseFloat(tokens[12]);
        planetIndex = Integer.parseInt(tokens[13]);
        avatarState = Integer.parseInt(tokens[14]);
    }
    
}
