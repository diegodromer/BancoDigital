package com.diegolima.bancodigital.notificacoes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.diegolima.bancodigital.R;
import com.diegolima.bancodigital.adapter.NotificacaoAdapter;
import com.diegolima.bancodigital.helper.FirebaseHelper;
import com.diegolima.bancodigital.model.Notificacao;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificacoesActivity extends AppCompatActivity implements NotificacaoAdapter.OnClick {

	private NotificacaoAdapter notificacaoAdapter;
	private final List<Notificacao> notificacaoList = new ArrayList<>();

	private RecyclerView rvNotificacoes;

	private ProgressBar progressBar;
	private TextView textInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notificacoes);

		iniciaComponentes();
		configToolbar();
		configRv();
		recuperaNotificacoes();
	}

	private void configRv() {
		rvNotificacoes.setLayoutManager(new LinearLayoutManager(this));
		rvNotificacoes.setHasFixedSize(true);
		notificacaoAdapter = new NotificacaoAdapter(notificacaoList, getBaseContext(), this);
		rvNotificacoes.setAdapter(notificacaoAdapter);
	}

	private void recuperaNotificacoes() {
		DatabaseReference notificacaoRef = FirebaseHelper.getDatabaseReference()
				.child("notificacoes")
				.child(FirebaseHelper.getIdFirebase());
		notificacaoRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				if (snapshot.exists()){
					notificacaoList.clear();
					for (DataSnapshot ds : snapshot.getChildren()){
						Notificacao notificacao = ds.getValue(Notificacao.class);
						notificacaoList.add(notificacao);
					}
					textInfo.setText("");
				}else{
					textInfo.setText("Você não tem nenhuma notificação");
				}

				Collections.reverse(notificacaoList);
				progressBar.setVisibility(View.GONE);
				notificacaoAdapter.notifyDataSetChanged();
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});
	}

	private void configToolbar(){
		TextView textTitulo = findViewById(R.id.textTitulo);
		textTitulo.setText("Notificações");

		findViewById(R.id.ibVoltar).setOnClickListener(v -> finish());
	}

	private void iniciaComponentes() {
		textInfo = findViewById(R.id.textInfo);
		progressBar = findViewById(R.id.progressBar);
		progressBar = findViewById(R.id.progressBar);
		rvNotificacoes = findViewById(R.id.rvNotificacoes);
	}

	@Override
	public void OnClickListener(Notificacao notificacao) {

	}
}