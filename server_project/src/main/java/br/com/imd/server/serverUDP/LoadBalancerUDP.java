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
        parkingsServers.add(8081);
        daoServers = new ArrayList<>();
        daoServers.add(8082);
        daoPort = 8082;
        parkingPort = 8081;
    }

    public void updateServer(List<Integer> serverPorts){
        //implement
    }

    public String dialogServices(String request, List<Integer> serverPorts){
            //implement
        return null;
    }

    @Override
    public void generateResponseToSend(String requestMsg)  {

        String target = ParkingSpaceDto.getTargetToString(requestMsg);
        System.out.println("request " + requestMsg);
        System.out.println("TARGET: " + target);
        try{
            switch (target){
                case "BD":
                    sendData(requestMsg, InetAddress.getByName("localhost"), daoPort);
                    break;
                case "PARKING":
                    sendData(requestMsg, InetAddress.getByName("localhost"), parkingPort);
                    break;
                case "CLIENT":
                    sendData(requestMsg, clientAdrr, clientPort);
                    break;

                default:
                    sendData("erro", this.adrReceive, this.portReceive);
            }
        }catch (IOException e){
            System.out.println(e);
        }

    }

    @Override
    public void startServer() throws IOException {

        this.socket = new DatagramSocket(this.myPort);
        System.out.println("iniciando serviço load balancer na porta:" + socket.getLocalPort());
        String requestMsg = receiveDataClient();

        while (true) {
            generateResponseToSend(requestMsg);
            requestMsg = receiveData();

        }
    }

    @Override
    public void stopServer() {
        this.socket.close();
    }

    //removr isso
    private String receiveDataClient() {
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
    }

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
