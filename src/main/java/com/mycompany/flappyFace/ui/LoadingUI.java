/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.flappyFace.ui; // package declaration - groups related classes

// imports for Swing GUI, AWT graphics, events, audio, and file IO
import javax.swing.*;               // Swing components (JFrame, JPanel, JLabel, etc.)
import java.awt.*;                  // AWT graphics classes (Color, Font, Graphics, etc.)
import java.awt.event.*;            // AWT event handling (ActionListener, etc.)
import javax.sound.sampled.*;       // Audio Clip support
import java.io.File;                // File handling
import java.io.IOException;         // Exception for IO operations

import com.mycompany.flappyFace.ui.HomeUI; // import the next screen to open after loading


// Main class for the Loading Screen window; extends JFrame to be a window
public class LoadingUI extends javax.swing.JFrame {

  
    // logger for warning, info, or severe messages (useful for debugging)
    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(LoadingUI.class.getName());

    // variable to hold the background image used by the loading screen
    private Image backgroundImage;
    // variable to hold a custom pixel font (PressStart2P) if available
    private Font pixelFont;

    // sunAlpha controls sun transparency (1.0 = fully opaque)
    private float sunAlpha = 1.0f;
    // fadingOut tells whether the sun is currently fading out (true) or fading in (false)
    private boolean fadingOut = true;
    // timer that updates the sun animation periodically
    private Timer sunTimer;

    // x positions for multiple clouds
    private int[] cloudX = {100, 300, 600, 200, 800};
    // y positions for multiple clouds
    private int[] cloudY = {120, 180, 150, 100, 130};
    // boolean array indicating whether each cloud moves to the right (true) or left (false)
    private boolean[] moveRight = {true, true, true, false, false};
    // timer that updates the cloud positions periodically
    private Timer cloudTimer;

    // timer that controls the title floating animation
    private Timer titleFloatTimer;
    // base Y coordinate of the title (used as center reference)
    private int titleBaseY = 225;
    // offset applied to the title for floating effect
    private int titleOffset = 0;
    // direction flag for title movement (true = moving up)
    private boolean movingUp = true;
    // label used to show the main game title
    private JLabel titleLabel;
    // label used as a shadow behind the main title to provide depth
    private JLabel shadowLabel;

    // audio clip variable to hold background music or sound effect
    private Clip clip;

    // progress bar used to display loading progress
    private JProgressBar progressBar;
    // timer used to simulate and update loading progress
    private Timer loadingTimer;
    // integer storing the current progress percentage (0 to 100)
    private int progressValue = 0;

