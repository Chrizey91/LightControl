package com.chriz.lightingcontrol.lightBulb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chriz.lightingcontrol.R;
import com.chriz.lightingcontrol.communication.CommunicationsFactory;
import com.chriz.lightingcontrol.communication.Communicator;
import com.chriz.lightingcontrol.communication.DataPackageSenderContinuously;
import com.chriz.lightingcontrol.main.LightBulbListAdapter;

/**
 * Here we can change the color and brightness of our lamp.
 */
public class LightBulbActivity extends AppCompatActivity {
    TextView colorText;
    TextView ipText;
    DataPackageSenderContinuously dataPackageSenderContinuously;
    Communicator communicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_llight_bulb);

        colorText = findViewById(R.id.colorText);
        ipText = findViewById(R.id.ipText);

        Intent intent = getIntent();
        String ip = intent.getStringExtra(LightBulbListAdapter.IP_MESSAGE);
        ipText.setText(ip);

        SeekBar red = findViewById(R.id.colorPickerRed);
        SeekBar green = findViewById(R.id.colorPickerGreen);
        SeekBar blue = findViewById(R.id.colorPickerBlue);
        SeekBar brightnessPicker = findViewById(R.id.brightnessPicker);

        dataPackageSenderContinuously = new DataPackageSenderContinuously("rgb=0");

        red.setOnSeekBarChangeListener(new SeekBarChangedListener(0));

        green.setOnSeekBarChangeListener(new SeekBarChangedListener(1));

        blue.setOnSeekBarChangeListener(new SeekBarChangedListener(2));
    }

    class SeekBarChangedListener implements SeekBar.OnSeekBarChangeListener {
        private int colorPos;

        SeekBarChangedListener(int colorPos) {
            this.colorPos = colorPos;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            dataPackageSenderContinuously.setData("rgb=" + changeColor(i, colorPos));
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            communicator = CommunicationsFactory.createUnreliableButFastCommunicator(getBaseContext(), ipText.getText().toString(), 4210);
            communicator.sendMessageContinously(dataPackageSenderContinuously);
            dataPackageSenderContinuously.activate();
            communicator.execute();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            dataPackageSenderContinuously.deactivate();
        }

        private int changeColor(int color, int colorPos) {
            String[] colorT = colorText.getText().toString().split(",");
            StringBuilder newColorText = new StringBuilder();
            int bits = 0;
            int newColor = 0;
            for (int i = 0; i < 3; i++) {
                if (i == colorPos) {
                    newColorText.append(color);
                    newColor = newColor | (color<<bits);
                } else {
                    newColorText.append(colorT[i]);
                    newColor = newColor | (Integer.parseInt(colorT[i])<<bits);
                }
                if (i != 2) {
                    newColorText.append(",");
                }
                bits += 8;
            }
            colorText.setText(newColorText.toString());
            return newColor;
        }
    }
}
