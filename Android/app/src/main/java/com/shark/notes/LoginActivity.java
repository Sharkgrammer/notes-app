package com.shark.notes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final TextView txtEmail = findViewById(R.id.logEmail);
        final TextView txtPassword = findViewById(R.id.logPass);
        Button login = findViewById(R.id.logLog);
        Button register = findViewById(R.id.logReg);
        final Database database = new Database(this);
        login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                if (txtEmail.getText().toString().equals("") || txtPassword.getText().toString().equals("")){
                    txtEmail.setText("");
                    txtPassword.setText("");
                    Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_SHORT).show();
                }else{
                    String Temp = database.login(txtEmail.getText().toString(), txtPassword.getText().toString());
                    if(Temp.equals("")){
                        txtEmail.setText("");
                        txtPassword.setText("");
                        Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_SHORT).show();
                    }else{
                        SharedPreferences prefs = LoginActivity.this.getSharedPreferences("com.shark.notes", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("key", Temp);
                        editor.apply();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                if (txtEmail.getText().toString().equals("") || txtPassword.getText().toString().equals("")){
                    txtEmail.setText("");
                    txtPassword.setText("");
                    Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_SHORT).show();
                }else{
                    boolean Temp = database.register(txtEmail.getText().toString(), txtPassword.getText().toString());
                    if(!Temp){
                        txtEmail.setText("");
                        txtPassword.setText("");
                        Toast.makeText(getApplicationContext(),"Error", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"Account registered, please login!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
