package br.com.imd.server.serverUDP;

import br.com.imd.dao.ParkingSpaceDao;
import br.com.imd.dto.ParkingSpaceDto;
import br.com.imd.factory.ConnetionFactory;
import br.com.imd.model.ParkingSpace;

import java.io.IOException;
import java.net.DatagramSocket;
import java.sql.SQLException;
import java.util.Objects;

public class DaoConnetionUDP extends ServerUDP {
    private ParkingSpaceDao dao;
    private int port;

    public DaoConnetionUDP(int port) {
        this.port = port;
        this.dao = new ParkingSpaceDao();
    }

    public String save(ParkingSpace parkingSpace) throws SQLException {
        String id = null;
        id = dao.saveParkingSpace(parkingSpace).toString();
        return id;
    }

    public String findById(String id) throws SQLException {
        ParkingSpace parkingSpace= dao.findParkingSpaceById(id);
        System.out.println("parking" + parkingSpace);
        return parkingSpace.toString();
    }


    @Override
    public String generateResponseToSend(String requestMsg) {
        String response = null;

            String msg = ParkingSpaceDto.convertStringToMsg(requestMsg);
            String data = null;
            System.out.println("MENSAGEM:" + msg );
            this.dao.setConnection(ConnetionFactory.getConnetion());

            if(msg.contains("SAVE")){
                try{
                    ParkingSpace parkingSpace = ParkingSpaceDto.convertRequestToParkingSpace(requestMsg);
                    String id = this.save(parkingSpace);
                    response =  ParkingSpaceDto.generateResponseObj("CLIENT", "SAVE",
                            "MENSAGEM: vaga de estacionamento gerada com sucesso id = "+id);
                }catch (SQLException e){
                    response =  ParkingSpaceDto.generateResponseObj("CLIENT", "ERROR",
                            "MENSAGEM: não foi possível gerar a vaga de estacionamento.");
                }

            }


            if(requestMsg.contains("REMOVE")){
                try{
                    String id = ParkingSpaceDto.getIdToString(requestMsg);
                    String parking_space = this.findById(id);
                    if(Objects.isNull(parking_space)) throw  new Exception();
                    if(dao.deleteParkingSpace(Integer.parseInt(id))){
                        response = ParkingSpaceDto.generateResponseObj("PARKING", "CREATE", parking_space.toString());
                    }else{
                        response =  ParkingSpaceDto.generateResponseObj("CLIENT", "ERROR",
                                "MENSAGEM: não foi possível remover a vaga ");
                    }
                }catch (Exception e){
                    response =  ParkingSpaceDto.generateResponseObj("CLIENT", "ERROR",
                            "MENSAGEM: vaga de estacionamento inválida.");
                }
            }

            System.out.println(response);
            this.dao.closeConeetion();
            //sendData(response, this.adrReceive, this.portReceive);
        return response;
    }

    @Override
    public void startServer() throws IOException {
        socket = new DatagramSocket(port);
        System.out.println("iniciando serviço dao na porta:" + socket.getLocalPort());

        while (true) {
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

    public static void main(String[] args)
    {
        DaoConnetionUDP server = new DaoConnetionUDP(8082);
        try
        {
            server.startServer();
            server.stopServer();

        }catch (IOException e){
            System.err.println("não foi possível conctar ao sevirdor" + e.getStackTrace());
        }
    }
}
