package com.example.gymnote.ui.menu;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.ArrayList;

public class CriartreinoFragment extends Fragment {

    EditText edtNomeTreino;

    Button btSalvarTreino;
    Button btAlterarTreino;
    Button btExcluirTreino;
    Button btTreinosCadastrados;
    Button btSalvarExercicios;

    ListView listaExercicios;

    ArrayList<String> dadosTreinos =
            new ArrayList<>();

    ArrayList<Integer> idsTreinos =
            new ArrayList<>();

    ArrayList<String> dadosExercicios =
            new ArrayList<>();

    ArrayList<Integer> idsExercicios =
            new ArrayList<>();

    ArrayAdapter<String> adaptadorExercicios;

    Connection con;
    PreparedStatement stmt;
    ResultSet rs;

    Sessao sessao;
    int idUsuario;

    int idTreinoSelecionado = -1;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View V =
                inflater.inflate(
                        R.layout.fragment_criartreino,
                        container,
                        false
                );

        sessao =
                new Sessao(requireContext());

        idUsuario =
                sessao.getIdUsuario();

        edtNomeTreino =
                V.findViewById(
                        R.id.edtNomeTreino
                );

        btSalvarTreino =
                V.findViewById(
                        R.id.btSalvarTreino
                );

        btAlterarTreino =
                V.findViewById(
                        R.id.btAlterarTreino
                );

        btExcluirTreino =
                V.findViewById(
                        R.id.btExcluirTreino
                );

        btTreinosCadastrados =
                V.findViewById(
                        R.id.btTreinosCadastrados
                );

        btSalvarExercicios =
                V.findViewById(
                        R.id.btSalvarExercicios
                );

        listaExercicios =
                V.findViewById(
                        R.id.listaExercicios
                );

        btAlterarTreino.setVisibility(
                View.GONE
        );

        btExcluirTreino.setVisibility(
                View.GONE
        );

        carregarTreinos();
        carregarExercicios();

        btSalvarTreino.setOnClickListener(
                view -> criarTreino()
        );

        btTreinosCadastrados.setOnClickListener(
                view -> mostrarTreinos()
        );

        btAlterarTreino.setOnClickListener(
                view -> alterarTreino()
        );

        btExcluirTreino.setOnClickListener(
                view -> excluirTreino()
        );

        btSalvarExercicios.setOnClickListener(
                view -> salvarExercicios()
        );

