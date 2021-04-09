package pers.cyz.bigdatatool.uiservice.bean;


public class ComponentDownloadData {
    String name;
    String version;

    public ComponentDownloadData() {
    }

    public ComponentDownloadData(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}


