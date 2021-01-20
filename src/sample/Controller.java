package sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField serverIp;

    @FXML
    private Button startServer;

    @FXML
    private TextField serverPort;

    @FXML
    private Label serverInfo;

    private boolean isActivThread;

    @FXML
    void initialize() {
        isActivThread = false;
        startServer.setOnAction(event -> {
            try {
                serverInfo.setText(Inet4Address.getLocalHost().getHostAddress() + ":" + serverPort.getText());
            }catch (IOException e) {
                e.printStackTrace();
            }
            GaussServer gaussServer = new GaussServer(Integer.parseInt(serverPort.getText()));
            Thread thread = new Thread(gaussServer, "server");
            if (!isActivThread)
            {
                thread.start();
                isActivThread = true;
            }
            else {
                try {
                    Thread.sleep(100);

                    thread.interrupt();

                    Thread.sleep(100);
                }catch (InterruptedException e)
                {

                }
            }
        });
    }
}

class GaussServer implements Runnable {
    int port;

    public GaussServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                InputStream in = socket.getInputStream();

                byte[] dataByte = new byte[1024 * 64];
                int readBytes = in.read(dataByte);
                String dataRecv = "";

                if (readBytes > 0) {
                    dataRecv = new String(dataByte, 0, readBytes);
                }

                if (!dataRecv.equals("")) {
                    String[] data = dataRecv.split(";");


                    int position = 0;
                    for (int i = 0; i < data.length; i++) {
                        if (data[i].equals("mas"))
                            position = i;
                    }

                    double[] mas = Arrays.stream(data, 0, position).mapToDouble(Double::parseDouble).toArray();
                    int j = Integer.parseInt(data[data.length - 1]);
                    double[] x = Arrays.stream(data, position + 1, data.length - 1).mapToDouble(Double::parseDouble).toArray();

                    double sum = 0.0;
                    for (int i = j; i < mas.length; i++) {
                        sum += mas[i] * x[i];
                    }

                    OutputStream out = socket.getOutputStream();
                    String str = String.valueOf(sum);
                    out.write(str.getBytes());
                    out.flush();
                }

                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}