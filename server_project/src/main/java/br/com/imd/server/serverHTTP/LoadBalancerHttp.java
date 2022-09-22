package br.com.imd.server.serverHTTP;
import br.com.imd.model.ObjRequest;
import br.com.imd.model.ObjResponse;

import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class LoadBalancerHttp extends ServerHttp {

    private ServerSocket serverSocket;
    private Integer port;
    private Socket clientSocket;
    private  List<Integer> daoServers;
    private  List<Integer> parkingServers;
   // private  List<Integer> authServers;
    public LoadBalancerHttp(Integer port) {
        this.port = port;
        daoServers = new ArrayList<>();
        parkingServers = new ArrayList<>();
        //authServers = new ArrayList<>();
        daoServers.add(8081);
        daoServers.add(8082);
        parkingServers.add(8083);
        parkingServers.add(8084);

    }

    @Override
    public void startServer() throws IOException {

        this.serverSocket = new ServerSocket(this.port);
        System.out.println("iniciando serviço load balancer na porta:" + serverSocket.getLocalPort());
        while (true){
            new Thread(new ServerClient(serverSocket.accept(), this)).start();
        }
    }

    @Override
    public void stopServer() throws IOException
    {
        System.out.println("encerrando conexão...");
        serverSocket.close();
    }

    public void updateServer(List<Integer> serverPorts){
        System.out.println("----------atualizando server-----------------");
        serverPorts.add(serverPorts.get(0));
        serverPorts.remove(0);
    }

    public String dialogServices(ObjRequest request, List<Integer> serverPorts) {
        String response = "";
        request.generateRequestLines();
        try {
            InetAddress serverInetAddress = InetAddress.getByName("127.0.0.1");
            Socket connection = new Socket(serverInetAddress, serverPorts.get(0));
            sendRequestToserver(connection.getOutputStream(), request);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine = in.readLine();
            while (true) {
                response = response.concat(inputLine + "\n");
                inputLine = in.readLine();
                if(inputLine==null) break;
            }
           // System.out.println("response enviada pelo servidor :" + response);
        }catch (ConnectException e){
            System.out.println(e);
            updateServer(serverPorts);
            response = dialogServices(request, serverPorts);
        }
        catch (IOException e) {
            System.out.println(e);
        }
            return  response;
        }

        @Override
    public void generateDataToSend(ObjRequest request, Socket socket)
    {

        String uri  = request.getRequestURI();
        System.out.println("-------------------dentro do load------------------------------");
        String response = "default";

        ObjRequest requestServers = request;
        while (!"/".equals(uri)){
            switch (uri) {
                case "/parking-db":
                    response = dialogServices(requestServers, daoServers);
                    break;
                case "/parking":
                    response = dialogServices(requestServers, parkingServers);
                    break;
                case "/auth":
                    //serverResponse = dialogServices(requestService, authPorts);
                    break;
                default:
                    break;
            }
            String[] lines_response = response.split("\n");
            requestServers.setHeader(lines_response[0]);
            requestServers.setRequestBody(ObjResponse.getResponseBody(response));
            uri = ObjResponse.getUri(response);
        }
        sendResponse(socket, ObjResponse.getResponseBody(response), ObjResponse.getStatusCode(response));
    }

    public static void main(String[] args) {
        LoadBalancerHttp server = new LoadBalancerHttp(8080);
        try{
            server.startServer();
            server.stopServer();
        }catch (IOException e){
            System.out.println("erro ao iniciar serviço");
        }
    }

}
