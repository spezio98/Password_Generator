package com.andrea.passwordgenerator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private EditText txtPassword;
    private ImageButton btnRefresh;
    private SwitchCompat switchNumbers, switchSymbols;
    private SeekBar seekBar;
    private TextView txtLength;
    private ProgressBar prgBar;
    private int passwordLength;
    private Button btnCopy, btnPwnd;

    private final String upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String lowerCaseChars = "abcdefghijklmnopqrstuvwxyz";
    private final String numberChars = "0123456789";
    private final String specialChars = "!@#$%^&*()_-+=<>?/{}~|";

    private String allowedCharacters = upperCaseChars.concat(lowerCaseChars);
    private String currentPassword;

    private ClipboardManager myClipboard;
    private ClipData myClip;

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
                changePrgBarValue();
                //System.out.println(currentPassword);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentPassword = getRandomPassword(passwordLength);
                txtPassword.setText(currentPassword);
                changePrgBarValue();
                //System.out.println(calculatePasswordStrength(currentPassword));
            }
        });

        switchNumbers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(switchSymbols.isChecked())
                    allowedCharacters = upperCaseChars.concat(lowerCaseChars).concat(specialChars);
                else
                    allowedCharacters = upperCaseChars.concat(lowerCaseChars);

                if(b)
                    allowedCharacters = allowedCharacters.concat(numberChars);


                currentPassword = getRandomPassword(passwordLength);
                txtPassword.setText(currentPassword);
                changePrgBarValue();
            }
        });

        switchSymbols.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(switchNumbers.isChecked())
                    allowedCharacters = upperCaseChars.concat(lowerCaseChars).concat(numberChars);
                else
                    allowedCharacters = upperCaseChars.concat(lowerCaseChars);

                if(b)
                    allowedCharacters = allowedCharacters.concat(specialChars);

                currentPassword = getRandomPassword(passwordLength);
                txtPassword.setText(currentPassword);
                changePrgBarValue();
            }
        });

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                String text = txtPassword.getText().toString();

                myClip = ClipData.newPlainText("Password", text);
                myClipboard.setPrimaryClip(myClip);

                Toast.makeText(getApplicationContext(), "Password Copied",Toast.LENGTH_SHORT).show();
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
        changePrgBarValue();
        btnCopy = findViewById(R.id.btnCopy);
        btnPwnd = findViewById(R.id.btnPwnd);
    }

    private String getRandomPassword(final int sizeOfPasswordString){
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfPasswordString);

        for(int i=0;i<seekBar.getProgress();i++){
            sb.append(allowedCharacters.charAt(random.nextInt(allowedCharacters.length())));

        }
        return sb.toString();
    }

    private int calculatePasswordStrength(String password){

        int passwordScore = 0;

        if( password.length() < 8 )
            return 0;
        else if( password.length() >= 10 )
            passwordScore += 2;
        else
            passwordScore += 1;

        /*
         * if password contains 2 digits, add 2 to score.
         * if contains 1 digit add 1 to score
         */
        if( password.matches("(?=.*[0-9].*[0-9]).*") )
            passwordScore += 2;
        else if ( password.matches("(?=.*[0-9]).*") )
            passwordScore += 1;

        //if password contains 1 lower case letter, add 2 to score
        if( password.matches("(?=.*[a-z]).*") )
            passwordScore += 2;

        /*
         * if password contains 2 upper case letters, add 2 to score.
         * if contains only 1 then add 1 to score.
         */
        if( password.matches("(?=.*[A-Z].*[A-Z]).*") )
            passwordScore += 2;
        else if( password.matches("(?=.*[A-Z]).*") )
            passwordScore += 1;

        /*
         * if password contains 2 special characters, add 2 to score.
         * if contains only 1 special character then add 1 to score.
         */
        if( password.matches("(?=.*[~!@#$%^&*()_-].*[~!@#$%^&*()_-]).*") )
            passwordScore += 2;
        else if( password.matches("(?=.*[-!@#$%^&*()_+=<>?/{}~|]).*") )
            passwordScore += 1;

        return passwordScore;
    }

    private void changePrgBarValue(){
        int value = calculatePasswordStrength(currentPassword);
        if(value <=5)
            prgBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        else
            if(value >5 && value <8)
                prgBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#ff8c00")));
            else
                if(value >=8 && value <10)
                    prgBar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#d9ff66")));
                else
                    prgBar.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        prgBar.setProgress(value);
    }
}