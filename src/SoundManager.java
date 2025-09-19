import java.io.File;
import javax.sound.sampled.*;

public class SoundManager {
    private Clip backgroundMusic;
    private boolean isBackgroundMusicPlaying = false;
    
    public SoundManager() {
        loadBackgroundMusic();
    }
    
    private void loadBackgroundMusic() {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File("assets/sfx/bg.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioStream);
            
            FloatControl volumeControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);
            float volume = -18.0f;
            volumeControl.setValue(volume);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void startBackgroundMusic() {
        if (backgroundMusic != null && !isBackgroundMusicPlaying) {
            backgroundMusic.start();
            isBackgroundMusicPlaying = true;
        }
    }
    
    public void stopBackgroundMusic() {
        if (backgroundMusic != null && isBackgroundMusicPlaying) {
            backgroundMusic.stop();
            isBackgroundMusicPlaying = false;
        }
    }
    
    public void pauseBackgroundMusic() {
        if (backgroundMusic != null && isBackgroundMusicPlaying) {
            backgroundMusic.stop();
        }
    }
    
    public void resumeBackgroundMusic() {
        if (backgroundMusic != null && isBackgroundMusicPlaying) {
            backgroundMusic.start();
        }
    }
}
