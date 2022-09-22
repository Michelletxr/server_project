package br.com.imd.model;

public class ResponseMsg {
    String target;
    String action;
    String data;

    public ResponseMsg(String target, String action, String data) {
        this.target = target;
        this.action = action;
        this.data = data;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "{" +
                "target: " + target + ", " +
                "action: " + action + ", "+
                 data + "}";
    }
}
