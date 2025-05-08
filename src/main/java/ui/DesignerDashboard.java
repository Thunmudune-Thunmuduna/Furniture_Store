package ui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import model.Design;
import model.DesignModel;
import model.FurnitureItemModel;
import service.DesignService;

public class DesignerDashboard extends JFrame {
    private DesignModel designModel;
    private Design2DPanel design2DPanel;
    private Design3DPanel design3DPanel;
    private RoomConfigPanel roomConfigPanel;
    private DesignService designService;
    
    public DesignerDashboard() {
        setTitle("Furniture Designer Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create the design service
        designService = new DesignService();
        
        // Create the shared design model
        designModel = new DesignModel();
        
        // Create panels with the shared model
        design2DPanel = new Design2DPanel(designModel);
        design3DPanel = new Design3DPanel(designModel);
        roomConfigPanel = new RoomConfigPanel(designModel);

        // Toolbar for actions
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton scaleButton = new JButton("Scale");
        JButton shadeButton = new JButton("Shade");
        JButton colorButton = new JButton("Color");
        JButton saveButton = new JButton("Save");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        
        // Add action listeners
        scaleButton.addActionListener(e -> scaleSelectedItem());
        shadeButton.addActionListener(e -> adjustShading());
        colorButton.addActionListener(e -> changeItemColor());
        saveButton.addActionListener(e -> saveDesign());
        editButton.addActionListener(e -> editSelectedItem());
        deleteButton.addActionListener(e -> design2DPanel.deleteSelectedItem());
        
        // Add buttons to toolbar
        toolBar.add(scaleButton);
        toolBar.add(shadeButton);
        toolBar.add(colorButton);
        toolBar.add(saveButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        add(toolBar, BorderLayout.NORTH);

        // Room configuration panel
        add(roomConfigPanel, BorderLayout.WEST);

        // Tabbed pane for 2D/3D
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("2D Design", design2DPanel);
        tabbedPane.addTab("3D Design", design3DPanel);
        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }
    
    /**
     * Opens a dialog to scale the selected item
     */
    private void scaleSelectedItem() {
        if (design2DPanel.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select an item to scale", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Get the current dimensions
        FurnitureItemModel item = design2DPanel.getSelectedItemModel();
        if (item == null) return;
        
        // Show dialog to get scale factor
        String input = JOptionPane.showInputDialog(this, 
                "Enter scale factor (0.5 to 2.0):", 
                "1.0");
        
        if (input != null && !input.isEmpty()) {
            try {
                float scaleFactor = Float.parseFloat(input);
                
                // Limit scale factor to reasonable range
                if (scaleFactor < 0.5f) scaleFactor = 0.5f;
                if (scaleFactor > 2.0f) scaleFactor = 2.0f;
                
                // Apply scaling
                int newWidth = (int)(item.getWidth() * scaleFactor);
                int newHeight = (int)(item.getHeight() * scaleFactor);
                int newDepth = (int)(item.getDepth() * scaleFactor);
                
                // Update the model
                item.setWidth(newWidth);
                item.setHeight(newHeight);
                item.setDepth(newDepth);
                designModel.updateFurnitureItem(item);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Opens a dialog to adjust shading settings
     */
    private void adjustShading() {
        // Create a panel for the shading controls
        JPanel shadingPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        
        // Get current values from the model
        float lightIntensity = designModel.getLightIntensity();
        float shadowIntensity = designModel.getShadowIntensity();
        float contrast = designModel.getContrast();
        
        // Create sliders
        JSlider lightSlider = new JSlider(0, 100, (int)(lightIntensity * 100));
        lightSlider.setMajorTickSpacing(25);
        lightSlider.setPaintTicks(true);
        lightSlider.setPaintLabels(true);
        
        JSlider shadowSlider = new JSlider(0, 100, (int)(shadowIntensity * 100));
        shadowSlider.setMajorTickSpacing(25);
        shadowSlider.setPaintTicks(true);
        shadowSlider.setPaintLabels(true);
        
        JSlider contrastSlider = new JSlider(0, 200, (int)(contrast * 100));
        contrastSlider.setMajorTickSpacing(50);
        contrastSlider.setPaintTicks(true);
        contrastSlider.setPaintLabels(true);
        
        // Add components to panel
        shadingPanel.add(new JLabel("Light Intensity:"));
        shadingPanel.add(lightSlider);
        shadingPanel.add(new JLabel("Shadow Intensity:"));
        shadingPanel.add(shadowSlider);
        shadingPanel.add(new JLabel("Contrast:"));
        shadingPanel.add(contrastSlider);
        
        // Show dialog
        int result = JOptionPane.showConfirmDialog(this, shadingPanel, 
                "Adjust Shading", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            // Update model with new values
            designModel.setLightIntensity(lightSlider.getValue() / 100.0f);
            designModel.setShadowIntensity(shadowSlider.getValue() / 100.0f);
            designModel.setContrast(contrastSlider.getValue() / 100.0f);
            // Notify listeners that lighting has changed
            design3DPanel.onModelChanged("LIGHTING_CHANGED");
        }
    }
    
    /**
     * Opens a color chooser to change the selected item's color
     */
    private void changeItemColor() {
        if (design2DPanel.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select an item to color", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        FurnitureItemModel item = design2DPanel.getSelectedItemModel();
        if (item == null) return;
        
        // Show color chooser dialog
        Color newColor = JColorChooser.showDialog(this, "Choose Color", item.getColor());
        
        if (newColor != null) {
            // Update the model
            item.setColor(newColor);
            designModel.updateFurnitureItem(item);
        }
    }
    
    /**
     * Saves the current design
     */
    private void saveDesign() {
        String name = JOptionPane.showInputDialog(this, "Enter a name for this design:");
        
        if (name != null && !name.isEmpty()) {
            try {
                // Convert DesignModel to Design for saving
                Design design = convertModelToDesign(name);
                
                // Save the design using the design service
                designService.saveDesign(design);
                
                JOptionPane.showMessageDialog(this, 
                        "Design saved as '" + name + "' in the designs folder", 
                        "Save Complete", 
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                        "Error saving design: " + e.getMessage(), 
                        "Save Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Converts the current DesignModel to a Design object for saving
     * @param name Name to give the design
     * @return Design object ready to be saved
     */
    private Design convertModelToDesign(String name) {
        // Create a new design with the specified name
        Design design = new Design(name, "current_user");
        
        // Copy room properties
        design.setRoomWidth(designModel.getRoomWidth());
        design.setRoomLength(designModel.getRoomLength());
        design.setRoomShape(designModel.getRoomShape());
        design.setRoomColor(designModel.getRoomColor());
        
        // Convert furniture items from FurnitureItemModel to Design.FurnitureItem
        for (FurnitureItemModel modelItem : designModel.getFurnitureItems()) {
            // Create a new furniture item for the design
            Design.FurnitureItem item = new Design.FurnitureItem(
                modelItem.getX(),
                modelItem.getY(),
                modelItem.getWidth(),
                modelItem.getHeight(),
                modelItem.getColor(),
                modelItem.getName()
            );
            design.addFurnitureItem(item);
        }
        
        return design;
    }
    
    /**
     * Opens a dialog to edit properties of the selected item
     */
    private void editSelectedItem() {
        if (design2DPanel.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select an item to edit", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        FurnitureItemModel item = design2DPanel.getSelectedItemModel();
        if (item == null) return;
        
        // Create a panel for the properties
        JPanel propertiesPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        
        // Create text fields for properties
        JTextField nameField = new JTextField(item.getName());
        JTextField widthField = new JTextField(String.valueOf(item.getWidth()));
        JTextField heightField = new JTextField(String.valueOf(item.getHeight()));
        JTextField depthField = new JTextField(String.valueOf(item.getDepth()));
        
        // Add components to panel
        propertiesPanel.add(new JLabel("Name:"));
        propertiesPanel.add(nameField);
        propertiesPanel.add(new JLabel("Width:"));
        propertiesPanel.add(widthField);
        propertiesPanel.add(new JLabel("Height:"));
        propertiesPanel.add(heightField);
        propertiesPanel.add(new JLabel("Depth:"));
        propertiesPanel.add(depthField);
        
        // Show dialog
        int result = JOptionPane.showConfirmDialog(this, propertiesPanel, 
                "Edit Item Properties", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                // Update the model
                item.setName(nameField.getText());
                item.setWidth(Integer.parseInt(widthField.getText()));
                item.setHeight(Integer.parseInt(heightField.getText()));
                item.setDepth(Integer.parseInt(depthField.getText()));
                designModel.updateFurnitureItem(item);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers for dimensions", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
