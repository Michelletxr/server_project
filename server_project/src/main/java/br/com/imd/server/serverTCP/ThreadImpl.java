package br.com.imd.server.serverTCP;
import java.io.*;
import java.net.Socket;

public class ThreadImpl extends Thread{
        private Socket connetion;
        private ServerTCP server;


        public ThreadImpl(Socket connetion, ServerTCP server){
            System.out.println("new connetion thread");
            this.connetion = connetion;
            this.server = server;
        }


        @Override
        public void run()
        {

            System.out.println("helo thread");
            String msg = null;
            try {

                msg = server.receiveMsg(connetion.getInputStream());
                String response = server.generateResponseToSend(msg);
                server.sendMsg(response, connetion.getOutputStream());
                connetion.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
}
