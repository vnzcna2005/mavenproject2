package com.mycompany.flappyFace.game;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SoundPlayer {
    
    private static final Logger logger = Logger.getLogger(SoundPlayer.class.getName());

    public void playClip(String filename) {
        new Thread(() -> {
            try {
                // Correct: use filename passed into method
                InputStream audioSrc = getClass().getResourceAsStream("/sounds/" + filename);

                if (audioSrc == null) {
                    logger.warning("Sound file not found: /sounds/" + filename);
                    return;
                }

                try (InputStream bufferedIn = new BufferedInputStream(audioSrc)) {
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);
                    clip.start();

                    clip.addLineListener(event -> {
                        if (event.getType() == LineEvent.Type.STOP) {
                            clip.close();
                        }
                    });
                }
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                logger.log(Level.SEVERE, "Error playing sound: " + filename, ex);
            }
        }).start();
    }
}
