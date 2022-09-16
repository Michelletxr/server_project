package br.com.imd.server.instances.UDP;

import br.com.imd.server.serverUDP.AuthenticationUDP;

public class AuthenticationUDP2 {
    public static void main(String[] args) {
        AuthenticationUDP server = new AuthenticationUDP(8086);
        try{
            server.startServer();
            server.stopServer();
        }catch (Exception e){
            System.err.println("não foi possível conectar ao sevidor" + e.getStackTrace());
        }
    }
}
