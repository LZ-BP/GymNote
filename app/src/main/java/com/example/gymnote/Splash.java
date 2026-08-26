package com.example.gymnote;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splash extends AppCompatActivity {

    View progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_splash
        );

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets systemBars =
                            insets.getInsets(
                                    WindowInsetsCompat.Type.systemBars()
                            );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        progress =
                findViewById(
                        R.id.progress
                );

        iniciarAnimacao();

        new Handler(
                Looper.getMainLooper()
        ).postDelayed(
                () -> {

                    Sessao sessao =
                            new Sessao(
                                    Splash.this
                            );

                    Intent tela;

                    if (
                            sessao.getIdUsuario() != -1
                    ) {

                        tela =
                                new Intent(
                                        Splash.this,
                                        MainActivity.class
                                );

                    } else {

                        tela =
                                new Intent(
                                        Splash.this,
                                        Login.class
                                );
                    }

                    startActivity(tela);

                    finish();

                },
                5000
        );
    }

    private void iniciarAnimacao() {

        progress.animate()
                .translationX(40f)
                .setDuration(750)
                .withEndAction(() ->
                        progress.animate()
                                .translationX(-40f)
                                .setDuration(750)
                                .withEndAction(
                                        this::iniciarAnimacao
                                )
                )
                .start();
    }
}