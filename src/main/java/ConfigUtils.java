import java.io.InputStream;
import java.util.Properties;

public class ConfigUtils {
    /**
     * 读取系统设置文件。
     *
     * @param propsFilename 设置文件名称
     * @return 一个 Property 对象
     */
    public static Properties loadProperties(String propsFilename) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();

        try {
            InputStream propsStream = loader.getResourceAsStream(propsFilename);
            props.load(propsStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return props;
    }
}
