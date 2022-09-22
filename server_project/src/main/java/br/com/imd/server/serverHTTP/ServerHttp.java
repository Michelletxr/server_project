package br.com.imd.server.serverHTTP;
import br.com.imd.dto.ParkingSpaceDto;
import br.com.imd.model.ObjRequest;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public abstract  class ServerHttp {

    protected ServerHttp() {
    }

    public abstract  void startServer() throws IOException, ClassNotFoundException;
    public abstract void stopServer() throws IOException;
    public abstract void generateDataToSend(ObjRequest request, Socket socket);


    //recebe mensagem e cria um objeto request
    public ObjRequest receiveRequest(Socket socket){
        ObjRequest objRequest = new ObjRequest();
        String header = null;
        Stream<String> lines = null;

        System.out.println("-------------------recebendo mensagem------------------------------");

        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            header = in.readLine();
            String result = "";
            System.out.println("meu header: " + header + "\n");
            if(header!=null){
                objRequest.setHeader(header);
                if(!objRequest.getRequestMethod().equals("GET"))
                {
                    System.out.println("existem dados para serem lidos!");
                    while (true)
                    {
                       String line = in.readLine();
                        if(line.isBlank())
                        {
                            List<String> body = new ArrayList<>();
                            line = in.readLine();
                            while (!line.isBlank()){
                                body.add(line);
                                result = result.concat(line);
                                line=in.readLine();
                                if(line==null){
                                    break;
                                };

                            }

                            System.out.println("resultado final: "+ result);
                            objRequest.setRequestBody(ParkingSpaceDto.convertStringToMsg(result));
                            break;
                        }
                    }
                }
            }
            objRequest.setRequestBody(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return objRequest;
    }


    public void sendRequestToserver(OutputStream out, ObjRequest request){


        for (String line: request.generateRequestLines()) {
            try {
                //System.out.println("enviado para servidor....." + line);
                out.write((line + "\n").getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void sendRequest(Socket socket, String method, String uri, String request_body)
    {
        System.out.println("enviar uma request: \n" + request_body);

        String header = method + " " +  uri +" HTTP/1.0\r\n";
        String content_type =  "Content-Type: text/html\r\n";
        String content_length = "Content-Length: " + request_body.length() + "\r\n";

        try (DataOutputStream out  = new DataOutputStream( socket.getOutputStream()))
        {

            out.writeBytes(header);
            out.write(content_type.getBytes());
            out.write(content_length.getBytes());
            out.write("\r".getBytes());
            out.write(request_body.getBytes());


        } catch (IOException ex) {
            System.out.println("erro " + ex);
        }

    }


    public void sendResponse(Socket socket, String responseString, Integer statusCode){

        System.out.println("enviar response : \n " + responseString);
        String statusLine;
        String serverHeader = "Server: WebServer\r\n";

        String contentTypeHeader = "Content-Type: text/HTML\r\n";
        try (DataOutputStream out = new DataOutputStream(socket.getOutputStream());) {

            if (statusCode == 200) {
                statusLine = "HTTP/1.0 200 OK" + "\r\n";
                String contentLengthHeader = "Content-Length: " + responseString.length() + "\r\n";
                out.writeBytes(statusLine);
                out.writeBytes(serverHeader);
                out.writeBytes(contentTypeHeader);
                out.writeBytes(contentLengthHeader);
                out.writeBytes("\r\n");
                out.writeBytes(responseString + "\r\n");

            }else if (statusCode == 405) {

                statusLine = "HTTP/1.0 405 Method Not Allowed" + "\r\n";
                out.writeBytes(statusLine);
                out.writeBytes("\r\n");
                out.writeBytes(responseString + "\r\n");

            } else {

                statusLine = "HTTP/1.0 404 Not Found" + "\r\n";
                out.writeBytes(statusLine);
                out.writeBytes("\r\n");
                out.writeBytes(responseString + "\r\n");

            }
        } catch(IOException ex){

                ex.printStackTrace();

            }
        }


}
