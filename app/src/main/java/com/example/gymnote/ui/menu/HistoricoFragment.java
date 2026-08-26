package com.example.gymnote.ui.menu;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HistoricoFragment extends Fragment {

    ListView listaHistorico;

    ArrayList<String> dadosHistorico =
            new ArrayList<>();

    ArrayList<Integer> idsRealizados =
            new ArrayList<>();

    ArrayAdapter<String> adaptadorHistorico;

    Connection con;
    PreparedStatement stmt;
    ResultSet rs;

    Sessao sessao;
    int idUsuario;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View V = inflater.inflate(
                R.layout.fragment_historico,
                container,
                false
        );

        sessao =
                new Sessao(requireContext());

        idUsuario =
                sessao.getIdUsuario();

        listaHistorico =
                V.findViewById(
                        R.id.listaHistorico
                );

        limparHistoricoAntigo();
        carregarHistorico();

        listaHistorico.setOnItemClickListener(
                (parent, view, position, id) -> {

                    int idRealizado =
                            idsRealizados.get(position);

                    if (idRealizado == -1) {
                        return;
                    }

                    mostrarDetalhes(idRealizado);
                }
        );

        return V;
    }

    @Override
    public void onResume() {

        super.onResume();

        if (sessao == null) {
            sessao =
                    new Sessao(requireContext());
        }

        idUsuario =
                sessao.getIdUsuario();

        if (listaHistorico != null) {

            limparHistoricoAntigo();
            carregarHistorico();
        }
    }

    private void carregarHistorico() {

        dadosHistorico.clear();
        idsRealizados.clear();

        try {

            con =
                    ConexaoMySQL.conectar();

            String sql =
                    "SELECT id_treino_realizado, " +
                            "nome_treino, data_realizacao " +
                            "FROM treino_realizado " +
                            "WHERE id_usuario = ? " +
                            "ORDER BY data_realizacao DESC, " +
                            "id_treino_realizado DESC";

            stmt =
                    con.prepareStatement(sql);

            stmt.setInt(
                    1,
                    idUsuario
            );

            rs =
                    stmt.executeQuery();

            String semanaAtual = "";

            SimpleDateFormat formato =
                    new SimpleDateFormat(
                            "dd/MM/yyyy",
                            Locale.getDefault()
                    );

            while (rs.next()) {

                int idRealizado =
                        rs.getInt(
                                "id_treino_realizado"
                        );

                String nomeTreino =
                        rs.getString(
                                "nome_treino"
                        );

                Date data =
                        rs.getDate(
                                "data_realizacao"
                        );

                String semana =
                        obterSemanaDoMes(data);

                if (!semana.equals(
                        semanaAtual
                )) {

                    dadosHistorico.add(
                            semana
                    );

                    idsRealizados.add(
                            -1
                    );

                    semanaAtual =
                            semana;
                }

                dadosHistorico.add(
                        formato.format(data)
                                + " - "
                                + nomeTreino
                );

                idsRealizados.add(
                        idRealizado
                );
            }

            adaptadorHistorico =
                    new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            dadosHistorico
                    );

            listaHistorico.setAdapter(
                    adaptadorHistorico
            );

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "Erro ao carregar histórico",
                    Toast.LENGTH_SHORT
            ).show();

            e.printStackTrace();

        } finally {

            fecharConexao();
        }
    }

    private String obterSemanaDoMes(
            Date data) {

        Calendar calendario =
                Calendar.getInstance();

        calendario.setTime(data);

        int semana =
                calendario.get(
                        Calendar.WEEK_OF_MONTH
                );

        SimpleDateFormat mes =
                new SimpleDateFormat(
                        "MMMM yyyy",
                        new Locale(
                                "pt",
                                "BR"
                        )
                );

        return "Semana "
                + semana
                + " - "
                + mes.format(data);
    }

    private void mostrarDetalhes(
            int idTreinoRealizado) {

        try {

            con =
                    ConexaoMySQL.conectar();

            String sql =
                    "SELECT trr.nome_treino, " +
                            "trr.data_realizacao, " +
                            "tre.nome_exercicio, " +
                            "tre.series, " +
                            "tre.repeticoes, " +
                            "tre.carga, " +
                            "tre.volume " +
                            "FROM treino_realizado trr " +
                            "LEFT JOIN treino_realizado_exercicio tre " +
                            "ON tre.id_treino_realizado = " +
                            "trr.id_treino_realizado " +
                            "WHERE trr.id_treino_realizado = ? " +
                            "AND trr.id_usuario = ? " +
                            "ORDER BY tre.ordem";

            stmt =
                    con.prepareStatement(sql);

            stmt.setInt(
                    1,
                    idTreinoRealizado
            );

            stmt.setInt(
                    2,
                    idUsuario
            );

            rs =
                    stmt.executeQuery();

            String nomeTreino = "";
            String dataTreino = "";

            StringBuilder texto =
                    new StringBuilder();

            boolean encontrou =
                    false;

            while (rs.next()) {

                if (!encontrou) {

                    nomeTreino =
                            rs.getString(
                                    "nome_treino"
                            );

                    Date data =
                            rs.getDate(
                                    "data_realizacao"
                            );

                    if (data != null) {

                        dataTreino =
                                new SimpleDateFormat(
                                        "dd/MM/yyyy",
                                        Locale.getDefault()
                                ).format(data);
                    }

                    encontrou = true;
                }

                String nomeExercicio =
                        rs.getString(
                                "nome_exercicio"
                        );

                if (nomeExercicio == null) {
                    continue;
                }

                texto.append(
                        nomeExercicio
                );

                texto.append(
                        "\n"
                );

                texto.append(
                        "Carga: "
                );

                texto.append(
                        rs.getDouble(
                                "carga"
                        )
                );

                texto.append(
                        " kg | "
                );

                texto.append(
                        rs.getInt(
                                "series"
                        )
                );

                texto.append(
                        " séries | "
                );

                texto.append(
                        rs.getInt(
                                "repeticoes"
                        )
                );

                texto.append(
                        " reps"
                );

                texto.append(
                        "\n\n"
                );
            }

            if (!encontrou) {

                Toast.makeText(
                        requireContext(),
                        "Treino não encontrado",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            if (texto.length() == 0) {

                texto.append(
                        "Nenhum exercício registrado."
                );
            }

            new AlertDialog.Builder(
                    requireContext()
            )
                    .setTitle(
                            nomeTreino
                                    + " - "
                                    + dataTreino
                    )
                    .setMessage(
                            texto.toString()
                    )
                    .setPositiveButton(
                            "FECHAR",
                            null
                    )
                    .setNegativeButton(
                            "EXCLUIR",
                            (dialog, which) ->
                                    confirmarExclusao(
                                            idTreinoRealizado
                                    )
                    )
                    .show();

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "Erro ao carregar detalhes",
                    Toast.LENGTH_SHORT
            ).show();

            e.printStackTrace();

        } finally {

            fecharConexao();
        }
    }

    private void confirmarExclusao(
            int idTreinoRealizado) {

        new AlertDialog.Builder(
                requireContext()
        )
                .setTitle(
                        "Excluir treino?"
                )
                .setMessage(
                        "Esse registro será removido do histórico."
                )
                .setNegativeButton(
                        "CANCELAR",
                        null
                )
                .setPositiveButton(
                        "EXCLUIR",
                        (dialog, which) ->
                                excluirTreinoRealizado(
                                        idTreinoRealizado
                                )
                )
                .show();
    }

    private void excluirTreinoRealizado(
            int idTreinoRealizado) {

        try {

            con =
                    ConexaoMySQL.conectar();

            String sql =
                    "DELETE FROM treino_realizado " +
                            "WHERE id_treino_realizado = ? " +
                            "AND id_usuario = ?";

            stmt =
                    con.prepareStatement(sql);

            stmt.setInt(
                    1,
                    idTreinoRealizado
            );

            stmt.setInt(
                    2,
                    idUsuario
            );

            int quantidade =
                    stmt.executeUpdate();

            if (quantidade > 0) {

                Toast.makeText(
                        requireContext(),
                        "Treino removido do histórico",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                Toast.makeText(
                        requireContext(),
                        "Treino não encontrado",
                        Toast.LENGTH_SHORT
                ).show();
            }

            carregarHistorico();

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "Erro ao excluir treino",
                    Toast.LENGTH_SHORT
            ).show();

            e.printStackTrace();

        } finally {

            fecharConexao();
        }
    }

    private void limparHistoricoAntigo() {

        try {

            con =
                    ConexaoMySQL.conectar();

            String sql =
                    "DELETE FROM treino_realizado " +
                            "WHERE id_usuario = ? " +
                            "AND data_realizacao < " +
                            "DATE_SUB(CURDATE(), INTERVAL 30 DAY)";

            stmt =
                    con.prepareStatement(sql);

            stmt.setInt(
                    1,
                    idUsuario
            );

            int quantidade =
                    stmt.executeUpdate();

            if (quantidade > 0) {

                Toast.makeText(
                        requireContext(),
                        quantidade
                                + " treino(s) antigo(s) removido(s)",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "Erro ao limpar histórico antigo",
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