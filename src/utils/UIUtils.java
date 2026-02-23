package utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.Border;


public class UIUtils {
    
    public static class RoundedBorder implements Border {
        private final int radius;
        private final Color color;
        private final int thickness;
        
        public RoundedBorder(int radius, Color color, int thickness) {
            this.radius = radius;
            this.color = color;
            this.thickness = thickness;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            
            RoundRectangle2D roundRectangle = new RoundRectangle2D.Float(
                x + thickness/2, 
                y + thickness/2, 
                width - thickness, 
                height - thickness, 
                radius, 
                radius
            );
            g2d.setStroke(new java.awt.BasicStroke(thickness));
            g2d.draw(roundRectangle);
            g2d.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}