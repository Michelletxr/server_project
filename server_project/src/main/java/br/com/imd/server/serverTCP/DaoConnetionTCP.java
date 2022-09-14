package br.com.imd.server.serverTCP;

import br.com.imd.dao.ParkingSpaceDao;
import br.com.imd.dto.ParkingSpaceDto;
import br.com.imd.model.ParkingSpace;

import java.io.*;
import java.net.ServerSocket;


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

            //salvar no banco
            if(requestMsg.contains("SAVE")){
                ParkingSpace parkingSpace = ParkingSpaceDto.convertRequestToData(requestMsg);
                String id = this.save(parkingSpace);
                response =  ParkingSpaceDto.generateResponseObj("CLIENT", "SAVE",
                        "MENSAGEM: vaga de estacionamento gerada com sucesso id = "+id);
            }
            //remover vaga
            if(requestMsg.contains("REMOVE")){
                String id = ParkingSpaceDto.getIdToString(requestMsg);
                String parking_space = this.findById(id);
                System.out.println(id);
                if(dao.deleteParkingSpace(Integer.parseInt(id))){
                    response = ParkingSpaceDto.generateResponseObj("PARKING", "CREATE", parking_space.toString());
                }else{
                    response =  ParkingSpaceDto.generateResponseObj("CLIENT", "ERROR",
                            "MENSAGEM: vaga de estacionamento inválida ");
                }
            }

        return response;

    }

    public String save(ParkingSpace parkingSpace){
        String id = dao.saveParkingSpace(parkingSpace).toString();
        return id;
    }

    public String findById(String id){
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
