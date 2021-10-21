package server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private ServerSocket server;
    private Socket socket;
    private final int PORT = 8892;

    private List<ClientHandler> clients;
    private AuthService authService;

    public Server() {
        clients = new CopyOnWriteArrayList<>();
        if (!SQLHandler.connect()) {
            throw new RuntimeException("Не удалось подключиться к БД");
        }
        authService = new DBAuthServise();
        try {
            server = new ServerSocket(PORT);
//            System.out.println("Server started!");
            logger.info("Server started!");

            while (true) {
                socket = server.accept();
//                System.out.println("Client connected");
                logger.info("Client connected");
                new ClientHandler(socket, this);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            SQLHandler.disconnect();
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void broadcastMsg(ClientHandler sender, String msg) {
        String message = String.format("[ %s ]: %s", sender.getNickname(), msg);
        SQLHandler.addMessage(sender.getNickname(), "null", msg);
        for (ClientHandler c : clients) {
            c.sendMsg(message);
        }
    }
    public void privateMsg(ClientHandler sender, String receiver, String msg) throws IOException {
        String message = String.format("[ %s ] to [ %s ] : %s", sender.getNickname(), receiver, msg);

        for (ClientHandler c : clients) {
            if (c.getNickname().equals(receiver)) {
                c.sendMsg(message);
                //==============//
                SQLHandler.addMessage(sender.getNickname(), receiver, msg);
                file_history_name(sender).append(sender + " for " + receiver + msg);
                file_history_name(receiver).append(sender + " for " + receiver + msg);
                if (!c.equals(sender)) {
                    sender.sendMsg(message);
                }
                return;
            }
        }
        sender.sendMsg("Not found user: "+ receiver);
    }
    public boolean isLoginAuthenticated(String login) {
        for (ClientHandler c : clients) {
           if(c.getLogin().equals(login)){
               return true;
           }
        }
        return false;
    }

    public void broadcastClientList() {
        StringBuilder sb = new StringBuilder("/clientlist");
        for (ClientHandler c : clients) {
           sb.append(" ").append(c.getNickname());
        }

        String message = sb.toString();
        for (ClientHandler c : clients) {
            c.sendMsg(message);
        }
    }
    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
        broadcastClientList();
    }
    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientList();
    }
    public static StringBuffer file_history_name(ClientHandler login){
        StringBuffer file_name = new StringBuffer("history_" + login + ".txt");
        return file_name;
    }
    public static StringBuffer file_history_name(String login){
        StringBuffer file_name = new StringBuffer("history_" + login + ".txt");
        return file_name;
    }
    public AuthService getAuthService() {
        return authService;
    }

}
