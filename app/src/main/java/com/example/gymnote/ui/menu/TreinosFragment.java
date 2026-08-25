package com.example.gymnote.ui.menu;

import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
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

public class TreinosFragment extends Fragment {

    ListView listaTreinos;
    TableLayout tabelaExercicios;

    TextView txtNomeTreino;
    Button btFinalizarTreino;

    ArrayList<String> dadosTreinos = new ArrayList<>();
    ArrayList<Integer> idsTreinos = new ArrayList<>();

    ArrayAdapter<String> adaptadorTreinos;

    Connection con;
    PreparedStatement stmt;
    ResultSet rs;

    int idUsuario = 1;
    int idTreinoSelecionado = -1;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View V = inflater.inflate(
                R.layout.fragment_treinos,
                container,
                false
        );

        listaTreinos = V.findViewById(R.id.listaTreinos);
        tabelaExercicios = V.findViewById(R.id.tabelaExercicios);
        txtNomeTreino = V.findViewById(R.id.txtNomeTreino);
        btFinalizarTreino = V.findViewById(R.id.btFinalizarTreino);

        carregarTreinos();

        listaTreinos.setOnItemClickListener(
                (parent, view, position, id) -> {

                    idTreinoSelecionado =
                            idsTreinos.get(position);

                    txtNomeTreino.setText(
                            dadosTreinos.get(position)
                    );

                    carregarExerciciosDoTreino();
                }
        );

        btFinalizarTreino.setOnClickListener(
                view -> finalizarTreino()
        );

        return V;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (listaTreinos != null) {
            carregarTreinos();
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
                            android.R.layout.simple_list_item_1,
                            dadosTreinos
                    );

            listaTreinos.setAdapter(
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

    private void carregarExerciciosDoTreino() {

        tabelaExercicios.removeAllViews();

        adicionarCabecalho();

        if (idTreinoSelecionado == -1) {
            return;
        }

        try {

            con = ConexaoMySQL.conectar();

            String sql =
                    "SELECT e.id_exercicio, e.nome " +
                            "FROM treino_exercicio te " +
                            "INNER JOIN exercicio e " +
                            "ON e.id_exercicio = te.id_exercicio " +
                            "WHERE te.id_treino = ? " +
                            "ORDER BY te.ordem";

            stmt = con.prepareStatement(sql);
            stmt.setInt(1, idTreinoSelecionado);

            rs = stmt.executeQuery();

            int ordem = 1;

            while (rs.next()) {

                adicionarLinha(
                        rs.getInt("id_exercicio"),
                        rs.getString("nome"),
                        ordem
                );

                ordem++;
            }

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "Erro ao carregar exercícios",
                    Toast.LENGTH_SHORT
            ).show();

            e.printStackTrace();

        } finally {

            fecharConexao();
        }
    }

    private void adicionarCabecalho() {

        TableRow linha = new TableRow(
                requireContext()
        );

        linha.addView(
                criarTexto("Exercício")
        );

        linha.addView(
                criarTexto("Carga")
        );

        linha.addView(
                criarTexto("Séries")
        );

        linha.addView(
                criarTexto("Reps")
        );

        tabelaExercicios.addView(linha);
    }

    private void adicionarLinha(
            int idExercicio,
            String nome,
            int ordem) {

        TableRow linha = new TableRow(
                requireContext()
        );

        TextView exercicio =
                criarTexto(nome);

        EditText carga =
                criarCampo("kg", true);

        EditText series =
                criarCampo("Séries", false);

        EditText repeticoes =
                criarCampo("Reps", false);

        linha.setTag(idExercicio);

        linha.addView(exercicio);
        linha.addView(carga);
        linha.addView(series);
        linha.addView(repeticoes);

        tabelaExercicios.addView(linha);
    }

    private EditText criarCampo(
            String hint,
            boolean decimal) {

        EditText campo =
                new EditText(requireContext());

        campo.setHint(hint);
        campo.setTextSize(16);
        campo.setGravity(Gravity.CENTER);

        campo.setPadding(
                10,
                10,
                10,
                10
        );

        if (decimal) {

            campo.setInputType(
                    InputType.TYPE_CLASS_NUMBER |
                            InputType.TYPE_NUMBER_FLAG_DECIMAL
            );

        } else {

            campo.setInputType(
                    InputType.TYPE_CLASS_NUMBER
            );
        }

        return campo;
    }

