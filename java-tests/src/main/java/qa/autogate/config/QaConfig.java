package qa.autogate.config;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

public final class QaConfig {
    private static final Properties props = new Properties();

    static {
        try (InputStream is = QaConfig.class.getClassLoader().getResourceAsStream("qa.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (Exception e) {
            System.err.println("Failed to load qa.properties: " + e.getMessage());
        }
    }

    private QaConfig() {}

    public static String getBaseUrl() {
        return getProperty("baseUrl", "AUTOGATE_BASE_URL", "http://localhost:3000");
    }

    public static String getDbPath() {
        String configured = getProperty("dbPath", "AUTOGATE_DB_PATH", "demo-app/data/autogate.db");
        File file = new File(configured);
        if (file.isAbsolute() && file.exists()) {
            return file.getAbsolutePath();
        }
        // If running from java-tests directory, check parent directory as well
        if (!file.exists()) {
            File parentResolved = Paths.get("..", configured).toFile();
            if (parentResolved.exists()) {
                return parentResolved.getAbsolutePath();
            }
        }
        return file.getAbsolutePath();
    }

    public static String getJdbcUrl() {
        return "jdbc:sqlite:" + getDbPath();
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "AUTOGATE_HEADLESS", "true"));
    }

    public static int getTimeoutSeconds() {
        return Integer.parseInt(getProperty("timeoutSeconds", "AUTOGATE_TIMEOUT_SECONDS", "10"));
    }

    private static String getProperty(String propKey, String envKey, String defaultValue) {
        String sysProp = System.getProperty(propKey);
        if (sysProp != null && !sysProp.isBlank()) {
            return sysProp.trim();
        }
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.isBlank()) {
            return envVal.trim();
        }
        String fileVal = props.getProperty(propKey);
        if (fileVal != null && !fileVal.isBlank()) {
            return fileVal.trim();
        }
        return defaultValue;
    }
}
