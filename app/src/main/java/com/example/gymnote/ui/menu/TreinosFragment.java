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

    TextView txtNomeTreino;

    TableLayout tabelaExercicios;

    Button btFinalizarTreino;

    ArrayList<String> dadosTreinos =
            new ArrayList<>();

    ArrayList<Integer> idsTreinos =
            new ArrayList<>();

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

    ArrayAdapter<String> adaptadorTreinos;

    Connection con;
    PreparedStatement stmt;
    ResultSet rs;

    int idUsuario = 1;
    int idTreinoSelecionado = -1;

    String nomeTreinoSelecionado = "";

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

        listaTreinos =
                V.findViewById(R.id.listaTreinos);

        txtNomeTreino =
                V.findViewById(R.id.txtNomeTreino);

        tabelaExercicios =
                V.findViewById(R.id.tabelaExercicios);

        btFinalizarTreino =
                V.findViewById(R.id.btFinalizarTreino);

        carregarTreinos();

        listaTreinos.setOnItemClickListener(
                (parent, view, position, id) -> {

                    idTreinoSelecionado =
                            idsTreinos.get(position);

                    nomeTreinoSelecionado =
                            dadosTreinos.get(position);

                    txtNomeTreino.setText(
                            nomeTreinoSelecionado
                    );

                    carregarExercicios();
                }
        );

        btFinalizarTreino.setOnClickListener(
                view -> finalizarTreino()
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

    private void carregarExercicios() {

        tabelaExercicios.removeAllViews();

        idsExercicios.clear();
        nomesExercicios.clear();

        camposCarga.clear();
        camposSeries.clear();
        camposRepeticoes.clear();

        adicionarCabecalho();

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

            stmt.setInt(
                    1,
                    idTreinoSelecionado
            );

            rs = stmt.executeQuery();

            while (rs.next()) {

                idsExercicios.add(
                        rs.getInt("id_exercicio")
                );

                nomesExercicios.add(
                        rs.getString("nome")
                );

                adicionarLinha(
                        rs.getString("nome")
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
                new TableRow(requireContext());

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

    private void adicionarLinha(String nome) {

        TableRow linha =
                new TableRow(requireContext());

        TextView exercicio =
                criarTexto(nome);

        EditText carga =
                criarCampo("kg");

        EditText series =
                criarCampo("Séries");

        EditText repeticoes =
                criarCampo("Reps");

        linha.addView(exercicio);
        linha.addView(carga);
        linha.addView(series);
        linha.addView(repeticoes);

        tabelaExercicios.addView(linha);

        camposCarga.add(carga);
        camposSeries.add(series);
        camposRepeticoes.add(repeticoes);
    }

    private TextView criarTexto(String texto) {

        TextView campo =
                new TextView(requireContext());

        campo.setText(texto);
        campo.setTextSize(16);
        campo.setGravity(Gravity.CENTER);
        campo.setPadding(15, 15, 15, 15);

        return campo;
    }

    private EditText criarCampo(String hint) {

        EditText campo =
                new EditText(requireContext());

        campo.setHint(hint);
        campo.setTextSize(16);
        campo.setGravity(Gravity.CENTER);
        campo.setPadding(10, 10, 10, 10);

        campo.setInputType(
                InputType.TYPE_CLASS_NUMBER |
                        InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        return campo;
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

        if (idsExercicios.isEmpty()) {

            Toast.makeText(
                    requireContext(),
                    "Esse treino não possui exercícios",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            con = ConexaoMySQL.conectar();

            String data =
                    new SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                    ).format(new Date());

            String sql =
                    "INSERT INTO treino_realizado " +
                            "(id_usuario, id_treino, nome_treino, data_realizacao) " +
                            "VALUES (?, ?, ?, ?)";

            stmt = con.prepareStatement(
                    sql,
                    PreparedStatement.RETURN_GENERATED_KEYS
            );

            stmt.setInt(
                    1,
                    idUsuario
            );

            stmt.setInt(
                    2,
                    idTreinoSelecionado
            );

            stmt.setString(
                    3,
                    nomeTreinoSelecionado
            );

            stmt.setString(
                    4,
                    data
            );

            stmt.executeUpdate();

            rs = stmt.getGeneratedKeys();

            int idRealizado = -1;

            if (rs.next()) {
                idRealizado = rs.getInt(1);
            }

            if (idRealizado == -1) {

                Toast.makeText(
                        requireContext(),
                        "Erro ao criar histórico",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            sql =
                    "INSERT INTO treino_realizado_exercicio " +
                            "(id_treino_realizado, id_exercicio, " +
                            "nome_exercicio, ordem, series, " +
                            "repeticoes, carga, volume) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            stmt = con.prepareStatement(sql);

            for (int i = 0;
                 i < idsExercicios.size();
                 i++) {

                String cargaTexto =
                        camposCarga.get(i)
                                .getText()
                                .toString()
                                .trim();

                String seriesTexto =
                        camposSeries.get(i)
                                .getText()
                                .toString()
                                .trim();

                String repsTexto =
                        camposRepeticoes.get(i)
                                .getText()
                                .toString()
                                .trim();

                if (seriesTexto.isEmpty() ||
                        repsTexto.isEmpty()) {

                    Toast.makeText(
                            requireContext(),
                            "Preencha séries e repetições",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                double carga =
                        cargaTexto.isEmpty()
                                ? 0
                                : Double.parseDouble(
                                cargaTexto
                        );

                int series =
                        Integer.parseInt(
                                seriesTexto
                        );

                int repeticoes =
                        Integer.parseInt(
                                repsTexto
                        );

                double volume =
                        carga *
                                series *
                                repeticoes;

                stmt.setInt(
                        1,
                        idRealizado
                );

                stmt.setInt(
                        2,
                        idsExercicios.get(i)
                );

                stmt.setString(
                        3,
                        nomesExercicios.get(i)
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

            Toast.makeText(
                    requireContext(),
                    "Treino finalizado!",
                    Toast.LENGTH_SHORT
            ).show();

            limparTreino();

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

    private void limparTreino() {

        idTreinoSelecionado = -1;
        nomeTreinoSelecionado = "";

        txtNomeTreino.setText(
                "Nenhum treino selecionado"
        );

        tabelaExercicios.removeAllViews();

        camposCarga.clear();
        camposSeries.clear();
        camposRepeticoes.clear();
    }

    private void fecharConexao() {

        try {

            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (con != null) con.close();

        } catch (Exception e) {

            e.printStackTrace();
        }

        rs = null;
        stmt = null;
        con = null;
    }
}