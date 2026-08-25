package com.example.gymnote.ui.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gymnote.ConexaoMySQL;
import com.example.gymnote.R;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CalendarioFragment extends Fragment {

    CalendarView calendario;
    TextView txtDataSelecionada;

    Spinner spinnerTreinos;

    Button btSalvarCalendario;

    String dataSelecionada;

    ArrayList<String> dadosTreinos =
            new ArrayList<>();

    ArrayList<Integer> idsTreinos =
            new ArrayList<>();

    ArrayAdapter<String> adaptadorTreinos;

    Connection con;
    PreparedStatement stmt;
    ResultSet rs;

    int idUsuario = 1;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View V = inflater.inflate(
                R.layout.fragment_calendario,
                container,
                false
        );

        calendario =
                V.findViewById(R.id.calendario);

        txtDataSelecionada =
                V.findViewById(R.id.txtDataSelecionada);

        spinnerTreinos =
                V.findViewById(R.id.spinnerTreinos);

        btSalvarCalendario =
                V.findViewById(R.id.btSalvarCalendario);

        dataSelecionada =
                new SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                ).format(new Date());

        txtDataSelecionada.setText(
                dataSelecionada
        );

        carregarTreinos();

        calendario.setOnDateChangeListener(
                (view, year, month, dayOfMonth) -> {

                    dataSelecionada = String.format(
                            Locale.getDefault(),
                            "%02d/%02d/%04d",
                            dayOfMonth,
                            month + 1,
                            year
                    );

                    txtDataSelecionada.setText(
                            dataSelecionada
                    );
                }
        );

        btSalvarCalendario.setOnClickListener(
                view -> salvarTreinoNoDia()
        );

        return V;
    }

    private void carregarTreinos() {

        dadosTreinos.clear();
        idsTreinos.clear();

        try {

            con = ConexaoMySQL.conectar();

            String sql =
                    "SELECT id_treino, nome_treino " +
                            "FROM treino " +
                            "WHERE id_usuario = ? " +
                            "ORDER BY nome_treino";

            stmt = con.prepareStatement(sql);

            stmt.setInt(
                    1,
                    idUsuario
            );

            rs = stmt.executeQuery();

            while (rs.next()) {

                idsTreinos.add(
                        rs.getInt("id_treino")
                );

                dadosTreinos.add(
                        rs.getString("nome_treino")
                );
            }

            adaptadorTreinos =
                    new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            dadosTreinos
                    );

            adaptadorTreinos.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
            );

            spinnerTreinos.setAdapter(
                    adaptadorTreinos
            );

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "Erro ao carregar treinos",
                    Toast.LENGTH_SHORT
            ).show();

            e.printStackTrace();

        } finally {

            fecharConexao();
        }
    }

    private void salvarTreinoNoDia() {

        if (idsTreinos.isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Nenhum treino cadastrado",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int posicao =
                spinnerTreinos.getSelectedItemPosition();

        int idTreino =
                idsTreinos.get(posicao);

        try {

            con = ConexaoMySQL.conectar();

            String sql =
                    "INSERT INTO calendario_treino " +
                            "(id_usuario, id_treino, data_treino) " +
                            "VALUES (?, ?, STR_TO_DATE(?, '%d/%m/%Y')) " +
                            "ON DUPLICATE KEY UPDATE " +
                            "id_treino = VALUES(id_treino)";

            stmt = con.prepareStatement(sql);

            stmt.setInt(
                    1,
                    idUsuario
            );

            stmt.setInt(
                    2,
                    idTreino
            );

            stmt.setString(
                    3,
                    dataSelecionada
            );

            stmt.executeUpdate();

            Toast.makeText(
                    requireContext(),
                    "Treino salvo no dia!",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "Erro ao salvar treino",
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