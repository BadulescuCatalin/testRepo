package ro.pub.cs.systems.eim.colocviu2btc.network;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;
import ro.pub.cs.systems.eim.colocviu2btc.Model.CurrencyInfo;
import ro.pub.cs.systems.eim.colocviu2btc.general.Constants;
import ro.pub.cs.systems.eim.colocviu2btc.general.Utilities;

public class CommunicationThread extends Thread {

    private final ServerThread serverThread;
    private final Socket socket;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (ccy / information type!");
            String ccy = bufferedReader.readLine();
            String informationType = bufferedReader.readLine();
            if (ccy == null || ccy.isEmpty() || informationType == null || informationType.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (ccy / information type!");
                return;
            }

            HashMap<String, CurrencyInfo> data = serverThread.getData();
            CurrencyInfo currencyInfo;
            if (data.containsKey(ccy)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                currencyInfo = data.get(ccy);
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
                HttpClient httpClient = new DefaultHttpClient();
                String pageSourceCode = "";
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS + ccy);
                HttpResponse httpGetResponse = httpClient.execute(httpGet);
                HttpEntity httpGetEntity = httpGetResponse.getEntity();
                if (httpGetEntity != null) {
                    pageSourceCode = EntityUtils.toString(httpGetEntity);
                }

                JSONObject content = new JSONObject(pageSourceCode);

                JSONObject main = content.getJSONObject(Constants.MAIN);
                JSONObject subMain = main.getJSONObject(ccy);
                String code = subMain.getString(Constants.CODE);
                String rate = subMain.getString(Constants.RATE);
                String description = subMain.getString(Constants.DESCRIPTION);
                String rate_float = subMain.getString(Constants.RATE_FLOAT);

                currencyInfo = new CurrencyInfo(code, rate, description, rate_float);

                serverThread.setData(ccy, currencyInfo);
            }
            if (currencyInfo == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Currency Information is null!");
                return;
            }
            String result;
            switch(informationType) {
                case Constants.ALL:
                    result = currencyInfo.toString();
                    break;
                case Constants.CODE:
                    result = currencyInfo.getCode();
                    break;
                case Constants.RATE:
                    result = currencyInfo.getRate();
                    break;
                case Constants.DESCRIPTION:
                    result = currencyInfo.getDescription();
                    break;
                case Constants.RATE_FLOAT:
                    result = currencyInfo.getRateFloat();
                    break;
                default:
                    result = "[COMMUNICATION THREAD] Wrong information type (all / code / rate / description / rate_float)!";
            }
            printWriter.println(result);
            printWriter.flush();
        } catch (IOException | JSONException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException ioException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            }
        }
    }

}