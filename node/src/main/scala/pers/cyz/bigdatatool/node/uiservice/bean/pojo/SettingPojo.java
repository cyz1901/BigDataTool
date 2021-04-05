package pers.cyz.bigdatatool.node.uiservice.bean.pojo;

public class SettingPojo {
    String name;
    String value;

    public SettingPojo(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
