

package rest.view.tabs.batch.vassal.columns.abs;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import rest.util.ViewUtil;
import rest.view.tabs.batch.BatchRequestTable;
import rest.view.tabs.batch.vassal.columns.handler.CellHandler;
import rest.view.model.RequestBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * abs column for impl,support register menu and process cell
 */
public class SuperColumn extends TableColumn<RequestBinding, RequestBinding> {
    public SuperColumn(BatchRequestTable table) {
        setSortable(false);
        setCellFactory(ViewUtil.createDefaultCell(getCellHandler(table)));

        ContextMenu contextMenu = new ContextMenu();
        List<MenuItem> items = new ArrayList<>();
        registerMenus(items, table);
        contextMenu.getItems().addAll(items);
        setContextMenu(contextMenu);
    }

    protected void registerMenus(List<MenuItem> items,BatchRequestTable requestTable) {

    }
    protected CellHandler getCellHandler(BatchRequestTable table) {
        return cell -> {

        };
    }

}
