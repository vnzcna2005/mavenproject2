package com.mycompany.flappyFace.game;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;

public class BackgroundMusic {

    private Clip clip;

    // Load music from resources (inside src)
    public void loadMusic(String musicFileName) {
        try {
            InputStream audioSrc = getClass().getResourceAsStream("/sounds/" + musicFileName);
            InputStream bufferedIn = new java.io.BufferedInputStream(audioSrc);

            AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);

            clip = AudioSystem.getClip();
            clip.open(ais);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Start music and loop forever
    public void playLoop() {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        }
    }

    // Stop music
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }

    // Restart from beginning
    public void restart() {
        if (clip != null) {
            clip.stop();
            clip.setFramePosition(0);
            clip.start();
        }
    }
}
