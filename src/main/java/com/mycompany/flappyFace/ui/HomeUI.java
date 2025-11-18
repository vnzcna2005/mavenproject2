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

    private JLabel audioLabel;
    private boolean isMuted = false;
    private Clip clip;

    /**
     * Creates new form homeui
     */
    public HomeUI() {
        // background image

        java.net.URL imgURL = getClass().getResource("/images/home_bg.png");
        if (imgURL != null) {
            backgroundImage = new ImageIcon(imgURL).getImage();
        } else {
            logger.warning("Background image not found: /images/home_bg.png");
        }

        setContentPane(new BackgroundPanel());
        initComponents();

        try {
            pixelFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    new File("src/main/resources/fonts/PressStart2P.ttf")
            ).deriveFont(20f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pixelFont);
        } catch (Exception e) {
            e.printStackTrace();

            pixelFont = new Font("Arial", Font.BOLD, 20);
        }

        // play and exit 
        jPanel1.setLayout(null);

        RoundedButton playButton = new RoundedButton("PLAY", 20);
        playButton.setFont(pixelFont.deriveFont(20f));
        playButton.setForeground(Color.WHITE);
        playButton.setBackground(new Color(0, 180, 0));
        playButton.setFocusPainted(false);
        playButton.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 0), 4));
        playButton.setBounds(350, 405, 300, 50);

        RoundedButton exitButton = new RoundedButton("EXIT", 20);
        exitButton.setFont(pixelFont.deriveFont(20f));
        exitButton.setForeground(Color.WHITE);
        exitButton.setBackground(new Color(200, 30, 30));
        exitButton.setFocusPainted(false);
        exitButton.setBorder(BorderFactory.createLineBorder(new Color(140, 0, 0), 4));
        exitButton.setBounds(350, 480, 300, 50);

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
            new Gameplay().setVisible(true);
            this.dispose(); // close the home screen

        });

        exitButton.addActionListener(ev -> {
            if (clip != null && clip.isOpen()) {
                clip.close();
            }
            System.exit(0);
        });

        JLabel playShadow = new JLabel("PLAY", SwingConstants.CENTER);
        playShadow.setFont(pixelFont.deriveFont(20f));
        playShadow.setForeground(new Color(20, 100, 20));
        playShadow.setBounds(playButton.getX() + 4, playButton.getY() + 4, playButton.getWidth(), playButton.getHeight());

        JLabel exitShadow = new JLabel("EXIT", SwingConstants.CENTER);
        exitShadow.setFont(pixelFont.deriveFont(20f));
        exitShadow.setForeground(new Color(100, 20, 20));
        exitShadow.setBounds(exitButton.getX() + 4, exitButton.getY() + 4, exitButton.getWidth(), exitButton.getHeight());

        jPanel1.add(playShadow);
        jPanel1.add(exitShadow);

        jPanel1.add(playButton);
        jPanel1.add(exitButton);

        jPanel1.setComponentZOrder(playButton, 0);
        jPanel1.setComponentZOrder(exitButton, 0);

        audioLabel = new JLabel("🔊");
        audioLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        audioLabel.setForeground(Color.WHITE);
        audioLabel.setBounds(20, getHeight() - 80, 50, 50);
        audioLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        audioLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isMuted = !isMuted;  // flip state
                if (isMuted) {
                    audioLabel.setText("🔇");
                    if (clip != null && clip.isRunning()) {
                        clip.stop(); // mute 
                    }
                } else {
                    audioLabel.setText("🔊");
                    if (clip != null) {
                        clip.start(); // unmute 
                    }
                }
            }
        });

        jPanel1.add(audioLabel);

        audioLabel.setBounds(20, 620, 50, 50);

        jPanel1.setComponentZOrder(audioLabel, 0);

        // refresh the UI
        jPanel1.revalidate();
        jPanel1.repaint();

        // Load your background music
        loadAndPlayBackgroundMusicFromResources();

        titleLabel = new JLabel("FLAPPY FACE", SwingConstants.CENTER);
        titleLabel.setForeground(new Color(255, 215, 0));

        try {

            Font pixelFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    new File("src/main/resources/fonts/PressStart2P.ttf")
            ).deriveFont(48f);

            titleLabel.setFont(pixelFont);
        } catch (Exception e) {
            titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
            e.printStackTrace();
        }

        titleLabel.setBounds(0, 225, 1000, 80);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        jPanel1.setLayout(null);
        jPanel1.add(titleLabel);

        shadowLabel = new JLabel("FLAPPY FACE", SwingConstants.CENTER);
        shadowLabel.setForeground(new Color(240, 128, 0));
        shadowLabel.setFont(titleLabel.getFont());
        shadowLabel.setBounds(4, 230, 1000, 80);

        jPanel1.add(shadowLabel);
        jPanel1.setComponentZOrder(shadowLabel, 1);
        jPanel1.setComponentZOrder(titleLabel, 0);

        if (jPanel1 != null) {
            jPanel1.setOpaque(false);
        }

        // Window settings
        setTitle("Flappy Face - Home");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        //Initialize sun animation
        sunTimer = new Timer(50, e -> animateSun());
        sunTimer.start();

        //️ Initialize cloud movement
        cloudTimer = new Timer(80, e -> animateClouds());
        cloudTimer.start();

        // Initialize floating animation for title
        titleFloatTimer = new Timer(60, e -> animateTitle());
        titleFloatTimer.start();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (clip != null && clip.isOpen()) {
                    clip.close();
                }
            }
        });
        //-----------------End of the constructor--------------------------
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
