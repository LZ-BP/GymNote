package com.example.gymnote.ui.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.gymnote.ConexaoMySQL;
import com.example.gymnote.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MenuFragment extends Fragment {

    TextView txtHoje;
    TextView txtTreinoHoje;

    Button btVerTreinoHoje;
    Button btTreinos;
    Button btCriarTreino;
    Button btExercicios;
    Button btHistorico;

    int idUsuario = 1;
    int idTreinoHoje = -1;

    Connection con;
    PreparedStatement stmt;
    ResultSet rs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View V = inflater.inflate(
                R.layout.fragment_menu,
                container,
                false
        );

        txtHoje = V.findViewById(R.id.txtHoje);
        txtTreinoHoje = V.findViewById(R.id.txtTreinoHoje);

        btVerTreinoHoje =
                V.findViewById(R.id.btVerTreinoHoje);

        btTreinos =
                V.findViewById(R.id.btTreinos);

        btCriarTreino =
                V.findViewById(R.id.btCriarTreino);

        btExercicios =
                V.findViewById(R.id.btExercicios);

        btHistorico =
                V.findViewById(R.id.btHistorico);

        String hoje = new SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
        ).format(new Date());

        txtHoje.setText(
                "Hoje: " + hoje
        );

        carregarTreinoHoje();

        btVerTreinoHoje.setOnClickListener(
                view -> {

                    if (idTreinoHoje == -1) {

                        Toast.makeText(
                                requireContext(),
                                "Nenhum treino agendado para hoje",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    Bundle dados = new Bundle();

                    dados.putInt(
                            "idTreino",
                            idTreinoHoje
                    );

                    Navigation.findNavController(view)
                            .navigate(
                                    R.id.nav_treinos,
                                    dados
                            );
                }
        );

        btTreinos.setOnClickListener(
                view -> Navigation.findNavController(view)
                        .navigate(R.id.nav_treinos)
        );

        btCriarTreino.setOnClickListener(
                view -> Navigation.findNavController(view)
                        .navigate(R.id.nav_criartreino)
        );

        btExercicios.setOnClickListener(
                view -> Navigation.findNavController(view)
                        .navigate(R.id.nav_exercicio)
        );

        btHistorico.setOnClickListener(
                view -> Navigation.findNavController(view)
                        .navigate(R.id.nav_historico)
        );

        return V;
    }

    @Override
    public void onResume() {

        super.onResume();

        carregarTreinoHoje();
    }

    private void carregarTreinoHoje() {

        idTreinoHoje = -1;

        try {

            con = ConexaoMySQL.conectar();

            String sql =
                    "SELECT t.id_treino, t.nome_treino " +
                            "FROM calendario_treino ct " +
                            "INNER JOIN treino t " +
                            "ON t.id_treino = ct.id_treino " +
                            "WHERE ct.id_usuario = ? " +
                            "AND ct.data_treino = CURDATE()";

            stmt = con.prepareStatement(sql);

            stmt.setInt(
                    1,
                    idUsuario
            );

            rs = stmt.executeQuery();

            if (rs.next()) {

                idTreinoHoje =
                        rs.getInt("id_treino");

                txtTreinoHoje.setText(
                        rs.getString("nome_treino")
                );

            } else {

                txtTreinoHoje.setText(
                        "Nenhum treino agendado"
                );
            }

        } catch (Exception e) {

            txtTreinoHoje.setText(
                    "Erro ao carregar treino"
            );

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