/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.flappyFace.ui;

/**
 *
 * @author VNZ
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import com.mycompany.flappyFace.game.Gameplay;
import java.awt.RadialGradientPaint;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import com.mycompany.flappyFace.ui.VideoUi;

public class HomeUI extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger
            = java.util.logging.Logger.getLogger(HomeUI.class.getName());
    private Image backgroundImage;
    private Font pixelFont;

    // Shining sun
    private float sunAlpha = 1.0f;
    private boolean fadingOut = true;
    private Timer sunTimer;

    //️ Cloud properties
    private int[] cloudX = {100, 300, 600, 200, 800};
    private int[] cloudY = {120, 180, 150, 100, 130};
    private boolean[] moveRight = {true, true, true, false, false};
    private Timer cloudTimer;

    // title variables
    private Timer titleFloatTimer;
    private int titleBaseY = 225;
    private int titleOffset = 0;
    private boolean movingUp = true;
    private JLabel titleLabel;
    private JLabel shadowLabel;

    private JLabel settingsLabel;
    private boolean isMuted = false;
    private Clip clip;
    private Clip switchClip;
    private JLabel trophyLabel;

    private final String[] CHARACTER_NAMES = {"Ralph", "Keziah", "Jeffrey", "James", "Venz", "John Kyle"};
    private final String[] IMAGE_PATHS = {
        "/images/face.png", 
        "/images/face1.png",
        "/images/face2.png",
        "/images/face3.png",
        "/images/face4.png",
        "/images/face5.png"

    };

    private int currentCharacterIndex = 0;
    private JLabel characterImageLabel;
    private JLabel characterNameLabel;
    private JPanel characterSelectorPanel;
    private RoundedButton prevButton;
    private RoundedButton nextButton;

    private Image groundImage;
    private int groundX1 = 0;

    private Timer groundTimer;

    // CHARACTER ANIMATION 
    private Timer characterAnimationTimer;
    private int animationDirection; // 1 for next, -1 for previous
    private int animationCounter = 0; // Tracks the current frame (0 to MAX_FRAMES)
    private final int MAX_ANIMATION_FRAMES = 15; // Controls speed and smoothness

    /**
     * Creates new form homeui ...
     *
     * /**
     * Creates new form homeui
     */
    public HomeUI() {

        try {
            setUndecorated(true);

        } catch (Exception e) {
            logger.warning("Opacity settings might not be fully supported on this OS/Java version.");
        }

        // Load background image
        java.net.URL imgURL = getClass().getResource("/images/home_bg.png");
        if (imgURL != null) {
            backgroundImage = new ImageIcon(imgURL).getImage();
        } else {
            logger.warning("Background image not found: /images/home_bg.png");
        }

        java.net.URL groundImgURL = getClass().getResource("/images/ground1.png");
        if (groundImgURL != null) {
            ImageIcon gi = new ImageIcon(groundImgURL);
            groundImage = gi.getImage();

        } else {
            logger.warning("Ground image not found: /images/ground1.png");
            groundImage = null;

        }

        setContentPane(new BackgroundPanel());
        initComponents();

        try (java.io.InputStream is = getClass().getResourceAsStream("/fonts/PressStart2P.ttf")) {
            if (is != null) {
                pixelFont = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(20f);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(pixelFont);
            } else {
                logger.warning("Font resource not found: /fonts/PressStart2P.ttf");
                pixelFont = new Font("Arial", Font.BOLD, 20);
            }
        } catch (Exception e) {
            e.printStackTrace();
            pixelFont = new Font("Arial", Font.BOLD, 20);
        }

        jPanel1.setLayout(null);

        // PLAY button
        RoundedButton playButton = new RoundedButton("PLAY", 25);
        playButton.setFont(pixelFont.deriveFont(20f));
        playButton.setForeground(Color.WHITE);
        playButton.setBackground(new Color(0, 180, 0));
        playButton.setFocusPainted(false);
        playButton.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 0), 4));
        playButton.setBounds(350, 480, 300, 50);

        // EXIT button
        RoundedButton exitButton = new RoundedButton("EXIT", 25);
        exitButton.setFont(pixelFont.deriveFont(20f));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBackground(new Color(200, 30, 30));
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createLineBorder(new Color(140, 0, 0), 4));
        exitButton.setBounds(350, 545, 300, 50);

        // Hover effects
        playButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                playButton.setBackground(new Color(0, 210, 0));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                playButton.setBackground(new Color(0, 180, 0));
            }
        });

        exitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exitButton.setBackground(new Color(230, 50, 50));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                exitButton.setBackground(new Color(200, 30, 30));
            }
        });

        playButton.addActionListener(ev -> {
            System.out.println("Play pressed");

            String selectedPath = getSelectedCharacterImagePath();

            fadeOutAndStartGame(selectedPath);

        });

        exitButton.addActionListener(ev -> {
            if (clip != null && clip.isOpen()) {
                clip.close();
            }
            System.exit(0);
        });

        // Shadows for play/exit
        JLabel playShadow = new JLabel("PLAY", SwingConstants.CENTER);
        playShadow.setFont(pixelFont.deriveFont(20f));
        playShadow.setForeground(new Color(20, 100, 20));
        playShadow.setBounds(playButton.getX() + 4, playButton.getY() + 4, playButton.getWidth(), playButton.getHeight());

        JLabel exitShadow = new JLabel("EXIT", SwingConstants.CENTER);
        exitShadow.setFont(pixelFont.deriveFont(20f));
        exitShadow.setForeground(new Color(100, 20, 20));
        exitShadow.setBounds(exitButton.getX() + 4, exitButton.getY() + 4, exitButton.getWidth(), exitButton.getHeight());

        // Add to panel (shadows first so buttons appear above)
        jPanel1.add(playShadow);
        jPanel1.add(exitShadow);

        jPanel1.add(playButton);
        jPanel1.add(exitButton);

        // Ensure proper z-order (buttons above shadows)
        jPanel1.setComponentZOrder(playButton, 0);
        jPanel1.setComponentZOrder(exitButton, 0);


        settingsLabel = new JLabel();


        try {
            
            ImageIcon icon = new ImageIcon(getClass().getResource("/images/settings_icon.png"));
            Image img = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH); 
            settingsLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
         
            settingsLabel.setText("⚙️");
            settingsLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        }

        settingsLabel.setForeground(Color.WHITE);
        settingsLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));


        settingsLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            
                showSettingsDialog();
            }
        });


        jPanel1.add(settingsLabel);
        jPanel1.setComponentZOrder(settingsLabel, 0);

