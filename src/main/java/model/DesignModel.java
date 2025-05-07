package model;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Shared model class for the furniture design application
 * This class acts as a bridge between the 2D and 3D panels
 * to ensure that changes in one panel are reflected in the other
 */
public class DesignModel {
    public interface DesignModelListener {
        void onModelChanged(String changeType);
    }
    
    private int roomWidth = 500;
    private int roomLength = 400;
    private int roomHeight = 250;
    private Color roomColor = Color.WHITE;
    private String roomShape = "Rectangle";
    private List<FurnitureItemModel> furnitureItems = new ArrayList<>();
    private List<DesignModelListener> listeners = new CopyOnWriteArrayList<>();
    
    // Lighting and shadow settings
    private float lightIntensity = 0.8f;
    private float shadowIntensity = 0.5f;
    private float contrast = 1.0f;
    private Color ambientLightColor = new Color(255, 255, 220); // Warm light
    
    public DesignModel() {
        // Default constructor
    }
    
    /**
     * Adds a listener to the model
     * @param listener the listener to add
     */
    public void addListener(DesignModelListener listener) {
        listeners.add(listener);
    }
    
    /**
     * Removes a listener from the model
     * @param listener the listener to remove
     */
    public void removeListener(DesignModelListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notifies all listeners of a change in the model
     * @param changeType the type of change that occurred
     */
    private void notifyListeners(String changeType) {
        for (DesignModelListener listener : listeners) {
            listener.onModelChanged(changeType);
        }
    }
    
    /**
     * Adds a furniture item to the model
     * @param item the furniture item to add
     */
    public void addFurnitureItem(FurnitureItemModel item) {
        furnitureItems.add(item);
        notifyListeners("ITEM_ADDED");
    }
    
    /**
     * Removes a furniture item from the model
     * @param item the furniture item to remove
     */
    public void removeFurnitureItem(FurnitureItemModel item) {
        furnitureItems.remove(item);
        notifyListeners("ITEM_REMOVED");
    }
    
    /**
     * Updates a furniture item in the model
     * @param item the furniture item to update
     */
    public void updateFurnitureItem(FurnitureItemModel item) {
        notifyListeners("ITEM_UPDATED");
    }
    
    /**
     * Gets all furniture items in the model
     * @return a list of all furniture items
     */
    public List<FurnitureItemModel> getFurnitureItems() {
        return new ArrayList<>(furnitureItems);
    }
    
    /**
     * Sets the room dimensions
     * @param width the room width
     * @param length the room length
     * @param height the room height
     */
    public void setRoomDimensions(int width, int length, int height) {
        this.roomWidth = width;
        this.roomLength = length;
        this.roomHeight = height;
        notifyListeners("ROOM_DIMENSIONS_CHANGED");
    }
    
    /**
     * Sets the room color
     * @param color the room color
     */
    public void setRoomColor(Color color) {
        this.roomColor = color;
        notifyListeners("ROOM_COLOR_CHANGED");
    }
    
    /**
     * Gets the room width
     * @return the room width
     */
    public int getRoomWidth() {
        return roomWidth;
    }
    
    /**
     * Gets the room length
     * @return the room length
     */
    public int getRoomLength() {
        return roomLength;
    }
    
    /**
     * Gets the room height
     * @return the room height
     */
    public int getRoomHeight() {
        return roomHeight;
    }
    
    /**
     * Gets the room color
     * @return the room color
     */
    public Color getRoomColor() {
        return roomColor;
    }
    
    /**
     * Gets the room shape
     * @return the room shape (Rectangle, L-Shape, etc.)
     */
    public String getRoomShape() {
        return roomShape;
    }
    
    /**
     * Sets the room shape
     * @param shape the new room shape
     */
    public void setRoomShape(String shape) {
        this.roomShape = shape;
        notifyListeners("ROOM_SHAPE_CHANGED");
    }
    
    /**
     * Gets the light intensity
     * @return the light intensity (0.0-1.0)
     */
    public float getLightIntensity() {
        return lightIntensity;
    }
    
    /**
     * Sets the light intensity
     * @param intensity the new light intensity (0.0-1.0)
     */
    public void setLightIntensity(float intensity) {
        this.lightIntensity = Math.max(0.0f, Math.min(1.0f, intensity));
        notifyListeners("LIGHTING_CHANGED");
    }
    
    /**
     * Gets the shadow intensity
     * @return the shadow intensity (0.0-1.0)
     */
    public float getShadowIntensity() {
        return shadowIntensity;
    }
    
    /**
     * Sets the shadow intensity
     * @param intensity the new shadow intensity (0.0-1.0)
     */
    public void setShadowIntensity(float intensity) {
        this.shadowIntensity = Math.max(0.0f, Math.min(1.0f, intensity));
        notifyListeners("LIGHTING_CHANGED");
    }
    
    /**
     * Gets the contrast level
     * @return the contrast level (0.5-1.5)
     */
    public float getContrast() {
        return contrast;
    }
    
    /**
     * Sets the contrast level
     * @param contrast the new contrast level (0.5-1.5)
     */
    public void setContrast(float contrast) {
        this.contrast = Math.max(0.5f, Math.min(1.5f, contrast));
        notifyListeners("LIGHTING_CHANGED");
    }
    
    /**
     * Gets the ambient light color
     * @return the ambient light color
     */
    public Color getAmbientLightColor() {
        return ambientLightColor;
    }
    
    /**
     * Sets the ambient light color
     * @param color the new ambient light color
     */
    public void setAmbientLightColor(Color color) {
        this.ambientLightColor = color;
        notifyListeners("LIGHTING_CHANGED");
    }
}
