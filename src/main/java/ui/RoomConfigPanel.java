package ui;

import javax.swing.*;
import java.awt.*;

import model.DesignModel;

public class RoomConfigPanel extends JPanel {
    private DesignModel model;
    private JTextField widthField;
    private JTextField lengthField;
    private JTextField heightField;
    private JComboBox<String> shapeCombo;
    private JComboBox<String> colorCombo;
    private JSlider lightIntensitySlider;
    private JSlider shadowIntensitySlider;
    private JSlider contrastSlider;
    private JButton applyButton;
    private JButton lightColorButton;
    
    public RoomConfigPanel(DesignModel model) {
        this.model = model;
        setBorder(BorderFactory.createTitledBorder("Room Configuration"));
        setLayout(new BorderLayout());
        
        // Initialize with model values
        widthField = new JTextField(String.valueOf(model.getRoomWidth()), 8);
        lengthField = new JTextField(String.valueOf(model.getRoomLength()), 8);
        heightField = new JTextField(String.valueOf(model.getRoomHeight()), 8);
        shapeCombo = new JComboBox<>(new String[]{"Rectangle", "Square", "L-Shape"});
        shapeCombo.setSelectedItem(model.getRoomShape());
        
        colorCombo = new JComboBox<>(new String[]{"White", "Beige", "Grey", "Blue", "Green", "Custom..."});
        
        // Create sliders for lighting settings
        lightIntensitySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(model.getLightIntensity() * 100));
        lightIntensitySlider.setMajorTickSpacing(25);
        lightIntensitySlider.setMinorTickSpacing(5);
        lightIntensitySlider.setPaintTicks(true);
        lightIntensitySlider.setPaintLabels(true);
        
        shadowIntensitySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(model.getShadowIntensity() * 100));
        shadowIntensitySlider.setMajorTickSpacing(25);
        shadowIntensitySlider.setMinorTickSpacing(5);
        shadowIntensitySlider.setPaintTicks(true);
        shadowIntensitySlider.setPaintLabels(true);
        
        contrastSlider = new JSlider(JSlider.HORIZONTAL, 50, 150, (int)(model.getContrast() * 100));
        contrastSlider.setMajorTickSpacing(25);
        contrastSlider.setMinorTickSpacing(5);
        contrastSlider.setPaintTicks(true);
        contrastSlider.setPaintLabels(true);
        
        lightColorButton = new JButton("Light Color");
        lightColorButton.setBackground(model.getAmbientLightColor());
        
        applyButton = new JButton("Apply Changes");
        
        // Create panels for organization
        JPanel basicPanel = new JPanel(new GridLayout(6, 2, 8, 8));
        JPanel lightingPanel = new JPanel(new GridLayout(7, 2, 8, 8));
        
        // Add components to basic panel
        basicPanel.add(new JLabel("Room Width (cm):"));
        basicPanel.add(widthField);
        basicPanel.add(new JLabel("Room Length (cm):"));
        basicPanel.add(lengthField);
        basicPanel.add(new JLabel("Room Height (cm):"));
        basicPanel.add(heightField);
        basicPanel.add(new JLabel("Room Shape:"));
        basicPanel.add(shapeCombo);
        basicPanel.add(new JLabel("Color Scheme:"));
        basicPanel.add(colorCombo);
        basicPanel.add(new JLabel(""));
        basicPanel.add(applyButton);
        
        // Add components to lighting panel
        lightingPanel.setBorder(BorderFactory.createTitledBorder("Lighting & Effects"));
        lightingPanel.add(new JLabel("Light Intensity:"));
        lightingPanel.add(lightIntensitySlider);
        lightingPanel.add(new JLabel("Shadow Intensity:"));
        lightingPanel.add(shadowIntensitySlider);
        lightingPanel.add(new JLabel("Contrast:"));
        lightingPanel.add(contrastSlider);
        lightingPanel.add(new JLabel("Light Color:"));
        lightingPanel.add(lightColorButton);
        
        // Add panels to main panel
        add(basicPanel, BorderLayout.NORTH);
        add(lightingPanel, BorderLayout.CENTER);
        
        // Add action listeners
        applyButton.addActionListener(e -> applyChanges());
        
        // Add change listeners for sliders
        lightIntensitySlider.addChangeListener(e -> {
            if (!lightIntensitySlider.getValueIsAdjusting()) {
                model.setLightIntensity(lightIntensitySlider.getValue() / 100.0f);
            }
        });
        
        shadowIntensitySlider.addChangeListener(e -> {
            if (!shadowIntensitySlider.getValueIsAdjusting()) {
                model.setShadowIntensity(shadowIntensitySlider.getValue() / 100.0f);
            }
        });
        
        contrastSlider.addChangeListener(e -> {
            if (!contrastSlider.getValueIsAdjusting()) {
                model.setContrast(contrastSlider.getValue() / 100.0f);
            }
        });
        
        // Add action listener for light color button
        lightColorButton.addActionListener(e -> {
            Color currentColor = model.getAmbientLightColor();
            Color newColor = JColorChooser.showDialog(this, "Choose Light Color", currentColor);
            if (newColor != null) {
                model.setAmbientLightColor(newColor);
                lightColorButton.setBackground(newColor);
            }
        });
    }
    
    /**
     * Applies the changes to the model
     */
    private void applyChanges() {
        try {
            // Parse values from text fields
            int width = Integer.parseInt(widthField.getText());
            int length = Integer.parseInt(lengthField.getText());
            int height = Integer.parseInt(heightField.getText());
            
            // Update model with new dimensions
            model.setRoomDimensions(width, length, height);
            
            // Update model with new room shape
            String shape = (String) shapeCombo.getSelectedItem();
            model.setRoomShape(shape);
            
            // Update model with new color
            Color color = Color.WHITE; // Default
            String colorName = (String) colorCombo.getSelectedItem();
            if ("Custom...".equals(colorName)) {
                Color customColor = JColorChooser.showDialog(this, "Choose Room Color", model.getRoomColor());
                if (customColor != null) {
                    color = customColor;
                }
            } else {
                switch (colorName) {
                    case "White":
                        color = Color.WHITE;
                        break;
                    case "Beige":
                        color = new Color(245, 245, 220);
                        break;
                    case "Grey":
                        color = Color.LIGHT_GRAY;
                        break;
                    case "Blue":
                        color = new Color(173, 216, 230);
                        break;
                    case "Green":
                        color = new Color(144, 238, 144);
                        break;
                }
            }
            model.setRoomColor(color);
            
            // Show confirmation
            JOptionPane.showMessageDialog(this, "Room configuration updated", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for room dimensions", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
