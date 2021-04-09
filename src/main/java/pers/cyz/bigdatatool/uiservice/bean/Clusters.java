package pers.cyz.bigdatatool.uiservice.bean;

public class Clusters implements java.io.Serializable{
    String colonyName;
    String nameNodeName;

    public Clusters() {
    }

    public Clusters(String colonyName, String nameNodeName) {
        this.colonyName = colonyName;
        this.nameNodeName = nameNodeName;
    }

    public String getColonyName() {
        return colonyName;
    }

    public void setColonyName(String colonyName) {
        this.colonyName = colonyName;
    }

    public String getNameNodeName() {
        return nameNodeName;
    }

    public void setNameNodeName(String nameNodeName) {
        this.nameNodeName = nameNodeName;
    }


}
