import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

@SuppressWarnings("InfiniteLoopStatement")
public class Server {
    static final int PORT = getSetting();
    public static final ArrayList<RunnableClient> CLIENTS = new ArrayList<>();

    public Server() {
        Socket clientSocket = null;
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            log("Сервер запущен...");

            while (true) {
                clientSocket = serverSocket.accept();
                RunnableClient client = new RunnableClient(clientSocket);
                CLIENTS.add(client);

                new Thread(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {

                assert clientSocket != null;
                clientSocket.close();
                log("Сервер остановлен.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void log(String msg) {
        RunnableClient.log(msg);
    }

    private static int getSetting() {
        String rootPath;
        try {
            rootPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath();
        } catch (Exception e) {
            rootPath = "";
        }
        String settingsPath = rootPath + "settings.properties";

        Properties settingProps = new Properties();
        try {
            settingProps.load(new FileInputStream(settingsPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(settingProps.getProperty("port"));
    }

    public static void removeClient(RunnableClient client, ArrayList<RunnableClient> CLIENTS) {
        CLIENTS.remove(client);
    }
}