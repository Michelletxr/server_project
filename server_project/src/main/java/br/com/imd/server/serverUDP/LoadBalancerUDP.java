package br.com.imd.server.serverUDP;

import br.com.imd.dto.ParkingSpaceDto;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class LoadBalancerUDP extends ServerUDP {
    private Integer myPort;
    private Integer daoPort;
    private Integer parkingPort;
    private InetAddress clientAdrr;
    private Integer clientPort;
    private List<Integer> parkingsServers;
    private List<Integer> daoServers;

    public LoadBalancerUDP(Integer port) {
        this.myPort = port;
        parkingsServers = new ArrayList<>();
        daoServers = new ArrayList<>();
        daoServers.add(8081);
        daoServers.add(8082);
        parkingsServers.add(8083);
        parkingsServers.add(8084);
    }

    public void updateServer(List<Integer> serverPorts){
        System.out.println("----------atualizando server-----------------");
        serverPorts.add(serverPorts.get(0));
        serverPorts.remove(0);
    }

    public String dialogServices(String request, List<Integer> serverPorts){
        String response = null;
        System.out.println(serverPorts);
        try {
            byte[] sendMsg = request.getBytes();
            InetAddress inetAddress = InetAddress.getByName("localhost");
            DatagramPacket sendPacket = new DatagramPacket(sendMsg, request.length(),
                    inetAddress, serverPorts.get(0));
            socket.send(sendPacket);

            byte[] responseData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(responseData, responseData.length);
            socket.receive(receivePacket);
            response = new String(receivePacket.getData());
            System.out.println("responseeeee" + response);
        }catch (IOException e){
            System.out.println("oiiiiiiiiii");
            updateServer(serverPorts);
            dialogServices(request, serverPorts);
        }catch (RuntimeException e){
            System.out.println("oiiiiiiiiii");
            response = ParkingSpaceDto.generateResponseObj("CLIENT", "ERROR",
                    "MENSAGEM: erro ao requisitar serviço");
        }

        return response;
    }

    @Override
    public String generateResponseToSend(String requestMsg)  {

        String target = ParkingSpaceDto.getTargetToString(requestMsg);
        String serverResponse = null;
        String serverRequest = requestMsg;
        System.out.println("request " + requestMsg);

        while(!"CLIENT".equals(target)) {

            System.out.println("TARGET: " + target);
            switch (target){
                case "BD":
                    serverResponse = dialogServices(serverRequest, daoServers);
                    break;
                case "PARKING":
                    serverResponse = dialogServices(serverRequest, parkingsServers);
                    break;
                default:
                    break;
            }
            target = ParkingSpaceDto.getTargetToString(serverResponse);
            serverRequest = serverResponse;
        }

        return serverResponse;
    }

    @Override
    public void startServer() throws IOException {
        this.socket = new DatagramSocket(this.myPort);
        System.out.println("iniciando serviço load balancer na porta:" + socket.getLocalPort());

        while (true) {
            String requestMsg = receiveData();
            String response = generateResponseToSend(requestMsg);
            sendData(response, this.adrReceive, this.portReceive);
        }
    }

    @Override
    public void stopServer() {
        this.socket.close();
    }

 /*   private String receiveDataClient() {
        String msg = null;
        try {
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            msg = new String(receivePacket.getData());
            this.clientAdrr = receivePacket.getAddress();
            this.clientPort = receivePacket.getPort();
        }catch (IOException e){
            System.err.println(e.getStackTrace());

        }
        return msg;
    }*/

    public static void main(String[] args) {
       ServerUDP server = new LoadBalancerUDP(8080);
        try
        {
            server.startServer();
            server.stopServer();

        }catch (IOException e){
            System.err.println("não foi possível conectar ao sevirdor" + e.getStackTrace());
        }
    }
}
