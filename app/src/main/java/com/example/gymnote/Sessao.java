package com.example.gymnote;

import android.content.Context;
import android.content.SharedPreferences;

public class Sessao {

    private static final String NOME = "sessao";

    private final SharedPreferences preferences;

    public Sessao(Context context) {
        preferences = context.getSharedPreferences(
                NOME,
                Context.MODE_PRIVATE
        );
    }

    public void salvarUsuario(
            int idUsuario,
            String nome,
            String email) {

        preferences.edit()
                .putInt("idUsuario", idUsuario)
                .putString("nomeUsuario", nome)
                .putString("emailUsuario", email)
                .apply();
    }

    public int getIdUsuario() {
        return preferences.getInt(
                "idUsuario",
                -1
        );
    }

    public String getNomeUsuario() {
        return preferences.getString(
                "nomeUsuario",
                ""
        );
    }

    public String getEmailUsuario() {
        return preferences.getString(
                "emailUsuario",
                ""
        );
    }

    public void limpar() {
        preferences.edit()
                .clear()
                .apply();
    }
}