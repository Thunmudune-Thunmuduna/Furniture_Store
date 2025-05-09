package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * A modern and visually appealing login frame for the furniture design application
 */
public class LoginFrame extends JFrame {
    // Predefined credentials
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "Test@123";
    
    // UI Components
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginBtn;
    private JLabel statusLabel;
    
    public LoginFrame() {
        // Set up the frame
        setTitle("Furniture Designer Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Create main panel with background color
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 250));
        
        // Create header panel with logo/title
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create form panel
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Add main panel to frame
        setContentPane(mainPanel);
        
        // Set up action listeners
        setupActionListeners();
        
        // Make the frame visible
        setVisible(true);
    }
    
    /**
     * Creates the header panel with logo and title
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(63, 81, 181)); // Material indigo
        headerPanel.setBorder(new EmptyBorder(30, 20, 30, 20));
        
        // App title
        JLabel titleLabel = new JLabel("Furniture Designer");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Subtitle
        JLabel subtitleLabel = new JLabel("Design your space in 2D and 3D");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(220, 220, 255));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Add components to header panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(63, 81, 181)); // Same as header
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        
        return headerPanel;
    }
    
    /**
     * Creates the form panel with login fields
     */
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 250));
        formPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        
        // Username field with icon
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Arial", Font.PLAIN, 20));
        userIcon.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        userField = new JTextField(15);
        userField.setFont(new Font("Arial", Font.PLAIN, 14));
        userField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(63, 81, 181)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        // Password field with icon
        JLabel passIcon = new JLabel("🔒");
        passIcon.setFont(new Font("Arial", Font.PLAIN, 20));
        passIcon.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        passField = new JPasswordField(15);
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        passField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(63, 81, 181)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        // Login button
        loginBtn = new JButton("LOGIN");
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setBackground(new Color(63, 81, 181));
        loginBtn.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        loginBtn.setFocusPainted(false);
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Status label for error messages
        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Add components to form panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        formPanel.add(userIcon, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(userLabel, gbc);
        
        gbc.gridy = 1;
        formPanel.add(userField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(passIcon, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(passLabel, gbc);
        
        gbc.gridy = 3;
        formPanel.add(passField, gbc);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(30, 0, 10, 0);
        formPanel.add(loginBtn, gbc);
        
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 0, 10, 0);
        formPanel.add(statusLabel, gbc);
        
        // Add hint text
        JLabel hintLabel = new JLabel("Hint: Use admin/Test@123");
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        hintLabel.setForeground(Color.GRAY);
        hintLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridy = 6;
        formPanel.add(hintLabel, gbc);
        
        return formPanel;
    }
    
    /**
     * Sets up action listeners for interactive components
     */
    private void setupActionListeners() {
        // Button hover effect
        loginBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginBtn.setBackground(new Color(83, 101, 201)); // Lighter shade
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginBtn.setBackground(new Color(63, 81, 181)); // Original color
            }
        });
        
        // Login button action
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());
                
                if (validateCredentials(username, password)) {
                    statusLabel.setText("Login successful!");
                    statusLabel.setForeground(new Color(76, 175, 80)); // Green
                    
                    // Delay before opening dashboard
                    Timer timer = new Timer(800, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            dispose();
                            new DesignerDashboard();
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                } else {
                    statusLabel.setText("Invalid username or password");
                    statusLabel.setForeground(Color.RED);
                    passField.setText("");
                    passField.requestFocus();
                }
            }
        });
        
        // Field focus effects
        FocusAdapter focusAdapter = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                JTextField field = (JTextField) e.getComponent();
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(103, 58, 183)), // Deeper purple
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                JTextField field = (JTextField) e.getComponent();
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(63, 81, 181)), // Original color
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
                ));
            }
        };
        
        userField.addFocusListener(focusAdapter);
        passField.addFocusListener(focusAdapter);
        
        // Enter key to submit
        getRootPane().setDefaultButton(loginBtn);
    }
    
    /**
     * Validates the entered credentials
     */
    private boolean validateCredentials(String username, String password) {
        return VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password);
    }
}
