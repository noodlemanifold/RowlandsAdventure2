package rowlandsAdventure2.character;

import org.joml.Vector3f;

import rowlandsAdventure2.builders.CommonResources;
import tage.audio.AudioResource;
import tage.audio.AudioResourceType;
import tage.audio.IAudioManager;
import tage.audio.Sound;
import tage.audio.SoundType;

public class SoundController {

    private enum SoundSurface{
        PAVEMENT,
        LEAVES,
        ROCKS,
        AIR
    };

    private Sound bonkSound;
    private Sound pavementStart, pavementLoop, pavementEnd;
    private Sound leavesStart, leavesLoop, leavesEnd;
    private Sound rocksStart, rocksLoop, rocksEnd;
    private Sound airStart, airLoop, airEnd;
    private Sound baseJump, doubleJump;
    private Sound music;
    private Sound honk;

    private SoundSurface soundSurface = SoundSurface.AIR;

    private boolean isRoving = false;
    private int roveVolume = 35;


    public void loadSounds(){
        IAudioManager audioMgr = CommonResources.audioMgr;
        AudioResource r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17;
        r1 = audioMgr.createAudioResource("assets/sounds/metalBonk.wav", AudioResourceType.AUDIO_SAMPLE);
		bonkSound = new Sound(r1, SoundType.SOUND_EFFECT, 60, false);
		bonkSound.initialize(audioMgr);
		bonkSound.setMaxDistance(50.0f);
		bonkSound.setMinDistance(0.5f);
		bonkSound.setRollOff(5.0f);

        r2 = audioMgr.createAudioResource("assets/sounds/pavementStart.wav", AudioResourceType.AUDIO_SAMPLE);
		pavementStart = new Sound(r2, SoundType.SOUND_EFFECT, roveVolume, false);
		pavementStart.initialize(audioMgr);
		pavementStart.setMaxDistance(50.0f);
		pavementStart.setMinDistance(0.5f);
		pavementStart.setRollOff(5.0f);

        r3 = audioMgr.createAudioResource("assets/sounds/pavementLoop.wav", AudioResourceType.AUDIO_SAMPLE);
		pavementLoop = new Sound(r3, SoundType.SOUND_EFFECT, roveVolume, true);
		pavementLoop.initialize(audioMgr);
		pavementLoop.setMaxDistance(50.0f);
		pavementLoop.setMinDistance(0.5f);
		pavementLoop.setRollOff(5.0f);

        r4 = audioMgr.createAudioResource("assets/sounds/pavementEnd.wav", AudioResourceType.AUDIO_SAMPLE);
		pavementEnd = new Sound(r4, SoundType.SOUND_EFFECT, roveVolume, false);
		pavementEnd.initialize(audioMgr);
		pavementEnd.setMaxDistance(50.0f);
		pavementEnd.setMinDistance(0.5f);
		pavementEnd.setRollOff(5.0f);

        r5 = audioMgr.createAudioResource("assets/sounds/leavesStart.wav", AudioResourceType.AUDIO_SAMPLE);
		leavesStart = new Sound(r5, SoundType.SOUND_EFFECT, roveVolume, false);
		leavesStart.initialize(audioMgr);
		leavesStart.setMaxDistance(50.0f);
		leavesStart.setMinDistance(0.5f);
		leavesStart.setRollOff(5.0f);

        r6 = audioMgr.createAudioResource("assets/sounds/leavesLoop.wav", AudioResourceType.AUDIO_SAMPLE);
		leavesLoop = new Sound(r6, SoundType.SOUND_EFFECT, roveVolume, true);
		leavesLoop.initialize(audioMgr);
		leavesLoop.setMaxDistance(50.0f);
		leavesLoop.setMinDistance(0.5f);
		leavesLoop.setRollOff(5.0f);

        r7 = audioMgr.createAudioResource("assets/sounds/leavesEnd.wav", AudioResourceType.AUDIO_SAMPLE);
		leavesEnd = new Sound(r7, SoundType.SOUND_EFFECT, roveVolume, false);
		leavesEnd.initialize(audioMgr);
		leavesEnd.setMaxDistance(50.0f);
		leavesEnd.setMinDistance(0.5f);
		leavesEnd.setRollOff(5.0f);

        r8 = audioMgr.createAudioResource("assets/sounds/rocksStart.wav", AudioResourceType.AUDIO_SAMPLE);
		rocksStart = new Sound(r8, SoundType.SOUND_EFFECT, roveVolume, false);
		rocksStart.initialize(audioMgr);
		rocksStart.setMaxDistance(50.0f);
		rocksStart.setMinDistance(0.5f);
		rocksStart.setRollOff(5.0f);

        r9 = audioMgr.createAudioResource("assets/sounds/rocksLoop.wav", AudioResourceType.AUDIO_SAMPLE);
		rocksLoop = new Sound(r9, SoundType.SOUND_EFFECT, roveVolume, true);
		rocksLoop.initialize(audioMgr);
		rocksLoop.setMaxDistance(50.0f);
		rocksLoop.setMinDistance(0.5f);
		rocksLoop.setRollOff(5.0f);

        r10 = audioMgr.createAudioResource("assets/sounds/rocksEnd.wav", AudioResourceType.AUDIO_SAMPLE);
		rocksEnd = new Sound(r10, SoundType.SOUND_EFFECT, roveVolume, false);
		rocksEnd.initialize(audioMgr);
		rocksEnd.setMaxDistance(50.0f);
		rocksEnd.setMinDistance(0.5f);
		rocksEnd.setRollOff(5.0f);

        r11 = audioMgr.createAudioResource("assets/sounds/baseJump.wav", AudioResourceType.AUDIO_SAMPLE);
		baseJump = new Sound(r11, SoundType.SOUND_EFFECT, 100, false);
		baseJump.initialize(audioMgr);
		baseJump.setMaxDistance(50.0f);
		baseJump.setMinDistance(0.5f);
		baseJump.setRollOff(5.0f);

        r12 = audioMgr.createAudioResource("assets/sounds/doubleJump.wav", AudioResourceType.AUDIO_SAMPLE);
		doubleJump = new Sound(r12, SoundType.SOUND_EFFECT, 100, false);
		doubleJump.initialize(audioMgr);
		doubleJump.setMaxDistance(50.0f);
		doubleJump.setMinDistance(0.5f);
		doubleJump.setRollOff(5.0f);

        r13 = audioMgr.createAudioResource("assets/sounds/airStart.wav", AudioResourceType.AUDIO_SAMPLE);
		airStart = new Sound(r13, SoundType.SOUND_EFFECT, roveVolume, false);
		airStart.initialize(audioMgr);
		airStart.setMaxDistance(50.0f);
		airStart.setMinDistance(0.5f);
		airStart.setRollOff(5.0f);

        r14 = audioMgr.createAudioResource("assets/sounds/airLoop.wav", AudioResourceType.AUDIO_SAMPLE);
		airLoop = new Sound(r14, SoundType.SOUND_EFFECT, roveVolume, true);
		airLoop.initialize(audioMgr);
		airLoop.setMaxDistance(50.0f);
		airLoop.setMinDistance(0.5f);
		airLoop.setRollOff(5.0f);

        r15 = audioMgr.createAudioResource("assets/sounds/airEnd.wav", AudioResourceType.AUDIO_SAMPLE);
		airEnd = new Sound(r15, SoundType.SOUND_EFFECT, roveVolume, false);
		airEnd.initialize(audioMgr);
		airEnd.setMaxDistance(50.0f);
		airEnd.setMinDistance(0.5f);
		airEnd.setRollOff(5.0f);

        r16 = audioMgr.createAudioResource("assets/sounds/music.wav", AudioResourceType.AUDIO_STREAM);
		music = new Sound(r16, SoundType.SOUND_MUSIC, 100, true);
		music.initialize(audioMgr);
		music.setMaxDistance(50.0f);
		music.setMinDistance(0.5f);
		music.setRollOff(5.0f);
        //music.setVolume(0);
        music.play();

        r17 = audioMgr.createAudioResource("assets/sounds/honk.wav", AudioResourceType.AUDIO_SAMPLE);
		honk = new Sound(r17, SoundType.SOUND_EFFECT, 100, false);
		honk.initialize(audioMgr);
		honk.setMaxDistance(50.0f);
		honk.setMinDistance(0.5f);
		honk.setRollOff(5.0f);
    }