    private TextView criarTexto(String texto) {

        TextView textView =
                new TextView(requireContext());

        textView.setText(texto);
        textView.setTextSize(16);
        textView.setGravity(Gravity.CENTER);

        textView.setPadding(
                15,
                15,
                15,
                15
        );

        return textView;
    }

    private void finalizarTreino() {

        if (idTreinoSelecionado == -1) {

            Toast.makeText(
                    requireContext(),
                    "Selecione um treino primeiro",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (tabelaExercicios.getChildCount() <= 1) {

            Toast.makeText(
                    requireContext(),
                    "Esse treino não possui exercícios",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            con = ConexaoMySQL.conectar();

            String nomeTreino =
                    txtNomeTreino.getText()
                            .toString()
                            .trim();

            String data =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    ).format(new Date());

            /*
             * Cria o treino realizado.
             */

            String sql =
                    "INSERT INTO treino_realizado " +
                            "(id_usuario, id_treino, nome_treino, data_realizacao) " +
                            "VALUES (?, ?, ?, ?)";

            stmt = con.prepareStatement(
                    sql,
                    java.sql.Statement.RETURN_GENERATED_KEYS
            );

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idTreinoSelecionado);
            stmt.setString(3, nomeTreino);
            stmt.setString(4, data);

            stmt.executeUpdate();

            ResultSet chave =
                    stmt.getGeneratedKeys();

            int idTreinoRealizado = -1;

            if (chave.next()) {
                idTreinoRealizado =
                        chave.getInt(1);
            }

            chave.close();
            stmt.close();

            /*
             * Salva os exercícios realizados.
             */

            sql =
                    "INSERT INTO treino_realizado_exercicio " +
                            "(id_treino_realizado, id_exercicio, ordem, " +
                            "series, repeticoes, carga, volume) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)";

            stmt = con.prepareStatement(sql);

            for (int i = 1;
                 i < tabelaExercicios.getChildCount();
                 i++) {

                TableRow linha =
                        (TableRow) tabelaExercicios
                                .getChildAt(i);

                int idExercicio =
                        (int) linha.getTag();

                EditText campoCarga =
                        (EditText) linha.getChildAt(1);

                EditText campoSeries =
                        (EditText) linha.getChildAt(2);

                EditText campoReps =
                        (EditText) linha.getChildAt(3);

                String textoCarga =
                        campoCarga.getText()
                                .toString()
                                .trim();

                String textoSeries =
                        campoSeries.getText()
                                .toString()
                                .trim();

                String textoReps =
                        campoReps.getText()
                                .toString()
                                .trim();

                if (textoCarga.isEmpty() ||
                        textoSeries.isEmpty() ||
                        textoReps.isEmpty()) {

                    Toast.makeText(
                            requireContext(),
                            "Preencha carga, séries e repetições",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                double carga =
                        Double.parseDouble(
                                textoCarga
                        );

                int series =
                        Integer.parseInt(
                                textoSeries
                        );

                int repeticoes =
                        Integer.parseInt(
                                textoReps
                        );

                double volume =
                        carga *
                                series *
                                repeticoes;

                stmt.setInt(
                        1,
                        idTreinoRealizado
                );

                stmt.setInt(
                        2,
                        idExercicio
                );

                stmt.setInt(
                        3,
                        i
                );

                stmt.setInt(
                        4,
                        series
                );

                stmt.setInt(
                        5,
                        repeticoes
                );

                stmt.setDouble(
                        6,
                        carga
                );

                stmt.setDouble(
                        7,
                        volume
                );

                stmt.executeUpdate();
            }

            Toast.makeText(
                    requireContext(),
                    "Treino finalizado!",
                    Toast.LENGTH_SHORT
            ).show();

            idTreinoSelecionado = -1;

            txtNomeTreino.setText(
                    "Selecione um treino"
            );

            tabelaExercicios.removeAllViews();

            adicionarCabecalho();

            listaTreinos.clearChoices();

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "Erro ao finalizar treino",
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