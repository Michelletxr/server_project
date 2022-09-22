package br.com.imd.server.serverUDP;

import br.com.imd.dto.ParkingSpaceDto;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class LoadBalancerUDP extends ServerUDP {
    private Integer myPort;
    private DatagramSocket clintSocket;
    private List<Integer> parkingsServers;
    private List<Integer> daoServers;
    private List<Integer> authPorts;

    public LoadBalancerUDP(Integer port) {
        this.myPort = port;
        parkingsServers = new ArrayList<>();
        daoServers = new ArrayList<>();
        authPorts = new ArrayList<>();
        daoServers.add(8081);
        daoServers.add(8082);
        parkingsServers.add(8083);
        parkingsServers.add(8084);
        authPorts.add(8085);
        authPorts.add(8086);
    }


    public String validateTokenClient(String requestClient){
        String serverResponse = null;
        String token = ParkingSpaceDto.getTokenToString(requestClient);
        System.out.println("token " + token);
        String request = "action: VALIDATE, token: " +  token;
        try {
            this.clintSocket = new DatagramSocket();
            serverResponse = dialogServices(request, authPorts);
            this.clintSocket.close();
        } catch (SocketException e) {
            System.out.println("erro ao conctar com socket client");
            throw new RuntimeException(e);
        }


        return serverResponse;
    }

    public void updateServer(List<Integer> serverPorts){
        System.out.println("----------atualizando server-----------------");
        serverPorts.add(serverPorts.get(0));
        serverPorts.remove(0);
    }

    public String dialogServices(String request, List<Integer> serverPorts){
        String response = null;
        System.out.println(serverPorts);

        //melhorar dao para diminuir o tempo de espera
        try {

            this.clintSocket.setSoTimeout(500);
            byte[] sendMsg = request.getBytes();
            InetAddress inetAddress = InetAddress.getByName("localhost");
            DatagramPacket sendPacket = new DatagramPacket(sendMsg, request.length(),
                    inetAddress, serverPorts.get(0));
            clintSocket.send(sendPacket);

            byte[] responseData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(responseData, responseData.length);
            clintSocket.receive(receivePacket);
            response = new String(receivePacket.getData());
        }catch (SocketTimeoutException e){
            System.out.println("Atualiza a porta");
            updateServer(serverPorts);
           response =  dialogServices(request, serverPorts);
        }catch (IOException e){
            response = ParkingSpaceDto.generateResponseObj("CLIENT", "ERROR",
                    "MENSAGEM: erro ao requisitar serviço");
        }

        return response;
    }

    @Override
    public String generateResponseToSend(String requestMsg)  {
        System.out.println("request " + requestMsg);
        String target = ParkingSpaceDto.getTargetToString(requestMsg);
        String serverRequest = requestMsg;
        String serverResponse = null;
        String responseAuth = null;
        boolean userIsValid = true;

        if(!"AUTH".equals(target)) {
            responseAuth = validateTokenClient(requestMsg);
            if (responseAuth.contains("ERROR")) {
                userIsValid = false;
            }
        }
        if(userIsValid){
            try{
                while(!"CLIENT".equals(target)) {
                    this.clintSocket = new DatagramSocket();
                    System.out.println("TARGET: " + target);
                    switch (target){
                        case "BD":
                            //this.clintSocket = new DatagramSocket(3000);
                            serverResponse = dialogServices(serverRequest, daoServers);
                            //this.clintSocket.close();
                            break;
                        case "PARKING":
                            //this.clintSocket = new DatagramSocket();
                            serverResponse = dialogServices(serverRequest, parkingsServers);
                            //this.clintSocket.close();
                            break;
                        case "AUTH":
                            //this.clintSocket = new DatagramSocket();
                            serverResponse = dialogServices(serverRequest, authPorts);
                            break;
                        default:
                            serverResponse = ParkingSpaceDto.generateResponseObj("CLIENT", "ERROR",
                                    "MENSAGEM: serviço solicitado não existe!");
                            break;
                    }
                    clintSocket.close();
                    target = ParkingSpaceDto.getTargetToString(serverResponse);
                    serverRequest = serverResponse;
                }
            }catch (IOException e){
                System.out.println("erro");
            }

        }else{
            serverResponse = responseAuth;
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
            System.out.println("send" + response);
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
