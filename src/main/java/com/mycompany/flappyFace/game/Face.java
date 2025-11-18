/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.flappyFace.game;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent; 
import java.util.logging.Level;
import java.util.logging.Logger;


public class Face {
    private static final Logger logger = Logger.getLogger(Face.class.getName());

    private int x, y; // Position of the face
    private int width, height; // Size of the face
    private Image faceImage; 
    private double velocityY; // Vertical speed of the face (for falling/jumping)
    private final double GRAVITY = 0.5; // How fast the face falls
    private final double JUMP_STRENGTH = -8; // How high the face jumps (negative for upwards movement)
    private boolean isJumping = false;

    public Face(int x, int y, int width, int height, Image faceImage) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.faceImage = faceImage;
        this.velocityY = 0; 
    }

    public void update() {
        // Apply gravity
        velocityY += GRAVITY;
        y += velocityY;

        //prevent face from going too high
        if (y < 0) {
            y = 0;
            velocityY = 0; // Stop vertical movement if hitting the top
        }
      
    }

    public void jump() {
        velocityY = JUMP_STRENGTH; // Apply jump strength
        isJumping = true; // Mark as jumping
    }

    public void draw(Graphics g) {
        if (faceImage != null) {
            g.drawImage(faceImage, x, y, width, height, null);
        } else {           
            logger.warning("Face image not loaded, drawing red rectangle.");
            g.setColor(java.awt.Color.RED);
            g.fillRect(x, y, width, height);
        }
    }

    // Getters for position and bounds (useful for collision detection later)
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }


public Rectangle getBounds() {
    // Define a margin to shrink the hitbox (e.g., 10 pixels on all sides)
    final int MARGIN = 39;
    
    // New X, Y, Width, and Height are calculated with the margin applied
    int boundsX = x + MARGIN;
    int boundsY = y + MARGIN;
    int boundsWidth = width - (2 * MARGIN);
    int boundsHeight = height - (2 * MARGIN);
    
    return new Rectangle(boundsX, boundsY, boundsWidth, boundsHeight);
}

    // This setter will be important to reset the face's position for restarts later
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.velocityY = 0; // Reset velocity too
    }

}