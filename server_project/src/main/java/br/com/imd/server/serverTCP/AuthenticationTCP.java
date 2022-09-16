package br.com.imd.server.serverTCP;

import br.com.imd.dto.ParkingSpaceDto;
import br.com.imd.model.JWTImpl;
import br.com.imd.model.UserLogin;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Objects;

public class AuthenticationTCP extends ServerTCP {
    ArrayList<String> tokensDB;
    JWTImpl jwt;
    ServerSocket socket;
    int port;

    public AuthenticationTCP(int port) {
        this.tokensDB = new ArrayList<>();
        this.port = port;
    }

    @Override
    public void startServer() throws IOException, ClassNotFoundException {
        this.socket = new ServerSocket(this.port);
        System.out.println("iniciando serviço de atutenticação na porta:" + socket.getLocalPort());
        while (true){

            new Thread(new ThreadImpl(socket.accept(), this)).start();
        }

    }

    @Override
    public void stopServer() throws IOException {
        System.out.println("encerrando conexão...");
        socket.close();
    }

    private void updateTokenList(String token){
        tokensDB.add(token);
    }

    private boolean verifyToken(String token){
        return tokensDB.contains(token);
    }

    @Override
    public String generateResponseToSend(String requestMsg) {

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

    public static void main(String[] args) {

    }
}
