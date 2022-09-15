package br.com.imd.server.instances.UDP;

import br.com.imd.server.serverUDP.ParkingUDP;

public class ParkingUDP2 {
    public static void main(String[] args) {
        ParkingUDP server = new ParkingUDP(8084);
        try{
            server.startServer();
            server.stopServer();
        }catch (Exception e){
            System.err.println("não foi possível conectar ao sevidor" + e.getStackTrace());
        }
    }
}
