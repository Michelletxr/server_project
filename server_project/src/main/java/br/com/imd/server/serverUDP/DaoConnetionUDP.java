package br.com.imd.server.serverUDP;

import br.com.imd.dao.ParkingSpaceDao;
import br.com.imd.dto.ParkingSpaceDto;
import br.com.imd.factory.ConnetionFactory;
import br.com.imd.model.ParkingSpace;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;

public class DaoConnetionUDP extends ServerUDP {
    private ParkingSpaceDao dao;

    private Connection connection;
    private int port;

    public DaoConnetionUDP(int port) {
        this.port = port;
    }

    public String save(ParkingSpace parkingSpace){
       String id = null;
        try{
            this.connection = ConnetionFactory.getConnetion();
            this.dao = new ParkingSpaceDao(ConnetionFactory.getConnetion());
            id = dao.saveParkingSpace(parkingSpace).toString();
        }catch (SQLException e){
            System.out.println(e.getStackTrace());
        }
      return id;
    }

    public String findById(String id){

        ParkingSpace parkingSpace= dao.findParkingSpaceById(id);
        System.out.println("parking" + parkingSpace);
        return parkingSpace.toString();

    }

    public void delete(){
      //dao.deleteParkingSpace();
    }


    @Override
    public void startServer() throws IOException {
        socket = new DatagramSocket(port);
        System.out.println("iniciando serviço dao na porta:" + socket.getLocalPort());

        while (true){
            String requestData = receiveData();
            String msg = ParkingSpaceDto.convertStringToMsg(requestData);
            String responseData = null;
            System.out.println("MENSAGEM:" + msg );

            if(msg.contains("SAVE")){
                ParkingSpace parkingSpace = ParkingSpaceDto.convertRequestToData(requestData);
                String id = this.save(parkingSpace);
                responseData = id;
                sendData(responseData, InetAddress.getByName("localhost"), 8080);
            }
            if(msg.contains("FIND")){
                String id = ParkingSpaceDto.getIdToString(msg);
                String parking_space = this.findById(id);
                responseData = parking_space;
                sendData(responseData, InetAddress.getByName("localhost"), 8081);
            }

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