        return V;
    }

    private void carregarTreinos() {

        dadosTreinos.clear();
        idsTreinos.clear();

        try {

            con =
                    ConexaoMySQL.conectar();

            String sql =
                    "SELECT id_treino, nome_treino " +
                            "FROM treino " +
                            "WHERE id_usuario = ? " +
                            "ORDER BY nome_treino";

            stmt =
                    con.prepareStatement(sql);

            stmt.setInt(
                    1,
                    idUsuario
            );

            rs =
                    stmt.executeQuery();

            while (rs.next()) {

                idsTreinos.add(
                        rs.getInt(
                                "id_treino"
                        )
                );

                dadosTreinos.add(
                        rs.getString(
                                "nome_treino"
                        )
                );
            }

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

    private void mostrarTreinos() {

        carregarTreinos();

        if (dadosTreinos.isEmpty()) {

            new AlertDialog.Builder(
                    requireContext()
            )
                    .setTitle(
                            "Treinos cadastrados"
                    )
                    .setMessage(
                            "Nenhum treino cadastrado."
                    )
                    .setPositiveButton(
                            "FECHAR",
                            null
                    )
                    .show();

            return;
        }

        String[] treinos =
                dadosTreinos.toArray(
                        new String[0]
                );

        new AlertDialog.Builder(
                requireContext()
        )
                .setTitle(
                        "Treinos cadastrados"
                )
                .setItems(
                        treinos,
                        (dialog, which) -> {

                            idTreinoSelecionado =
                                    idsTreinos.get(
                                            which
                                    );

                            edtNomeTreino.setText(
                                    dadosTreinos.get(
                                            which
                                    )
                            );

                            btAlterarTreino.setVisibility(
                                    View.VISIBLE
                            );

                            btExcluirTreino.setVisibility(
                                    View.VISIBLE
                            );

                            marcarExercicios();
                        }
                )
                .setNegativeButton(
                        "FECHAR",
                        null
                )
                .show();
    }

    private void carregarExercicios() {

        dadosExercicios.clear();
        idsExercicios.clear();

        try {

            con =
                    ConexaoMySQL.conectar();

            String sql =
                    "SELECT id_exercicio, nome " +
                            "FROM exercicio " +
                            "WHERE id_usuario = ? " +
                            "ORDER BY nome";

            stmt =
                    con.prepareStatement(sql);

            stmt.setInt(
                    1,
                    idUsuario
            );

            rs =
                    stmt.executeQuery();

            while (rs.next()) {

                idsExercicios.add(
                        rs.getInt(
                                "id_exercicio"
                        )
                );

                dadosExercicios.add(
                        rs.getString(
                                "nome"
                        )
                );
            }

            adaptadorExercicios =
                    new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout
                                    .simple_list_item_multiple_choice,
                            dadosExercicios
                    );

            listaExercicios.setAdapter(
                    adaptadorExercicios
            );

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

    private void criarTreino() {

        String nome =
                edtNomeTreino
                        .getText()
                        .toString()
                        .trim();

        if (nome.isEmpty()) {

            edtNomeTreino.setError(
                    "Digite o nome do treino"
            );

            return;
        }

        try {

            con =
                    ConexaoMySQL.conectar();

            String sql =
                    "INSERT INTO treino " +
                            "(id_usuario, nome_treino) " +
                            "VALUES (?, ?)";

            stmt =
                    con.prepareStatement(sql);

            stmt.setInt(
                    1,
                    idUsuario
            );

            stmt.setString(
                    2,
                    nome
            );

            stmt.executeUpdate();

            Toast.makeText(
                    requireContext(),
                    "Treino criado!",
                    Toast.LENGTH_SHORT
            ).show();

            limparTela();
            carregarTreinos();

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "Erro ao criar treino",
                    Toast.LENGTH_SHORT
            ).show();

            e.printStackTrace();

        } finally {

            fecharConexao();
        }
    }

    private void alterarTreino() {

        if (idTreinoSelecionado == -1) {

            Toast.makeText(
                    requireContext(),
                    "Selecione um treino",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String nome =
                edtNomeTreino
                        .getText()
                        .toString()
                        .trim();

        if (nome.isEmpty()) {

            edtNomeTreino.setError(
                    "Digite o nome do treino"
            );

            return;
        }

        try {

            con =
                    ConexaoMySQL.conectar();

            String sql =
                    "UPDATE treino " +
                            "SET nome_treino = ? " +
                            "WHERE id_treino = ? " +
                            "AND id_usuario = ?";

            stmt =
                    con.prepareStatement(sql);

            stmt.setString(
                    1,
                    nome
            );

            stmt.setInt(
                    2,
                    idTreinoSelecionado
            );

            stmt.setInt(
                    3,
                    idUsuario
            );

            stmt.executeUpdate();

            Toast.makeText(
                    requireContext(),
                    "Treino alterado!",
                    Toast.LENGTH_SHORT
            ).show();

            carregarTreinos();

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "Erro ao alterar treino",
                    Toast.LENGTH_SHORT
            ).show();

            e.printStackTrace();

        } finally {

            fecharConexao();
        }
    }

    private void excluirTreino() {

        if (idTreinoSelecionado == -1) {

            Toast.makeText(
                    requireContext(),
                    "Selecione um treino",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        new AlertDialog.Builder(
                requireContext()
        )
                .setTitle(
                        "Excluir treino?"
                )
                .setMessage(
                        "Deseja realmente excluir este treino?"
                )
                .setNegativeButton(
                        "CANCELAR",
                        null
                )
                .setPositiveButton(
                        "EXCLUIR",
                        (dialog, which) ->
                                executarExclusaoTreino()
                )
                .show();
    }

    private void executarExclusaoTreino() {

        try {

            con =
                    ConexaoMySQL.conectar();

            String sql =
                    "DELETE FROM treino " +
                            "WHERE id_treino = ? " +
                            "AND id_usuario = ?";

            stmt =
                    con.prepareStatement(sql);

            stmt.setInt(
                    1,
                    idTreinoSelecionado
            );

            stmt.setInt(
                    2,
                    idUsuario
            );

            stmt.executeUpdate();

            Toast.makeText(
                    requireContext(),
                    "Treino excluído!",
                    Toast.LENGTH_SHORT
            ).show();

            limparTela();
            carregarTreinos();

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

    private void salvarExercicios() {

        if (idTreinoSelecionado == -1) {

            Toast.makeText(
                    requireContext(),
                    "Selecione um treino",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        try {

            con =
                    ConexaoMySQL.conectar();

            String sql =
                    "DELETE FROM treino_exercicio " +
                            "WHERE id_treino = ?";

            stmt =
                    con.prepareStatement(sql);

            stmt.setInt(
                    1,
                    idTreinoSelecionado
            );

            stmt.executeUpdate();

            sql =
                    "INSERT INTO treino_exercicio " +
                            "(id_treino, id_exercicio, ordem) " +
                            "VALUES (?, ?, ?)";

            stmt =
                    con.prepareStatement(sql);

            int ordem = 1;

            for (
                    int i = 0;
                    i < listaExercicios.getCount();
                    i++
            ) {

                if (
                        listaExercicios
                                .isItemChecked(i)
                ) {

                    stmt.setInt(
                            1,
                            idTreinoSelecionado
                    );

                    stmt.setInt(
                            2,
                            idsExercicios.get(i)
                    );

                    stmt.setInt(
                            3,
                            ordem
                    );

                    stmt.executeUpdate();

                    ordem++;
                }
            }

            Toast.makeText(
                    requireContext(),
                    "Exercícios salvos!",
                    Toast.LENGTH_SHORT
            ).show();

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "Erro ao salvar exercícios",
                    Toast.LENGTH_SHORT
            ).show();

            e.printStackTrace();

        } finally {

            fecharConexao();
        }
    }

    private void marcarExercicios() {

        listaExercicios.clearChoices();

        try {

            con =
                    ConexaoMySQL.conectar();

            String sql =
                    "SELECT id_exercicio " +
                            "FROM treino_exercicio " +
                            "WHERE id_treino = ?";

            stmt =
                    con.prepareStatement(sql);

            stmt.setInt(
                    1,
                    idTreinoSelecionado
            );

            rs =
                    stmt.executeQuery();

            while (rs.next()) {

                int idExercicio =
                        rs.getInt(
                                "id_exercicio"
                        );

                for (
                        int i = 0;
                        i < idsExercicios.size();
                        i++
                ) {

                    if (
                            idsExercicios.get(i)
                                    == idExercicio
                    ) {

                        listaExercicios
                                .setItemChecked(
                                        i,
                                        true
                                );

                        break;
                    }
                }
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

    private void limparTela() {

        idTreinoSelecionado = -1;

        edtNomeTreino.setText("");

        listaExercicios.clearChoices();

        btAlterarTreino.setVisibility(
                View.GONE
        );

        btExcluirTreino.setVisibility(
                View.GONE
        );
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