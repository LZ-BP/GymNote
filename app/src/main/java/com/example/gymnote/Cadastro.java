package com.example.gymnote;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Cadastro extends AppCompatActivity {
    Button btCadastra, btLoginCadastra;
    EditText usuarioCadastra, senhaCadastra, emailCadastra;
    Connection con = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    String sql;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btCadastra = findViewById(R.id.btCadastra);
        btLoginCadastra = findViewById(R.id.btLoginCadastra);
        usuarioCadastra = findViewById(R.id.usuarioCadastra);
        senhaCadastra = findViewById(R.id.senhaCadastra);
        emailCadastra = findViewById(R.id.emailCadastra);

        btLoginCadastra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login = new Intent(Cadastro.this,Login.class);
                startActivity(login);
                finish();
            }
        });
        btCadastra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    con = ConexaoMySQL.conectar();
                    sql = "INSERT INTO usuario (nome, email, senha) VALUES (?, ?, ?)";
                    stmt = con.prepareStatement(sql);
                    stmt.setString(1, usuarioCadastra.getText().toString());
                    stmt.setString(2, emailCadastra.getText().toString());
                    stmt.setString(3, senhaCadastra.getText().toString());
                    stmt.executeUpdate();

                    // Fecha a conexão com o banco imediatamente após a execução
                    stmt.close();
                    con.close();

                    AlertDialog.Builder mensagem = new AlertDialog.Builder(Cadastro.this);
                    mensagem.setTitle("Cadastro");
                    mensagem.setMessage("Usuário cadastrado com sucesso!");

                    // Passa a ação a ser executada ao clicar no botão "OK"
                    mensagem.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            usuarioCadastra.setText("");
                            senhaCadastra.setText("");
                            emailCadastra.setText("");

                            Intent login = new Intent(Cadastro.this, Login.class);
                            startActivity(login);
                            finish();
                        }
                    });

                    mensagem.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}