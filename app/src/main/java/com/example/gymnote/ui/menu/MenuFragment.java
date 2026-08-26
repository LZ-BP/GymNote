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
import com.example.gymnote.Sessao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MenuFragment extends Fragment {

    TextView txtHoje;
    TextView txtTreinoHoje;

    Button btTreino;
    Button btCriarTreino;
    Button btExercicios;
    Button btHistorico;

    Connection con;
    PreparedStatement stmt;
    ResultSet rs;

    Sessao sessao;
    int idUsuario;

    int idTreinoHoje = -1;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View V = inflater.inflate(
                R.layout.fragment_menu,
                container,
                false
        );

        sessao =
                new Sessao(requireContext());

        idUsuario =
                sessao.getIdUsuario();

        txtHoje =
                V.findViewById(R.id.txtHoje);

        txtTreinoHoje =
                V.findViewById(R.id.txtTreinoHoje);

        btTreino =
                V.findViewById(R.id.btTreino);

        btCriarTreino =
                V.findViewById(R.id.btCriarTreino);

        btExercicios =
                V.findViewById(R.id.btExercicios);

        btHistorico =
                V.findViewById(R.id.btHistorico);

        String hoje =
                new SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                ).format(new Date());

        txtHoje.setText(
                "Hoje: " + hoje
        );

        carregarTreinoHoje();

        btCriarTreino.setOnClickListener(
                view -> {

                    Navigation.findNavController(view)
                            .navigate(
                                    R.id.nav_criartreino
                            );
                }
        );

        btTreino.setOnClickListener(
                view -> {

                    if (idTreinoHoje == -1) {

                        Toast.makeText(
                                requireContext(),
                                "Nenhum treino agendado para hoje",
                                Toast.LENGTH_SHORT
                        ).show();

                        return;
                    }

                    Bundle dados =
                            new Bundle();

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

        btExercicios.setOnClickListener(
                view -> {

                    Navigation.findNavController(view)
                            .navigate(
                                    R.id.nav_exercicio
                            );
                }
        );

        btHistorico.setOnClickListener(
                view -> {

                    Navigation.findNavController(view)
                            .navigate(
                                    R.id.nav_historico
                            );
                }
        );

        return V;
    }

    @Override
    public void onResume() {

        super.onResume();

        if (txtTreinoHoje != null) {
            carregarTreinoHoje();
        }
    }

    private void carregarTreinoHoje() {

        idTreinoHoje = -1;

        String dataHoje =
                new SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                ).format(new Date());

        try {

            con =
                    ConexaoMySQL.conectar();

            String sql =
                    "SELECT ct.id_treino, t.nome_treino " +
                            "FROM calendario_treino ct " +
                            "INNER JOIN treino t " +
                            "ON t.id_treino = ct.id_treino " +
                            "WHERE ct.id_usuario = ? " +
                            "AND ct.data_treino = ?";

            stmt =
                    con.prepareStatement(sql);

            stmt.setInt(
                    1,
                    idUsuario
            );

            stmt.setString(
                    2,
                    dataHoje
            );

            rs =
                    stmt.executeQuery();

            if (rs.next()) {

                idTreinoHoje =
                        rs.getInt(
                                "id_treino"
                        );

                String nomeTreino =
                        rs.getString(
                                "nome_treino"
                        );

                txtTreinoHoje.setText(
                        nomeTreino
                );

                btTreino.setText(
                        "INICIAR TREINO"
                );

            } else {

                txtTreinoHoje.setText(
                        "Nenhum treino agendado"
                );

                btTreino.setText(
                        "TREINO"
                );
            }

        } catch (Exception e) {

            txtTreinoHoje.setText(
                    "Erro ao carregar treino"
            );

            Toast.makeText(
                    requireContext(),
                    "Erro ao consultar treino de hoje",
                    Toast.LENGTH_SHORT
            ).show();

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