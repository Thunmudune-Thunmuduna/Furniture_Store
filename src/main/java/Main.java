import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import ui.LoginFrame;
import model.Design;
import service.DesignService;

/**
 * Main class for the Furniture Design Application
 * This application allows furniture designers to create and visualize
 * room layouts in both 2D and 3D views.
 * 
 * Inspired by 3D bedroom projects that use OpenGL for rendering,
 * this application uses Java2D for a simplified 3D-like rendering approach.
 */
public class Main {
    // Application constants
    public static final String APP_NAME = "Furniture Designer Pro";
    public static final String APP_VERSION = "1.0.1";
    public static final String APP_AUTHOR = "Design Studio Team";
    
    // Default room dimensions
    public static final int DEFAULT_ROOM_WIDTH = 500;
    public static final int DEFAULT_ROOM_LENGTH = 400;
    public static final int DEFAULT_ROOM_HEIGHT = 250;
    
    // Default furniture colors
    public static final Color[] FURNITURE_COLORS = {
        new Color(139, 69, 19),   // Brown
        new Color(160, 82, 45),    // Sienna
        new Color(210, 180, 140),  // Tan
        new Color(120, 80, 40),    // Dark Brown
        new Color(70, 130, 180),   // Steel Blue
        new Color(255, 200, 100)   // Gold
    };
    
    // Logger for application-wide logging
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    /**
     * Application entry point
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        // Configure logging
        configureLogging();
        
        LOGGER.info("Starting " + APP_NAME + " version " + APP_VERSION);
        
        // Set the look and feel to the system default
        try {
            // Try to use Nimbus look and feel if available
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    LOGGER.info("Using Nimbus look and feel");
                    break;
                }
            }
            // Fall back to system look and feel if Nimbus is not available
            if (!UIManager.getLookAndFeel().getName().equals("Nimbus")) {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                LOGGER.info("Using system look and feel");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not set look and feel: " + e.getMessage(), e);
        }
        
        // Set some better default fonts
        setUIFont(new javax.swing.plaf.FontUIResource("SansSerif", Font.PLAIN, 12));
        
        // Ensure application directories exist
        ensureApplicationDirectories();
        
        // Start the application on the Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            // Show splash screen with progress
            showEnhancedSplashScreen();
        });
    }
    
    /**
     * Configure application logging
     */
    private static void configureLogging() {
        // Set default logging level
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.INFO);
    }
    
    /**
     * Ensure that all required application directories exist
     */
    private static void ensureApplicationDirectories() {
        // Ensure designs directory exists
        File designsDir = new File("designs");
        if (!designsDir.exists()) {
            boolean created = designsDir.mkdirs();
            if (created) {
                LOGGER.info("Created designs directory");
            } else {
                LOGGER.warning("Failed to create designs directory");
            }
        }
        
        // Ensure exports directory exists
        File exportsDir = new File("exports");
        if (!exportsDir.exists()) {
            boolean created = exportsDir.mkdirs();
            if (created) {
                LOGGER.info("Created exports directory");
            } else {
                LOGGER.warning("Failed to create exports directory");
            }
        }
    }
    
    /**
     * Sets the default font for all Swing components
     * @param font the font to use
     */
    private static void setUIFont(javax.swing.plaf.FontUIResource font) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, font);
            }
        }
    }
    
    /**
     * Shows an enhanced splash screen with progress bar before launching the login screen
     */
    private static void showEnhancedSplashScreen() {
        JWindow splashScreen = new JWindow();
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(240, 240, 240));
        content.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));
        
        // Add a logo/image panel at the top
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw a simple furniture icon
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                
                // Draw a table
                g2d.setColor(new Color(139, 69, 19));
                g2d.fillRect(centerX - 40, centerY - 10, 80, 10);
                
                // Draw table legs
                g2d.fillRect(centerX - 35, centerY, 10, 30);
                g2d.fillRect(centerX + 25, centerY, 10, 30);
                
                // Draw a chair
                g2d.setColor(new Color(160, 82, 45));
                g2d.fillRect(centerX - 60, centerY, 15, 15);
                g2d.fillRect(centerX - 60, centerY + 15, 5, 15);
                g2d.fillRect(centerX - 50, centerY + 15, 5, 15);
            }
        };
        logoPanel.setPreferredSize(new Dimension(400, 100));
        content.add(logoPanel, BorderLayout.NORTH);
        
        // Add a center panel with title and subtitle
        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        centerPanel.setOpaque(false);
        
        // Add a title label
        JLabel titleLabel = new JLabel(APP_NAME, JLabel.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(new Color(50, 50, 50));
        centerPanel.add(titleLabel);
        
        // Add a subtitle
        JLabel subtitleLabel = new JLabel("3D Furniture Design Tool", JLabel.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        centerPanel.add(subtitleLabel);
        
        content.add(centerPanel, BorderLayout.CENTER);
        
        // Add a bottom panel with progress bar and version info
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        
        // Add a progress bar
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("Loading...");
        bottomPanel.add(progressBar, BorderLayout.CENTER);
        
        // Add version and author info
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setOpaque(false);
        JLabel versionLabel = new JLabel("Version " + APP_VERSION, JLabel.CENTER);
        JLabel authorLabel = new JLabel(APP_AUTHOR, JLabel.CENTER);
        infoPanel.add(versionLabel);
        infoPanel.add(authorLabel);
        bottomPanel.add(infoPanel, BorderLayout.SOUTH);
        
        content.add(bottomPanel, BorderLayout.SOUTH);
        
        splashScreen.setContentPane(content);
        splashScreen.setSize(450, 300);
        splashScreen.setLocationRelativeTo(null);
        splashScreen.setVisible(true);
        
        // Simulate loading with progress updates
        Timer progressTimer = new Timer(50, new ActionListener() {
            private int progress = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                progress += 2;
                progressBar.setValue(progress);
                
                // Update loading message
                if (progress < 30) {
                    progressBar.setString("Initializing application...");
                } else if (progress < 60) {
                    progressBar.setString("Loading resources...");
                } else if (progress < 90) {
                    progressBar.setString("Preparing user interface...");
                } else {
                    progressBar.setString("Starting application...");
                }
                
                if (progress >= 100) {
                    ((Timer)e.getSource()).stop();
                    splashScreen.dispose();
                    new LoginFrame();
                }
            }
        });
        progressTimer.start();
    }
    
    /**
     * Creates a sample design for demonstration purposes
     * @return a sample design
     */
    public static Design createSampleDesign() {
        // Create a new design with name and designer ID
        Design design = new Design("Sample Room Design", "demo-user");
        
        // Set room properties
        design.setRoomWidth(DEFAULT_ROOM_WIDTH);
        design.setRoomLength(DEFAULT_ROOM_LENGTH);
        design.setRoomShape("Rectangle");
        design.setRoomColor(new Color(240, 240, 240));
        
        // Furniture items will be added in the Design2DPanel and Design3DPanel
        
        return design;
    }
}
