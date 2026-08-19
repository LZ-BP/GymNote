package com.example.gymnote;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;



public class Login extends AppCompatActivity {
    Button btLogin, btCadastraLogin;
    EditText usuarioLogin, senhaLogin;

    Connection con = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usuarioLogin = findViewById(R.id.usuarioLogin);
        senhaLogin = findViewById(R.id.senhaLogin);
        btLogin = findViewById(R.id.btLogin);
        btCadastraLogin = findViewById(R.id.btCadastroLogin);




        btCadastraLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cadastro = new Intent(Login.this,Cadastro.class);
                startActivity(cadastro);
                finish();
            }
        });


        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    con = ConexaoMySQL.conectar();

                    sql = "SELECT * FROM usuario WHERE nome = ? AND senha = ?;";

                    stmt = con.prepareStatement(sql);

                    stmt.setString(1, usuarioLogin.getText().toString());
                    stmt.setString(2, senhaLogin.getText().toString());

                    rs = stmt.executeQuery();

                    if (rs.next()) {

                        Intent menu = new Intent(Login.this, MainActivity.class);
                        startActivity(menu);
                        finish();

                    } else {

                        new AlertDialog.Builder(Login.this)
                                .setTitle("Erro no login")
                                .setMessage("Verifique o e-mail e a senha!")
                                .show();
                    }

                    rs.close();
                    stmt.close();
                    con.close();

                } catch (SQLException e) {

                }
            }
        });
    }
}