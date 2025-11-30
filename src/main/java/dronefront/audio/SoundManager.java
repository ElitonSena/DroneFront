package dronefront.audio;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundManager {
    private static SoundManager instance;
    private final Map<String, AudioClip> soundCache = new HashMap<>();
    private MediaPlayer menuBGM; 
    private MediaPlayer gameLoop;
    private boolean isSoundEnabled = true;

    private SoundManager() {
        loadMenuBGM("bgm_menu_loop.mp3"); 
        loadGameLoop("enemy_flying_loop.mp3"); 
        loadAudioClips();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private String getResourcePath(String filename) {
        return "/assets/audio/" + filename;
    }

    private void loadMenuBGM(String filename) {
        try {
            String path = getResourcePath(filename);
            Media media = new Media(getClass().getResource(path).toExternalForm());
            menuBGM = new MediaPlayer(media);
            menuBGM.setCycleCount(MediaPlayer.INDEFINITE);
            menuBGM.setVolume(0.5);
        } catch (Exception e) {
            System.err.println("Erro ao carregar BGM do menu: " + filename + ". " + e.getMessage());
        }
    }

    private void loadGameLoop(String filename) {
        try {
            String path = getResourcePath(filename);
            Media media = new Media(getClass().getResource(path).toExternalForm());
            gameLoop = new MediaPlayer(media);
            gameLoop.setCycleCount(MediaPlayer.INDEFINITE);
            gameLoop.setVolume(0.3);
        } catch (Exception e) {
            System.err.println("Erro ao carregar loop do jogo: " + filename + ". " + e.getMessage());
        }
    }

    private void loadAudioClips() {
        loadClip("ui_build.mp3");
        loadClip("ui_upgrade.mp3");
        loadClip("ui_sell.mp3");
        loadClip("ui_coin_gain.mp3");
        loadClip("ui_error.mp3");
        loadClip("ui_pause_toggle.mp3");
        loadClip("base_hit.mp3");
        loadClip("enemy_explode.mp3");

        loadClip("proj_gun_fire.mp3");
        loadClip("proj_pem_fire.mp3");
        loadClip("proj_fire_fire.mp3");
        loadClip("impact_pem_slow.mp3");
        loadClip("impact_fire_burn.mp3");
    }

    private void loadClip(String filename) {
        try {
            String path = getResourcePath(filename);
            AudioClip clip = new AudioClip(getClass().getResource(path).toExternalForm());
            soundCache.put(filename, clip);
        } catch (Exception e) {
            System.err.println("Erro ao carregar clipe de áudio: " + filename + ". " + e.getMessage());
        }
    }

    public void play(String filename) {
        if (!isSoundEnabled) return;
        AudioClip clip = soundCache.get(filename);
        if (clip != null) {
            clip.play();
        }
    }

    public void playMenuBGM() {
        if (!isSoundEnabled || menuBGM == null) return;
        if (menuBGM.getStatus() != MediaPlayer.Status.PLAYING) {
            menuBGM.play();
        }
    }

    public void stopMenuBGM() {
        if (menuBGM != null) {
            menuBGM.stop();
        }
    }

    public void playGameLoop() { 
        if (!isSoundEnabled || gameLoop == null) return;
        if (gameLoop.getStatus() != MediaPlayer.Status.PLAYING) {
            gameLoop.play();
        }
    }

    public void stopGameLoop() { 
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    public void setSoundEnabled(boolean enabled) {
        this.isSoundEnabled = enabled;
        if (enabled) {
            playMenuBGM();
        } else {
            stopMenuBGM();
            stopGameLoop();
        }
    }

    public boolean isSoundEnabled() {
        return isSoundEnabled;
    }
}