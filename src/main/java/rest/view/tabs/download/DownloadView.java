


package rest.view.tabs.download;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXProgressBar;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import rest.controller.builtin.model.Request;
import rest.controller.plugin.concurrent.ThreadManager;
import rest.controller.plugin.stages.StageManager;
import rest.persitence.PersistEngine;
import rest.persitence.model.DownLoadItem;
import rest.util.CommonUtil;
import rest.util.ViewUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * manager download items
 */
public class DownloadView extends Tab {
    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadView.class);

    private static final DownloadView downloadView = new DownloadView();

    private static final String TITLE = "download view";

    private List<DownloadTask> tasks = new ArrayList<>();

    private JFXListView<DownloadBinding> listView = new JFXListView<>();

    private DownloadView() {
        init();
        LOGGER.info("down load manager new");
    }

    public static DownloadView instance() {
        return downloadView;
    }

    private void init() {
        LOGGER.info("begin to init download manager");
        setText(TITLE);
        setContent(listView);
        setGraphic(ViewUtil.genImageViewForTab(CommonUtil.DOWNLOAD_TAB_GRAPHIC_ICON));

        listView.setCellFactory(new Callback<ListView<DownloadBinding>, ListCell<DownloadBinding>>() {
            @Override
            public ListCell<DownloadBinding> call(ListView<DownloadBinding> param) {
                return new ListCell<DownloadBinding>() {
                    {
                        ContextMenu c = new ContextMenu();
                        MenuItem item = new MenuItem("delete it");
                        item.setOnAction(
                            event -> listView.getItems().remove(listView.getSelectionModel().getSelectedItem()));
                        c.getItems().add(item);
                        setContextMenu(c);
                    }

                    @Override
                    protected void updateItem(DownloadBinding item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            DownLoadPane pane = new DownLoadPane(item);
                            setGraphic(pane);
                            setText(null);
                        }
                    }
                };
            }
        });
        List<DownLoadItem> items = PersistEngine.instance().loadDownloadItems();
        List<DownloadBinding> res = items.stream().map(this::toBinding).collect(Collectors.toList());
        listView.getItems().addAll(res);
    }

    private DownLoadItem toItem(DownloadBinding binding) {
        DownLoadItem item = new DownLoadItem().name(binding.getName())
            .url(binding.getUrl())
            .percent(binding.getPercent())
            .state(binding.getState())
            .progress(binding.getProgress())
            .time(binding.getExitTime());
        if (!binding.getState().equals("Finished")) {
            item.state("Failed");
        }
        if (binding.getExitTime().equals("---")) {
            item.time(String.valueOf(LocalDateTime.now()));
        }
        return item;
    }

    private DownloadBinding toBinding(DownLoadItem item) {
        DownloadBinding binding = new DownloadBinding();
        binding.setName(item.name());
        binding.setUrl(item.url());
        binding.setState(item.state());
        binding.setExitTime(item.time());
        binding.setPercent(item.percent());
        binding.setProgress(item.progress());
        return binding;
    }

    public void createTask(Request request, String name) {
        LOGGER.info("new download task:{},{}", request, name);
        DownloadBinding binding = new DownloadBinding();
        binding.setName(name);
        binding.setUrl(request.url());
        DownloadTask task = new DownloadTask(request, binding);

        CompletableFuture.runAsync(task, ThreadManager.downloadPool());
        listView.getItems().add(0, binding);
        tasks.add(task);
        show();
    }

    public boolean isDownloading() {
        return listView.getItems().stream().anyMatch(i -> i.getState().equals("Downloading"));
    }

    public void close() {
        tasks.forEach(Task::cancel);
        listView.getItems().forEach(binding -> {
            if (binding.getState().equals("Downloading")) {
                binding.setState("Failed");
            }
        });
        PersistEngine.instance()
            .persistDownloadItems(listView.getItems().stream().map(this::toItem).collect(Collectors.toList()));
    }

    public void show() {
        Tab tab = StageManager.primaryFrame()
            .getTabs()
            .stream()
            .filter(tab1 -> tab1.getText().equals(TITLE))
            .findFirst()
            .orElse(null);
        if (tab != null) {
            StageManager.primaryFrame().getSelectionModel().select(tab);
        } else {
            StageManager.primaryFrame().getTabs().add(downloadView);
            StageManager.primaryFrame().getSelectionModel().select(downloadView);
        }
    }

    class DownLoadPane extends HBox {
        private final Label name = new Label();
        private final JFXProgressBar progress = new JFXProgressBar();
        private final Label percent = new Label();

        private final Label url = new Label();
        private final Label result = new Label();
        private final Label time = new Label();

        DownLoadPane(DownloadBinding binding) {
            LOGGER.info("start to render download pane :{}", binding);
            GridPane pane = new GridPane();
            name.textProperty().bind(binding.nameProperty());
            name.setFont(Font.font(15));
            progress.progressProperty().bind(binding.progressProperty());
            progress.setMinWidth(600);
            progress.setStyle("-fx-accent: green");
            progress.getStyleClass().add("success");
            url.textProperty().bind(binding.urlProperty());
            url.setStyle("-fx-text-fill: gray");
            url.setMinWidth(600);
            url.setFont(Font.font(13));
            result.setFont(Font.font("CamingoCode", FontWeight.BOLD, 16));
            result.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue.equals("Finished")) {
                    result.setStyle("-fx-text-fill: green");
                } else if (newValue.equals("Downloading")) {
                    result.setStyle("-fx-text-fill: blue");
                } else {
                    result.setStyle("-fx-text-fill: red");
                }
            });
            percent.textProperty().bind(binding.percentProperty());
            time.textProperty().bind(binding.exitTimeProperty());

            result.textProperty().addListener((observable, oldValue, newValue) -> Platform.runLater(() -> {
                pane.getChildren().clear();
                pane.add(name, 0, 0, 4, 1);
                pane.add(result, 7, 0, 2, 1);
                if (binding.getState().equals("Finished") || binding.getState().equals("Failed")) {
                    pane.add(url, 4, 0, 3, 1);
                    pane.add(time, 9, 0, 1, 1);
                } else {
                    pane.add(progress, 4, 0, 3, 1);
                    pane.add(percent, 9, 0, 1, 1);
                }
            }));
            result.textProperty().bind(binding.stateProperty());

            pane.setHgap(5);
            pane.setVgap(5);

            setPadding(new Insets(5));
            setSpacing(10);
            getChildren().addAll(pane);
            setAlignment(Pos.CENTER_LEFT);

        }

    }
}
