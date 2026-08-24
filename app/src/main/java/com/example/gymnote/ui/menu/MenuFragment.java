package com.example.gymnote.ui.menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.fragment.app.Fragment;

import com.example.gymnote.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MenuFragment extends Fragment {

    TextView txtHoje;
    Button btVerTreinoHoje;
    Button btExercicios;
    Button btHistorico;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View V = inflater.inflate(
                R.layout.fragment_menu,
                container,
                false
        );

        txtHoje = V.findViewById(R.id.txtHoje);
        btVerTreinoHoje = V.findViewById(R.id.btVerTreinoHoje);
        btExercicios = V.findViewById(R.id.btExercicios);
        btHistorico = V.findViewById(R.id.btHistorico);

        String hoje = new SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
        ).format(new Date());

        txtHoje.setText("Hoje: " + hoje);

        btVerTreinoHoje.setOnClickListener(view -> {

            Navigation.findNavController(view)
                    .navigate(R.id.nav_treinos);
        });

        btExercicios.setOnClickListener(view -> {

            Navigation.findNavController(view)
                    .navigate(R.id.nav_exercicio);
        });

        btHistorico.setOnClickListener(view -> {

            Navigation.findNavController(view)
                    .navigate(R.id.nav_historico);
        });

        return V;
    }
}