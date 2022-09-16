package br.com.imd.server.serverTCP;

import br.com.imd.dto.ParkingSpaceDto;
import br.com.imd.model.ParkingImpl;
import br.com.imd.model.ParkingSpace;
import br.com.imd.model.ThreadImpl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ParkingTCP extends ServerTCP {
    private ServerSocket serverSocket;
    private ParkingImpl parking;
    private int port;
    private Socket nextClient;
    private InetAddress addressClient;
    @Override
    public void startServer() throws IOException {
        this.serverSocket = new ServerSocket(this.port);
        System.out.println("iniciando serviço parking na porta:" + serverSocket.getLocalPort());
        while (true){
            nextClient = serverSocket.accept();
            addressClient = nextClient.getInetAddress();
            System.out.println("New client connected " + nextClient.getPort());
            new Thread(new ThreadImpl(nextClient, this)).start();
        }
    }

    public ParkingTCP(int port) {
        this.port = port;
        this.parking = new ParkingImpl();
    }

    @Override
    public void stopServer() throws IOException {
        System.out.println("encerrando conexão...");
        serverSocket.close();
    }

    @Override
    public String generateResponseToSend(String requestMsg) {
        System.out.println("request " + requestMsg);
        String response = null;

            if(requestMsg.contains("CREATE")) {
                ParkingSpace parkingSpace = ParkingSpaceDto.convertStringToParkingSpace(requestMsg);
                Integer taxa = parking.createTaxa(parkingSpace.getHoursInit(), parkingSpace.getHours());
                String data = "MENSAGEM: carro retirado; valor a pagar = " +  parkingSpace.getValue() + " sua taxa = " + taxa;
                response = ParkingSpaceDto.generateResponseObj("CLIENT", "CREATE", data);
            }

        return response;

    }

    public static void main(String[] args) {
        ParkingTCP server = new ParkingTCP(8081);
        try{
            server.startServer();
            server.stopServer();
        }catch (Exception e){
            System.err.println(e);
        }
    }
}
