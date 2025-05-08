package model;

import java.awt.Color;

/**
 * Model class for furniture items
 * This class is used by both the 2D and 3D panels
 */
public class FurnitureItemModel {
    private String name;
    private int x, y, z;
    private int width, height, depth;
    private Color color;
    
    /**
     * Creates a new furniture item model
     * @param name the name of the furniture item
     * @param x the x position
     * @param y the y position
     * @param z the z position
     * @param width the width
     * @param height the height
     * @param depth the depth
     * @param color the color
     */
    public FurnitureItemModel(String name, int x, int y, int z, int width, int height, int depth, Color color) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.color = color;
    }
    
    /**
     * Creates a new furniture item model from 2D coordinates
     * @param name the name of the furniture item
     * @param x the x position in 2D
     * @param y the y position in 2D
     * @param width the width
     * @param height the height
     * @param color the color
     */
    public static FurnitureItemModel from2D(String name, int x, int y, int width, int height, Color color) {
        // Convert 2D coordinates to 3D
        // Adjust the coordinates to maintain proper spacing in 3D
        // We'll use x as x, y as z, and a fixed y value for the floor level
        int depth = determineFurnitureDepth(name, width, height);
        return new FurnitureItemModel(name, x - 250, 0, y - 200, width, height, depth, color);
    }
    
    /**
     * Determines an appropriate depth for the furniture based on its type and dimensions
     * @param name the furniture type
     * @param width the width
     * @param height the height
     * @return an appropriate depth value
     */
    private static int determineFurnitureDepth(String name, int width, int height) {
        switch (name) {
            case "Dining Table":
                return width/2;
            case "Chair":
                return width;
            case "Sofa":
                return width/3;
            case "Coffee Table":
                return width/2;
            case "Bed":
                return height/4;
            case "Wardrobe":
                return width/2;
            case "Lamp":
                return width/2;
            default:
                return width/2;
        }
    }
    
    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
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
    public void setColor(Color color) { this.color = color; }
}
