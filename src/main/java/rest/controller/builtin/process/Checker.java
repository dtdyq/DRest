

package rest.controller.builtin.process;

import rest.controller.builtin.model.Request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**request process stream:
 *  checker --> filter --> requester --> render
 * request input check,url must not null;header and form cannot contains '---'
 */
public class Checker implements Processer<Request, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Checker.class);
    public String process(Request item) {
        LOGGER.info("start checker process:{}",item);
        if (item == null) {
            return "request is null,please check!";
        }
        if (invalidStr(item.url())) {
            return String.format("req url %s is invalid,please check!", item.url());
        }
        if (invalidStr(item.method())) {
            return String.format("req method %s is invalid,please check!", item.method());
        }
        Map<String, String> headers = item.header();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            if (invalidStr(k) || invalidStr(v) || "---".equals(k) || "---".equals(v)) {
                return String.format("invalid header %s:%s,please check!", k, v);
            }
        }
        Map<String, String> form = item.form();
        for (Map.Entry<String, String> entry : form.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            if (invalidStr(k) || invalidStr(v) || "---".equals(k) || "---".equals(v)) {
                return String.format("invalid form %s:%s,please check!", k, v);
            }
        }
        return null;
    }

    private boolean invalidStr(String s) {
        return s == null || "".equals(s);
    }
}
