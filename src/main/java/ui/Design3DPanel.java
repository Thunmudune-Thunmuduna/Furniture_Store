package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

import model.DesignModel;
import model.FurnitureItemModel;

/**
 * 3D View Panel using Java2D for a simplified 3D rendering
 * This class creates a 3D-like view of the furniture design
 * Note: For full 3D rendering, JOGL libraries would need to be properly installed
 */
public class Design3DPanel extends JPanel implements MouseMotionListener, MouseWheelListener, DesignModel.DesignModelListener {
    private float xRotation = 20.0f;
    private float yRotation = 30.0f;
    private float zRotation = 0.0f;
    private float zoom = 1.0f;
    private int lastX, lastY;
    private List<Furniture3DItem> furnitureItems = new ArrayList<>();
    private boolean isRotating = false;
    private boolean isShiftDown = false;
    private DesignModel model;
    private Timer rotationTimer;
    
    /**
     * Applies lighting effects to a color based on current lighting settings
     * @param color The original color
     * @return The color with lighting effects applied
     */
    private Color applyLightingToColor(Color color) {
        float lightIntensity = model.getLightIntensity();
        float contrast = model.getContrast();
        Color ambientLight = model.getAmbientLightColor();
        
        // Extract RGB components
        float[] colorComponents = color.getRGBColorComponents(null);
        float[] lightComponents = ambientLight.getRGBColorComponents(null);
        
        // Apply light color influence
        float r = colorComponents[0] * lightComponents[0] * lightIntensity;
        float g = colorComponents[1] * lightComponents[1] * lightIntensity;
        float b = colorComponents[2] * lightComponents[2] * lightIntensity;
        
        // Apply contrast
        r = 0.5f + (r - 0.5f) * contrast;
        g = 0.5f + (g - 0.5f) * contrast;
        b = 0.5f + (b - 0.5f) * contrast;
        
        // Clamp values
        r = Math.max(0, Math.min(1, r));
        g = Math.max(0, Math.min(1, g));
        b = Math.max(0, Math.min(1, b));
        
        return new Color(r, g, b);
    }
    
