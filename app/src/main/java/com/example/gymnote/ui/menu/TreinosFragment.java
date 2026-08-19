package com.example.gymnote.ui.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gymnote.R;

public class TreinosFragment extends Fragment {

    TextView txtDataTreino, txtNomeTreino;
    Button btAdicionarExercicio;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.fragment_treinos, container, false);

        txtDataTreino = V.findViewById(R.id.txtDataTreino);
        txtNomeTreino = V.findViewById(R.id.txtNomeTreino);
        btAdicionarExercicio = V.findViewById(R.id.btAdicionarExercicio);

        Bundle dados = getArguments();

        if (dados != null) {
            String data = dados.getString("dataTreino");
            txtDataTreino.setText("Data: " + data);
        }

        return V;
    }
}