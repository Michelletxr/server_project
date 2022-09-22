package br.com.imd.server.serverHTTP;

import br.com.imd.model.ObjRequest;

import java.net.Socket;

public class ServerClient implements Runnable{
    private Socket clientSocket;
    private ServerHttp server;

    public ServerClient(Socket clientSocket, ServerHttp server) {
        this.clientSocket = clientSocket;
        this.server = server;
    }


    @Override
    public void run() {
        System.out.println("helo thread");
        ObjRequest request = server.receiveRequest(clientSocket);
         server.generateDataToSend(request, clientSocket);
       // server.sendResponse(clientSocket, response, 200);
    }



}
