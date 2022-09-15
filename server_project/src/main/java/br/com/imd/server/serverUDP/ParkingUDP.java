package br.com.imd.server.serverUDP;

import br.com.imd.dto.ParkingSpaceDto;
import br.com.imd.model.ParkingImpl;
import br.com.imd.model.ParkingSpace;

import java.io.IOException;
import java.net.DatagramSocket;

public class ParkingUDP extends ServerUDP {

    private Integer port;
    private ParkingImpl parking;

    public ParkingUDP(int port) {
        this.port = port;
        this.parking = new ParkingImpl();
    }

    @Override
    public String generateResponseToSend(String request) {
        String resquestMsg = ParkingSpaceDto.convertStringToMsg(request);
        System.out.println("mensagem: " + resquestMsg);
        String response = null;
            if(resquestMsg.contains("CREATE")) {
                ParkingSpace parkingSpace = ParkingSpaceDto.convertStringToParkingSpace(resquestMsg);
                Integer taxa = parking.createTaxa(parkingSpace.getHoursInit(), parkingSpace.getHours());
                String data = "MENSAGEM: carro retirado; valor a pagar = " +  parkingSpace.getValue() + " sua taxa = " + taxa;
                response = ParkingSpaceDto.generateResponseObj("CLIENT", "CREATE", data);
            }
            System.out.println("response " + response);

        return response;
    }

    @Override
    public void startServer() throws IOException {
        socket = new DatagramSocket(port);
        System.out.println("iniciando serviço Parking na porta:" + socket.getLocalPort());

        while (true){
            String requestData = receiveData();
            String response = this.generateResponseToSend(requestData);
            sendData(response, this.adrReceive, this.portReceive);
        }
    }
    @Override
    public void stopServer() {
        System.out.println("finalizando serviço na porta:" + socket.getLocalPort());
        socket.close();
    }

    public static void main(String[] args) {
        ServerUDP server = new ParkingUDP(8081);
        try
        {
            server.startServer();
            server.stopServer();

        }catch (IOException e){
            System.err.println("não foi possível conectar ao sevidor" + e.getStackTrace());
        }
    }
}
