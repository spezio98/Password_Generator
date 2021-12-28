package com.andrea.passwordgenerator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Stream;

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
    private Integer ntimes; //number of times pwned
    private boolean manual = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                //System.out.println(progress);
                if(!manual) {
                    passwordLength = progress;
                    txtLength.setText(String.valueOf(passwordLength));
                    currentPassword = getRandomPassword(passwordLength);
                    txtPassword.setText(currentPassword);
                    changePrgBarValue();
                }
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

        btnPwnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Get password digest
                String digest = SHA1(currentPassword);
                System.out.println(digest);

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url ="https://api.pwnedpasswords.com/range/" + digest.substring(0,5); //It requires first 5 chars
                System.out.println(url);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.

                                if(response.contains(digest.substring(5))) //remaining chars after the 5th.
                                {
                                    String[] lines = response.split("\n");

                                    for(String l: lines) {
                                        String[] values = l.replace("\r","").split(":");
                                        if(values[0].equals(digest.substring(5)))
                                        {
                                            ntimes = Integer.parseInt(values[1]);
                                            break;
                                        }
                                    }

                                }
                                else
                                    ntimes=0;

                                //show alert
                                System.out.println(ntimes);
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                if(ntimes>0) {
                                    builder.setMessage("Your password has been pwned " + ntimes + " times!");
                                }
                                else{
                                    builder.setMessage("Perfect!\nYour password has not been pwned!");
                                }
                                builder.setPositiveButton("OK", null);
                                builder.show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error");
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);


            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                manual = true;
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                currentPassword = charSequence.toString();
                changePrgBarValue();
                passwordLength = currentPassword.length();
                txtLength.setText(String.valueOf(passwordLength));
                if(passwordLength>=8)
                    seekBar.setProgress(passwordLength);

            }

            @Override
            public void afterTextChanged(Editable editable) {
                manual = false;
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
        ntimes = 0;
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

    private String SHA1(String input)
    {
        try {
            // getInstance() method is called with algorithm SHA-1
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext.toUpperCase(Locale.ROOT);
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}