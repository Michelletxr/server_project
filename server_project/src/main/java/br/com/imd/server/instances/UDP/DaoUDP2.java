package br.com.imd.server.instances.UDP;

import br.com.imd.server.serverUDP.DaoConnetionUDP;

public class DaoUDP2 {
    public static void main(String[] args) {
        DaoConnetionUDP server = new DaoConnetionUDP(8082);
        try{
            server.startServer();
            server.stopServer();
        }catch (Exception e){
            System.err.println("não foi possível conectar ao sevidor" + e.getStackTrace());
        }
    }
}
