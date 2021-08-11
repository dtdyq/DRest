

package rest.view.component;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import rest.controller.plugin.concurrent.ThreadManager;
import rest.persitence.model.RestEntity;
import rest.util.ModelUtil;
import rest.view.model.EntityBinding;
import rest.view.tabs.batch.BatchRequestTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class PrimaryFrame extends TabPane {
    private static final Logger LOGGER = LoggerFactory.getLogger(PrimaryFrame.class);

    public PrimaryFrame() {
        register();
    }

    private void register() {
        setTabClosingPolicy(TabClosingPolicy.SELECTED_TAB);
        setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.S) {
                BatchRequestTable frame = (BatchRequestTable) getSelectionModel().getSelectedItem();
                frame.persist();
            }
        });
        CompletableFuture.runAsync(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000 * 30);
                    getTabs().stream()
                        .filter(t -> t instanceof BatchRequestTable)
                        .map(v -> (BatchRequestTable) v)
                        .forEach(BatchRequestTable::persist);
                } catch (InterruptedException e) {
                    LOGGER.error("persist damon thread interrupt.", e);
                    Thread.currentThread().interrupt();

                }

            }
        }, ThreadManager.persistPool());
    }

    public void loadEntity(RestEntity entity) {
        // just select the frame if it has be load
        loadEntity(ModelUtil.restEntity2Binding(entity));
    }

    public void loadEntity(EntityBinding binding) {
        // just select the frame if it has be load
        Tab find = getTabs().stream().filter(tab -> tab.getText().equals(binding.getName())).findFirst().orElse(null);
        if (find != null) {
            getSelectionModel().select(find);
            return;
        }
        binding.setOpen(true);
        BatchRequestTable table = new BatchRequestTable(binding);
        getTabs().add(table);
        if (binding.isCurrent()) {
            getSelectionModel().select(table);
        }
    }

    public void close() {
        getTabs().stream()
            .filter(t -> t instanceof BatchRequestTable)
            .map(v -> (BatchRequestTable) v)
            .forEach(BatchRequestTable::persist);
    }

}
