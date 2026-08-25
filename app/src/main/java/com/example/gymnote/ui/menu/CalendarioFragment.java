package com.example.gymnote.ui.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    TextView txtTreinoSelecionado;

    Spinner spinnerTreinos;
    Button btSalvarTreino;

    ArrayList<String> dadosTreinos = new ArrayList<>();
    ArrayList<Integer> idsTreinos = new ArrayList<>();

    ArrayAdapter<String> adaptadorTreinos;

    Connection con;
    PreparedStatement stmt;
    ResultSet rs;

    int idUsuario = 1;
    String dataSelecionada;

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

        calendario = V.findViewById(R.id.calendario);
        txtDataSelecionada = V.findViewById(R.id.txtDataSelecionada);
        txtTreinoSelecionado = V.findViewById(R.id.txtTreinoSelecionado);
        spinnerTreinos = V.findViewById(R.id.spinnerTreinos);
        btSalvarTreino = V.findViewById(R.id.btSalvarTreino);

        dataSelecionada = new SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
        ).format(new Date());

        atualizarDataTexto();
        carregarTreinos();

        calendario.setOnDateChangeListener(
                (view, year, month, dayOfMonth) -> {

                    dataSelecionada = String.format(
                            Locale.getDefault(),
                            "%04d-%02d-%02d",
                            year,
                            month + 1,
                            dayOfMonth
                    );

                    atualizarDataTexto();
                    carregarTreinoDoDia();
                }
        );

        spinnerTreinos.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        if (!dadosTreinos.isEmpty()) {

                            txtTreinoSelecionado.setText(
                                    dadosTreinos.get(position)
                            );
                        }
                    }

                    @Override
                    public void onNothingSelected(
                            AdapterView<?> parent) {
                    }
                }
        );

        btSalvarTreino.setOnClickListener(
                view -> salvarTreinoNoDia()
        );

        return V;
    }

    private void atualizarDataTexto() {

        try {

            SimpleDateFormat banco =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    );

            SimpleDateFormat tela =
                    new SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                    );

            Date data = banco.parse(dataSelecionada);

            txtDataSelecionada.setText(
                    tela.format(data)
            );

        } catch (Exception e) {

            e.printStackTrace();
        }
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
            stmt.setInt(1, idUsuario);

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

            carregarTreinoDoDia();

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

    private void carregarTreinoDoDia() {

        if (dadosTreinos.isEmpty()) {

            txtTreinoSelecionado.setText(
                    "Nenhum treino cadastrado"
            );

            return;
        }

        try {

            con = ConexaoMySQL.conectar();

            String sql =
                    "SELECT ct.id_treino, t.nome_treino " +
                            "FROM calendario_treino ct " +
                            "INNER JOIN treino t " +
                            "ON t.id_treino = ct.id_treino " +
                            "WHERE ct.id_usuario = ? " +
                            "AND ct.data_treino = ?";

            stmt = con.prepareStatement(sql);

            stmt.setInt(1, idUsuario);
            stmt.setString(2, dataSelecionada);

            rs = stmt.executeQuery();

            if (rs.next()) {

                int idTreino =
                        rs.getInt("id_treino");

                String nomeTreino =
                        rs.getString("nome_treino");

                txtTreinoSelecionado.setText(
                        nomeTreino
                );

                for (int i = 0;
                     i < idsTreinos.size();
                     i++) {

                    if (idsTreinos.get(i) == idTreino) {

                        spinnerTreinos.setSelection(i);
                        break;
                    }
                }

            } else {

                txtTreinoSelecionado.setText(
                        "Nenhum treino neste dia"
                );
            }

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "Erro ao consultar treino",
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
                    "Cadastre um treino primeiro",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        int posicao =
                spinnerTreinos.getSelectedItemPosition();

        if (posicao < 0) {
            return;
        }

        int idTreino =
                idsTreinos.get(posicao);

        try {

            con = ConexaoMySQL.conectar();

            String sql =
                    "INSERT INTO calendario_treino " +
                            "(id_usuario, id_treino, data_treino) " +
                            "VALUES (?, ?, ?) " +
                            "ON DUPLICATE KEY UPDATE " +
                            "id_treino = VALUES(id_treino)";

            stmt = con.prepareStatement(sql);

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idTreino);
            stmt.setString(3, dataSelecionada);

            stmt.executeUpdate();

            Toast.makeText(
                    requireContext(),
                    "Treino salvo no calendário!",
                    Toast.LENGTH_SHORT
            ).show();

            carregarTreinoDoDia();

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