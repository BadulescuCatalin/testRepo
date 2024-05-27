package ro.pub.cs.systems.eim.colocviu2btc.network;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ro.pub.cs.systems.eim.colocviu2btc.general.Constants;
import ro.pub.cs.systems.eim.colocviu2btc.general.Utilities;

public class ClientThread extends Thread {

    private final String address;
    private final int port;
    private final String ccy;
    private final String informationType;
    private final TextView resultTextView;

    private Socket socket;

    public ClientThread(String address, int port, String ccy, String informationType, TextView resultTextView) {
        this.address = address;
        this.port = port;
        this.ccy = ccy;
        this.informationType = informationType;
        this.resultTextView = resultTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            printWriter.println(ccy);
            printWriter.flush();
            printWriter.println(informationType);
            printWriter.flush();
            String ccyInformation;
            while ((ccyInformation = bufferedReader.readLine()) != null) {
                final String finalizedCcyInfo = ccyInformation;
                resultTextView.post(() -> resultTextView.setText(finalizedCcyInfo));
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                }
            }
        }
    }

}