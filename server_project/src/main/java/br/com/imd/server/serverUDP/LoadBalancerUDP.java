package br.com.imd.server.serverUDP;

import br.com.imd.dto.ParkingSpaceDto;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class LoadBalancerUDP extends ServerUDP {
    private Integer myPort;
    private Integer daoPort;
    private Integer parkingPort;
    private InetAddress clientAdrr;
    private Integer clientPort;

    public LoadBalancerUDP(Integer port) {
        this.myPort = port;
        daoPort = 8082;
        parkingPort = 8081;
    }

    @Override
    public void startServer() throws IOException {
        this.socket = new DatagramSocket(this.myPort);
        System.out.println("iniciando serviço load balancer na porta:" + socket.getLocalPort());


        while (true){

            String requestMsg = null;
            String responseMsg = null;
            requestMsg = receiveDataClient();
            String msg = ParkingSpaceDto.convertStringToMsg(requestMsg);
            System.out.println("mensagem: " + msg);

            if(msg.contains("estacionar")){
                sendData(msg.replace("estacionar", "SAVE"), InetAddress.getByName("localhost"), daoPort);
                String msgId = ParkingSpaceDto.convertStringToMsg(receiveData());
                if(!"".equals(msgId)) {
                    sendData("CREATE".concat(msgId), InetAddress.getByName("localhost"), parkingPort);
                    responseMsg = receiveData();
                }
            }
            if(msg.contains("retirar")){
                sendData(msg.replace("retirar", "REMOVE"), InetAddress.getByName("localhost"), parkingPort);
                responseMsg = receiveData();
            }
            sendData(responseMsg, clientAdrr, clientPort);
        }
    }

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

    @Override
    public void stopServer() {
       this.socket.close();
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
