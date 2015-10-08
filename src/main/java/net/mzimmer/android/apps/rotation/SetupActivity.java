package net.mzimmer.android.apps.rotation;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.HashMap;
import java.util.Map;

public class SetupActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private static final HashMap<Integer, Integer> SENSOR_DELAY_RADIO_BUTTON_IDS;

    static {
        SENSOR_DELAY_RADIO_BUTTON_IDS = new HashMap<>();
        SENSOR_DELAY_RADIO_BUTTON_IDS.put(SensorManager.SENSOR_DELAY_FASTEST, R.id.sensor_delay_fastest);
        SENSOR_DELAY_RADIO_BUTTON_IDS.put(SensorManager.SENSOR_DELAY_GAME, R.id.sensor_delay_game);
        SENSOR_DELAY_RADIO_BUTTON_IDS.put(SensorManager.SENSOR_DELAY_NORMAL, R.id.sensor_delay_normal);
        SENSOR_DELAY_RADIO_BUTTON_IDS.put(SensorManager.SENSOR_DELAY_UI, R.id.sensor_delay_ui);
    }

    private Preferences preferences;
    private EditText destinationPortEditText;

    private static int getSensorDelayRadioButtonIdFromValue(int value) {
        if (SENSOR_DELAY_RADIO_BUTTON_IDS.containsKey(value)) {
            return SENSOR_DELAY_RADIO_BUTTON_IDS.get(value);
        } else {
            return SENSOR_DELAY_RADIO_BUTTON_IDS.get(Preferences.DEFAULT_SENSOR_DELAY);
        }
    }

    private static int getSensorDelayValueFromRadioButtonId(int radioButtonId) {
        for (Map.Entry<Integer, Integer> entry : SENSOR_DELAY_RADIO_BUTTON_IDS.entrySet()) {
            if (radioButtonId == entry.getValue()) {
                return entry.getKey();
            }
        }
        return Preferences.DEFAULT_SENSOR_DELAY;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SensorService.start(getApplicationContext());
                startActivityForResult(new Intent(getApplicationContext(), RunActivity.class), 0);
            }
        });

        preferences = new Preferences(getApplicationContext());
        RadioGroup sensorDelayRadioGroup = (RadioGroup) findViewById(R.id.sensor_delay);
        EditText destinationHostEditText = (EditText) findViewById(R.id.destination_host);
        destinationPortEditText = (EditText) findViewById(R.id.destination_port);

        sensorDelayRadioGroup.setOnCheckedChangeListener(this);
        destinationHostEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                preferences.setDestinationHost(s.toString());
            }
        });
        destinationPortEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int destinationPort = Integer.parseInt(s.toString());
                    preferences.setDestinationPort(destinationPort);
                    destinationPortEditText.setError(null);
                } catch (NumberFormatException e) {
                    destinationPortEditText.setError(getString(R.string.unable_to_parse_integer));
                }
            }
        });

        int sensorDelay = preferences.getSensorDelay();
        String destinationHost = preferences.getDestinationHost();
        int destinationPort = preferences.getDestinationPort();

        ((RadioButton) findViewById(getSensorDelayRadioButtonIdFromValue(sensorDelay))).setChecked(true);
        destinationHostEditText.setText(destinationHost);
        destinationPortEditText.setText(String.valueOf(destinationPort));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()) {
            case R.id.sensor_delay:
                preferences.setSensorDelay(getSensorDelayValueFromRadioButtonId(checkedId));
                break;
            default:
                throw new IllegalStateException();
        }
    }
}
