package br.com.imd.server.serverHTTP;

import br.com.imd.dao.ParkingSpaceDao;
import br.com.imd.dto.ParkingSpaceDto;
import br.com.imd.factory.ConnetionFactory;
import br.com.imd.model.ObjRequest;
import br.com.imd.model.ParkingSpace;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class DaoConnetionHttp extends ServerHttp{
    private ServerSocket serverSocket;
    private Integer port;
    private Socket clientSocket;
    private ParkingSpaceDao dao;
    public DaoConnetionHttp(Integer port) {
        this.port = port;
        this.dao = new ParkingSpaceDao();
    }

    @Override
    public void startServer() throws IOException {
        this.serverSocket = new ServerSocket(this.port);
        System.out.println("iniciando serviço dao na porta:" + serverSocket.getLocalPort());

        while (true){
            new Thread(new ServerClient(serverSocket.accept(), this)).start();
        }
    }

    @Override
    public void stopServer() throws IOException
    {
        System.out.println("encerrando conexão...");
        serverSocket.close();
    }

    @Override
    public void generateDataToSend(ObjRequest request, Socket socket)
    {
        this.dao.setConnection(ConnetionFactory.getConnetion());
        String response = "default";

        //salvar no banco
        if(request.getRequestMethod().contains("POST")){
            try{
                ParkingSpace parkingSpace = ParkingSpaceDto.convertRequestToParkingSpace(request.getRequestBody());
                String id = this.save(parkingSpace);
                //generate response sucess
                sendResponse(socket, "id:" + id, 200);
            }catch (SQLException e){
                //generate response error
                String responseMsg = "MENSAGEM: não foi possível gerar a vaga de estacionamento.";
                sendResponse(socket, responseMsg , 500);
            }

        }

        //remover vaga
        //generate request
        if(request.getRequestMethod().contains("DELETE")) {
            try{
                String id = ParkingSpaceDto.getIdToString(request.getRequestBody());
                System.out.println("id dao " + id);
                String parking_space = this.findById(id);
               if(dao.deleteParkingSpace(Integer.parseInt(id))){
                    //generate request sucess
                  sendRequest(socket, "POST", "/parking", parking_space.toString());
                }else{
                    String responseMsg = "MENSAGEM: não foi possível remover a vaga ";
                    sendResponse(socket, responseMsg , 404 );
                }
            }catch (Exception e){
                String responseMsg = "MENSAGEM: erro ao tentar remover a vaga ";
                sendResponse(socket, responseMsg , 405 );
            }
        }
        dao.closeConeetion();
    }

    public String save(ParkingSpace parkingSpace) throws SQLException {
        String id = dao.saveParkingSpace(parkingSpace).toString();
        return id;
    }

    public String findById(String id) throws SQLException
    {
        ParkingSpace parkingSpace= dao.findParkingSpaceById(id);
        System.out.println("parking" + parkingSpace);
        return parkingSpace.toString();
    }


    public static void main(String[] args) {
        DaoConnetionHttp server = new DaoConnetionHttp(8081);
        try{
            server.startServer();
            server.stopServer();
        }catch (IOException e){
            System.out.println("erro ao iniciar serviço");
        }
    }
}
