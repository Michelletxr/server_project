package br.com.imd.server.serverHTTP;

import br.com.imd.dto.ParkingSpaceDto;
import br.com.imd.model.ObjRequest;
import br.com.imd.model.ParkingImpl;
import br.com.imd.model.ParkingSpace;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ParkingHttp extends ServerHttp{
    private ServerSocket serverSocket;
    private ParkingImpl parking;
    private int port;

    @Override
    public void startServer() throws IOException {
        this.serverSocket = new ServerSocket(this.port);
        System.out.println("iniciando serviço parking na porta:" + serverSocket.getLocalPort());
            while (true){
                new Thread(new ServerClient(serverSocket.accept(), this)).start();
        }
    }

    public ParkingHttp(int port) {
        this.port = port;
        this.parking = new ParkingImpl();
    }

    @Override
    public void stopServer() throws IOException {
        System.out.println("encerrando conexão...");
        serverSocket.close();
    }

    @Override
    public void generateDataToSend(ObjRequest request, Socket socket) {

        System.out.println("request " + request.getRequestBody());


        if(request.getRequestMethod().equals("POST")) {
            ParkingSpace parkingSpace = ParkingSpaceDto.convertStringToParkingSpace(request.getRequestBody());
            Integer taxa = parking.createTaxa(parkingSpace.getHoursInit(), parkingSpace.getHours());
            String response_body = "MENSAGEM: carro retirado; valor inicial = " +  parkingSpace.getValue() + " sua taxa = " + taxa;
            sendResponse(socket, response_body , 200);
        }
    }



    public static void main(String[] args) {
        ParkingHttp server = new ParkingHttp(8082);
        try{
            server.startServer();
            server.stopServer();
        }catch (Exception e){
            System.err.println(e);
        }
    }
}
