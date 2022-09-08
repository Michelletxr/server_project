package br.com.imd.server.serverUDP;

import br.com.imd.dto.ParkingSpaceDto;
import br.com.imd.model.ParkingSpace;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class ParkingUDP extends ServerUDP {

    private Integer port;
    private ArrayList<String> parking_spaces;


    public ParkingUDP(int port) {
        this.port = port;
        this.parking_spaces = new ArrayList<>();
    }

    @Override
    public void startServer() throws IOException {
        socket = new DatagramSocket(port);
        System.out.println("iniciando serviço Parking na porta:" + socket.getLocalPort());

        while (true){

            String requestData = receiveData();
            String msg = ParkingSpaceDto.convertStringToMsg(requestData);

            System.out.println("mensagem: " + msg);
            String id=ParkingSpaceDto.getIdToString(msg);
            String response = null;

            if(msg.contains("CREATE")){
                this.addNewParkingSpace(id);
                response = "carro estacionado com sucesso! id: " + id;
            }
            if(msg.contains("REMOVE")){
                if(verifyParkingSpace(id)){
                    sendData(msg.replace("REMOVE","FIND"), InetAddress.getByName("localhost"), 8082);
                    ParkingSpace data = ParkingSpaceDto.convertStringToParkingSpace(receiveData());
                    Integer taxa = calculateTaxa(data.getHoursInit(), data.getHours());
                    response = "carro retirado; valor a pagar: " +  data.getValue() + " sua taxa: " + taxa;
                }else{
                    response = "id invalido!";
                }
            }
            System.out.println("response " + response);
            sendData(response, InetAddress.getByName("localhost"), 8080);
        }
    }
    @Override
    public void stopServer() {
        System.out.println("finalizando serviço na porta:" + socket.getLocalPort());
        socket.close();
    }


    private void addNewParkingSpace(String idParkingSpace){
        parking_spaces.add(idParkingSpace);
        System.out.println("Lista de parking_spaces: ");
        for (String id: parking_spaces
             ) {
            System.out.println(id);
        }
    }

    private boolean verifyParkingSpace(String idParkingSpace){
        return parking_spaces.contains(idParkingSpace);
    }

    private static Integer calculateTaxa(Integer hours_init, Integer hours_current) {

        Integer taxa = 0;
        Integer hours_atual = ZonedDateTime.now().getHour();
        Integer total = Math.abs(hours_init - hours_atual);
        if(total < hours_current){
            taxa = (Math.abs(total - hours_current)* ParkingSpace.taxaPorHora) * -1;
        }else{
            taxa = Math.abs(total - hours_current)* ParkingSpace.multaPorHora;
        }
        return taxa;
    }

    public static void main(String[] args) {
        ServerUDP server = new ParkingUDP(8081);
        try
        {
            server.startServer();
            server.stopServer();

        }catch (IOException e){
            System.err.println("não foi possível conectar ao sevirdor" + e.getStackTrace());
        }
    }
}
