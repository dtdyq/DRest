

package rest.controller.plugin.eximport.json;

import com.google.gson.Gson;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.FileChooser;
import rest.controller.plugin.eximport.model.Entity;
import rest.controller.plugin.eximport.model.Request;
import rest.controller.plugin.stages.StageManager;
import rest.persitence.PersistEngine;
import rest.persitence.model.RestEntity;
import rest.util.CommonUtil;
import rest.util.ModelUtil;
import rest.util.ViewUtil;
import rest.view.model.EntityBinding;
import rest.view.model.RequestBinding;
import rest.view.tabs.batch.BatchRequestTable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JsonPort {
    public static void export(EntityBinding entityBinding) {
        Entity entity = new Entity();
        entity.setName(entityBinding.getName());
        entity.setBatch(entityBinding.getRequests().stream().map(new Function<RequestBinding, Request>() {
            @Override
            public Request apply(RequestBinding binding) {
                Request request = new Request();
                if (binding.getBasicAuth().getUsername() != null && !"".equals(binding.getBasicAuth().getUsername())) {
                    request.basicAuth(binding.getBasicAuth());
                }
                if (binding.getProxy().username() != null && !"".equals(binding.getProxy().username())) {
                    request.proxy(binding.getProxy());
                }
                request.name(binding.getName())
                    .body(binding.getBody())
                    .code(binding.getCode())
                    .form(ModelUtil.keyVal2Map(binding.getForm()))
                    .header(ModelUtil.keyVal2Map(binding.getHeader()))
                    .method(binding.getMethod())
                    .respHeader(ModelUtil.keyVal2Map(binding.getRespHeader()))
                    .response(binding.getResponse())
                    .time(binding.getTime())
                    .url(binding.getUrl());
                return request;
            }
        }).collect(Collectors.toList()));
        Map<String, String> global = PersistEngine.instance().loadVariable();
        entityBinding.getVariable().forEach(global::put);
        entity.variable(global);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("select save dir");
        fileChooser.setInitialDirectory(new File(System.getProperties().getProperty("user.home")));
        String defaultName = entity.getName() + CommonUtil.ENTITY_FILE_NAME_SUFFIX;
        fileChooser.setInitialFileName(defaultName);
        Optional.ofNullable(fileChooser.showSaveDialog(StageManager.mainStage())).ifPresent(file -> {
            String data = new Gson().toJson(entity);
            try {

                Files.write(file.toPath(), data.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void inport() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("select to load");
            fileChooser.setInitialDirectory(new File(System.getProperties().getProperty("user.home")));
            File f = fileChooser.showOpenDialog(StageManager.mainStage());
            if (f != null) {
                Entity entity = new Gson().fromJson(new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8),
                    Entity.class);
                EntityBinding entityBinding = ModelUtil.entity2Binding(entity);
                entityBinding.setOpen(true);
                entityBinding.setCurrent(true);

                List<RestEntity> entities = PersistEngine.instance().loadEntities();
                RestEntity find = entities.stream()
                    .filter(tab -> tab.getName().equals(entityBinding.getName()))
                    .findFirst()
                    .orElse(null);
                if (find != null) {
                    Dialog<Boolean> confirm = new Dialog<>();
                    ButtonType ok = ViewUtil.customDialog(confirm);
                    confirm.setTitle("confirm");
                    confirm.setContentText(String.format("entity %s already exist,merge it or not?", find.getName()));
                    confirm.setResultConverter(param -> param == ok);
                    confirm.showAndWait().ifPresent(aBoolean -> {
                        if (aBoolean) {
                            EntityBinding tmp = ModelUtil.restEntity2Binding(find);
                            tmp.addRequests(entityBinding.getRequests());
                            tmp.setOpen(true);
                            tmp.setCurrent(true);
                            StageManager.primaryFrame().loadEntity(tmp);
                        }
                    });
                } else {
                    entityBinding.setOpen(true);
                    BatchRequestTable table = new BatchRequestTable(entityBinding);
                    StageManager.primaryFrame().getTabs().add(table);
                    StageManager.primaryFrame().getSelectionModel().select(table);
                }
            } else {
                throw new Exception("specify a invalid file");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            ViewUtil.alert("import occur error:" + e.getMessage());
        }
    }
}
