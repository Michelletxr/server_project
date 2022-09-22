package br.com.imd.server.instances.Http;
import br.com.imd.server.serverHTTP.DaoConnetionHttp;

public class DaoHttp1 {
    public static void main(String[] args) {
        DaoConnetionHttp server = new DaoConnetionHttp(8081);
        try{
            server.startServer();
            server.stopServer();
        }catch (Exception e){
            System.err.println("não foi possível conectar ao sevidor" + e.getStackTrace());
        }
    }
}
