

package rest.controller.builtin.process;

import kong.unirest.Config;
import kong.unirest.Headers;
import kong.unirest.HttpRequest;
import kong.unirest.HttpRequestSummary;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.HttpResponse;
import kong.unirest.Interceptor;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import rest.controller.builtin.model.Request;
import rest.controller.builtin.model.Response;
import rest.util.CommonUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.Holder;

/**
 * resolve request and send http req,return response.
 *
 */
public class Requester implements Processer<Request, Response> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Requester.class);

    public Requester() {
    }

    @Override
    public Response process(Request reqData) {
        LOGGER.info("begin to proc request:{}", reqData);
        Response data = new Response();
        UnirestInstance instance = Unirest.spawnInstance();
        Holder<Long> start = new Holder<>((long) 0);
        Holder<Long> end = new Holder<>((long) 0);
        instance.config().interceptor(new Interceptor() {
            @Override
            public void onRequest(HttpRequest<?> request, Config config) {
                start.value = System.currentTimeMillis();
            }

            @Override
            public void onResponse(HttpResponse<?> response, HttpRequestSummary request, Config config) {
                end.value = System.currentTimeMillis();
            }
        });
        try {
            CommonUtil.unirestCfg(instance, reqData);
            String method = reqData.method();
            HttpResponse<String> response;
            HttpRequestWithBody tmp =
                instance.request(reqData.method().toLowerCase(), reqData.url()).headers(reqData.header());
            switch (method.toUpperCase()) {
                case "DELETE":
                case "GET":
                    response = tmp.queryString(mapStr2MapObj(reqData.form())).asString();
                    break;
                case "PUT":
                case "POST":
                case "PATCH":
                    if (reqData.form().isEmpty()) {
                        response = tmp.body(reqData.body()).asString();
                    } else {
                        response = tmp.fields(mapStr2MapObj(reqData.form())).asString();
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + method);
            }
            data.code(String.valueOf(response.getStatus()))
                .body(response.getBody())
                .time(end.value - start.value)
                .header(listMap2String(response.getHeaders()));
        } catch (Throwable e) {
            LOGGER.error("request catch exception:{}", e.getMessage());
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            data.body(stringWriter.toString());
        } finally {
            instance.shutDown();
        }
        LOGGER.info("request end:{}", data);
        return data;
    }

    private Map<String, Object> mapStr2MapObj(Map<String, String> map) {
        Map<String, Object> ret = new HashMap<>();
        map.forEach(ret::put);
        return ret;
    }

    private Map<String, String> listMap2String(Headers headers) {
        Map<String, String> ret = new HashMap<>();
        headers.all().forEach(head -> {
            ret.put(head.getName(), head.getValue());
        });
        return ret;
    }
}
