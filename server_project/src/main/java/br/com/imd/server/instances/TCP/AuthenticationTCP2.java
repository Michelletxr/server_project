package br.com.imd.server.instances.TCP;

import br.com.imd.server.serverTCP.AuthenticationTCP;

public class AuthenticationTCP2 {
    public static void main(String[] args) {
        AuthenticationTCP server = new AuthenticationTCP(8086);
        try{
            server.startServer();
            server.stopServer();
        }catch (Exception e){
            System.err.println("não foi possível conectar ao sevidor" + e.getStackTrace());
        }
    }
}
