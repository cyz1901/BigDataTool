package pers.cyz.bigdatatool.node.uiservice.untils;

public class Result {
    int code;
    Object data;

    public Result() {
    }

    public Result(int code, Object data) {
        this.code = code;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
