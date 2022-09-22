package br.com.imd.server.instances.Http;

import br.com.imd.server.serverHTTP.ParkingHttp;


public class ParkingHttp1 {
    public static void main(String[] args) {
        ParkingHttp server = new ParkingHttp(8083);
        try{
            server.startServer();
            server.stopServer();
        }catch (Exception e){
            System.err.println("não foi possível conectar ao sevidor" + e.getStackTrace());
        }
    }
}
