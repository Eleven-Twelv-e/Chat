import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

public class Client {
    static final int PORT = Integer.parseInt(getSetting("port"));
    static final String HOST = getSetting("host");

    public Client() throws IOException {

        Socket clientSocket = new Socket(HOST, PORT);

        new Thread(new InMessageRunnable(clientSocket)).start();

        try (Scanner scanner = new Scanner(System.in);
             clientSocket;
             PrintWriter out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true)) {


            System.out.println("Как вас называть?");
            String name = scanner.nextLine();
            out.println(name);

            String msg;
            while (true) {
                msg = scanner.nextLine();
                if ("/exit".equals(msg)) {
                    out.println(msg);
                    break;
                }
                out.printf("%s: %s\n", name, msg);
                log(String.format("%s: %s", name, msg));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void log(String msg) {
        InMessageRunnable.log(msg);
    }

    private static String getSetting(String setting) {
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
        return settingProps.getProperty(setting);
    }

}