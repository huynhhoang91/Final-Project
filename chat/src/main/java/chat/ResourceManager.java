package chat;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ResourceManager {
    private static ResourceManager resourceManager;
    private static final String PATH = "src/main/resources/config.properties";
    private static FileInputStream fileInputStream;
    private static Properties properties = new Properties();

    public ResourceManager() {
        try {
            fileInputStream = new FileInputStream(new File(PATH));
            properties.load(fileInputStream);
        } catch (Exception e) {
            ConsoleHelper.writeMessage("Error");
        }
    }

    public int getPort() {
        return Integer.parseInt(properties.getProperty("port"));
    }

    public static ResourceManager getInstance() {
        if (resourceManager == null) {
            resourceManager = new ResourceManager();
        }
        return resourceManager;
    }
}
