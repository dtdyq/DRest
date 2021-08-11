

package rest.view.tabs.download;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.StringJoiner;

public class DownloadBinding {
    private SimpleStringProperty name = new SimpleStringProperty("");
    private SimpleStringProperty url = new SimpleStringProperty("");
    private SimpleDoubleProperty progress = new SimpleDoubleProperty(0);
    private SimpleStringProperty percent = new SimpleStringProperty("0/0");
    private SimpleStringProperty exitTime = new SimpleStringProperty("---");
    private SimpleStringProperty state = new SimpleStringProperty("Init");

    public String getName() {
        return name.get();
    }

    public void setName(final String s) {
        if (s != null && s.length() > 80) {
            name.set("... " + s.substring(s.length() - 80));
        } else {
            name.set(s);
        }
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getUrl() {
        return url.get();
    }

    public void setUrl(final String s) {
        if (s.length() > 80) {
            url.set(s.substring(0, 80) + " ...");
        } else {
            url.set(s);
        }
    }

    public SimpleStringProperty urlProperty() {
        return url;
    }

    public double getProgress() {
        return progress.get();
    }

    public void setProgress(double d) {
        Platform.runLater(() -> progress.set(d));
    }

    public SimpleDoubleProperty progressProperty() {
        return progress;
    }

    public String getPercent() {
        return percent.get();
    }

    public void setPercent(String p) {
        Platform.runLater(() -> percent.set(p));
    }

    public SimpleStringProperty percentProperty() {
        return percent;
    }

    public String getExitTime() {
        return exitTime.get();
    }

    public void setExitTime(String e) {
        Platform.runLater(() -> exitTime.set(e));
    }

    public SimpleStringProperty exitTimeProperty() {
        return exitTime;
    }

    public String getState() {
        return state.get();
    }

    public void setState(String s) {
        Platform.runLater(() -> state.set(s));
    }

    public SimpleStringProperty stateProperty() {
        return state;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DownloadBinding.class.getSimpleName() + "[", "]").add("name=" + name)
            .add("url=" + url)
            .add("progress=" + progress)
            .add("percent=" + percent)
            .add("exitTime=" + exitTime)
            .add("state=" + state)
            .toString();
    }
}
