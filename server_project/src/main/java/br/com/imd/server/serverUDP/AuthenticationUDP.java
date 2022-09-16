package br.com.imd.server.serverUDP;

import br.com.imd.dto.ParkingSpaceDto;
import br.com.imd.model.JWTImpl;
import br.com.imd.model.UserLogin;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Objects;

public class AuthenticationUDP extends ServerUDP{

    ArrayList<String> tokensDB;
    JWTImpl jwt;
    private ServerSocket serverSocket;
    int port;

    public AuthenticationUDP(int port) {
        this.port = port;
        this.tokensDB = new ArrayList<>();

    }

    private void updateTokenList(String token){
        tokensDB.add(token);
    }

    private boolean verifyToken(String token){
        return tokensDB.contains(token);
    }
    @Override
    public String generateResponseToSend(String requestMsg) throws UnknownHostException {

        String response = null;
        if(requestMsg.contains("CREATE")) {
            UserLogin user = ParkingSpaceDto.convertStringToUser(requestMsg);
            JWTImpl jwt = new JWTImpl();
            String token = jwt.generateJWTToken(user.getLogin(), user.getPassword());
            String data = null;
            String status = "CREATE";;
            if(verifyToken(token)){
                data = "MENSAGEM: usuário já possui cadastrado no sistema! token = " + token;
            }else if(!Objects.isNull(token)){
                updateTokenList(token);
                data = "MENSAGEM: novo usuário cadastrado no sistema! token = " + token;
            }else{
                data = "MENSAGEM: informações de login inválidos!";
                status = "ERROR";
            }

            response = ParkingSpaceDto.generateResponseObj("CLIENT", status, data);
        }


        if(requestMsg.contains("VALIDATE")) {
            String token = ParkingSpaceDto.getTokenToString(requestMsg);
            if(verifyToken(token)){
                response = ParkingSpaceDto.generateResponseObj("SERVER", "VALIDATE", token);
            }else{
                response = ParkingSpaceDto.generateResponseObj("CLIENT", "ERROR", "MENSAGEM: token inválido");
            }

        }
        return response;
    }

    @Override
    public void startServer() throws IOException {
        this.socket = new DatagramSocket(this.port);
        System.out.println("iniciando serviço load balancer na porta:" + socket.getLocalPort());

        while (true) {
            String requestMsg = receiveData();
            String response = generateResponseToSend(requestMsg);
            sendData(response, this.adrReceive, this.portReceive);
        }
    }

    @Override
    public void stopServer() {
        this.socket.close();
    }

    public static void main(String[] args) {
        AuthenticationUDP server = new AuthenticationUDP(8085);
        try{
            server.startServer();
            server.stopServer();
        }catch (Exception e){}
    }

}
