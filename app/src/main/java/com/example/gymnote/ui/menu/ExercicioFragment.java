package com.example.gymnote.ui.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gymnote.R;

public class ExercicioFragment extends Fragment {

    Spinner spMusculo;
    EditText exercicio;
    Button btSalvarExercicio;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View V = inflater.inflate(
                R.layout.fragment_exercicio,
                container,
                false
        );

        spMusculo = V.findViewById(R.id.spMusculo);
        exercicio = V.findViewById(R.id.exercicio);
        btSalvarExercicio = V.findViewById(R.id.btSalvarExercicio);

        String[] musculos = {
                "Peito",
                "Costas",
                "Ombros",
                "Bíceps",
                "Tríceps",
                "Quadríceps",
                "Posterior",
                "Glúteos",
                "Panturrilha",
                "Abdômen"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                musculos
        );

        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spMusculo.setAdapter(adapter);

        return V;
    }
}