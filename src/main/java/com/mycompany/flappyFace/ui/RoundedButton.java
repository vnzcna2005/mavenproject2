/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.flappyFace.ui;

import javax.swing.*;
import java.awt.*;

/**
 * RoundedButton - simple custom button with rounded corners and basic hover/pressed coloring.
 * Place this class in the same package as HomeUI (com.mycompany.flappyFace.ui).
 */
public class RoundedButton extends JButton {
    private int radius;

    public RoundedButton(String text, int radius) {
        super(text);
        this.radius = radius;
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setForeground(Color.WHITE);
        // Do not set font here if you will set pixelFont from HomeUI.
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Determine base color based on state
        Color base = getBackground();
        if (getModel().isPressed()) {
            base = base.darker();
        } else if (getModel().isRollover()) {
            base = base.brighter();
        }

        // Optional subtle shadow
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillRoundRect(3, 4, getWidth() - 6, getHeight() - 6, radius, radius);

        // Main rounded background
        g2.setColor(base);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        // Border
        g2.setColor(base.darker());
        g2.setStroke(new BasicStroke(3f));
        g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, radius, radius);

        // Draw centered text
        FontMetrics fm = g2.getFontMetrics(getFont());
        int textWidth = fm.stringWidth(getText());
        int textAscent = fm.getAscent();
        int tx = (getWidth() - textWidth) / 2;
        int ty = (getHeight() + textAscent) / 2 - 3;
        g2.setColor(getForeground());
        g2.setFont(getFont());
        g2.drawString(getText(), tx, ty);

        g2.dispose();

        // Do not call super.paintComponent(g) because we painted everything ourselves.
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        // Ensure reasonable padding for pixel font button
        d.width += 40;
        d.height += 16;
        return d;
    }
}

