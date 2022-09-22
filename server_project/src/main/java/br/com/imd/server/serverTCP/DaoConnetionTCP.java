package br.com.imd.server.serverTCP;

import br.com.imd.dao.ParkingSpaceDao;
import br.com.imd.dto.ParkingSpaceDto;
import br.com.imd.factory.ConnetionFactory;
import br.com.imd.model.ObjRequest;
import br.com.imd.model.ParkingSpace;
import br.com.imd.model.ThreadImpl;

import java.io.*;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.Objects;


public class DaoConnetionTCP extends ServerTCP{
    private ServerSocket serverSocket;
    private ParkingSpaceDao dao;
    private int port;

    public DaoConnetionTCP(int port) {
        this.port = port;
        this.dao = new ParkingSpaceDao();
    }
    @Override
    public void startServer() throws IOException, ClassNotFoundException {
        this.serverSocket = new ServerSocket(this.port);
        System.out.println("iniciando serviço dao na porta:" + serverSocket.getLocalPort());
        while (true){

            new Thread(new ThreadImpl(serverSocket.accept(), this)).start();
        }
    }
    @Override
    public void stopServer() throws IOException {
        System.out.println("encerrando conexão...");
        serverSocket.close();
    }

    @Override
    public String generateResponseToSend(String requestMsg) {
        System.out.println("resquest " + requestMsg);
        String response = null;

        this.dao.setConnection(ConnetionFactory.getConnetion());

            //salvar no banco
            if(requestMsg.contains("SAVE")){
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
            //remover vaga
            if(requestMsg.contains("REMOVE")){
                try{
                    String id = ParkingSpaceDto.getIdToString(requestMsg);
                    System.out.println("id dao" + id);
                    String parking_space = this.findById(id);
                    if(Objects.isNull(parking_space)) throw new Exception();
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
            dao.closeConeetion();

        return response;

    }

    public String save(ParkingSpace parkingSpace) throws SQLException {
        String id = dao.saveParkingSpace(parkingSpace).toString();
        return id;
    }

    public String findById(String id) throws SQLException {
        ParkingSpace parkingSpace= dao.findParkingSpaceById(id);
        System.out.println("parking" + parkingSpace);
        return parkingSpace.toString();
    }

    public static void main(String[] args) {
        DaoConnetionTCP server = new DaoConnetionTCP(8082);
        try{
            server.startServer();
            server.stopServer();
        }catch (Exception e){}
    }
}
