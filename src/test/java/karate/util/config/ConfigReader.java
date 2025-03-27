package karate.util.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigReader {
    public static void readConfig() {
        Properties properties = new Properties();
        try {
            String path = System.getProperty("user.dir") + "/src/test/java/config.properties";
            properties.load(new FileInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Settings.keyVaultName = properties.getProperty("keyVaultName");
        Settings.clientId = properties.getProperty("clientId");
        Settings.clientScret = properties.getProperty("clientSecret");
        Settings.tenantId = properties.getProperty("tenantId");
        Settings.components = properties.getProperty("components");
        Settings.plataforma = properties.getProperty("plataforma");
        Settings.quarter = properties.getProperty("quarter");
        Settings.sprint = properties.getProperty("sprint");
        Settings.squad = properties.getProperty("squad");
        Settings.jiraUserId = properties.getProperty("jiraUserId");
        Settings.userStory = properties.getProperty("userStory");
        Settings.cycleFolderId = properties.getProperty("cycleFolderId");
        Settings.testCaseFolderId = properties.getProperty("testCaseFolderId");
        Settings.environment = properties.getProperty("environment");
        Settings.jiraUserMail = properties.getProperty("jiraUserMail");
        Settings.jiraAPIToken = properties.getProperty("jiraAPIToken");
        Settings.jiraProjectId = properties.getProperty("jiraProjectId");
   }
}