    /**
     * Draws shadows on the floor based on furniture placement
     * @param g2d Graphics context
     * @param roomShape Shape of the room
     * @param width Room width
     * @param length Room length
     */
    private void drawShadows(Graphics2D g2d, String roomShape, int width, int length) {
        float shadowIntensity = model.getShadowIntensity();
        
        // Create a semi-transparent shadow color
        Color shadowColor = new Color(0, 0, 0, (int)(100 * shadowIntensity));
        g2d.setColor(shadowColor);
        
        // Save the current composite
        Composite originalComposite = g2d.getComposite();
        
        // Use alpha composite for shadow blending
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, shadowIntensity));
        
        // Draw shadows for each furniture item
        for (Furniture3DItem item : furnitureItems) {
            int x = item.getX();
            int itemY = item.getY(); // Using y for shadow positioning
            int itemWidth = item.getWidth();
            int itemHeight = item.getHeight();
            
            // Calculate shadow position (offset based on light direction)
            int shadowX = x + 15;
            int shadowY = itemY + 15;
            
            // Draw an oval shadow beneath the item
            g2d.fillOval(shadowX - itemWidth/2, shadowY - itemHeight/4, itemWidth, itemHeight/2);
        }
        
        // Draw wall shadows
        if (roomShape.equals("L-Shape")) {
            // L-shape specific shadows
            int shortWidth = width * 2/3;
            int shortLength = length * 2/3;
            
            // Shadow along the inner corner
            g2d.fillRect(width/2 - shortWidth - 10, length/2 - shortLength - 10, 10, shortLength);
            g2d.fillRect(width/2 - shortWidth - 10, length/2 - shortLength - 10, shortWidth, 10);
        }
        
        // Restore the original composite
        g2d.setComposite(originalComposite);
    }
    
    // Inner class for the actual 3D rendering area
    private class DrawingArea extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // Enable anti-aliasing for smoother lines
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Set the origin to the center of the panel
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            g2d.translate(centerX, centerY);
            
            // Apply zoom
            g2d.scale(zoom, zoom);
            
            // In Java2D, we need to use a combination of techniques to simulate 3D rotation
            
            // Store the original transform for later restoration if needed
            // Removed unused variable = g2d.getTransform();
            
            // First apply Y rotation (around vertical axis - this is the main rotation)
            g2d.rotate(Math.toRadians(yRotation));
            
            // Apply X rotation (tilt) - we simulate this with a combination of scaling and shearing
            double tiltRadians = Math.toRadians(xRotation);
            double tiltFactor = Math.cos(tiltRadians);
            double shearFactor = Math.sin(tiltRadians) * 0.5;
            
            // Apply the tilt effect
            g2d.scale(1.0, tiltFactor);
            g2d.shear(0, shearFactor);
            
            // Apply Z rotation (roll) - this rotates the entire view
            if (zRotation != 0) {
                g2d.rotate(Math.toRadians(zRotation));
            }
            
            // Draw room
            drawRoom(g2d);
            
            // Draw furniture
            drawFurniture(g2d);
        }
    }
    
    private DrawingArea drawingArea;
    
    public Design3DPanel(DesignModel model) {
        this.model = model;
        model.addListener(this);
        setLayout(new BorderLayout());
        
        // Create the drawing area for 3D content
        drawingArea = new DrawingArea();
        drawingArea.setBackground(new Color(240, 240, 240));
        
        // Add mouse listeners for rotation and zooming to the drawing area
        drawingArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getX();
                lastY = e.getY();
                isRotating = true;
                drawingArea.requestFocusInWindow(); // Ensure panel has focus for key events
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                isRotating = false;
            }
        });
        
        // Add key listener for modifier keys (shift for z-rotation)
        drawingArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    isShiftDown = true;
                } else if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    // Reset view on space bar
                    resetView();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    isShiftDown = false;
                }
            }
        });
        
        // Make drawing area focusable to receive key events
        drawingArea.setFocusable(true);
        
        drawingArea.addMouseMotionListener(this);
        drawingArea.addMouseWheelListener(this);
        
        // Add controls panel with buttons
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        
        JButton resetViewBtn = new JButton("Reset View");
        resetViewBtn.addActionListener(unused -> resetView());
        
        JButton rotateLeftBtn = new JButton("Rotate Left");
        rotateLeftBtn.addActionListener(unused -> startRotation(-1));
        
        JButton rotateRightBtn = new JButton("Rotate Right");
        rotateRightBtn.addActionListener(unused -> startRotation(1));
        
        JButton stopRotateBtn = new JButton("Stop");
        stopRotateBtn.addActionListener(unused -> stopRotation());
        
        controlPanel.add(resetViewBtn);
        controlPanel.add(rotateLeftBtn);
        controlPanel.add(rotateRightBtn);
        controlPanel.add(stopRotateBtn);
        controlPanel.add(new JLabel("Drag: Rotate | Shift+Drag: Z-Rotate | Scroll: Zoom"));
        add(drawingArea, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
        
        // Synchronize with the model
        syncWithModel();
    }
    
    /**
     * Synchronizes the 3D furniture items with the model
     */
    private void syncWithModel() {
        // Clear the current items
        furnitureItems.clear();
        
        // Add items from the model
        for (FurnitureItemModel item : model.getFurnitureItems()) {
            furnitureItems.add(new Furniture3DItem(
                item.getName(),
                item.getX(),
                item.getY(),
                item.getZ(),
                item.getWidth(),
                item.getHeight(),
                item.getDepth(),
                item.getColor()
            ));
        }
        
        repaint();
    }
    
    @Override
    public void onModelChanged(String changeType) {
        if (changeType.equals("ITEM_ADDED") || 
            changeType.equals("ITEM_REMOVED") ||
            changeType.equals("ITEM_UPDATED")) {
            // When items are added, removed, or updated, synchronize with the model
            syncWithModel();
        } else if (changeType.equals("ROOM_DIMENSIONS_CHANGED") || 
            changeType.equals("ROOM_COLOR_CHANGED") ||
            changeType.equals("ROOM_SHAPE_CHANGED") ||
            changeType.equals("LIGHTING_CHANGED")) {
            // For other changes, just repaint
            drawingArea.repaint();
        }
    }
    
    /**
     * This method is no longer used as we've moved the painting to the DrawingArea inner class
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // All drawing is now done in the DrawingArea class
    }
    
    private void drawRoom(Graphics2D g2d) {
        // Get room dimensions, color, and shape from the model
        int width = model.getRoomWidth();
        int length = model.getRoomLength();
        int height = model.getRoomHeight();
        Color color = model.getRoomColor();
        String shape = model.getRoomShape();
        
        // Apply lighting effects to room color
        Color adjustedColor = applyLightingToColor(color);
        
        // Draw floor based on room shape
        g2d.setColor(adjustedColor);
        
        switch (shape) {
            case "L-Shape":
                // Draw L-shaped floor
                int shortWidth = width * 2/3;
                int shortLength = length * 2/3;
                
                // Main rectangle
                g2d.fillRect(-width/2, -length/2, width, length);
                
                // Cut out the inner corner to create L-shape
                g2d.setColor(Color.LIGHT_GRAY); // Background color
                g2d.fillRect(width/2 - shortWidth, length/2 - shortLength, shortWidth, shortLength);
                
                // Redraw the outline
                g2d.setColor(Color.BLACK);
                // Outer edge
                g2d.drawRect(-width/2, -length/2, width, length);
                // Inner edge
                g2d.drawRect(width/2 - shortWidth, length/2 - shortLength, shortWidth, shortLength);
                break;
                
            case "Square":
                // Make sure dimensions are equal
                int size = Math.max(width, length);
                g2d.fillRect(-size/2, -size/2, size, size);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(-size/2, -size/2, size, size);
                break;
                
            case "Rectangle":
            default:
                // Standard rectangle room
                g2d.fillRect(-width/2, -length/2, width, length);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(-width/2, -length/2, width, length);
                break;
        }
        
        // Draw walls with lighting effects
        Color wallColor = new Color(240, 240, 240);
        Color adjustedWallColor = applyLightingToColor(wallColor);
        
        // Back wall
        g2d.setColor(adjustedWallColor);
        
        if (shape.equals("L-Shape")) {
            // Draw L-shaped back wall
            int shortWidth = width * 2/3;
            @SuppressWarnings("unused")
            int shortLength = length * 2/3;
            
            // Main part
            g2d.fillRect(-width/2, -length/2, width, height/3);
            // Cut out the inner corner
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.fillRect(width/2 - shortWidth, -length/2, shortWidth, height/3);
            
            g2d.setColor(Color.BLACK);
            g2d.drawRect(-width/2, -length/2, width - shortWidth, height/3);
        } else {
            g2d.fillRect(-width/2, -length/2, width, height/3);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(-width/2, -length/2, width, height/3);
        }
        
        // Left wall with shadow effect
        Color leftWallColor = new Color(220, 220, 220);
        Color adjustedLeftWallColor = applyLightingToColor(leftWallColor);
        g2d.setColor(adjustedLeftWallColor);
        
        if (shape.equals("L-Shape")) {
            // Draw L-shaped left wall
            g2d.fillRect(-width/2, -length/2, width/6, length * 2/3);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(-width/2, -length/2, width/6, length * 2/3);
        } else {
            g2d.fillRect(-width/2, -length/2, width/6, length);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(-width/2, -length/2, width/6, length);
        }
        
        // Draw shadows on the floor if shadow intensity is high enough
        if (model.getShadowIntensity() > 0.2f) {
            drawShadows(g2d, shape, width, length);
        }
    }
    
    private void drawFurniture(Graphics2D g2d) {
        // Sort furniture by z-order for proper rendering
        furnitureItems.sort((a, b) -> b.getZ() - a.getZ());
        
        for (Furniture3DItem item : furnitureItems) {
            // Calculate position with perspective
            int x = item.getX();
            @SuppressWarnings("unused")
            int y = item.getY();
            int z = item.getZ();
            int width = item.getWidth();
            int height = item.getHeight();
            int depth = item.getDepth();
            
            // Apply perspective scaling based on z-position
            double perspectiveScale = 1.0 - (z / 1000.0);
            if (perspectiveScale < 0.5) perspectiveScale = 0.5;
            
            // Create ambient shadow color for 3D effect
            Color ambientColor = new Color(0, 0, 0, 50);
            
            // Apply lighting effects to furniture color
            Color adjustedColor = applyLightingToColor(item.getColor());
            
            // Draw the furniture item based on its type
            switch (item.getName()) {
                case "Table":
                case "Dining Table":
                case "Coffee Table":
                    drawTable(g2d, x, z, width, height, depth, adjustedColor);
                    break;
                case "Chair":
                    drawChair(g2d, x, z, width, height, depth, adjustedColor);
                    break;
                case "Sofa":
                    drawSofa(g2d, x, z, width, height, depth, adjustedColor);
                    break;
                case "Bed":
                    drawBed(g2d, x, z, width, height, depth, adjustedColor);
                    break;
                case "Wardrobe":
                    drawWardrobe(g2d, x, z, width, height, depth, adjustedColor);
                    break;
                case "Lamp":
                    drawLamp(g2d, x, z, width, height, depth, adjustedColor);
                    break;
                default:
                    // Draw a simple box for unknown furniture types
                    drawBox(g2d, x, z, width, height, depth, item.getColor(), ambientColor);
            }
        }
    }
    
    private void drawBox(Graphics2D g2d, int x, int y, int width, int height, int depth, Color color, Color ambientColor) {
        // Top face
        g2d.setColor(color.brighter());
        Path2D topFace = new Path2D.Double();
        topFace.moveTo(x - width/2, y - height/2 - depth/4);
        topFace.lineTo(x + width/2, y - height/2 - depth/4);
        topFace.lineTo(x + width/2 - depth/4, y - height/2);
        topFace.lineTo(x - width/2 - depth/4, y - height/2);
        topFace.closePath();
        g2d.fill(topFace);
        g2d.setColor(Color.BLACK);
        g2d.draw(topFace);
        
        // Front face
        g2d.setColor(color);
        g2d.fillRect(x - width/2 - depth/4, y - height/2, width, height);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x - width/2 - depth/4, y - height/2, width, height);
        
        // Side face
        g2d.setColor(color.darker());
        Path2D sideFace = new Path2D.Double();
        sideFace.moveTo(x + width/2 - depth/4, y - height/2);
        sideFace.lineTo(x + width/2, y - height/2 - depth/4);
        sideFace.lineTo(x + width/2, y + height/2 - depth/4);
        sideFace.lineTo(x + width/2 - depth/4, y + height/2);
        sideFace.closePath();
        g2d.fill(sideFace);
        g2d.setColor(Color.BLACK);
        g2d.draw(sideFace);
    }
    
    private void drawTable(Graphics2D g2d, int x, int y, int width, int height, int depth, Color color) {
        // Create ambient shadow color for 3D effect
        Color ambientColor = new Color(0, 0, 0, 50);
        
        // Draw table top
        drawBox(g2d, x, y - height/2, width, height/4, depth, color, ambientColor);
        
        // Draw table legs
        int legWidth = width/10;
        int legDepth = depth/10;
        
        // Front legs
        drawBox(g2d, x - width/2 + legWidth, y, legWidth, height, legDepth, color.darker(), ambientColor);
        drawBox(g2d, x + width/2 - legWidth, y, legWidth, height, legDepth, color.darker(), ambientColor);
        
        // Back legs (partially hidden)
        drawBox(g2d, x - width/2 + legWidth, y - depth + legDepth, legWidth, height, legDepth, color.darker(), ambientColor);
        drawBox(g2d, x + width/2 - legWidth, y - depth + legDepth, legWidth, height, legDepth, color.darker(), ambientColor);
    }
    
    private void drawChair(Graphics2D g2d, int x, int y, int width, int height, int depth, Color color) {
        // Create ambient shadow color for 3D effect
        Color ambientColor = new Color(0, 0, 0, 50);
        
        // Draw seat
        drawBox(g2d, x, y - height/3, width, height/6, depth, color, ambientColor);
        
        // Draw back
        drawBox(g2d, x, y - height*2/3, width, height*2/3, depth/6, color.darker(), ambientColor);
        
        // Draw legs
        int legWidth = width/12;
        int legDepth = depth/12;
        
        // Front legs
        drawBox(g2d, x - width/2 + legWidth, y, legWidth, height/2, legDepth, color.darker(), ambientColor);
        drawBox(g2d, x + width/2 - legWidth, y, legWidth, height/2, legDepth, color.darker(), ambientColor);
        
        // Back legs
        drawBox(g2d, x - width/2 + legWidth, y - depth + legDepth, legWidth, height/2, legDepth, color.darker(), ambientColor);
        drawBox(g2d, x + width/2 - legWidth, y - depth + legDepth, legWidth, height/2, legDepth, color.darker(), ambientColor);
    }
    
    private void drawSofa(Graphics2D g2d, int x, int y, int width, int height, int depth, Color color) {
        // Create ambient shadow color for 3D effect
        Color ambientColor = new Color(0, 0, 0, 50);
        
        // Draw base
        drawBox(g2d, x, y - height/4, width, height/2, depth, color, ambientColor);
        
        // Draw back
        drawBox(g2d, x, y - height*3/4, width, height/2, depth/3, color.darker(), ambientColor);
        
        // Draw arms
        drawBox(g2d, x - width/2 + width/10, y - height/3, width/5, height/2, depth, color.darker(), ambientColor);
        drawBox(g2d, x + width/2 - width/10, y - height/3, width/5, height/2, depth, color.darker(), ambientColor);
    }
    
    /**
     * Draws a bed with headboard, mattress, pillows, and blanket
     * Inspired by the 3D bedroom project implementation
     */
    private void drawBed(Graphics2D g2d, int x, int y, int width, int height, int depth, Color color) {
        // Create ambient shadow color for 3D effect
        Color ambientColor = new Color(0, 0, 0, 50);
        
        // Draw bed frame/base
        drawBox(g2d, x, y, width, height/6, depth, color.darker(), ambientColor);
        
        // Draw mattress
        Color mattressColor = new Color(220, 220, 220);  // Light gray
        Color mattressAmbient = new Color(180, 180, 180);
        drawBox(g2d, x, y - height/8, (int)(width * 0.95), height/8, (int)(depth * 0.9), mattressColor, mattressAmbient);
        
        // Draw headboard
        drawBox(g2d, x - width/2 + width/20, y - height/4, width/10, height/2, depth/10, color, ambientColor);
        
        // Draw pillows
        Color pillowColor = new Color(240, 240, 240);  // Off-white
        Color pillowAmbient = new Color(200, 200, 200);
        
        // Left pillow
        drawBox(g2d, x - width/4, y - height/6, width/4, height/12, depth/3, pillowColor, pillowAmbient);
        
        // Right pillow
        drawBox(g2d, x + width/4, y - height/6, width/4, height/12, depth/3, pillowColor, pillowAmbient);
        
        // Draw blanket
        Color blanketColor = new Color(70, 130, 180);  // Steel blue
        Color blanketAmbient = new Color(35, 65, 90);
        drawBox(g2d, x, y, (int)(width * 0.9), height/20, (int)(depth * 0.7), blanketColor, blanketAmbient);
    }
    
    /**
     * Draws a wardrobe with doors, drawers and handles
     * Inspired by the 3D bedroom project implementation
     */
    private void drawWardrobe(Graphics2D g2d, int x, int y, int width, int height, int depth, Color color) {
        // Create ambient shadow color for 3D effect
        Color ambientColor = new Color(0, 0, 0, 50);
        
        // Draw main body
        drawBox(g2d, x, y - height/2, width, height, depth, color, ambientColor);
        
        // Draw door lines
        g2d.setColor(Color.BLACK);
        g2d.drawLine(x, y - height, x, y);
        
        // Draw handles
        int handleSize = width/20;
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillOval(x - handleSize*2, y - height/2, handleSize, handleSize);
        g2d.fillOval(x + handleSize, y - height/2, handleSize, handleSize);
    }
    
    /**
     * Draws a lamp with base, stand, and shade
     * Inspired by the 3D bedroom project implementation
     */
    private void drawLamp(Graphics2D g2d, int x, int y, int width, int height, int depth, Color color) {
        // Create ambient shadow color for 3D effect
        Color ambientColor = new Color(0, 0, 0, 50);
        
        // Lamp base
        Color baseColor = color.darker();
        drawBox(g2d, x, y, width/3, height/10, width/3, baseColor, ambientColor);
        
        // Lamp stand
        drawBox(g2d, x, y - height/2, width/20, height, width/20, color, ambientColor);
        
        // Lamp shade
        // Draw a cone for the lampshade
        int shadeWidth = width;
        int shadeHeight = height/4;
        
        // Draw the shade as an oval from above
        g2d.setColor(color.brighter());
        g2d.fillOval(x - shadeWidth/2, y - height, shadeWidth, shadeHeight);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(x - shadeWidth/2, y - height, shadeWidth, shadeHeight);
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (isRotating) {
            int x = e.getX();
            int y = e.getY();
            
            // Calculate rotation based on mouse movement
            if (lastX != 0 && lastY != 0) {
                int dx = x - lastX;
                int dy = y - lastY;
                
                if (isShiftDown) {
                    // Shift key is down - rotate around Z axis (roll)
                    zRotation += dx * 0.5f;
                } else {
                    // Normal rotation (X and Y axes)
                    yRotation += dx * 0.5f;
                    xRotation += dy * 0.5f;
                    
                    // Normalize rotation values
                    yRotation = yRotation % 360;
                    if (yRotation < 0) yRotation += 360;
                    
                    // Limit vertical rotation to prevent flipping
                    if (xRotation > 90) xRotation = 90;
                    if (xRotation < -90) xRotation = -90;
                }
                
                drawingArea.repaint(); // Request a repaint to update the view
            }
            
            lastX = x;
            lastY = y;
        }
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        // Not used
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        // Zoom in/out based on scroll direction
        zoom += e.getWheelRotation() * -0.1f;
        
        // Limit zoom
        if (zoom < 0.2f) zoom = 0.2f;
        if (zoom > 3.0f) zoom = 3.0f;
        
        drawingArea.repaint(); // Request a repaint to update the view
    }
    
    /**
     * Resets the view to default rotation and zoom
     */
    private void resetView() {
        xRotation = 20.0f;
        yRotation = 30.0f;
        zRotation = 0.0f;
        zoom = 1.0f;
        drawingArea.repaint();
    }
    
    /**
     * Starts automatic rotation
     * @param direction 1 for right, -1 for left
     */
    private void startRotation(int direction) {
        // Stop any existing rotation
        stopRotation();
        
        // Create a new timer for rotation
        rotationTimer = new Timer(50, unused -> {
            yRotation += direction * 2.0f;
            yRotation = yRotation % 360;
            if (yRotation < 0) yRotation += 360;
            drawingArea.repaint();
        });
        
        rotationTimer.start();
    }
    
    /**
     * Stops automatic rotation
     */
    private void stopRotation() {
        if (rotationTimer != null && rotationTimer.isRunning()) {
            rotationTimer.stop();
        }
    }
    
    // These methods are no longer needed as we're using the shared model
    // The model will notify this panel when room dimensions or color changes
}
