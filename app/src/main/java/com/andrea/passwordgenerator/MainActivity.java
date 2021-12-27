package com.andrea.passwordgenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private EditText txtPassword;
    private ImageButton btnRefresh;
    private SwitchCompat switchNumbers, switchSymbols;
    private SeekBar seekBar;
    private TextView txtLength;
    private ProgressBar prgBar;
    private int passwordLength;
    private String allowedCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private String currentPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                //System.out.println(progress);
                passwordLength = progress;
                txtLength.setText(String.valueOf(passwordLength));
                currentPassword = getRandomPassword(passwordLength);
                txtPassword.setText(currentPassword);
                //System.out.println(currentPassword);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //System.out.println(seekBar);
        //txtLength.setText(seekBar.getProgress());
    }

    private void init(){
        passwordLength = 8;
        txtPassword = findViewById(R.id.edtPassword);
        btnRefresh = findViewById(R.id.imgBtnRefresh);
        switchNumbers = findViewById(R.id.switchNumbers);
        switchSymbols = findViewById(R.id.switchSymbols);
        seekBar = findViewById(R.id.seekBar);
        txtLength = findViewById(R.id.txtLength);
        txtLength.setText(String.valueOf(seekBar.getProgress()));
        prgBar = findViewById(R.id.progressBar);
        currentPassword = getRandomPassword(passwordLength);
        txtPassword.setText(currentPassword);
    }

    private String getRandomPassword(final int sizeOfPasswordString){
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfPasswordString);

        for(int i=0;i<seekBar.getProgress();i++){
            sb.append(allowedCharacters.charAt(random.nextInt(allowedCharacters.length())));

        }
        return sb.toString();
    }
}