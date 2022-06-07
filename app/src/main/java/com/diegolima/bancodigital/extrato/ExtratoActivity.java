package com.diegolima.bancodigital.extrato;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.diegolima.bancodigital.R;

public class ExtratoActivity extends AppCompatActivity {

	private ProgressBar progressBar;
	private TextView textInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_extrato);

		configToolbar();
		iniciaComponentes();
	}

	private void iniciaComponentes() {
		textInfo = findViewById(R.id.textInfo);
		progressBar = findViewById(R.id.progressBar);
	}

	private void configToolbar(){
		TextView textTitulo = findViewById(R.id.textTitulo);
		textTitulo.setText("Depositar");

		findViewById(R.id.ibVoltar).setOnClickListener(v -> finish());
	 }
}