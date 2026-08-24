package com.example.gymnote.ui.menu;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gymnote.R;

import java.util.HashMap;
import java.util.Map;

public class TreinosFragment extends Fragment {

    Spinner spTreino;
    TableLayout tabelaExercicios;
    Button btSalvarTreino;

    Map<String, String[]> exerciciosPorTreino = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View V = inflater.inflate(
                R.layout.fragment_treinos,
                container,
                false
        );

        spTreino = V.findViewById(R.id.spTreino);
        tabelaExercicios = V.findViewById(R.id.tabelaExercicios);
        btSalvarTreino = V.findViewById(R.id.btSalvarTreino);

        criarTreinos();

        String[] treinos = {
                "Peito e Bíceps",
                "Push",
                "Pull",
                "Legs"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                treinos
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spTreino.setAdapter(adapter);

        spTreino.setOnItemSelectedListener(
                new android.widget.AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(
                            android.widget.AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {

                        mostrarExercicios(treinos[position]);
                    }

                    @Override
                    public void onNothingSelected(
                            android.widget.AdapterView<?> parent) {
                    }
                }
        );

        btSalvarTreino.setOnClickListener(view -> {

            // Depois vamos ligar isso ao MySQL.
            // Aqui serão salvos peso, séries e repetições.
        });

        mostrarExercicios(treinos[0]);

        return V;
    }

    private void criarTreinos() {

        exerciciosPorTreino.put(
                "Peito e Bíceps",
                new String[]{
                        "Supino reto",
                        "Supino inclinado",
                        "Rosca Scott",
                        "Rosca direta"
                }
        );

        exerciciosPorTreino.put(
                "Push",
                new String[]{
                        "Supino reto",
                        "Desenvolvimento",
                        "Tríceps pulley"
                }
        );

        exerciciosPorTreino.put(
                "Pull",
                new String[]{
                        "Puxada frontal",
                        "Remada baixa",
                        "Rosca Scott"
                }
        );

        exerciciosPorTreino.put(
                "Legs",
                new String[]{
                        "Agachamento",
                        "Leg Press",
                        "Cadeira extensora",
                        "Mesa flexora"
                }
        );
    }

    private void mostrarExercicios(String treino) {

        tabelaExercicios.removeAllViews();

        adicionarCabecalho();

        String[] exercicios = exerciciosPorTreino.get(treino);

        if (exercicios == null) {
            return;
        }

        for (String nome : exercicios) {

            TableRow linha = new TableRow(requireContext());

            TextView txtExercicio = criarTexto(nome);

            EditText peso = criarCampo("kg");
            EditText series = criarCampo("Séries");
            EditText repeticoes = criarCampo("Reps");

            linha.addView(txtExercicio);
            linha.addView(peso);
            linha.addView(series);
            linha.addView(repeticoes);

            tabelaExercicios.addView(linha);
        }
    }

    private void adicionarCabecalho() {

        TableRow linha = new TableRow(requireContext());

        linha.addView(criarTexto("Exercício"));
        linha.addView(criarTexto("Peso"));
        linha.addView(criarTexto("Séries"));
        linha.addView(criarTexto("Reps"));

        tabelaExercicios.addView(linha);
    }

    private TextView criarTexto(String texto) {

        TextView textView = new TextView(requireContext());

        textView.setText(texto);
        textView.setTextSize(16);
        textView.setPadding(15, 15, 15, 15);
        textView.setGravity(Gravity.CENTER);

        return textView;
    }

    private EditText criarCampo(String hint) {

        EditText campo = new EditText(requireContext());

        campo.setHint(hint);
        campo.setTextSize(16);
        campo.setPadding(10, 10, 10, 10);
        campo.setGravity(Gravity.CENTER);
        campo.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER |
                        android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        return campo;
    }
}