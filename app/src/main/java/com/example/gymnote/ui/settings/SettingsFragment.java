package com.example.gymnote.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.gymnote.Login;
import com.example.gymnote.R;
import com.example.gymnote.Sessao;

public class SettingsFragment extends Fragment {

    TextView txtNomeUsuario;
    TextView txtEmailUsuario;

    Button btLogout;

    Sessao sessao;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View V = inflater.inflate(
                R.layout.fragment_settings,
                container,
                false
        );

        sessao =
                new Sessao(requireContext());

        txtNomeUsuario =
                V.findViewById(
                        R.id.txtNomeUsuario
                );

        txtEmailUsuario =
                V.findViewById(
                        R.id.txtEmailUsuario
                );

        btLogout =
                V.findViewById(
                        R.id.btLogout
                );

        carregarUsuario();

        btLogout.setOnClickListener(
                view -> confirmarLogout()
        );

        return V;
    }

    private void carregarUsuario() {

        txtNomeUsuario.setText(
                sessao.getNomeUsuario()
        );

        txtEmailUsuario.setText(
                sessao.getEmailUsuario()
        );
    }

    private void confirmarLogout() {

        new AlertDialog.Builder(
                requireContext()
        )
                .setTitle(
                        "Sair da conta"
                )
                .setMessage(
                        "Deseja realmente sair da conta?"
                )
                .setNegativeButton(
                        "CANCELAR",
                        null
                )
                .setPositiveButton(
                        "SAIR",
                        (dialog, which) ->
                                fazerLogout()
                )
                .show();
    }

    private void fazerLogout() {

        sessao.limpar();

        Intent intent =
                new Intent(
                        requireContext(),
                        Login.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
    }
}