package com.example.gymnote.ui.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.gymnote.R;

public class CalendarioFragment extends Fragment {

    CalendarView calendario;
    TextView dataSelecionada;
    Button btTreino;

    String data;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View V = inflater.inflate(R.layout.fragment_calendario, container, false);

        calendario = V.findViewById(R.id.calendario);
        dataSelecionada = V.findViewById(R.id.dataSelecionada);
        btTreino = V.findViewById(R.id.btTreino);

        calendario.setOnDateChangeListener((view, year, month, dayOfMonth) -> {

            data = dayOfMonth + "/" + (month + 1) + "/" + year;

            dataSelecionada.setText("Data selecionada: " + data);
        });

        btTreino.setOnClickListener(v -> {

            if (data == null) {
                return;
            }

            Bundle dados = new Bundle();
            dados.putString("dataTreino", data);

            Navigation.findNavController(v)
                    .navigate(R.id.nav_treinos, dados);
        });

        return V;
    }
}