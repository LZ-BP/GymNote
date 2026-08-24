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
import androidx.navigation.Navigation;

import com.example.gymnote.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CalendarioFragment extends Fragment {

    CalendarView calendario;
    TextView txtDataSelecionada;
    Button btVerTreinos;

    String dataSelecionada;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View V = inflater.inflate(
                R.layout.fragment_calendario,
                container,
                false
        );

        calendario = V.findViewById(R.id.calendario);
        txtDataSelecionada = V.findViewById(R.id.txtDataSelecionada);
        btVerTreinos = V.findViewById(R.id.btVerTreinos);

        dataSelecionada = new SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
        ).format(new Date());

        txtDataSelecionada.setText(dataSelecionada);

        calendario.setOnDateChangeListener((view, year, month, dayOfMonth) -> {

            dataSelecionada = String.format(
                    Locale.getDefault(),
                    "%02d/%02d/%04d",
                    dayOfMonth,
                    month + 1,
                    year
            );

            txtDataSelecionada.setText(dataSelecionada);
        });

        btVerTreinos.setOnClickListener(view -> {

            Bundle dados = new Bundle();
            dados.putString("data", dataSelecionada);

            Navigation.findNavController(view)
                    .navigate(
                            R.id.nav_treinos,
                            dados
                    );
        });

        return V;
    }
}