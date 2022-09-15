package br.com.imd.server.instances.TCP;

import br.com.imd.server.serverTCP.DaoConnetionTCP;

public class DaoTCP2 {

    public static void main(String[] args) {
        DaoConnetionTCP server = new DaoConnetionTCP(8082);
        try{
            server.startServer();
            server.stopServer();
        }catch (Exception e){
            System.err.println("não foi possível conectar ao sevidor" + e.getStackTrace());
        }
    }

}