    // array of strings that show various loading messages while progress advances
    private String[] loadingMessages = {
        "Loading assets...",
        "Initializing game engine...",
        "Preparing environment...",
        "Loading characters...",
        "Setting up world...",
        "Almost there...",
        "Starting game..."
    };

     
     // Constructor – this runs when you create a new LoadingUI window
    public LoadingUI() {
    initComponents(); // call NetBeans-generated method to initialize jPanel1 and layout

        // load background image resource from the JAR/classpath (/images/home_bg.png)
        java.net.URL imgURL = getClass().getResource("/images/home_bg.png");
        // if the URL returned is not null, create an ImageIcon and extract the Image
        if (imgURL != null) {
            backgroundImage = new ImageIcon(imgURL).getImage(); // set backgroundImage
        } else {
            // if the image was not found, log a warning so developer can notice missing resource
            logger.warning("Background image not found: /images/home_bg.png");
        }

        // set the content pane to our custom BackgroundPanel which overrides paintComponent
        setContentPane(new BackgroundPanel());
        // call initComponents again if needed (NetBeans sometimes expects this order)
        initComponents();

        // attempt to load a custom TrueType font from the project resources
        try {
            pixelFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    new File("src/main/resources/fonts/PressStart2P.ttf") // path to font file
            ).deriveFont(20f); // set base size to 20f
            // register the font with the local graphics environment so it can be used app-wide
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pixelFont);
        } catch (Exception e) {
            // if font loading fails for any reason, print stack trace and use fallback font
            e.printStackTrace();
            pixelFont = new Font("Arial", Font.BOLD, 20); // fallback font if custom not available
        }

        // set the layout manager of jPanel1 to null for absolute positioning (manual bounds)
        jPanel1.setLayout(null);

        // =================== PROGRESS BAR DESIGN ===================
        // create a new JProgressBar from 0 to 100 and override paintComponent to custom draw it
        progressBar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                // create a Graphics2D copy for advanced drawing without modifying original Graphics
                Graphics2D g2d = (Graphics2D) g.create();
                // get the current width and height of the progressBar component
                int width = getWidth();
                int height = getHeight();

                // create a vertical gradient for the background of the bar
                GradientPaint backgroundPaint = new GradientPaint(
                        0, 0, new Color(30, 30, 30), // top color
                        0, height, new Color(50, 50, 50) // bottom color
                );
                g2d.setPaint(backgroundPaint); // set paint to gradient
                // draw a rounded rectangle as the background of the bar
                g2d.fillRoundRect(0, 0, width, height, 25, 25);

                // calculate the width of filled portion based on percent complete
                int progressWidth = (int) ((getPercentComplete()) * width);
                // create a gradient for the filled/progress area
                GradientPaint fillPaint = new GradientPaint(
                        0, 0, new Color(0, 255, 100), // left color
                        width, height, new Color(0, 180, 255) // right/bottom color
                );
                g2d.setPaint(fillPaint); // apply fill gradient
                // draw filled rounded rectangle for the progress amount
                g2d.fillRoundRect(0, 0, progressWidth, height, 25, 25);

                // create a glossy highlight gradient at the top half for a shiny effect
                GradientPaint gloss = new GradientPaint(
                        0, 0, new Color(255, 255, 255, 80),
                        0, height / 2, new Color(255, 255, 255, 0)
                );
                g2d.setPaint(gloss); // set gloss paint
                // draw glossy highlight over the filled portion
                g2d.fillRoundRect(0, 0, progressWidth, height / 2, 25, 25);

                // create a glow animation for the border using time-based sine function
                float glowPhase = (System.currentTimeMillis() % 1000) / 1000f;
                // compute alpha value for glow between ~0-200 range
                int glowAlpha = (int) (100 + 100 * Math.sin(glowPhase * Math.PI * 2));
                // set border color using computed alpha
                g2d.setColor(new Color(0, 255, 100, glowAlpha));
                g2d.setStroke(new BasicStroke(3f)); // set stroke width for border drawing
                // draw rounded rectangle border inside the component
                g2d.drawRoundRect(1, 1, width - 3, height - 3, 25, 25);

                // get the string shown by the progress bar (if any)
                String text = getString();
                // if there is text, auto-resize and draw it centered inside the progress bar
                if (text != null && !text.isEmpty()) {
                    // maximum font size based on height with small padding
                    int maxFontSize = height - 10;
                    int fontSize = maxFontSize;
                    // create a test font instance using the component's base font
                    Font testFont = getFont().deriveFont(Font.BOLD, fontSize);
                    // measure the font metrics for width calculation
                    FontMetrics fm = g2d.getFontMetrics(testFont);

                    // reduce font size until text fits within width - 20 or until fontSize is small
                    while (fm.stringWidth(text) > width - 20 && fontSize > 10) {
                        fontSize--; // decrease font size
                        testFont = getFont().deriveFont(Font.BOLD, fontSize); // recreate font
                        fm = g2d.getFontMetrics(testFont); // update metrics
                    }

                    g2d.setFont(testFont); // set chosen font
                    // compute horizontal position to center the text
                    int textX = (width - fm.stringWidth(text)) / 2;
                    // compute vertical baseline for vertical centering
                    int textY = (height + fm.getAscent()) / 2 - 3;

                    // draw a small black shadow for contrast/readability
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(text, textX + 1, textY + 1);
                    // draw the main white text
                    g2d.setColor(Color.WHITE);
                    g2d.drawString(text, textX, textY);
                }

                g2d.dispose(); // dispose of the Graphics2D copy to free resources
            }
        };

        // initialize progress bar properties: value, text display, font and appearance
        progressBar.setValue(0);                    // start at 0%
        progressBar.setStringPainted(true);         // enable text display (getString()/setString())
        progressBar.setFont(pixelFont);             // use pixelFont as base (auto-resize logic will adjust)
        progressBar.setOpaque(false);               // let background show through where not painted
        progressBar.setBorder(BorderFactory.createEmptyBorder()); // remove default border
        progressBar.setBounds(350, 450, 300, 40);  // absolute position and size on jPanel1
        jPanel1.add(progressBar);                  // add progress bar to panel
        // ===========================================================

        // =================== TITLE SETUP ===================
        // create a centered JLabel for the main title text
        titleLabel = new JLabel("FLAPPY FACE", SwingConstants.CENTER);
        // set the title color (gold-ish)
        titleLabel.setForeground(new Color(255, 215, 0));
        try {
            // attempt to load a big version of the pixel font for the title
            Font pixelFont = Font.createFont(
                    Font.TRUETYPE_FONT,
                    new File("src/main/resources/fonts/PressStart2P.ttf")
            ).deriveFont(48f); // 48pt font for title
            titleLabel.setFont(pixelFont); // apply font to titleLabel
        } catch (Exception e) {
            // if loading fails, use a default bold Arial 48pt and print error
            titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
            e.printStackTrace();
        }
        // set title label bounds: x=0, y=225, width=1000, height=80
        titleLabel.setBounds(0, 225, 1000, 80);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // center align text
        jPanel1.add(titleLabel); // add title to panel

        // create a shadow label slightly offset to imitate a shadow behind the title
        shadowLabel = new JLabel("FLAPPY FACE", SwingConstants.CENTER);
        // darker/orange color for the shadow
        shadowLabel.setForeground(new Color(240, 128, 0));
        // use the same font as the title for consistent sizing
        shadowLabel.setFont(titleLabel.getFont());
        // position the shadow slightly offset from the title (x=4, y=230)
        shadowLabel.setBounds(4, 230, 1000, 80);
        jPanel1.add(shadowLabel); // add shadow label to panel
        // manipulate z-order so shadow is behind the main title for correct layering
        jPanel1.setComponentZOrder(shadowLabel, 1);
        jPanel1.setComponentZOrder(titleLabel, 0);

        // ensure the panel is transparent so BackgroundPanel's image is visible
        if (jPanel1 != null) jPanel1.setOpaque(false);

        // =================== WINDOW SETTINGS ===================
        setTitle("Flappy Face - Loading"); // set window title text
        setSize(1000, 700);               // set fixed window size
        setLocationRelativeTo(null);      // center window on screen
        setResizable(false);              // prevent resizing to keep layout stable

        // =================== ANIMATION TIMERS ===================
        // create a timer that fires every 50ms and calls animateSun()
        sunTimer = new Timer(50, e -> animateSun());
        sunTimer.start(); // start the timer so animation begins

        // create a timer for cloud movement (80ms interval)
        cloudTimer = new Timer(80, e -> animateClouds());
        cloudTimer.start(); // start cloud timer

        // create a timer for title floating animation (60ms interval)
        titleFloatTimer = new Timer(60, e -> animateTitle());
        titleFloatTimer.start(); // start title float timer

        // simulate loading progress by incrementing the bar every 40ms
        loadingTimer = new Timer(40, e -> updateProgress());
        loadingTimer.start(); // start progress simulation

        // add a window listener to ensure audio clip is closed when window is closing
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                // if audio clip exists and is open, close it to free resources
                if (clip != null && clip.isOpen()) clip.close();
            }
        });
    }

    // =================== PROGRESS BAR LOGIC ===================
    // method that increments and updates the loading progress
    private void updateProgress() {
        // if progressValue is still below 100, increment it
        if (progressValue < 100) {
            progressValue++;                // increase numeric progress by 1
            progressBar.setValue(progressValue); // update visual bar to match value

            // choose a loading message depending on the current progress range
            if (progressValue < 15)
                progressBar.setString(loadingMessages[0]); // early stage message
            else if (progressValue < 30)
                progressBar.setString(loadingMessages[1]);
            else if (progressValue < 45)
                progressBar.setString(loadingMessages[2]);
            else if (progressValue < 60)
                progressBar.setString(loadingMessages[3]);
            else if (progressValue < 75)
                progressBar.setString(loadingMessages[4]);
            else if (progressValue < 90)
                progressBar.setString(loadingMessages[5]);
            else
                progressBar.setString(loadingMessages[6]); // near-complete message

        } else {
            // when progress reaches 100 or above:
            loadingTimer.stop();                 // stop the loading timer
            progressBar.setString("Done!");      // set final text
            System.out.println("Loading complete!"); // optional console log

            // open HomeUI on Event Dispatch Thread and dispose this loading window
            SwingUtilities.invokeLater(() -> {
                dispose(); // close and free this JFrame resources
                new HomeUI().setVisible(true); // open the next screen (HomeUI)
            });
        }
    }
    // =================== END PROGRESS LOGIC ===================

    // =================== ANIMATION METHODS ===================
    // animateSun adjusts sunAlpha up/down to create fade-in/out effect, then repaints
    private void animateSun() {
        if (fadingOut) {
            sunAlpha -= 0.01f; // make sun slightly more transparent
            if (sunAlpha <= 0.6f) fadingOut = false; // reverse direction when lower bound reached
        } else {
            sunAlpha += 0.01f; // make sun slightly more opaque
            if (sunAlpha >= 1.0f) fadingOut = true; // reverse when fully opaque
        }
        repaint(); // request re-drawing so background (sun) updates visually
    }

    // animateClouds moves each cloud left or right; wraps clouds around when off-screen
    private void animateClouds() {
        for (int i = 0; i < cloudX.length; i++) { // iterate through all clouds
            if (moveRight[i]) {
                cloudX[i] += 1; // move cloud to the right by 1 pixel
                if (cloudX[i] > getWidth()) cloudX[i] = -100; // wrap to left if it exits right
            } else {
                cloudX[i] -= 1; // move cloud to the left by 1 pixel
                if (cloudX[i] < -100) cloudX[i] = getWidth(); // wrap to right if exits left
            }
        }
        repaint(); // repaint panel to show updated cloud positions
    }

    // animateTitle changes titleOffset to make the title float up/down and repositions labels
    private void animateTitle() {
        int amplitude = 10; // how far the title moves up/down
        if (movingUp) {
            titleOffset--; // move slightly up
            if (titleOffset <= -amplitude) movingUp = false; // change direction when reaching top
        } else {
            titleOffset++; // move slightly down
            if (titleOffset >= amplitude) movingUp = true; // change direction when reaching bottom
        }

        // reposition the main title and the shadow according to titleOffset
        titleLabel.setBounds(0, titleBaseY + titleOffset, 1000, 80);
        shadowLabel.setBounds(4, titleBaseY + 5 + titleOffset, 1000, 80);
        jPanel1.repaint(); // repaint panel to show title movement
    }
    // =================== END ANIMATION METHODS ===================

    // =================== BACKGROUND PANEL CLASS ===================
    // private inner class for painting background image, sun glow, and clouds
    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); // call parent to clear the background
            Graphics2D g2d = (Graphics2D) g; // cast to Graphics2D for advanced painting
            // enable anti-aliasing to smooth shapes
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // draw the backgroundImage stretched to fill panel bounds if available
            if (backgroundImage != null) {
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight  (), this);
            }

            // draw animated sun using radial gradient for glow
            int sunX = 20, sunY = 20, sunSize = 100; // sun location and size
            Paint oldPaint = g2d.getPaint();        // store old paint to restore later
            Composite oldComposite = g2d.getComposite(); // store old composite
            // set composite with alpha equal to sunAlpha so sun can fade
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, sunAlpha));

            // radial gradient used to create glowing halo around sun
            RadialGradientPaint glow = new RadialGradientPaint(
                    new Point(sunX + sunSize / 2, sunY + sunSize / 2), // center of gradient
                    sunSize, // radius
                    new float[]{0f, 1f}, // distribution of gradient
                    new Color[]{new Color(255, 255, 150, 180), new Color(255, 255, 150, 0)} // inner->outer colors
            );

            g2d.setPaint(glow); // set paint to glow gradient
            // draw large oval for the glow (slightly bigger than sun to give halo)
            g2d.fillOval(sunX - 20, sunY - 20, sunSize + 40, sunSize + 40);

            // draw the sun circle using a gradient for a more natural look
            GradientPaint gradient = new GradientPaint(
                    sunX, sunY, new Color(255, 255, 180),
                    sunX + sunSize, sunY + sunSize, new Color(255, 204, 0)
            );
            g2d.setPaint(gradient); // apply gradient paint for the sun body
            g2d.fillOval(sunX, sunY, sunSize, sunSize); // draw the sun body

            // restore previous composite and paint to avoid affecting other drawings
            g2d.setComposite(oldComposite);
            g2d.setPaint(oldPaint);

            // draw clouds by looping through cloud arrays and calling drawCloud helper
            for (int i = 0; i < cloudX.length; i++) {
                int size = 80 + (i * 12); // vary cloud size slightly by index
                drawCloud(g2d, cloudX[i], cloudY[i], size); // draw each cloud
            }
        }

        // helper method that draws a fluffy cloud composed of several ovals
        private void drawCloud(Graphics2D g2d, int x, int y, int size) {
            Paint oldPaint = g2d.getPaint();   // save current paint
            Stroke oldStroke = g2d.getStroke(); // save current stroke
            Color outlineColor = new Color(255, 255, 255, 180); // outline color for cloud

            // gradient for cloud body (slightly off-white to give volume)
            GradientPaint cloudGradient = new GradientPaint(
                    x, y, new Color(255, 255, 255, 230),
                    x, y + size / 2, new Color(230, 230, 230, 180)
            );
            g2d.setPaint(cloudGradient); // set paint to cloud gradient

            // draw multiple overlapping ovals to form a fluffy cloud
            g2d.fillOval(x, y, size, size / 2);
            g2d.fillOval(x + size / 4, y - size / 6, size / 2, (int) (size / 1.8));
            g2d.fillOval(x + size / 2, y, (int) (size / 1.8), size / 2);
            g2d.fillOval(x + size / 8, y + size / 5, size / 2, size / 3);
            g2d.fillOval(x + size / 3, y + size / 6, size / 2, size / 3);

            // draw an outline around the main cloud shape for definition
            g2d.setColor(outlineColor);
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawOval(x, y, size, size / 2);

            // restore old paint and stroke to avoid side effects on other drawings
            g2d.setPaint(oldPaint);
            g2d.setStroke(oldStroke);
        }
    }
    // =================== END BACKGROUND PANEL CLASS ===================

    
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
            .addGap(0, 700, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoadingUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoadingUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoadingUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoadingUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        // schedule creating and showing the GUI on the Event Dispatch Thread
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoadingUI().setVisible(true); // create an instance and show the window
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
}
