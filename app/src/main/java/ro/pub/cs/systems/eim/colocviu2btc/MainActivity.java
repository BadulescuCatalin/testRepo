package ro.pub.cs.systems.eim.colocviu2btc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ro.pub.cs.systems.eim.colocviu2btc.general.Constants;
import ro.pub.cs.systems.eim.colocviu2btc.network.ClientThread;
import ro.pub.cs.systems.eim.colocviu2btc.network.ServerThread;

public class MainActivity extends AppCompatActivity {

    private EditText serverPortEditText;
    private EditText clientAddressEditText;
    private EditText clientPortEditText;
    private Button connectButton;
    private EditText currencyEditText;
    private Spinner informationTypeSpinner;
    private Button getButton;
    private TextView resultTextView;
    private ServerThread serverThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverPortEditText = findViewById(R.id.server_port_edit_text);
        clientAddressEditText = findViewById(R.id.client_address_edit_text);
        clientPortEditText = findViewById(R.id.client_port_edit_text);
        connectButton = findViewById(R.id.connect_button);
        currencyEditText = findViewById(R.id.ccy_edit_text);
        informationTypeSpinner = findViewById(R.id.information_type_spinner);
        getButton = findViewById(R.id.get_ccy_info_button);
        resultTextView = findViewById(R.id.ccy_info_view);

        connectButton.setOnClickListener(view -> {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort.isEmpty()) {
                Log.d(Constants.TAG, "Server port is empty");
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        });

        getButton.setOnClickListener(view -> {
            String clientAddress = clientAddressEditText.getText().toString().trim();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String ccy = currencyEditText.getText().toString();
            String informationType = informationTypeSpinner.getSelectedItem().toString();
            if (ccy.isEmpty() || informationType.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }
            resultTextView.setText("");
            ClientThread clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), ccy, informationType, resultTextView);
            clientThread.start();
        });

    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}