    public void update(Vector3f pos, Vector3f forward, Vector3f up){
        bonkSound.setLocation(pos);
        pavementStart.setLocation(pos);
        pavementLoop.setLocation(pos);
        pavementEnd.setLocation(pos);
        leavesStart.setLocation(pos);
        leavesLoop.setLocation(pos);
        leavesEnd.setLocation(pos);
        rocksStart.setLocation(pos);
        rocksLoop.setLocation(pos);
        rocksEnd.setLocation(pos);
        baseJump.setLocation(pos);
        doubleJump.setLocation(pos);
        airStart.setLocation(pos);
        airLoop.setLocation(pos);
        airEnd.setLocation(pos);
        music.setLocation(pos);
        honk.setLocation(pos);
        CommonResources.audioMgr.getEar().setLocation(pos);
        CommonResources.audioMgr.getEar().setOrientation(forward, up);

        boolean s = isStarting();
        boolean l = isLooping();

        if (isRoving && !s && !l){
            loopRove();
        }

        if (isRoving && !s && l){
            Sound surfLoop = pavementLoop;
            switch (soundSurface){
                case AIR:
                    surfLoop = airLoop;
                    break;
                case PAVEMENT:
                    surfLoop = pavementLoop;
                    break;
                case LEAVES:
                    surfLoop = leavesLoop;
                    break;
                case ROCKS:
                    surfLoop = rocksLoop;
                    break;
            }
            if (!surfLoop.getIsPlaying()){
                airLoop.stop();
                pavementLoop.stop();
                leavesLoop.stop();
                rocksLoop.stop();
                surfLoop.play();
            }
        }
    }

