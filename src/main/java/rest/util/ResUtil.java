

package rest.util;

import java.io.InputStream;

/**
 * load resource like ico .etc
 */
public class ResUtil {

    public static String resPath(String name) {
        return ResUtil.class.getResource(name).toExternalForm();
    }

    public static InputStream resInputStream(String name) {
        return ResUtil.class.getResourceAsStream(name);
    }

}
