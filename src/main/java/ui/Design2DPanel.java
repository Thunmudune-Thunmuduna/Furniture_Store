package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import model.DesignModel;
import model.FurnitureItemModel;

public class Design2DPanel extends JPanel implements DesignModel.DesignModelListener {
    private DesignModel model;
    private List<FurnitureItem> furnitureItems;
    private FurnitureItem selectedItem;
    private Point dragStart;
    private JPanel toolPanel;

    public Design2DPanel(DesignModel model) {
        this.model = model;
        model.addListener(this);
        setLayout(new BorderLayout());
        furnitureItems = new ArrayList<>();
        
        // Main drawing area
        JPanel drawingArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                        // Draw room outline
                g2d.setColor(model.getRoomColor());
                g2d.fillRect(50, 50, model.getRoomWidth(), model.getRoomLength());
                g2d.setColor(Color.BLACK);
                g2d.drawRect(50, 50, model.getRoomWidth(), model.getRoomLength());
                
                // Draw furniture items
                for (FurnitureItem item : furnitureItems) {
                    item.draw(g2d);
                }
            }
        };
        drawingArea.setBackground(new Color(240, 240, 240));
        drawingArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                selectedItem = null;
                dragStart = e.getPoint();
                
                // Check if an item was clicked
                for (int i = furnitureItems.size() - 1; i >= 0; i--) {
                    if (furnitureItems.get(i).contains(e.getPoint())) {
                        selectedItem = furnitureItems.get(i);
                        break;
                    }
                }
                repaint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                dragStart = null;
                repaint();
            }
        });
        
        drawingArea.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedItem != null && dragStart != null) {
                    int dx = e.getX() - dragStart.x;
                    int dy = e.getY() - dragStart.y;
                    selectedItem.move(dx, dy);
                    
                    // Update the model with the new position
                    for (FurnitureItemModel modelItem : model.getFurnitureItems()) {
                        if (modelItem.getName().equals(selectedItem.name) && 
                            modelItem.getX() + 250 == selectedItem.x - dx && 
                            modelItem.getZ() + 200 == selectedItem.y - dy) {
                            modelItem.setX(selectedItem.x - 250);
                            modelItem.setZ(selectedItem.y - 200);
                            // This will notify listeners with "ITEM_UPDATED"
                            model.updateFurnitureItem(modelItem);
                            break;
                        }
                    }
                    
                    dragStart = e.getPoint();
                    repaint();
                }
            }
        });
        
        // Furniture palette
        toolPanel = new JPanel();
        toolPanel.setLayout(new GridLayout(0, 1, 5, 5));
        toolPanel.setBorder(BorderFactory.createTitledBorder("Furniture"));
        
        // Add furniture types
        addFurnitureButton("Dining Table", new Color(139, 69, 19));
        addFurnitureButton("Chair", new Color(160, 82, 45));
        addFurnitureButton("Sofa", new Color(210, 180, 140));
        addFurnitureButton("Coffee Table", new Color(101, 67, 33));
        addFurnitureButton("Bed", new Color(205, 133, 63));
        addFurnitureButton("Wardrobe", new Color(222, 184, 135));
        
        add(drawingArea, BorderLayout.CENTER);
        add(toolPanel, BorderLayout.EAST);
    }
    
    private void addFurnitureButton(String name, Color color) {
        JButton button = new JButton(name);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createFurnitureItem(name, color);
            }
        });
        toolPanel.add(button);
    }
    
    private void createFurnitureItem(String name, Color color) {
        FurnitureItem item = null;
        FurnitureItemModel modelItem = null;
        int x = 100;
        int y = 100;
        int width = 0;
        int height = 0;
        
        switch (name) {
            case "Dining Table":
                width = 120;
                height = 80;
                break;
            case "Chair":
                width = 40;
                height = 40;
                break;
            case "Sofa":
                width = 150;
                height = 60;
                break;
            case "Coffee Table":
                width = 80;
                height = 60;
                break;
            case "Bed":
                width = 160;
                height = 200;
                break;
            case "Wardrobe":
                width = 100;
                height = 50;
                break;
        }
        
        if (width > 0 && height > 0) {
            // Create the 2D item
            item = new FurnitureItem(x, y, width, height, color, name);
            furnitureItems.add(item);
            
            // Create and add the model item
            modelItem = FurnitureItemModel.from2D(name, x, y, width, height, color);
            model.addFurnitureItem(modelItem);
            
            repaint();
        }
    }
    
    /**
     * Deletes the currently selected item
     */
    public void deleteSelectedItem() {
        if (selectedItem != null) {
            // Find the corresponding model item
            for (FurnitureItemModel modelItem : model.getFurnitureItems()) {
                if (modelItem.getName().equals(selectedItem.name) && 
                    modelItem.getX() + 250 == selectedItem.x && 
                    modelItem.getZ() + 200 == selectedItem.y) {
                    model.removeFurnitureItem(modelItem);
                    break;
                }
            }
            
            // Remove from local list
            furnitureItems.remove(selectedItem);
            selectedItem = null;
            repaint();
        }
    }
    
    /**
     * Gets the currently selected furniture item
     * @return The selected FurnitureItem or null if none is selected
     */
    public FurnitureItem getSelectedItem() {
        return selectedItem;
    }
    
    /**
     * Gets the model representation of the currently selected item
     * @return The FurnitureItemModel for the selected item or null if none is selected
     */
    public FurnitureItemModel getSelectedItemModel() {
        if (selectedItem == null) return null;
        
        // Find the corresponding model item
        for (FurnitureItemModel modelItem : model.getFurnitureItems()) {
            if (modelItem.getName().equals(selectedItem.name) && 
                modelItem.getX() + 250 == selectedItem.x && 
                modelItem.getZ() + 200 == selectedItem.y) {
                return modelItem;
            }
        }
        
        return null;
    }
    
    @Override
    public void onModelChanged(String changeType) {
        // Refresh the panel when the model changes
        if (changeType.equals("ROOM_COLOR_CHANGED") || 
            changeType.equals("ROOM_DIMENSIONS_CHANGED")) {
            repaint();
        } else if (changeType.equals("ITEM_REMOVED")) {
            // Only sync when items are removed
            syncWithModel();
        } else if (changeType.equals("ITEM_UPDATED")) {
            // Just repaint for updates
            repaint();
        }
        // We don't sync on ITEM_ADDED because we're the ones adding the item
    }
    
    /**
     * Synchronizes the local furniture items with the model
     */
    private void syncWithModel() {
        // Clear the local list
        furnitureItems.clear();
        
        // Add items from the model
        for (FurnitureItemModel modelItem : model.getFurnitureItems()) {
            FurnitureItem item = new FurnitureItem(
                modelItem.getX() + 250, // Adjust for the offset we applied in from2D
                modelItem.getZ() + 200, // Adjust for the offset we applied in from2D
                modelItem.getWidth(), 
                modelItem.getHeight(), 
                modelItem.getColor(), 
                modelItem.getName());
            furnitureItems.add(item);
        }
        
        repaint();
    }
    
    // Inner class to represent furniture items
    class FurnitureItem {
        private int x, y, width, height;
        private Color color;
        private String name;
        
        public FurnitureItem(int x, int y, int width, int height, Color color, String name) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.color = color;
            this.name = name;
        }
        
        public void draw(Graphics2D g) {
            g.setColor(color);
            g.fillRect(x, y, width, height);
            
            // Draw selection border
            if (this == selectedItem) {
                g.setColor(Color.RED);
                g.setStroke(new BasicStroke(2));
                g.drawRect(x, y, width, height);
                g.setStroke(new BasicStroke(1));
            } else {
                g.setColor(Color.BLACK);
                g.drawRect(x, y, width, height);
            }
            
            // Draw label
            g.setColor(Color.WHITE);
            FontMetrics fm = g.getFontMetrics();
            int textWidth = fm.stringWidth(name);
            if (textWidth < width - 4) {
                g.drawString(name, x + (width - textWidth) / 2, y + height / 2);
            }
        }
        
        public boolean contains(Point p) {
            return p.x >= x && p.x <= x + width && p.y >= y && p.y <= y + height;
        }
        
        public void move(int dx, int dy) {
            x += dx;
            y += dy;
        }
    }
}
