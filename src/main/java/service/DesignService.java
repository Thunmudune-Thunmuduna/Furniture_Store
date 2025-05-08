package service;

import model.Design;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DesignService {
    private static final String DESIGNS_DIRECTORY = "designs";
    
    public DesignService() {
        // Create the designs directory if it doesn't exist
        File dir = new File(DESIGNS_DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    public void saveDesign(Design design) throws IOException {
        File file = new File(DESIGNS_DIRECTORY, design.getId() + ".ser");
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(design);
        }
    }
    
    public Design loadDesign(String designId) throws IOException, ClassNotFoundException {
        File file = new File(DESIGNS_DIRECTORY, designId + ".ser");
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Design) ois.readObject();
        }
    }
    
    public void deleteDesign(String designId) {
        File file = new File(DESIGNS_DIRECTORY, designId + ".ser");
        if (file.exists()) {
            file.delete();
        }
    }
    
    public List<Design> getAllDesigns() {
        List<Design> designs = new ArrayList<>();
        File dir = new File(DESIGNS_DIRECTORY);
        
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".ser");
                }
            });
            if (files != null) {
                for (File file : files) {
                    try {
                        designs.add(loadDesign(file.getName().replace(".ser", "")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        return designs;
    }
}
