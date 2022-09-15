package br.com.imd.server.serverTCP;

import br.com.imd.dto.ParkingSpaceDto;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class LoadBalancerTCP extends ServerTCP {
    private ServerSocket serverSocket;
    private Integer port;
    private int daoPort;
    private Integer parkingPort;

    private List<Integer> parkingsServers;
    private List<Integer> daoServers;
    static boolean  jmeterLogged = false;

    public LoadBalancerTCP(Integer port) {
        this.port = port;
        parkingsServers = new ArrayList<>();
        daoServers = new ArrayList<>();
        daoServers.add(8081);
        daoServers.add(8082);
        parkingsServers.add(8083);
        parkingsServers.add(8084);
    }

    public void startServer() throws IOException {
        this.serverSocket = new ServerSocket(this.port);
        System.out.println("iniciando serviço load balancer na porta:" + serverSocket.getLocalPort());
        jmeterLogged = true;

        while (true){
            new Thread(new ThreadImpl(serverSocket.accept(), this)).start();
       }

    }

    public void stopServer() throws IOException {
        System.out.println("encerrando conexão...");
        serverSocket.close();
    }

    @Override
    public String receiveMsg(InputStream input){
        String msg = null;

        try {
            System.out.println("jmeter");
            InputStream inputstream = new BufferedInputStream(input);
            byte[] dataAsByte = new byte[1042];
            inputstream.read(dataAsByte);
            String data = new String(dataAsByte);
            msg = ParkingSpaceDto.convertStringToMsg(data);
        }catch (Exception e){
            System.err.println(e);
        }

        return msg;
    }

    public void updateServer(List<Integer> serverPorts){
        System.out.println("----------atualizando server-----------------");
        serverPorts.add(serverPorts.get(0));
        serverPorts.remove(0);
    }

    public String dialogServices(String request, List<Integer> serverPorts)
            throws IOException, ClassNotFoundException {

        Socket service = null;
        String msg = null;
        try{
            service = new Socket("localhost", serverPorts.get(0));
            ObjectOutputStream out = new ObjectOutputStream(service.getOutputStream());
            out.writeObject(request);
            out.flush();
            ObjectInputStream input = new ObjectInputStream(service.getInputStream());
            msg  = (String) input.readObject();
            out.close();
            input.close();
        }catch (ConnectException e){
            System.out.println(e);
            updateServer(serverPorts);
            msg = dialogServices(request, serverPorts);

        }
        return msg;
    }

    @Override
    public String generateResponseToSend(String requestMsg) {
        String target = ParkingSpaceDto.getTargetToString(requestMsg);
        System.out.println("request " + requestMsg);
        String response = null;
        String serverResponse =  null;
        String requestService = requestMsg;

        try{
            while(!"CLIENT".equals(target)){
                System.out.println("TARGET: " + target);
                switch (target) {
                    case "BD":
                        serverResponse = dialogServices(requestService, daoServers);
                        break;
                    case "PARKING":
                        serverResponse = dialogServices(requestService, parkingsServers);
                        break;
                    default:
                        break;
                }
                requestService = serverResponse;
                target = ParkingSpaceDto.getTargetToString(requestService);
            }
            System.out.println("server"+ serverResponse);
            response = serverResponse;
        }catch (Exception e){
            System.out.println("ocorreu um erro ao gerar resposta");
            response = ParkingSpaceDto.generateResponseObj("CLIENT", "ERROR",
                    "MENSAGEM: erro ao requisitar serviço");
        }
        return response;
    }


    public static void main(String[] args) {
        LoadBalancerTCP server = new LoadBalancerTCP(8080);
        try{
            server.startServer();
            server.stopServer();
        }catch (IOException e){}
    }
}
