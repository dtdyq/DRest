
package rest.view.tabs.batch.vassal.columns;

import static rest.util.ViewUtil.createCellBtn;

import com.jfoenix.controls.JFXSpinner;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import rest.controller.builtin.RequestEngine;
import rest.controller.builtin.model.Request;
import rest.util.ModelUtil;
import rest.util.ViewUtil;
import rest.view.model.FieldChangeListener;
import rest.view.model.RequestBinding;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.columns.abs.SuperColumn;
import rest.view.tabs.batch.vassal.columns.handler.CellHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * run button column,support run current request,and batched run request
 */
public class RunColumn extends SuperColumn {
    private static final Logger LOGGER = LoggerFactory.getLogger(RunColumn.class);

    private final Semaphore semaphore = new Semaphore(1);

    private final Executor exec = Executors.newSingleThreadExecutor();

    public RunColumn(BatchRequestTable table) {
        super(table);
        setText("run");
    }

    @Override
    protected CellHandler getCellHandler(BatchRequestTable table) {
        return new RunCellHandler(table);
    }

    private class RunCellHandler implements CellHandler {
        private final BatchRequestTable table;

        public RunCellHandler(BatchRequestTable table) {
            this.table = table;
        }

        @Override
        public void handle(TableCell<RequestBinding, RequestBinding> cell) {
            Button bt1 = createCellBtn("      ");
            bt1.setGraphic(ViewUtil.genRunView("/icons/run.png"));
            bt1.setContentDisplay(ContentDisplay.CENTER);
            RequestBinding binding = table.getTable().getItems().get(cell.getIndex());

            binding.runEventListener(new FieldChangeListener<RequestBinding>() {
                @Override
                public void change(RequestBinding src, String propName, Object old, Object neu) {
                    RequestBinding.RunEvent runEvent = (RequestBinding.RunEvent) neu;
                    if (runEvent.getIndex() != -1 && runEvent.getIndex() == cell.getIndex()) {
                        LOGGER.info("received task index:{}", runEvent.getIndex());
                        if (runEvent.isSequential()) {
                            exec.execute(() -> {
                                try {
                                    semaphore.acquire();
                                    runRest(cell, bt1, true, binding);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            });
                        } else {
                            runRest(cell, bt1, false, binding);
                        }
                    }
                }
            });

            bt1.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    runRest(cell, bt1, false, binding);
                }
            });
            cell.setGraphic(bt1);

        }

        public void runRest(TableCell<RequestBinding, RequestBinding> cell, Button button, boolean sequential,
            RequestBinding binding) {
            CompletableFuture<PrepareModel> completableFuture = new CompletableFuture<>();
            Platform.runLater(() -> {
                JFXSpinner spinner = new JFXSpinner();
                spinner.setStyle("-fx-pref-height: 25");
                spinner.getStyleClass().addAll("materialDesign-cyan");
                binding.setCode("---");
                binding.setTime("---");
                binding.setRespHeader(new ArrayList<>());
                binding.setResponse("");
                cell.setGraphic(spinner);
                Request item = ModelUtil.requestBinding2RequestAndFlipVariable(table.getEntityBinding(), binding);
                PrepareModel model = new PrepareModel();
                model.binding(binding).item(item);
                completableFuture.complete(model);
            });
            completableFuture.whenComplete(
                (prepareModel, throwable) -> new RequestEngine().procRequest(prepareModel.request, response -> {
                    ModelUtil.refreshRequestBindingWithResp(prepareModel.binding, response);
                    cell.setGraphic(button);
                    prepareModel.binding.setRunEvent(new RequestBinding.RunEvent(-1, sequential));
                    if (sequential) {
                        semaphore.release();
                    }
                }));

        }
    }

    private class PrepareModel {
        private Request request;
        private RequestBinding binding;

        public Request item() {
            return request;
        }

        public PrepareModel item(Request item) {
            this.request = item;
            return this;
        }

        public RequestBinding binding() {
            return binding;
        }

        public PrepareModel binding(RequestBinding binding) {
            this.binding = binding;
            return this;
        }
    }
}
