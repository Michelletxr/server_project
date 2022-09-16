package br.com.imd.server;

import br.com.imd.dto.ParkingSpaceDto;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class TesteSever {

    private static final int BUFFER_SIZE = 1024;
    private static Selector selector = null;
    private InetAddress hostIP;
    int port = 8080;
    private Integer parkingPort = 8081;
    ServerSocketChannel mySocket;

    private List<Integer> parkingsServers;
    private List<Integer> daoServers;

    public TesteSever(int port) {
        this.port = port;
        parkingsServers = new ArrayList<>();
        parkingsServers.add(8081);
        daoServers = new ArrayList<>();
        daoServers.add(8082);

    }

    public void startServer() throws IOException {

        selector = Selector.open();
        mySocket = ServerSocketChannel.open();
        mySocket.socket().bind(new InetSocketAddress(port));
        mySocket.configureBlocking(false);
        mySocket.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> i = selectedKeys.iterator();
            while (i.hasNext()) {
                SelectionKey key = i.next();
                if (key.isAcceptable()) {
                    processAcceptEvent(mySocket, key);
                } else if (key.isReadable()) {
                    processReadEvent(key);
                    processSendEvent(key);
                }
                i.remove();
            }
        }

    }

    private void processReadEvent(SelectionKey key) throws IOException {

        SocketChannel myClient = (SocketChannel) key.channel();
        ByteBuffer myBuffer = ByteBuffer.allocate(BUFFER_SIZE);
        myClient.read(myBuffer);
        String data = new String(myBuffer.array()).trim();
        if (data.length() > 0) {
            //System.out.println(String.format("Message Received.....: %s\n", data));
            String target = ParkingSpaceDto.getTargetToString(data);
            System.out.println("request " + data);
            System.out.println("TARGET: " + target);
            try{
                switch (target){
                    case "BD":
                        InetSocketAddress adrrDao = new InetSocketAddress("localhost", daoServers.get(0));
                        SocketChannel dao = SocketChannel.open(adrrDao);
                        break;
                    case "PARKING":
                        InetSocketAddress adrrParking = new InetSocketAddress("localhost", parkingsServers.get(0));
                        SocketChannel parking = SocketChannel.open(adrrParking);
                        break;
                    default:
                        break;
                }
            }catch (IOException e){
                System.out.println(e);
            }
        }
    }

    private void processSendEvent(SelectionKey key) throws IOException {
            SocketChannel myclient = (SocketChannel) key.channel();
            String msg = "ok";
            ByteBuffer myBuffer=ByteBuffer.allocate(BUFFER_SIZE);
            myBuffer.put(msg.getBytes());
            myBuffer.flip();
            myclient.write(myBuffer);
            myclient.close();

        }



    private void processAcceptEvent(ServerSocketChannel mySocket, SelectionKey key) throws IOException {
        SocketChannel myClient = mySocket.accept();
        myClient.configureBlocking(false);
        myClient.register(selector, SelectionKey.OP_READ);
    }


    public static void main(String[] args) {
        TesteSever testeSever = new TesteSever(8080);
        try {
            testeSever.startServer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
