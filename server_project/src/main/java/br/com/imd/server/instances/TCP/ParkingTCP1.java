package br.com.imd.server.instances.TCP;

import br.com.imd.server.serverTCP.ParkingTCP;

public class ParkingTCP1 {
    public static void main(String[] args) {
        ParkingTCP server = new ParkingTCP(8083);
        try{
            server.startServer();
            server.stopServer();
        }catch (Exception e){
            System.err.println("não foi possível conectar ao sevidor" + e.getStackTrace());
        }
    }
}
