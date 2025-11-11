/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.flappyFace.game;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.List;
import java.util.ArrayList;


public class Pipe {
    private int x;
    private final int width;
    private int gapY;         // top of gap (y coordinate)
    private final int gapHeight;
    private int speed;
    private final Image pipeImage;
    private final Image flippedPipeImage;

   
    private boolean passed = false;
    
    public Pipe(int startX, int width, int gapY, int gapHeight, int speed,
                Image pipeImage, Image flippedPipeImage) {
        this.x = startX;
        this.width = width;
        this.gapY = gapY;
        this.gapHeight = gapHeight;
        this.speed = speed;
        this.pipeImage = pipeImage;
        this.flippedPipeImage = flippedPipeImage;
    }

    public void update() {
        x -= speed;
    }

    public void draw(Graphics g, int panelHeight) {
        // top pipe
        int topHeight = gapY;
        if (flippedPipeImage != null) {
            g.drawImage(flippedPipeImage, x, 0, width, topHeight, null);
        } else {
            // fallback: draw rectangle
            g.fillRect(x, 0, width, topHeight);
        }

        // bottom pipe
        int bottomY = gapY + gapHeight;
        int bottomHeight = panelHeight - bottomY;
        if (pipeImage != null) {
            g.drawImage(pipeImage, x, bottomY, width, bottomHeight, null);
        } else {
            // fallback
            g.fillRect(x, bottomY, width, bottomHeight);
        }
    }

    public boolean isOffScreen() {
        return x + width < 0;
    }

    public Rectangle getTopBounds() {
        return new Rectangle(x, 0, width, gapY);
    }

    public Rectangle getBottomBounds(int panelHeight) {
        int bottomY = gapY + gapHeight;
        return new Rectangle(x, bottomY, width, panelHeight - bottomY);
    }
    public void setSpeed(int s) {
 
    this.speed = s;
    }
    
    public List<Rectangle> getBoundsList(int panelHeight) {
    List<Rectangle> bounds = new ArrayList<>();
    
   
    bounds.add(getTopBounds()); 

    bounds.add(getBottomBounds(panelHeight)); 
    
    return bounds;
    }
    
   
    public boolean isPassed() {
    return passed;
    }

    public void setPassed(boolean passed) {
    this.passed = passed;
    }
    
    public int getWidth() {
    return width;
    }
    
    public int getX() { return x; }
    
    
}

