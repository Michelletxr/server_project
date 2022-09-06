package br.com.imd.server;

import br.com.imd.dto.ParkingSpaceDto;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ServerUDP extends Server {
    private final int port;
    private DatagramSocket socket;

    private ParkingSpaceDto parkingSpaceDto = new ParkingSpaceDto();
    public ServerUDP(int port) {
        this.port = port;
    }

    @Override
    public void startServer() throws IOException {
        socket = new DatagramSocket(port);
        System.out.println("iniciando serviço na porta:" + socket.getLocalPort());

        while (true){
            //fica esperando cliente mandar uma requisição
            this.comunicate();
        }
    }
    @Override
    public void stopServer() {
        System.out.println("finalizando serviço na porta:" + socket.getLocalPort());
        socket.close();
    }
    public void comunicate() {
        //fica esperando cliente mandar uma requisição
        try{
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            System.out.println("cliente conectado na porta: " + receivePacket.getPort());

            //realiza operação com os dados
            String dataRequest = new String(receivePacket.getData());
            String msgResponse = this.generateResponse(dataRequest);

            //enviando dados para o cliente
            byte[] sendMsg = msgResponse.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendMsg,sendMsg.length,receivePacket.getAddress(),receivePacket.getPort());
            socket.send(sendPacket);
        }catch (IOException e){
            System.err.println("erro ao tentar estabelecer uma comunicação com o cliente" + e.getStackTrace());
        }
    }

    public static void main(String[] args) {
        Server server = new ServerUDP(8080);
        try
        {
            server.startServer();
            server.stopServer();

        }catch (IOException e){
            System.err.println("não foi possível conctar ao sevirdor" + e.getStackTrace());
        }
    }

    //usar singleton para conexao no drive
    //tirar o singleton da conexao com dao
    //usar pool de conexao
}