    public void bonk(){
        bonkSound.play();
    }

    public void honk(){
        honk.play();
    }

    public void jump(){
        baseJump.play();
    }

    public void doubleJump(){
        doubleJump.play();
    }

    public void setSurface(CharacterSurface surface){
        if (surface == CharacterSurface.AirSurface || surface == CharacterSurface.DiveSurface){
            soundSurface = SoundSurface.AIR;
        }else if (surface == CharacterSurface.DirtSurface){
            soundSurface = SoundSurface.ROCKS;
        }else if (surface == CharacterSurface.GrassSurface){
            soundSurface = SoundSurface.LEAVES;
        }else{
            soundSurface = SoundSurface.PAVEMENT;
        }
    }

    public void setRoving(boolean r){
        if (!isRoving && r){
            startRove();
        }

        if (isRoving && !r){
            endRove();
        }

        isRoving = r;
    }

    private void startRove(){
        airLoop.stop();
        stopAllRove();
        switch (soundSurface){
            case AIR:
                airStart.play();
                break;
            case PAVEMENT:
                pavementStart.play();
                break;
            case LEAVES:
                leavesStart.play();
                break;
            case ROCKS:
                rocksStart.play();
                break;
        }
    }

    private void loopRove(){
        stopAllRove();
        switch (soundSurface){
            case AIR:
                airLoop.play();
                break;
            case PAVEMENT:
                pavementLoop.play();
                break;
            case LEAVES:
                leavesLoop.play();
                break;
            case ROCKS:
                rocksLoop.play();
                break;
        }
    }

    private void endRove(){
        airLoop.stop();
        pavementLoop.stop();
        leavesLoop.stop();
        rocksLoop.stop();
        if (!isStarting()){
            switch (soundSurface){
                case AIR:
                    airEnd.play();
                    break;
                case PAVEMENT:
                    pavementEnd.play();
                    break;
                case LEAVES:
                    leavesEnd.play();
                    break;
                case ROCKS:
                    rocksEnd.play();
                    break;
            }
        }else{
            stopAllRove();
        }
    }

    public void stopAllRove(){
        airStart.stop();
        pavementStart.stop();
        leavesStart.stop();
        rocksStart.stop();
        airLoop.stop();
        pavementLoop.stop();
        leavesLoop.stop();
        rocksLoop.stop();
        airEnd.stop();
        pavementEnd.stop();
        leavesEnd.stop();
        rocksEnd.stop();
    }

    private boolean isStarting(){
        return airStart.getIsPlaying() || pavementStart.getIsPlaying() || leavesStart.getIsPlaying() || rocksStart.getIsPlaying();
    }

    private boolean isLooping(){
        return airLoop.getIsPlaying() || pavementLoop.getIsPlaying() || leavesLoop.getIsPlaying() || rocksLoop.getIsPlaying();
    }
    
}
