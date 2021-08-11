

package rest.util;

import ch.qos.logback.core.PropertyDefinerBase;
import rest.persitence.PersistEngine;

public class LoggerCfg  extends PropertyDefinerBase {

    @Override
    public String getPropertyValue() {
        return PersistEngine.LOG_DIR;
    }
}
