package br.com.imd.model;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class ObjRequest {
    private  String requestMethod;
    private String requestBody;
    private String requestURI;
    private String request = "";

    public ObjRequest(String requestMethod, String requestBody, String requestURI) {
        this.requestMethod = requestMethod;
        this.requestBody = requestBody;
        this.requestURI = requestURI;
    }
    public ObjRequest(
    ){
        //this.requestBody= new ArrayList<>();
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getRequestURI() {
        if(requestURI == null){
            return "/";
        }
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public void setHeader(String request){
        if(request!=null){
            StringTokenizer tokenizer = new StringTokenizer(request);
            this.requestMethod = tokenizer.nextToken();
            this.requestURI = tokenizer.nextToken();
           // System.out.println(requestMethod);
            //System.out.println(requestURI);
        }
    }

    public String generateRequest(){
        List<String> requestList = new ArrayList<>();
        request = request.concat(requestMethod + " " +  requestURI +" HTTP/1.0 " + "\r\n");
        request = request.concat("Content-Type: text/html\r\n");
        request = request.concat( "Content-Length: " + 10 + "\r\n");
        request = request.concat("\r");
        request = request.concat(requestBody+"\n");
        System.out.println("request criada: \n" + request);
        return  request;
    }


    public List<String> generateRequestLines(){
        List<String> requestList = new ArrayList<>();
        requestList.add(requestMethod + " " +  requestURI +" HTTP/1.0 ");
        requestList.add("Content-Type: text/html");
        requestList.add("Content-Length: " + 10 );
        requestList.add("\r");
        requestList.add(requestBody);
        requestList.add("\r");


        return requestList;
    }


}
