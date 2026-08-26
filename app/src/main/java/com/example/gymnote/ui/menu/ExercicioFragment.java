package com.example.gymnote.ui.menu;

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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.gymnote.ConexaoMySQL;
import com.example.gymnote.R;
import com.example.gymnote.Sessao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ExercicioFragment extends Fragment {

    EditText edtNomeExercicio;
    EditText edtMusculo;

    Button btSalvarExercicio;

    ListView listaExercicios;

    ArrayList<String> dadosLista =
            new ArrayList<>();

    ArrayList<Integer> idsLista =
            new ArrayList<>();

    ArrayAdapter<String> adaptador;

    Connection con;
    PreparedStatement stmt;
    ResultSet rs;

    int idExercicio = -1;

    Sessao sessao;
    int idUsuario;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View V =
                inflater.inflate(
                        R.layout.fragment_exercicio,
                        container,
                        false
                );

        sessao =
                new Sessao(requireContext());

        idUsuario =
                sessao.getIdUsuario();

        edtNomeExercicio =
                V.findViewById(
                        R.id.edtNomeExercicio
                );

        edtMusculo =
                V.findViewById(
                        R.id.edtMusculo
                );

        btSalvarExercicio =
                V.findViewById(
                        R.id.btSalvarExercicio
                );

        listaExercicios =
                V.findViewById(
                        R.id.listaExercicios
                );

        carregarLista();

        btSalvarExercicio.setOnClickListener(
                view -> salvarExercicio()
        );

        listaExercicios.setOnItemClickListener(
                (parent, view, position, id) ->
                        abrirOpcoes(position)
        );

        return V;
    }

    private void carregarLista() {

        dadosLista.clear();
        idsLista.clear();

        try {

            con =
                    ConexaoMySQL.conectar();

            String sql =
                    "SELECT id_exercicio, nome, grupo_muscular " +
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

                int id =
                        rs.getInt(
                                "id_exercicio"
                        );

                String nome =
                        rs.getString(
                                "nome"
                        );

                String musculo =
                        rs.getString(
                                "grupo_muscular"
                        );

                if (musculo == null) {
                    musculo = "";
                }

                String texto =
                        nome;

                if (!musculo.isEmpty()) {

                    texto +=
                            " - " +
                                    musculo;
                }

                idsLista.add(id);
                dadosLista.add(texto);
            }

            adaptador =
                    new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            dadosLista
                    );

            listaExercicios.setAdapter(
                    adaptador
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

    private void salvarExercicio() {

        String nome =
                edtNomeExercicio
                        .getText()
                        .toString()
                        .trim();

        String musculo =
                edtMusculo
                        .getText()
                        .toString()
                        .trim();

        if (nome.isEmpty()) {

            edtNomeExercicio.setError(
                    "Digite o nome do exercício"
            );

            edtNomeExercicio.requestFocus();

            return;
        }

        try {

            con =
                    ConexaoMySQL.conectar();

            if (idExercicio == -1) {

                String sql =
                        "INSERT INTO exercicio " +
                                "(id_usuario, nome, grupo_muscular) " +
                                "VALUES (?, ?, ?)";

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

                stmt.setString(
                        3,
                        musculo
                );

                stmt.executeUpdate();

                Toast.makeText(
                        requireContext(),
                        "Exercício cadastrado!",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                String sql =
                        "UPDATE exercicio " +
                                "SET nome = ?, grupo_muscular = ? " +
                                "WHERE id_exercicio = ? " +
                                "AND id_usuario = ?";

                stmt =
                        con.prepareStatement(sql);

                stmt.setString(
                        1,
                        nome
                );

                stmt.setString(
                        2,
                        musculo
                );

                stmt.setInt(
                        3,
                        idExercicio
                );

                stmt.setInt(
                        4,
                        idUsuario
                );

                stmt.executeUpdate();

                Toast.makeText(
                        requireContext(),
                        "Exercício alterado!",
                        Toast.LENGTH_SHORT
                ).show();

                idExercicio = -1;

                btSalvarExercicio.setText(
                        "SALVAR EXERCÍCIO"
                );
            }

            limparCampos();
            carregarLista();

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "Erro ao salvar exercício",
                    Toast.LENGTH_SHORT
            ).show();

            e.printStackTrace();

        } finally {

            fecharConexao();
        }
    }

    private void abrirOpcoes(
            int position) {

        String[] opcoes = {
                "Alterar",
                "Excluir"
        };

        new AlertDialog.Builder(
                requireContext()
        )
                .setTitle("Exercício")
                .setItems(
                        opcoes,
                        (dialog, which) -> {

                            if (which == 0) {

                                alterarExercicio(
                                        position
                                );

                            } else {

                                excluirExercicio(
                                        position
                                );
                            }
                        }
                )
                .show();
    }

    private void alterarExercicio(
            int position) {

        int id =
                idsLista.get(position);

        try {

            con =
                    ConexaoMySQL.conectar();

            String sql =
                    "SELECT nome, grupo_muscular " +
                            "FROM exercicio " +
                            "WHERE id_exercicio = ? " +
                            "AND id_usuario = ?";

            stmt =
                    con.prepareStatement(sql);

            stmt.setInt(
                    1,
                    id
            );

            stmt.setInt(
                    2,
                    idUsuario
            );

            rs =
                    stmt.executeQuery();

            if (rs.next()) {

                edtNomeExercicio.setText(
                        rs.getString(
                                "nome"
                        )
                );

                String musculo =
                        rs.getString(
                                "grupo_muscular"
                        );

                edtMusculo.setText(
                        musculo == null
                                ? ""
                                : musculo
                );

                idExercicio = id;

                btSalvarExercicio.setText(
                        "ALTERAR EXERCÍCIO"
                );
            }

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "Erro ao carregar exercício",
                    Toast.LENGTH_SHORT
            ).show();

            e.printStackTrace();

        } finally {

            fecharConexao();
        }
    }

    private void excluirExercicio(
            int position) {

        int id =
                idsLista.get(position);

        new AlertDialog.Builder(
                requireContext()
        )
                .setTitle(
                        "Excluir exercício"
                )
                .setMessage(
                        "Deseja realmente excluir este exercício?"
                )
                .setNegativeButton(
                        "Cancelar",
                        null
                )
                .setPositiveButton(
                        "Excluir",
                        (dialog, which) ->
                                executarExclusao(id)
                )
                .show();
    }

    private void executarExclusao(
            int id) {

        try {

            con =
                    ConexaoMySQL.conectar();

            String sql =
                    "DELETE FROM exercicio " +
                            "WHERE id_exercicio = ? " +
                            "AND id_usuario = ?";

            stmt =
                    con.prepareStatement(sql);

            stmt.setInt(
                    1,
                    id
            );

            stmt.setInt(
                    2,
                    idUsuario
            );

            stmt.executeUpdate();

            Toast.makeText(
                    requireContext(),
                    "Exercício excluído!",
                    Toast.LENGTH_SHORT
            ).show();

            carregarLista();

        } catch (Exception e) {

            Toast.makeText(
                    requireContext(),
                    "Erro ao excluir exercício",
                    Toast.LENGTH_SHORT
            ).show();

            e.printStackTrace();

        } finally {

            fecharConexao();
        }
    }

    private void limparCampos() {

        edtNomeExercicio.setText("");
        edtMusculo.setText("");

        idExercicio = -1;

        btSalvarExercicio.setText(
                "SALVAR EXERCÍCIO"
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