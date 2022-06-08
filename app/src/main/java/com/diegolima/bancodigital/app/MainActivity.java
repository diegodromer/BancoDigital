package com.diegolima.bancodigital.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.diegolima.bancodigital.R;
import com.diegolima.bancodigital.adapter.ExtratoAdapter;
import com.diegolima.bancodigital.deposito.DepositoFormActivity;
import com.diegolima.bancodigital.extrato.ExtratoActivity;
import com.diegolima.bancodigital.helper.FirebaseHelper;
import com.diegolima.bancodigital.helper.GetMask;
import com.diegolima.bancodigital.model.Extrato;
import com.diegolima.bancodigital.model.Usuario;
import com.diegolima.bancodigital.recarga.RecargaFormActivity;
import com.diegolima.bancodigital.transferencia.TransferenciaFormActivity;
import com.diegolima.bancodigital.usuario.MinhaContaActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	private final List<Extrato> extratoList = new ArrayList<>();
	private ExtratoAdapter extratoAdapter;
	private RecyclerView rvExtrato;

	private TextView textSaldo;
	private Usuario usuario;
	private ProgressBar progressBar;
	private TextView textInfo;
	private ImageView imagemPerfil;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		iniciaComponentes();
		configCliques();
		configRv();
	}

	@Override
	protected void onStart() {
		super.onStart();
		recuperaDados();
	}

	private void recuperaExtrato() {
		DatabaseReference extratoRef = FirebaseHelper.getDatabaseReference()
				.child("extratos")
				.child(FirebaseHelper.getIdFirebase());
		extratoRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				if (snapshot.exists()) {
					extratoList.clear();

					for (DataSnapshot ds : snapshot.getChildren()) {
						Extrato extrato = ds.getValue(Extrato.class);
						extratoList.add(extrato);
						if (extratoList.size() == 6){
							break;
						}
					}

					textInfo.setText("");
				} else {
					textInfo.setText("Nenhuma movimentação encontrada.");
				}

				Collections.reverse(extratoList);
				progressBar.setVisibility(View.GONE);
				extratoAdapter.notifyDataSetChanged();

			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});
	}

	private void configRv(){
		rvExtrato.setLayoutManager(new LinearLayoutManager(this));
		rvExtrato.setHasFixedSize(true);
		extratoAdapter = new ExtratoAdapter(extratoList, getBaseContext());
		rvExtrato.setAdapter(extratoAdapter);
	}

	private void recuperaDados() {
		recuperaUsuario();
		recuperaExtrato();
	}

	private void recuperaUsuario() {
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

		if (usuario.getUrlImagem() != null){
			Picasso.get().load(usuario.getUrlImagem())
					.placeholder(R.drawable.loading)
					.into(imagemPerfil);

		}

		textInfo.setText("");
		progressBar.setVisibility(View.GONE);
	}

	private void configCliques() {
		findViewById(R.id.cardDeposito).setOnClickListener(view -> {
			startActivity(new Intent(this, DepositoFormActivity.class));
		});

		imagemPerfil.setOnClickListener(v -> {
			if (usuario != null) {
				Intent intent = new Intent(this, MinhaContaActivity.class);
				intent.putExtra("usuario", usuario);
				startActivity(intent);
			} else {
				Toast.makeText(this, "Ainda estamos recuperando as informações.", Toast.LENGTH_SHORT).show();
			}
		});

		findViewById(R.id.cardRecarga).setOnClickListener(v -> {
			startActivity(new Intent(this, RecargaFormActivity.class));
		});

		findViewById(R.id.cardTransferir).setOnClickListener(v -> {
			startActivity(new Intent(this, TransferenciaFormActivity.class));
		});

		findViewById(R.id.cardExtrato).setOnClickListener(v -> {
			verTodosMovimentos();
		});

		findViewById(R.id.textVerTodas).setOnClickListener(v -> {
			verTodosMovimentos();
		});
	}

	private void verTodosMovimentos() {
		startActivity(new Intent(this, ExtratoActivity.class));
	}

	private void iniciaComponentes() {
		textSaldo = findViewById(R.id.textSaldo);
		textInfo = findViewById(R.id.textInfo);
		progressBar = findViewById(R.id.progressBar);
		rvExtrato = findViewById(R.id.rvExtrato);
		imagemPerfil = findViewById(R.id.imagemPerfil);
	}
}