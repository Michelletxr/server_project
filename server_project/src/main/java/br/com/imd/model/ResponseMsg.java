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

    @Override
    public String toString() {
        return "{" +
                "target: " + target + ", " +
                "action: " + action + ", "+
                 data + "}";
    }
}
