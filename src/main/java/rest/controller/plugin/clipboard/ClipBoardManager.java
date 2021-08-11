

package rest.controller.plugin.clipboard;

import javafx.scene.input.ClipboardContent;

public class ClipBoardManager {
    private static final ClipboardContent CLIPBOARD_CONTENT = new ClipboardContent();

    public static ClipboardContent instance() {
        return CLIPBOARD_CONTENT;
    }
}
