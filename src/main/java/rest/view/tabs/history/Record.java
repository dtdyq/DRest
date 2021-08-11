


package rest.view.tabs.history;

class Record {
    private String desc;

    // 0=info 1=warning 2=error
    private int level;

    private String detail;

    public Record() {
    }

    public Record(int level, String desc) {
        this.desc = desc;
        this.level = level;
    }

    String desc() {
        return desc;
    }

    Record desc(String desc) {
        this.desc = desc;
        return this;
    }

    public int level() {
        return level;
    }

    public Record level(int level) {
        this.level = level;
        return this;
    }

    public String detail() {
        return detail;
    }

    public Record detail(String detail) {
        this.detail = detail;
        return this;
    }
}
