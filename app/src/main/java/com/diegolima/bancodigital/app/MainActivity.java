package com.diegolima.bancodigital.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.diegolima.bancodigital.R;
import com.diegolima.bancodigital.deposito.DepositoFormActivity;
import com.diegolima.bancodigital.helper.FirebaseHelper;
import com.diegolima.bancodigital.helper.GetMask;
import com.diegolima.bancodigital.model.Usuario;
import com.diegolima.bancodigital.usuario.MinhaContaActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

	private TextView textSaldo;
	private Usuario usuario;
	private ProgressBar progressBar;
	private TextView textInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		iniciaComponentes();
		configCliques();
	}

	@Override
	protected void onStart() {
		super.onStart();
		recuperaDados();
	}

	private void recuperaDados() {
		DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
				.child("usuarios")
				.child(FirebaseHelper.getIdFirebase());
		usuarioRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				usuario = snapshot.getValue(Usuario.class);
				configDados();
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
			}
		});
	}

	private void configDados() {
		textSaldo.setText(getString(R.string.text_valor, GetMask.getValor(usuario.getSaldo())));
		textInfo.setText("");
		progressBar.setVisibility(View.GONE);
	}

	private void configCliques(){
		findViewById(R.id.cardDeposito).setOnClickListener(view -> {
			startActivity(new Intent(this, DepositoFormActivity.class));
		});

		findViewById(R.id.minhaConta).setOnClickListener(v -> {
			if (usuario != null){
				Intent intent = new Intent(this, MinhaContaActivity.class);
				intent.putExtra("usuario", usuario);
				startActivity(intent);
			}else{
				Toast.makeText(this, "Ainda estamos recuperando as informações.", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void iniciaComponentes() {
		textSaldo = findViewById(R.id.textSaldo);
		textInfo = findViewById(R.id.textInfo);
		progressBar = findViewById(R.id.progressBar);
	}
}