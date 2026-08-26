package com.example.gymnote;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login extends AppCompatActivity {

    Button btLogin, btCadastraLogin;
    EditText usuarioLogin, senhaLogin;

    Connection con;
    PreparedStatement stmt;
    ResultSet rs;

    Sessao sessao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        sessao = new Sessao(this);

        if (sessao.getIdUsuario() != -1) {

            Intent intent =
                    new Intent(
                            Login.this,
                            MainActivity.class
                    );

            startActivity(intent);
            finish();

            return;
        }

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_login
        );

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        usuarioLogin =
                findViewById(
                        R.id.usuarioLogin
                );

        senhaLogin =
                findViewById(
                        R.id.senhaLogin
                );

        btLogin =
                findViewById(
                        R.id.btLogin
                );

        btCadastraLogin =
                findViewById(
                        R.id.btCadastroLogin
                );

        btCadastraLogin.setOnClickListener(
                view -> {

                    Intent cadastro =
                            new Intent(
                                    Login.this,
                                    Cadastro.class
                            );

                    startActivity(cadastro);
                    finish();
                }
        );

        btLogin.setOnClickListener(
                view -> fazerLogin()
        );
    }

    private void fazerLogin() {

        try {

            con =
                    ConexaoMySQL.conectar();

            String sql =
                    "SELECT id_usuario, nome, email " +
                            "FROM usuario " +
                            "WHERE email = ? " +
                            "AND senha = ?";

            stmt =
                    con.prepareStatement(sql);

            stmt.setString(
                    1,
                    usuarioLogin
                            .getText()
                            .toString()
                            .trim()
            );

            stmt.setString(
                    2,
                    senhaLogin
                            .getText()
                            .toString()
            );

            rs =
                    stmt.executeQuery();

            if (rs.next()) {

                int idUsuario =
                        rs.getInt(
                                "id_usuario"
                        );

                String nome =
                        rs.getString(
                                "nome"
                        );

                String email =
                        rs.getString(
                                "email"
                        );

                sessao.salvarUsuario(
                        idUsuario,
                        nome,
                        email
                );

                Intent menu =
                        new Intent(
                                Login.this,
                                MainActivity.class
                        );

                startActivity(menu);
                finish();

            } else {

                new AlertDialog.Builder(
                        Login.this
                )
                        .setTitle(
                                "Erro no login"
                        )
                        .setMessage(
                                "Verifique o usuário e a senha!"
                        )
                        .setPositiveButton(
                                "OK",
                                null
                        )
                        .show();
            }

        } catch (Exception e) {

            new AlertDialog.Builder(
                    Login.this
            )
                    .setTitle(
                            "Erro"
                    )
                    .setMessage(
                            "Não foi possível realizar o login."
                    )
                    .setPositiveButton(
                            "OK",
                            null
                    )
                    .show();

            e.printStackTrace();

        } finally {

            fecharConexao();
        }
    }

    private void fecharConexao() {

        try {

            if (rs != null) {
                rs.close();
            }

            if (stmt != null) {
                stmt.close();
            }

            if (con != null) {
                con.close();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        rs = null;
        stmt = null;
        con = null;
    }
}