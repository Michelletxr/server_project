package br.com.imd.server.serverUDP;

import br.com.imd.dao.ParkingSpaceDao;
import br.com.imd.dto.ParkingSpaceDto;
import br.com.imd.factory.ConnetionFactory;
import br.com.imd.model.ParkingSpace;

import java.io.IOException;
import java.net.DatagramSocket;
import java.sql.Connection;
import java.sql.SQLException;

public class DaoConnetionUDP extends ServerUDP {
    private ParkingSpaceDao dao;
    private int port;

    public DaoConnetionUDP(int port) {
        this.port = port;
    }

    public String save(ParkingSpace parkingSpace){
       String id = null;
        this.dao = new ParkingSpaceDao();
        id = dao.saveParkingSpace(parkingSpace).toString();
        return id;
    }

    public String findById(String id){
        ParkingSpace parkingSpace= dao.findParkingSpaceById(id);
        System.out.println("parking" + parkingSpace);
        return parkingSpace.toString();
    }


    @Override
    public void generateResponseToSend(String requestMsg) {
        String response = null;



        try {
            String msg = ParkingSpaceDto.convertStringToMsg(requestMsg);
            String data = null;
            System.out.println("MENSAGEM:" + msg );

            if(msg.contains("SAVE")){
                ParkingSpace parkingSpace = ParkingSpaceDto.convertRequestToData(requestMsg);
                String id = this.save(parkingSpace);
                response =  ParkingSpaceDto.generateResponseObj("CLIENT", "SAVE",
                        "MENSAGEM: vaga de estacionamento gerada com sucesso id = "+id);
            }

            if(requestMsg.contains("REMOVE")){
                String id = ParkingSpaceDto.getIdToString(requestMsg);
                String parking_space = this.findById(id);
                if(dao.deleteParkingSpace(Integer.parseInt(id))){
                    response = ParkingSpaceDto.generateResponseObj("PARKING", "CREATE", parking_space.toString());
                }else{
                    response =  ParkingSpaceDto.generateResponseObj("CLIENT", "ERROR",
                            "MENSAGEM: vaga de estacionamento inválida ");
                }
            }

            System.out.println(response);
            sendData(response, this.adrReceive, this.portReceive);

        }catch (IOException e){
            System.out.println(e);
        }
    }

    @Override
    public void startServer() throws IOException {
        socket = new DatagramSocket(port);
        System.out.println("iniciando serviço dao na porta:" + socket.getLocalPort());

        while (true) {
            String requestData = receiveData();
            this.generateResponseToSend(requestData);
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
