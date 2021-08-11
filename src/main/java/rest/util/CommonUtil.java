
package rest.util;

import kong.unirest.UnirestInstance;
import rest.controller.builtin.model.Request;
import rest.persitence.model.Proxy;
import rest.persitence.model.RestEntity;

import java.util.List;

public class CommonUtil {

    public static final String TITLE = ">DREST";
    public static final String REST_ENTITY_NAME_PAT = "[a-zA-Z0-9\\-]+";
    public static final String ENTITY_FILE_NAME_SUFFIX = ".rest";
    public static final String ENTITY_TAB_GRAPHIC_ICON = "/icons/collections.png";
    public static final String ITEM_TAB_GRAPHIC_ICON = "/icons/http.png";
    public static final String HISTORY_TAB_GRAPHIC_ICON = "/icons/history.png";
    public static final String DOWNLOAD_TAB_GRAPHIC_ICON = "/icons/download.png";

    public static String getFileName(RestEntity restEntity) {
        return restEntity.getName() + ENTITY_FILE_NAME_SUFFIX;
    }

    public static String noNullTrimStr(String s) {
        return s == null ? "" : s.trim();
    }

    public static void unirestCfg(UnirestInstance instance, Request request) {
        if (request.basicAuth() != null && !"".equals(request.basicAuth().getUsername())) {
            instance.config().setDefaultBasicAuth(request.basicAuth().getUsername(), request.basicAuth().getPassword());
        }
        if (request.proxy() != null && !"".equals(request.proxy().host())) {
            Proxy proxy = request.proxy();
            if (!"".equals(proxy.username())) {
                instance.config().proxy(proxy.host(), proxy.port(), proxy.username(), proxy.password());
            } else {
                instance.config().proxy(proxy.host(), proxy.port());
            }
        }
        instance.config().verifySsl(request.verify());
    }

    public static String uniqueItemName(List<String> names, String name) {
        int cpVal = 0;
        String namePrefix = name;
        if (name.contains("-") && name.substring(name.lastIndexOf("-") + 1).matches("[0-9]+")) {
            cpVal = Integer.parseInt(name.substring(name.lastIndexOf("-") + 1));
            namePrefix = name.substring(0, name.lastIndexOf("-"));
        }
        if (!namePrefix.endsWith("-")) {
            namePrefix += "-";
        }
        while (names.contains(namePrefix + cpVal)) {
            cpVal++;
        }
        return namePrefix + cpVal;
    }

    public static String record(String pref, String entityName, String itemName, String data) {
        data = data.replaceAll("\\r\\n"," \\\\r\\\\n ").replaceAll("\\r", " \\\\r ").replaceAll("\\n", " \\\\n ");
        String show = data.length() > 20 ? data.substring(0, 20) + "..." : data;
        return String.format(pref + " [%s-%s]: " + show, entityName, itemName);
    }

    public static String record(String pref, String name, String data) {
        data = data.replaceAll("\\r\\n"," \\\\r\\\\n ").replaceAll("\\r", " \\\\r ").replaceAll("\\n", " \\\\n ");
        String show = data.length() > 20 ? data.substring(0, 20) + "..." : data;
        return String.format(pref + " [%s]: " + show, name);
    }
}
