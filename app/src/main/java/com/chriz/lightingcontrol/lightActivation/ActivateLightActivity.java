package com.chriz.lightingcontrol.lightActivation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.chriz.lightingcontrol.R;
import com.chriz.lightingcontrol.communication.CommunicationsFactory;
import com.chriz.lightingcontrol.communication.Communicator;

/**
 * Here we can activate a light. Do do so, we connect via WLAN to the lamps network.
 * Then we simply call this activity to activate it.
 * As we have full control over the network (because we programmed the firmware of the lamp
 * oneself) we know the IP and port. Hence it is static for now.
 *
 * The activation of the lamp consists of sending the SSID and PW of our router to the lamp.
 */
public class ActivateLightActivity extends AppCompatActivity {
    private static final String TAG = ActivateLightActivity.class.getSimpleName();
    private static final String LIGHTBULB_IP = "192.168.4.22";
    private static final int LIGHTBULB_PORT = 443;

    private TextView ssidText;
    private TextView passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_light);

        ssidText = findViewById(R.id.ssidTextField);
        passwordText = findViewById(R.id.passwordTextField);
        findViewById(R.id.activateButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Communicator communicator = CommunicationsFactory
                        .createReliableButSlowCommunicator(getBaseContext(), LIGHTBULB_IP,
                                                           LIGHTBULB_PORT, true);

                // We send the SSID and PW is send in the following form:
                // SSID=<ssid>\nPW=<pw>\n
                // The lamps firmware for know expects the SSID and PW to be on two separate lines
                // and a key-value pair separated by an equals sign. Keys are SSID and PW,
                // respectively.
                communicator.sendMesage("SSID=" + ssidText.getText()
                                      + "\nPW=" + passwordText.getText() + "\n");

                communicator.receiveAnswer(new Communicator.OnReceiveAnswerListener() {
                    @Override
                    public void onReceiveAnswer(String answer) {
                        if (answer.contains("OK")) {
                            Log.d(TAG, "MESSAGE SUCCESSFULLY SENT");
                        }
                    }
                });
                communicator.execute();
            }
        });
    }
}
