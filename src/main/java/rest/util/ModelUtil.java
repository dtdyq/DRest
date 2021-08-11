
package rest.util;

import rest.controller.builtin.model.Request;
import rest.controller.builtin.model.Response;
import rest.controller.plugin.eximport.model.Entity;
import rest.persitence.PersistEngine;
import rest.persitence.model.RestEntity;
import rest.persitence.model.RestItem;
import rest.view.model.EntityBinding;
import rest.view.model.KeyValue;
import rest.view.model.RequestBinding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * model transfer for persist、controller and view
 */
public final class ModelUtil {

    public static List<KeyValue> map2KeyVal(Map<String, String> map) {
        return map.entrySet().stream().map(e -> new KeyValue(e.getKey(), e.getValue())).collect(Collectors.toList());
    }

    public static Map<String, String> keyVal2Map(List<KeyValue> keyValues) {
        HashMap<String, String> map = new HashMap<>();
        keyValues.forEach(kv -> map.put(kv.getKey(), kv.getValue()));
        return map;
    }

    public static RestItem requestBinding2RestItem(RequestBinding binding) {
        return new RestItem().method(binding.getMethod())
            .header(binding.getHeader())
            .form(binding.getForm())
            .url(binding.getUrl())
            .body(binding.getBody())
            .code(binding.getCode())
            .time(binding.getTime())
            .respHeader(binding.getRespHeader())
            .response(binding.getResponse())
            .basicAuth(binding.getBasicAuth())
            .proxy(binding.getProxy())
            .name(binding.getName())
            .verify(binding.isVerify())
            .opened(binding.isOpen())
            .current(binding.isCurrent());
    }

    public static RequestBinding RestItem2RequestBinding(RestItem restItem) {
        RequestBinding binding = new RequestBinding();
        refreshBindingWithRestItem(binding, restItem);
        return binding;
    }

    public static void refreshBindingWithRestItem(RequestBinding binding, RestItem restItem) {
        binding.setMethod(restItem.method());
        binding.setHeader(restItem.header());
        binding.setForm(restItem.form());
        binding.setUrl(restItem.url());
        binding.setBody(restItem.body());
        binding.setTime(restItem.time());
        binding.setCode(restItem.code());
        binding.setRespHeader(restItem.respHeader());
        binding.setResponse(restItem.response());
        binding.setBasicAuth(restItem.basicAuth());
        binding.setVerify(restItem.verify());
        binding.setProxy(restItem.proxy());
        binding.setName(restItem.name());
        binding.setCurrent(restItem.current());
        binding.setOpen(restItem.opened());
    }

    public static void refreshRequestBindingWithResp(RequestBinding binding, Response response) {
        binding.setCode(response.code());
        binding.setRespHeader(map2KeyVal(response.header()));
        binding.setTime(String.valueOf(response.time()));
        binding.setResponse(response.body());
    }

    public static Request requestBinding2Request(RequestBinding binding) {
        return new Request().header(keyVal2Map(binding.getHeader()))
            .form(keyVal2Map(binding.getForm()))
            .body(binding.getBody())
            .url(binding.getUrl())
            .method(binding.getMethod())
            .basicAuth(binding.getBasicAuth())
            .proxy(binding.getProxy())
            .verify(binding.isVerify());
    }

    public static Request requestBinding2RequestAndFlipVariable(EntityBinding entityBinding, RequestBinding binding) {
        Request request = new Request().header(keyVal2Map(binding.getHeader()))
            .form(keyVal2Map(binding.getForm()))
            .body(binding.getBody())
            .url(binding.getUrl())
            .method(binding.getMethod())
            .basicAuth(binding.getBasicAuth())
            .proxy(binding.getProxy())
            .verify(binding.isVerify());
        request.url(renderVar(request.url(), entityBinding.getVariable()));
        Map<String, String> global = PersistEngine.instance().loadVariable();
        request.url(renderVar(request.url(), global));
        return request;
    }

    private static String renderVar(String url, Map<String, String> vars) {
        if (!checkString(url)) {
            return url;
        }
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            url = url.replaceAll("\\{\\{" + k + "}}", v);
        }
        return url;
    }

    private static boolean checkString(String s) {
        return s != null && !"".equals(s);
    }

    public static EntityBinding restEntity2Binding(RestEntity entity) {
        EntityBinding binding = new EntityBinding();
        binding.setName(entity.getName());
        binding.setOpen(entity.isOpened());
        binding.setCurrent(entity.isCurrent());
        binding.addRequests(entity.getBatch().stream().map(ModelUtil::RestItem2RequestBinding).collect(Collectors.toList()));
        binding.setVariable(entity.variable());
        return binding;
    }
    public static EntityBinding entity2Binding(Entity entity) {
        EntityBinding binding = new EntityBinding();
        binding.setName(entity.getName());
        binding.addRequests(entity.getBatch().stream().map(request -> {
            RequestBinding requestBinding = new RequestBinding();
            requestBinding.setMethod(request.method());
            requestBinding.setHeader(map2KeyVal(request.header()));
            requestBinding.setForm(map2KeyVal(request.form()));
            requestBinding.setUrl(request.url());
            requestBinding.setBody(request.body());
            requestBinding.setTime(request.time());
            requestBinding.setCode(request.code());
            requestBinding.setRespHeader(map2KeyVal(request.respHeader()));
            requestBinding.setResponse(request.response());
            requestBinding.setBasicAuth(request.basicAuth());
            requestBinding.setVerify(request.verify());
            requestBinding.setProxy(request.proxy());
            requestBinding.setName(request.name());
            return requestBinding;
        }).collect(Collectors.toList()));
        binding.setVariable(entity.variable());
        return binding;
    }
}