// Refresh UI
        jPanel1.revalidate();
        jPanel1.repaint();

    
        loadAndPlayBackgroundMusicFromResources();

        trophyLabel = new JLabel("🏆");
        trophyLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        trophyLabel.setForeground(new Color(255, 215, 0)); // Gold color
        trophyLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        trophyLabel.setBounds(920, 20, 50, 50);
        trophyLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showLeaderboard();
            }
        });
        jPanel1.add(trophyLabel);
        jPanel1.setComponentZOrder(trophyLabel, 0);

        // Title label + shadow
        titleLabel = new JLabel("FLAPPY FACE", SwingConstants.CENTER);
        titleLabel.setForeground(new Color(255, 215, 0));

        try {
            titleLabel.setFont(pixelFont.deriveFont(48f));
        } catch (Exception e) {
            titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
            e.printStackTrace();
        }

        titleLabel.setBounds(0, 225, 1000, 80);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        shadowLabel = new JLabel("FLAPPY FACE", SwingConstants.CENTER);
        shadowLabel.setForeground(new Color(240, 128, 0));
        shadowLabel.setFont(titleLabel.getFont());
        shadowLabel.setBounds(4, 230, 1000, 80);

   
        jPanel1.add(shadowLabel);
        jPanel1.add(titleLabel);
        jPanel1.setComponentZOrder(shadowLabel, 1);
        jPanel1.setComponentZOrder(titleLabel, 0);

        if (jPanel1 != null) {
            jPanel1.setOpaque(false);
        }

        titleFloatTimer = new Timer(60, e -> animateTitle());
        titleFloatTimer.start();

        setTitle("Flappy Face - Home");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        
        settingsLabel.setBounds(20, getHeight() - 80, 50, 50);

     
        sunTimer = new Timer(50, e -> animateSun());
        sunTimer.start();

       
        cloudTimer = new Timer(80, e -> animateClouds());
        cloudTimer.start();

        groundTimer = new Timer(20, e -> animateGround());
        groundTimer.start();

       
        setupCharacterSelector();

       
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (clip != null && clip.isOpen()) {
                    clip.close();
                }
            }
        });

    } // end constructor--------------------------------------

    private void showLeaderboard() {
      
        LeaderboardDialog dialog = new LeaderboardDialog(this);
        dialog.setVisible(true);
    }

    // sun
    private void animateSun() {
        if (fadingOut) {
            sunAlpha -= 0.01f;
            if (sunAlpha <= 0.6f) {
                fadingOut = false;
            }
        } else {
            sunAlpha += 0.01f;
            if (sunAlpha >= 1.0f) {
                fadingOut = true;
            }
        }
        repaint();
    }

    private void animateCharacterSelection() {
        animationCounter++;

        float progress = (float) animationCounter / MAX_ANIMATION_FRAMES;

        if (progress <= 0.5) {

            float alpha = 1.0f - (progress * 2.0f);

            int slideOffset = (int) (animationDirection * 50 * progress * 2.0f);

            characterImageLabel.setForeground(new Color(255, 255, 255, (int) (alpha * 255)));
            characterNameLabel.setForeground(new Color(255, 255, 255, (int) (alpha * 255)));

            characterImageLabel.setBounds(150 - slideOffset, 0, 100, 70);
            characterNameLabel.setBounds(150 - slideOffset, 80, 100, 30);

            characterSelectorPanel.repaint();
        }

        if (progress > 0.5 && animationCounter == Math.ceil(MAX_ANIMATION_FRAMES / 2.0)) {

            int newIndex = currentCharacterIndex + animationDirection;
            int maxIndex = IMAGE_PATHS.length - 1;

            if (newIndex < 0) {
                currentCharacterIndex = maxIndex;
            } else if (newIndex > maxIndex) {
                currentCharacterIndex = 0;
            } else {
                currentCharacterIndex = newIndex;
            }

            updateCharacterDisplay();
        }

        if (progress > 0.5) {

            float alpha = (progress - 0.5f) * 2.0f;

            int slideOffset = (int) (animationDirection * 50 * (1.0f - alpha));

            characterImageLabel.setForeground(new Color(255, 255, 255, (int) (alpha * 255)));
            characterNameLabel.setForeground(Color.BLACK);

            characterImageLabel.setBounds(150 + slideOffset, 0, 100, 70);
            characterNameLabel.setBounds(150 + slideOffset, 80, 100, 30);

            characterSelectorPanel.repaint();
        }

        if (animationCounter >= MAX_ANIMATION_FRAMES) {
            characterAnimationTimer.stop();

            characterImageLabel.setBounds(150, 0, 100, 70);
            characterNameLabel.setBounds(100, 80, 200, 30);

            characterImageLabel.setForeground(Color.WHITE);
            characterSelectorPanel.repaint();
        }
    }

    private void animateGround() {
        if (groundImage == null) {
            return;
        }

        int scrollSpeed = 2;
        int imageWidth = groundImage.getWidth(this);

        if (imageWidth <= 0) {
            return;
        }

        groundX1 -= scrollSpeed;

        if (groundX1 < -imageWidth) {

            groundX1 %= imageWidth;
        }

        repaint();
    }

    public String getSelectedCharacterImagePath() {

        if (currentCharacterIndex >= 0 && currentCharacterIndex < IMAGE_PATHS.length) {
            return IMAGE_PATHS[currentCharacterIndex];
        }

        return IMAGE_PATHS[0];
    }

    private void fadeOutAndStartGame(String selectedPath) {
        Timer fadeOutTimer = new Timer(20, null);
        final float[] opacity = {1.0f};

        fadeOutTimer.addActionListener(e -> {
            opacity[0] -= 0.05f;
            if (opacity[0] <= 0.0f) {
                opacity[0] = 0.0f;
                fadeOutTimer.stop();

                if (clip != null && clip.isOpen()) {
                    clip.stop();
                    clip.close();
                }

                setOpacity(0.0f);

                startNewGame(selectedPath);

                dispose();

            } else {

                setOpacity(opacity[0]);
            }
        });

        fadeOutTimer.start();
    }

    private void startNewGame(String selectedPath) {

        java.awt.EventQueue.invokeLater(() -> {
            Gameplay gameFrame = new Gameplay(selectedPath);

            gameFrame.setTitle("Flappy Face - The Game!");
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.pack();
            gameFrame.setLocationRelativeTo(null);

            gameFrame.setVisible(true);

            gameFrame.fadeIn();
        });
    }

    // ️ clouds 
    private void animateClouds() {
        for (int i = 0; i < cloudX.length; i++) {
            if (moveRight[i]) {
                cloudX[i] += 1;
                if (cloudX[i] > getWidth()) {
                    cloudX[i] = -100;
                }
            } else {
                cloudX[i] -= 1;
                if (cloudX[i] < -100) {
                    cloudX[i] = getWidth();
                }
            }
        }
        repaint();
    }

    // title
    private void animateTitle() {
        int amplitude = 10;
        if (movingUp) {
            titleOffset--;
            if (titleOffset <= -amplitude) {
                movingUp = false;
            }
        } else {
            titleOffset++;
            if (titleOffset >= amplitude) {
                movingUp = true;
            }
        }

        titleLabel.setBounds(0, titleBaseY + titleOffset, 1000, 80);
        shadowLabel.setBounds(4, titleBaseY + 5 + titleOffset, 1000, 80);
        jPanel1.repaint();
    }

    private void loadAndPlayBackgroundMusicFromResources() {
        try {

            java.net.URL audioUrl = getClass().getResource("/sounds/bg_music.wav");
            if (audioUrl == null) {
                System.err.println("Audio resource not found: /sounds/bg_music.wav");
                return;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(audioUrl);

            AudioFormat baseFormat = ais.getFormat();
            AudioFormat decodedFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false);

            AudioInputStream dais = AudioSystem.getAudioInputStream(decodedFormat, ais);

            clip = AudioSystem.getClip();
            clip.open(dais);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

            dais.close();
            ais.close();
        } catch (UnsupportedAudioFileException uafe) {
            uafe.printStackTrace();
            System.err.println("Unsupported audio format. Ensure bg_music.wav is uncompressed PCM WAV.");
        } catch (LineUnavailableException lue) {
            lue.printStackTrace();
            System.err.println("Audio line unavailable.");
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.err.println("IO error loading audio.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void playSoundEffect(String filePath) {
        try {
            java.net.URL soundURL = getClass().getResource(filePath);
            if (soundURL == null) {
                System.err.println("Sound file not found: " + filePath);
                return;
            }

           
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundURL);

          
            final Clip effectClip = AudioSystem.getClip(); 
            effectClip.open(audioIn);

           
            effectClip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    effectClip.close();
                }
            });

           
            effectClip.setFramePosition(0); 
            effectClip.start();

        } catch (Exception e) {
            System.err.println("Error playing sound effect from " + filePath);
            e.printStackTrace();
        }
    }

   
    private void showSettingsDialog() {
      
        SettingsDialog dialog = new SettingsDialog(this, clip);
        dialog.setVisible(true);
    }

    private void setupCharacterSelector() {

        characterSelectorPanel = new JPanel();
        characterSelectorPanel.setOpaque(false);
        characterSelectorPanel.setLayout(null);

        characterSelectorPanel.setBounds(300, 315, 400, 150);

        characterImageLabel = new JLabel();
        characterImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        characterImageLabel.setBounds(100, 0, 200, 100);
        characterSelectorPanel.add(characterImageLabel);

        characterNameLabel = new JLabel("", SwingConstants.CENTER);
        characterNameLabel.setForeground(Color.BLACK);

        Font nameFont = pixelFont != null ? pixelFont.deriveFont(18f) : new Font("Arial", Font.BOLD, 18);
        characterNameLabel.setFont(nameFont);
        characterNameLabel.setBounds(100, 110, 200, 30);
        characterSelectorPanel.add(characterNameLabel);

        prevButton = new RoundedButton("<", 15);
        prevButton.setFont(nameFont.deriveFont(20f));
        prevButton.setBackground(new Color(50, 50, 50, 180));
        prevButton.setForeground(Color.WHITE);
        prevButton.setBounds(25, 35, 45, 45);
        prevButton.addActionListener(e -> navigateCharacters(-1));
        characterSelectorPanel.add(prevButton);

        nextButton = new RoundedButton(">", 15);
        nextButton.setFont(nameFont.deriveFont(20f));
        nextButton.setBackground(new Color(50, 50, 50, 180));
        nextButton.setForeground(Color.WHITE);
        nextButton.setBounds(325, 35, 45, 45);
        nextButton.addActionListener(e -> navigateCharacters(1));
        characterSelectorPanel.add(nextButton);

        jPanel1.add(characterSelectorPanel);
        jPanel1.setComponentZOrder(characterSelectorPanel, 0);

        updateCharacterDisplay();
    }

    private void navigateCharacters(int direction) {

      
        if (characterAnimationTimer != null && characterAnimationTimer.isRunning()) {
            return; 
        }

     
        playSoundEffect("/sounds/switch1.wav");

      
        animationDirection = direction;
        animationCounter = 0;

        if (characterAnimationTimer == null) {
    
            characterAnimationTimer = new Timer(10, e -> animateCharacterSelection());
        }
        characterAnimationTimer.start();
    }

    private void updateCharacterDisplay() {
        String name = CHARACTER_NAMES[currentCharacterIndex];
        String path = IMAGE_PATHS[currentCharacterIndex];

        characterNameLabel.setText(name);

        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            ImageIcon icon = new ImageIcon(imgURL);

            Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            characterImageLabel.setIcon(new ImageIcon(scaledImage));
        } else {

            characterImageLabel.setText("< " + name.toUpperCase() + " >");
            characterImageLabel.setIcon(null);
        }

        characterSelectorPanel.repaint();
    }

    private class BackgroundPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (backgroundImage != null) {
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }

            int sunX = 20;
            int sunY = 20;
            int sunSize = 100;

            Paint oldPaint = g2d.getPaint();
            Composite oldComposite = g2d.getComposite();

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sunAlpha));

            RadialGradientPaint glow = new RadialGradientPaint(
                    new Point(sunX + sunSize / 2, sunY + sunSize / 2),
                    sunSize,
                    new float[]{0f, 1f},
                    new Color[]{new Color(255, 255, 150, 180), new Color(255, 255, 150, 0)}
            );

            g2d.setPaint(glow);
            g2d.fillOval(sunX - 20, sunY - 20, sunSize + 40, sunSize + 40);

            GradientPaint gradient = new GradientPaint(
                    sunX, sunY, new Color(255, 255, 180),
                    sunX + sunSize, sunY + sunSize, new Color(255, 204, 0)
            );

            g2d.setPaint(gradient);
            g2d.fillOval(sunX, sunY, sunSize, sunSize);

            g2d.setComposite(oldComposite);
            g2d.setPaint(oldPaint);

            for (int i = 0; i < cloudX.length; i++) {
                int size = 80 + (i * 12);
                drawCloud(g2d, cloudX[i], cloudY[i], size);
            }

            final int TARGET_GROUND_HEIGHT = 35;

            if (groundImage != null) {
                int imageWidth = groundImage.getWidth(this);
                if (imageWidth <= 0) {
                    return;
                }
                int groundY = getHeight() - TARGET_GROUND_HEIGHT;

                g2d.drawImage(groundImage, groundX1, groundY, imageWidth, TARGET_GROUND_HEIGHT, this);

                g2d.drawImage(groundImage, groundX1 + imageWidth, groundY, imageWidth, TARGET_GROUND_HEIGHT, this);

                if (groundX1 < 0) {
                    g2d.drawImage(groundImage, groundX1 + 2 * imageWidth, groundY, imageWidth, TARGET_GROUND_HEIGHT, this);
                }
            }
        }

        private void drawCloud(Graphics2D g2d, int x, int y, int size) {

            Paint oldPaint = g2d.getPaint();
            Stroke oldStroke = g2d.getStroke();
            Color outlineColor = new Color(255, 255, 255, 180);

            GradientPaint cloudGradient = new GradientPaint(
                    x, y, new Color(255, 255, 255, 230),
                    x, y + size / 2, new Color(230, 230, 230, 180)
            );
            g2d.setPaint(cloudGradient);

            g2d.fillOval(x, y, size, size / 2);
            g2d.fillOval(x + size / 4, y - size / 6, size / 2, (int) (size / 1.8));
            g2d.fillOval(x + size / 2, y, (int) (size / 1.8), size / 2);
            g2d.fillOval(x + size / 8, y + size / 5, size / 2, size / 3);
            g2d.fillOval(x + size / 3, y + size / 6, size / 2, size / 3);

            g2d.setColor(outlineColor);
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawOval(x, y, size, size / 2);

            g2d.setPaint(oldPaint);
            g2d.setStroke(oldStroke);

        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setPreferredSize(new java.awt.Dimension(1000, 700));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1000, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 664, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 664, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 36, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>       

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new HomeUI().setVisible(true));

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
