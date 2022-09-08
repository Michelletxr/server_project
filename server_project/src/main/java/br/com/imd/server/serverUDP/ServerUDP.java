package br.com.imd.server.serverUDP;

import br.com.imd.dao.ParkingSpaceDao;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public abstract class ServerUDP {


    protected DatagramSocket socket;

    public String receiveData()  throws IOException {
        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        socket.receive(receivePacket);

        System.out.println("cliente conectado na porta: " + receivePacket.getPort());
        String dataRequest = new String(receivePacket.getData());
        return dataRequest;
    }

    public void sendData(String msg, InetAddress address, Integer port ) throws IOException{
        byte[] sendMsg = msg.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendMsg,sendMsg.length, address, port);
        socket.send(sendPacket);
    }

    public abstract  void startServer() throws IOException;

    public abstract void stopServer();
}
