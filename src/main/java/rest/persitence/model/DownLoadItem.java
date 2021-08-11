

package rest.persitence.model;

import java.util.StringJoiner;

public class DownLoadItem {
    private String name;
    private String url;
    private String percent;
    private double progress;
    private String time;
    private String state;

    public String name() {
        return name;
    }

    public DownLoadItem name(String name) {
        this.name = name;
        return this;
    }

    public String url() {
        return url;
    }

    public DownLoadItem url(String url) {
        this.url = url;
        return this;
    }

    public String percent() {
        return percent;
    }

    public DownLoadItem percent(String percent) {
        this.percent = percent;
        return this;
    }

    public double progress() {
        return progress;
    }

    public DownLoadItem progress(double progress) {
        this.progress = progress;
        return this;
    }

    public String time() {
        return time;
    }

    public DownLoadItem time(String time) {
        this.time = time;
        return this;
    }

    public String state() {
        return state;
    }

    public DownLoadItem state(String state) {
        this.state = state;
        return this;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", DownLoadItem.class.getSimpleName() + "[", "]").add("name='" + name + "'")
            .add("url='" + url + "'")
            .add("percent='" + percent + "'")
            .add("progress=" + progress)
            .add("time='" + time + "'")
            .add("state='" + state + "'")
            .toString();
    }
}
