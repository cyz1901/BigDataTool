package pers.cyz.bigdatatool.node.uiservice.pojo;

public class DeployMsgData {
//    subtitle: '解压组件',
//    title: '解压',
//    status: 'defeat'
    String subtitle;
    String title;
    String status;

    public DeployMsgData() {
    }

    public DeployMsgData(String subtitle, String title, String status) {
        this.subtitle = subtitle;
        this.title = title;
        this.status = status;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
