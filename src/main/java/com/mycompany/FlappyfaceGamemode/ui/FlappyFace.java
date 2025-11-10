package com.mycompany.FlappyfaceGamemode.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyFace extends JPanel implements ActionListener, KeyListener {
    int boardwidth = 360;
    int boardheight = 640;

    // Images
    Image BackgroundImg;
    Image FaceImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // Face (bird)
    int FaceX = boardwidth / 8;
    int FaceY = boardheight / 2;
    int Facewidth = 60;
    int Faceheight = 83;

    class Face {
        int x = FaceX;
        int y = FaceY;
        int width = Facewidth;
        int height = Faceheight;
        Image img;

        Face(Image img) {
            this.img = img;
        }
    }
    //Pipes
    int PipeX = boardwidth;
    int PipeY =10;
    int PipeWidth=64;
    int PipeHeight=512;
    
    class Pipe{
        int X = PipeX;
        int Y = PipeY;
        int width = PipeWidth;
        int height = PipeHeight;
        Image img;
        boolean passed = false;
        
        Pipe(Image img){
            this.img = img;
        }
    }

    Face face;
    int velocityX = -3;
    int velocityY = 0;
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();
    
    Timer Gameloop;
    Timer placePipesTimer;
    
    FlappyFace() {
        setPreferredSize(new Dimension(boardwidth, boardheight));
        setFocusable(true);
        addKeyListener(this);

        // Load Images
        BackgroundImg = new ImageIcon(getClass().getResource("/images/flappyfacebg.png")).getImage();
        FaceImg = new ImageIcon(getClass().getResource("/images/Face.png")).getImage();
        topPipeImg = new ImageIcon(getClass().getResource("/images/toppipe(1).png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("/images/bottompipe(1).png")).getImage();

        // Create face
        face = new Face(FaceImg);
        pipes = new ArrayList<Pipe>();
        
        //Place Pipes Timer
        placePipesTimer = new Timer(1500, new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                placePipes();
            }
        });
            placePipesTimer.start();
        // Game timer (60 FPS)
        Gameloop = new Timer(1000 / 60, this);
        Gameloop.start();
    }
 
    public void placePipes() {
    int randomPipeY = (int) (PipeY - PipeHeight / 6 - Math.random() * (PipeHeight / 2));
    
    // smaller opening (closer pipes)
    int openingSpace = boardheight / 4; // was boardheight / 4 → smaller gap
    
    Pipe topPipe = new Pipe(topPipeImg);
    topPipe.Y = randomPipeY;
    pipes.add(topPipe);

    Pipe bottomPipe = new Pipe(bottomPipeImg);
    bottomPipe.Y = topPipe.Y + PipeHeight + openingSpace;
    pipes.add(bottomPipe);
}

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //Background
        g.drawImage(BackgroundImg, 0, 0, boardwidth, boardheight, null);
        
        //Face
        g.drawImage(face.img, face.x, face.y, face.width, face.height, null);
        
        //Pipes
        for (int i=0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.X, pipe.Y, pipe.width, pipe.height,null);
        }
    }


    public void move() {
        
        //Bird
        velocityY += gravity;
        face.y += velocityY;
        face.y = Math.max(face.y, 0); // prevent going above screen
        
        //Pipes
        for (int i = 0; i < pipes.size(); i++){
            Pipe pipe = pipes.get(i);
            pipe.X += velocityX;
        }
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9; // flap up
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
