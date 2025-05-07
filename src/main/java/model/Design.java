package model;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Design implements Serializable {
    private String id;
    private String name;
    private String designerId;
    private int roomWidth;
    private int roomLength;
    private String roomShape; // "Rectangle", "Square", "L-Shape"
    private Color roomColor;
    private List<FurnitureItem> furnitureItems;
    private long createdTime;
    private long lastModifiedTime;
    
    public Design(String name, String designerId) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.designerId = designerId;
        this.roomWidth = 500;
        this.roomLength = 400;
        this.roomShape = "Rectangle";
        this.roomColor = Color.WHITE;
        this.furnitureItems = new ArrayList<>();
        this.createdTime = System.currentTimeMillis();
        this.lastModifiedTime = this.createdTime;
    }
    
    // Getters and setters
    public String getId() { return id; }
    
    public String getName() { return name; }
    public void setName(String name) { 
        this.name = name; 
        updateModifiedTime();
    }
    
    public String getDesignerId() { return designerId; }
    
    public int getRoomWidth() { return roomWidth; }
    public void setRoomWidth(int roomWidth) { 
        this.roomWidth = roomWidth; 
        updateModifiedTime();
    }
    
    public int getRoomLength() { return roomLength; }
    public void setRoomLength(int roomLength) { 
        this.roomLength = roomLength; 
        updateModifiedTime();
    }
    
    public String getRoomShape() { return roomShape; }
    public void setRoomShape(String roomShape) { 
        this.roomShape = roomShape; 
        updateModifiedTime();
    }
    
    public Color getRoomColor() { return roomColor; }
    public void setRoomColor(Color roomColor) { 
        this.roomColor = roomColor; 
        updateModifiedTime();
    }
    
    public List<FurnitureItem> getFurnitureItems() { return furnitureItems; }
    
    public void addFurnitureItem(FurnitureItem item) {
        furnitureItems.add(item);
        updateModifiedTime();
    }
    
    public void removeFurnitureItem(FurnitureItem item) {
        furnitureItems.remove(item);
        updateModifiedTime();
    }
    
    public long getCreatedTime() { return createdTime; }
    
    public long getLastModifiedTime() { return lastModifiedTime; }
    
    private void updateModifiedTime() {
        this.lastModifiedTime = System.currentTimeMillis();
    }
    
    // Serializable nested class for furniture items
    public static class FurnitureItem implements Serializable {
        private int x, y, width, height;
        private Color color;
        private String type;
        private double scale = 1.0;
        private float shadeIntensity = 0.0f;
        
        public FurnitureItem(int x, int y, int width, int height, Color color, String type) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
            this.type = type;
        }
        
        // Getters and setters
        public int getX() { return x; }
        public void setX(int x) { this.x = x; }
        
        public int getY() { return y; }
        public void setY(int y) { this.y = y; }
        
        public int getWidth() { return width; }
        public void setWidth(int width) { this.width = width; }
        
        public int getHeight() { return height; }
        public void setHeight(int height) { this.height = height; }
        
        public Color getColor() { return color; }
        public void setColor(Color color) { this.color = color; }
        
        public String getType() { return type; }
        
        public double getScale() { return scale; }
        public void setScale(double scale) { this.scale = scale; }
        
        public float getShadeIntensity() { return shadeIntensity; }
        public void setShadeIntensity(float shadeIntensity) { 
            this.shadeIntensity = shadeIntensity; 
        }
        
        // Utility methods
        public void move(int dx, int dy) {
            x += dx;
            y += dy;
        }
        
        public void rescale(double factor) {
            scale *= factor;
            width = (int)(width * factor);
            height = (int)(height * factor);
        }
    }
}
