package ui;

import java.awt.Color;

/**
 * Represents a 3D furniture item with position, size, and color
 */
public class Furniture3DItem {
    private String name;
    private int x, y, z;
    private int width, height, depth;
    private Color color;
    private float shadeIntensity = 0.0f;
    private double scale = 1.0;
    
    public Furniture3DItem(String name, int x, int y, int z, int width, int height, int depth, Color color) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.color = color;
    }
    
    // Getters and setters
    public String getName() { return name; }
    
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    
    public int getZ() { return z; }
    public void setZ(int z) { this.z = z; }
    
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    
    public int getDepth() { return depth; }
    public void setDepth(int depth) { this.depth = depth; }
    
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    
    public float getShadeIntensity() { return shadeIntensity; }
    public void setShadeIntensity(float shadeIntensity) { this.shadeIntensity = shadeIntensity; }
    
    public double getScale() { return scale; }
    public void setScale(double scale) { 
        this.scale = scale;
        // Adjust dimensions based on scale
        this.width = (int)(this.width * scale);
        this.height = (int)(this.height * scale);
        this.depth = (int)(this.depth * scale);
    }
    
    // Apply shading to the color
    public Color getShadedColor() {
        int r = (int)(color.getRed() * (1.0f - shadeIntensity));
        int g = (int)(color.getGreen() * (1.0f - shadeIntensity));
        int b = (int)(color.getBlue() * (1.0f - shadeIntensity));
        return new Color(r, g, b);
    }
}
