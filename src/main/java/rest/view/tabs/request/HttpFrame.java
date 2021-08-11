


package rest.view.tabs.request;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import kong.unirest.Unirest;
import kong.unirest.UnirestInstance;
import rest.controller.builtin.RequestEngine;
import rest.controller.builtin.model.Request;
import rest.controller.plugin.stages.StageManager;
import rest.util.CommonUtil;
import rest.util.ModelUtil;
import rest.util.ViewUtil;
import rest.view.basic.BasicAuthVBox;
import rest.view.basic.EditableKeyValTable;
import rest.view.basic.KeyValTable;
import rest.view.basic.ProxyVBox;
import rest.view.model.RequestBinding;
import rest.view.tabs.download.DownloadView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * display rest requestBinding detail
 */
public class HttpFrame extends Tab {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpFrame.class);

    private final RequestEngine requestEngine = new RequestEngine();
    private final SplitMenuButton sendButton = new SplitMenuButton();
    private final MenuItem downloadBtn = new MenuItem("Download");
    private final JFXComboBox<String> methodChoiceBox = new JFXComboBox<>();
    private final JFXTextField urlTextField = new JFXTextField();
    private final StatusProgress progressBar = new StatusProgress();
    private final Label statusLabel = new Label("---");
    private final Label timeLabel = new Label("---");
    private final JFXTextArea reqBodyTextArea = new JFXTextArea();
    private final EditableKeyValTable reqHeaderTable = new EditableKeyValTable();
    private final EditableKeyValTable reqFormTable = new EditableKeyValTable();
    private final JFXTextArea respBodyTextArea = new JFXTextArea();
    private final KeyValTable respHeaderTable = new KeyValTable();
    private final BasicAuthVBox basicAuthBox = new BasicAuthVBox();
    private final ProxyVBox proxyVBox = new ProxyVBox();
    private final JFXCheckBox verify = new JFXCheckBox("enable ssl verify");

    private final SimpleStringProperty parent;

    private RequestBinding requestBinding;

    public HttpFrame(SimpleStringProperty parent, RequestBinding binding) {
        this.requestBinding = binding;
        this.parent = parent;
        draw();
        register();
        render();
    }

    private static TitledPane customTitlePane(String title, Node node) {
        TitledPane pane = new TitledPane();
        pane.setText(title);
        pane.setAnimated(false);
        pane.setAlignment(Pos.CENTER_LEFT);
        pane.setContent(node);
        pane.setBorder(Border.EMPTY);
        pane.setFont(Font.font(13));
        pane.setExpanded(false);
        return pane;
    }

    private void draw() {
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(new VBox(loadHeadPane(), loadBannerPane()));
        borderPane.setCenter(loadRequestResponsePane());
        borderPane.setPadding(new Insets(10, 5, 5, 5));
        setContent(borderPane);
    }

    private void register() {
        sendButton.setOnAction(event -> {
            sendStart();
            Request request = ModelUtil.requestBinding2Request(requestBinding);
            requestEngine.procRequest(request, response -> {
                requestBinding.setResponse(response.body());
                requestBinding.setRespHeader(ModelUtil.map2KeyVal(response.header()));
                requestBinding.setTime(String.valueOf(response.time()));
                requestBinding.setCode(response.code());
                String code = response.code();
                if (code.startsWith("2")) {
                    sendSuccess();
                }else{
                    sendFail();
                }
            });
        });
        downloadBtn.setOnAction(event -> {
            Request request = ModelUtil.requestBinding2Request(requestBinding);
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("select save dir");
            fileChooser.setInitialDirectory(new File(System.getProperties().getProperty("user.home")));
            String defaultName = getFileName(request);
            fileChooser.setInitialFileName(defaultName);
            Optional.ofNullable(fileChooser.showSaveDialog(StageManager.mainStage())).ifPresent(file -> {
                try {
                    DownloadView.instance().createTask(request, file.getCanonicalPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });

        setOnSelectionChanged(event -> requestBinding.setCurrent(isSelected()));
        setOnClosed(event -> requestBinding.setOpen(false));
    }

    private String getFileName(Request request) {
        final String[] name = {""};
        try {
            String url = request.url();
            if (url.contains("/") && !url.trim().endsWith("/")) {
                String sub = url.substring(url.lastIndexOf("/") + 1);
                if (sub.contains(".")) {
                    if (sub.contains("?")) {
                        sub = sub.substring(0, sub.indexOf("?")).trim();
                    }
                    return sub;
                }
            }
            UnirestInstance instance = Unirest.spawnInstance();
            instance.config().followRedirects(true);
            CommonUtil.unirestCfg(instance, request);

            instance.request("head", request.url()).getHeaders().all().forEach(header -> {
                if (header.getName().equalsIgnoreCase("Content-Disposition")) {
                    String val = header.getValue();
                    for (String tmp : val.split(";")) {
                        if (tmp.toLowerCase().contains("filename") && tmp.contains("=")) {
                            name[0] = tmp.substring(tmp.indexOf("=") + 1).trim();
                        }
                    }
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return name[0];
    }

    private Node loadRequestResponsePane() {
        SplitPane pane = new SplitPane();
        pane.getItems().addAll(loadRequestPane(), loadResponsePane());
        return pane;
    }

    private Node loadResponsePane() {
        TabPane pane = new TabPane();
        pane.getTabs().addAll(new Tab("response", respBodyTextArea), new Tab("header", respHeaderTable));
        pane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        return pane;
    }

    private Node loadRequestPane() {
        TabPane req = new TabPane();
        req.getTabs()
            .addAll(new Tab("body", reqBodyTextArea), new Tab("header", reqHeaderTable), new Tab("form", reqFormTable),
                new Tab("setting ", loadSettingPane()));
        req.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        return req;
    }

    private Node loadSettingPane() {
        VBox vBox = new VBox();
        vBox.setSpacing(5);
        HBox hBox = ViewUtil.customHBox();
        hBox.getChildren().add(verify);
        hBox.setPadding(new Insets(5, 5, 5, 25));
        vBox.getChildren()
            .addAll(customTitlePane("Basic Auth", basicAuthBox), customTitlePane("Proxy Config", proxyVBox),
                customTitlePane("Ssl Verify", hBox));
        vBox.setPadding(new Insets(3));
        return vBox;
    }

    private Node loadBannerPane() {
        Label status = new Label("status:");
        status.setMinWidth(70);
        Label time = new Label("cost(ms):");
        time.setMinWidth(80);
        statusLabel.setMinWidth(30);
        timeLabel.setMinWidth(100);
        HBox hBox = new HBox();
        hBox.getChildren().addAll(progressBar, status, statusLabel, time, timeLabel);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(0, 5, 5, 5));
        hBox.setMaxWidth(Integer.MAX_VALUE);
        hBox.getChildren().forEach(v -> HBox.setHgrow(v, Priority.ALWAYS));
        hBox.setSpacing(10);
        return hBox;
    }

    private Node loadHeadPane() {
        methodChoiceBox.setItems(FXCollections.observableArrayList("get", "post", "put", "del", "patch"));
        methodChoiceBox.getSelectionModel().selectFirst();
        methodChoiceBox.setPadding(new Insets(7, 0, 0, 0));
        urlTextField.setMinHeight(35);
        sendButton.setText(" Send ");
        sendButton.getStyleClass().addAll("success");
        sendButton.getItems().add(downloadBtn);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(methodChoiceBox, urlTextField, sendButton);
        hBox.getChildren().forEach(it -> HBox.setHgrow(it, Priority.ALWAYS));
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(0, 5, 5, 5));
        return hBox;
    }

    private void render() {
        setGraphic(ViewUtil.genImageViewForTab(CommonUtil.ITEM_TAB_GRAPHIC_ICON));
        StringBinding name = Bindings.createStringBinding(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return parent.get() + "/" + requestBinding.getName();
            }
        }, parent, requestBinding.nameProperty());
        textProperty().bind(name);

        methodChoiceBox.valueProperty().bindBidirectional(requestBinding.methodProperty());
        urlTextField.textProperty().bindBidirectional(requestBinding.urlProperty());
        reqBodyTextArea.textProperty().bindBidirectional(requestBinding.bodyProperty());

        reqHeaderTable.setItems(requestBinding.headerProperty());
        reqFormTable.setItems(requestBinding.formProperty());
        respBodyTextArea.textProperty().bindBidirectional(requestBinding.responseProperty());
        respHeaderTable.setItems(requestBinding.respHeaderProperty());
        statusLabel.textProperty().bindBidirectional(requestBinding.codeProperty());
        timeLabel.textProperty().bindBidirectional(requestBinding.timeProperty());
        basicAuthBox.render(requestBinding.getBasicAuth());
        proxyVBox.render(requestBinding.getProxy());
        verify.selectedProperty().bindBidirectional(requestBinding.verifyProperty());
        String status = requestBinding.getCode();
        if (status != null && (status.startsWith("2"))) {
            System.out.println("send success------------------------------");
            sendSuccess();
        } else {
            sendFail();
        }
        requestBinding.addLister((src, propName, old, neu) -> {
            proxyVBox.render(src.getProxy());
            basicAuthBox.render(src.getBasicAuth());
        });
        basicAuthBox.addListener((src, propName, old, neu) -> requestBinding.setBasicAuth(src));
        proxyVBox.addListener((src, propName, old, neu) -> requestBinding.setProxy(src));
    }
    //
    // public Request persist() {
    // requestBinding.method(methodChoiceBox.getValue())
    // .url(urlTextField.getText())
    // .body(reqBodyTextArea.getText())
    // .basicAuth(basicAuthBox.persist())
    // .proxy(proxyVBox.persist())
    // .verify(verify.isSelected())
    // .header(keyVal2Map(reqHeaderTable.getItems()))
    // .form(keyVal2Map(reqFormTable.getItems()))
    // .respHeader(keyVal2Map(respHeaderTable.getItems()))
    // .response(respBodyTextArea.getText())
    // .code(statusLabel.getText())
    // .time(timeLabel.getText());
    // LOGGER.info("persist rest requestBinding:{}", requestBinding);
    // return requestBinding;
    // }
    private void sendStart() {
        statusLabel.setText("---");
        timeLabel.setText("---");
        progressBar.running();
        sendButton.setDisable(true);
    }

    private void sendFail() {
        progressBar.failed();
        sendButton.setDisable(false);
    }

    private void sendSuccess() {
        progressBar.success();
        sendButton.setDisable(false);
    }

    private static class StatusProgress extends StackPane {
        private final JFXProgressBar running = new JFXProgressBar();
        private final JFXProgressBar failed = new JFXProgressBar(1);
        private final JFXProgressBar success = new JFXProgressBar(1);

        StatusProgress() {
            running.setPrefWidth(Integer.MAX_VALUE);
            failed.setPrefWidth(Integer.MAX_VALUE);
            success.setPrefWidth(Integer.MAX_VALUE);
            failed.getStyleClass().add("danger");
            success.getStyleClass().add("success");
            getChildren().addAll(running, failed, success);
        }

        void failed() {
            getChildren().clear();
            getChildren().add(failed);
        }

        void running() {
            getChildren().clear();
            getChildren().add(running);
        }

        void success() {
            getChildren().clear();
            getChildren().add(success);
        }
    }
}