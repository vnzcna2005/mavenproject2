/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.flappyFace.ui;

/**
 *
 * @author VNZ
 */
import javax.sound.sampled.*;
import java.io.File;

public class AudioPlayer {

    private Clip clip;
    private String filepath;

    public AudioPlayer(String filepath) {
        this.filepath = filepath;
    }

    public void play() {
        try {
            if (clip != null && clip.isRunning()) {
                return; // already playing
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filepath));
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}
