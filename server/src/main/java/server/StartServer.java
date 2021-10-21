package server;
import java.util.logging.Logger;

public class StartServer {
    private static final Logger logger = Logger.getLogger(StartServer.class.getName());
    public static void main(String[] args) {
        new Server();
    }
}
