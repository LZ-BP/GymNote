package com.example.gymnote.ui.menu;

import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gymnote.ConexaoMySQL;
import com.example.gymnote.R;
import com.example.gymnote.Sessao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class TreinosFragment extends Fragment {

    TextView txtNomeTreino;
    Button btFinalizarTreino;
    TableLayout tabelaExercicios;

    ArrayList<Integer> idsExercicios =
            new ArrayList<>();

    ArrayList<String> nomesExercicios =
            new ArrayList<>();

    ArrayList<EditText> camposCarga =
            new ArrayList<>();

    ArrayList<EditText> camposSeries =
            new ArrayList<>();

    ArrayList<EditText> camposRepeticoes =
            new ArrayList<>();

    Connection con;
    PreparedStatement stmt;
    ResultSet rs;

    Sessao sessao;
    int idUsuario;

    int idTreino = -1;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View V =
                inflater.inflate(
                        R.layout.fragment_treinos,
                        container,
                        false
                );

        sessao =
                new Sessao(requireContext());

        idUsuario =
                sessao.getIdUsuario();

        txtNomeTreino =
                V.findViewById(
                        R.id.txtNomeTreino
                );

        tabelaExercicios =
                V.findViewById(
                        R.id.tabelaExercicios
                );

        btFinalizarTreino =
                V.findViewById(
                        R.id.btFinalizarTreino
                );

        Bundle dados =
                getArguments();

        if (dados != null) {

            idTreino =
                    dados.getInt(
                            "idTreino",
                            -1
                    );
        }

        if (idTreino == -1) {

            txtNomeTreino.setText(
                    "Nenhum treino selecionado"
            );

            btFinalizarTreino.setEnabled(
                    false
            );

            return V;
        }

        carregarTreino();

        btFinalizarTreino.setOnClickListener(
                view -> finalizarTreino()
        );

        return V;
    }

    private void carregarTreino() {

        try {

            con =
                    ConexaoMySQL.conectar();

            String sql =
                    "SELECT nome_treino " +
                            "FROM treino " +
                            "WHERE id_treino = ? " +
                            "AND id_usuario = ?";

            stmt =
                    con.prepareStatement(sql);

            stmt.setInt(
                    1,
                    idTreino
            );

            stmt.setInt(
                    2,
                    idUsuario
            );

            rs =
                    stmt.executeQuery();

            if (rs.next()) {

                txtNomeTreino.setText(
                        rs.getString(
                                "nome_treino"
                        )
                );

                carregarExercicios();

            } else {

                txtNomeTreino.setText(
                        "Treino não encontrado"
                );

                btFinalizarTreino.setEnabled(
                        false
                );
            }

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "Erro ao carregar treino",
                    Toast.LENGTH_SHORT
            ).show();

            e.printStackTrace();

        } finally {

            fecharConexao();
        }
    }

    private void carregarExercicios() {

        tabelaExercicios.removeAllViews();

        idsExercicios.clear();
        nomesExercicios.clear();

        camposCarga.clear();
        camposSeries.clear();
        camposRepeticoes.clear();

        adicionarCabecalho();

        try {

            con =
                    ConexaoMySQL.conectar();

            String sql =
                    "SELECT e.id_exercicio, e.nome " +
                            "FROM treino_exercicio te " +
                            "INNER JOIN exercicio e " +
                            "ON e.id_exercicio = te.id_exercicio " +
                            "WHERE te.id_treino = ? " +
                            "AND e.id_usuario = ? " +
                            "ORDER BY te.ordem";

            stmt =
                    con.prepareStatement(sql);

            stmt.setInt(
                    1,
                    idTreino
            );

            stmt.setInt(
                    2,
                    idUsuario
            );

            rs =
                    stmt.executeQuery();

            while (rs.next()) {

                int idExercicio =
                        rs.getInt(
                                "id_exercicio"
                        );

                String nome =
                        rs.getString(
                                "nome"
                        );

                idsExercicios.add(
                        idExercicio
                );

                nomesExercicios.add(
                        nome
                );

                adicionarLinha(
                        nome
                );
            }

            if (idsExercicios.isEmpty()) {

                Toast.makeText(
                        requireContext(),
                        "Esse treino não possui exercícios",
                        Toast.LENGTH_SHORT
                ).show();

                btFinalizarTreino.setEnabled(
                        false
                );
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

        TableRow linha =
                new TableRow(
                        requireContext()
                );

        linha.addView(
                criarTexto(
                        "Exercício"
                )
        );

        linha.addView(
                criarTexto(
                        "Carga"
                )
        );

        linha.addView(
                criarTexto(
                        "Séries"
                )
        );

        linha.addView(
                criarTexto(
                        "Reps"
                )
        );

        tabelaExercicios.addView(
                linha
        );
    }

    private void adicionarLinha(
            String nome) {

        TableRow linha =
                new TableRow(
                        requireContext()
                );

        TextView exercicio =
                criarTexto(nome);

        EditText carga =
                criarCampo(
                        "kg",
                        true
                );

        EditText series =
                criarCampo(
                        "Séries",
                        false
                );

        EditText repeticoes =
                criarCampo(
                        "Reps",
                        false
                );

        camposCarga.add(carga);
        camposSeries.add(series);
        camposRepeticoes.add(repeticoes);

        linha.addView(exercicio);
        linha.addView(carga);
        linha.addView(series);
        linha.addView(repeticoes);

        tabelaExercicios.addView(
                linha
        );
    }

    private TextView criarTexto(
            String texto) {

        TextView textView =
                new TextView(
                        requireContext()
                );

        textView.setText(texto);
        textView.setTextSize(16);
        textView.setPadding(
                15,
                15,
                15,
                15
        );

        textView.setGravity(
                Gravity.CENTER
        );

        return textView;
    }

    private EditText criarCampo(
            String hint,
            boolean decimal) {

        EditText campo =
                new EditText(
                        requireContext()
                );

        campo.setHint(hint);
        campo.setTextSize(16);

        campo.setGravity(
                Gravity.CENTER
        );

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

    private void finalizarTreino() {

        if (idTreino == -1) {
            return;
        }

        if (idsExercicios.isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Esse treino não possui exercícios",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            con =
                    ConexaoMySQL.conectar();

            con.setAutoCommit(false);

            for (
                    int i = 0;
                    i < idsExercicios.size();
                    i++
            ) {

                String series =
                        camposSeries.get(i)
                                .getText()
                                .toString()
                                .trim();

                String repeticoes =
                        camposRepeticoes.get(i)
                                .getText()
                                .toString()
                                .trim();

                if (
                        series.isEmpty() ||
                                repeticoes.isEmpty()
                ) {

                    Toast.makeText(
                            requireContext(),
                            "Preencha séries e repetições de todos os exercícios",
                            Toast.LENGTH_SHORT
                    ).show();

                    con.rollback();
                    return;
                }
            }

            String sql =
                    "INSERT INTO treino_realizado " +
                            "(id_usuario, id_treino, nome_treino, data_realizacao) " +
                            "SELECT ?, id_treino, nome_treino, CURDATE() " +
                            "FROM treino " +
                            "WHERE id_treino = ? " +
                            "AND id_usuario = ?";

            stmt =
                    con.prepareStatement(
                            sql,
                            java.sql.Statement.RETURN_GENERATED_KEYS
                    );

            stmt.setInt(1, idUsuario);
            stmt.setInt(2, idTreino);
            stmt.setInt(3, idUsuario);

            stmt.executeUpdate();

            rs =
                    stmt.getGeneratedKeys();

            int idTreinoRealizado = -1;

            if (rs.next()) {

                idTreinoRealizado =
                        rs.getInt(1);
            }

            if (idTreinoRealizado == -1) {

                throw new Exception(
                        "Não foi possível criar o treino realizado"
                );
            }

            rs.close();
            stmt.close();

            sql =
                    "INSERT INTO treino_realizado_exercicio " +
                            "(id_treino_realizado, id_exercicio, nome_exercicio, " +
                            "ordem, series, repeticoes, carga, volume) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            stmt =
                    con.prepareStatement(sql);

            for (
                    int i = 0;
                    i < idsExercicios.size();
                    i++
            ) {

                String valorCarga =
                        camposCarga.get(i)
                                .getText()
                                .toString()
                                .trim();

                String valorSeries =
                        camposSeries.get(i)
                                .getText()
                                .toString()
                                .trim();

                String valorRepeticoes =
                        camposRepeticoes.get(i)
                                .getText()
                                .toString()
                                .trim();

                double carga =
                        valorCarga.isEmpty()
                                ? 0
                                : Double.parseDouble(
                                valorCarga
                        );

                int series =
                        Integer.parseInt(
                                valorSeries
                        );

                int repeticoes =
                        Integer.parseInt(
                                valorRepeticoes
                        );

                double volume =
                        carga *
                                series *
                                repeticoes;

                TableRow linha =
                        (TableRow)
                                tabelaExercicios
                                        .getChildAt(i + 1);

                TextView textoExercicio =
                        (TextView)
                                linha.getChildAt(0);

                String nomeExercicio =
                        textoExercicio
                                .getText()
                                .toString();

                stmt.setInt(
                        1,
                        idTreinoRealizado
                );

                stmt.setInt(
                        2,
                        idsExercicios.get(i)
                );

                stmt.setString(
                        3,
                        nomeExercicio
                );

                stmt.setInt(
                        4,
                        i + 1
                );

                stmt.setInt(
                        5,
                        series
                );

                stmt.setInt(
                        6,
                        repeticoes
                );

                stmt.setDouble(
                        7,
                        carga
                );

                stmt.setDouble(
                        8,
                        volume
                );

                stmt.executeUpdate();
            }

            con.commit();

            Toast.makeText(
                    requireContext(),
                    "Treino finalizado!",
                    Toast.LENGTH_SHORT
            ).show();

            idTreino = -1;

            txtNomeTreino.setText(
                    "Nenhum treino selecionado"
            );

            tabelaExercicios.removeAllViews();

            adicionarCabecalho();

            btFinalizarTreino.setEnabled(
                    false
            );

        } catch (Exception e) {

            try {

                if (con != null) {
                    con.rollback();
                }

            } catch (Exception rollbackError) {

                rollbackError.printStackTrace();
            }

            Toast.makeText(
                    requireContext(),
                    "Erro: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            e.printStackTrace();

        } finally {

            try {

                if (con != null) {
                    con.setAutoCommit(true);
                }

            } catch (Exception e) {

                e.printStackTrace();
            }

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