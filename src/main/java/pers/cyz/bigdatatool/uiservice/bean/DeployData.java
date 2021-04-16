package pers.cyz.bigdatatool.uiservice.bean;

import java.util.ArrayList;

public class DeployData {
    String colonyName;
    String deployType;
    String deployWay;
    ArrayList<NodeData> nodeData;
    ArrayList<ComponentDownloadData> componentData;

    public DeployData() {
    }

    public DeployData(String colonyName, String deployType, String deployWay, ArrayList<NodeData> nodeData, ArrayList<ComponentDownloadData> componentData) {
        this.colonyName = colonyName;
        this.deployType = deployType;
        this.deployWay = deployWay;
        this.nodeData = nodeData;
        this.componentData = componentData;
    }

    public String getDeployWay() {
        return deployWay;
    }

    public void setDeployWay(String deployWay) {
        this.deployWay = deployWay;
    }

    public ArrayList<ComponentDownloadData> getComponentData() {
        return componentData;
    }

    public void setComponentData(ArrayList<ComponentDownloadData> componentData) {
        this.componentData = componentData;
    }

    public String getColonyName() {
        return colonyName;
    }

    public void setColonyName(String colonyName) {
        this.colonyName = colonyName;
    }

    public String getDeployType() {
        return deployType;
    }

    public void setDeployType(String deployType) {
        this.deployType = deployType;
    }

    public ArrayList<NodeData> getNodeData() {
        return nodeData;
    }

    public void setNodeData(ArrayList<NodeData> nodeData) {
        this.nodeData = nodeData;
    }

    public static class NodeData {
        String hostname;
        String ip;
        String nodeType;

        public NodeData() {
        }

        public NodeData(String hostname, String ip, String nodeType) {
            this.hostname = hostname;
            this.ip = ip;
            this.nodeType = nodeType;
        }

        public String getHostname() {
            return hostname;
        }

        public void setHostname(String hostname) {
            this.hostname = hostname;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getNodeType() {
            return nodeType;
        }

        public void setNodeType(String nodeType) {
            this.nodeType = nodeType;
        }
    }
}
