package com.diegolima.bancodigital.transferencia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.diegolima.bancodigital.R;
import com.diegolima.bancodigital.adapter.UsuarioAdapter;
import com.diegolima.bancodigital.cobrar.CobrancaConfirmaActivity;
import com.diegolima.bancodigital.helper.FirebaseHelper;
import com.diegolima.bancodigital.model.Cobranca;
import com.diegolima.bancodigital.model.Transferencia;
import com.diegolima.bancodigital.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelecionarUsuarioActivity extends AppCompatActivity implements UsuarioAdapter.OnClick {

	private UsuarioAdapter usuarioAdapter;
	private final List<Usuario> usuarioList = new ArrayList<>();
	private RecyclerView rvUsuarios;

	private TextView textPesquisa;
	private TextView textLimpar;
	private EditText edtPesquisa;
	private String pesquisa = "";
	private LinearLayout llPesquisa;

	private TextView textInfo;
	private ProgressBar progressBar;

	private Transferencia transferencia;
	private Cobranca cobranca;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selecionar_usuario);
		configToolbar();
		iniciaComponentes();
		configRv();
		recuperarUsuarios();
		configPesquisa();
		configCliques();
		getExtra();
	}

	private void getExtra() {
		if (getIntent().hasExtra("transferencia")){
			transferencia = (Transferencia) getIntent().getSerializableExtra("transferencia");
		}else if (getIntent().hasExtra("cobranca")){
			cobranca = (Cobranca) getIntent().getSerializableExtra("cobranca");
		}
	}

	private void configCliques(){
		textLimpar.setOnClickListener(v -> {
			pesquisa = "";
			configFiltro();
			recuperarUsuarios();
		});
	}

	private void configPesquisa() {
		edtPesquisa.setOnEditorActionListener((v, actionId, event) -> {
			if (actionId == EditorInfo.IME_ACTION_SEARCH) {
				ocultarTeclado();
				progressBar.setVisibility(View.VISIBLE);
				pesquisa = v.getText().toString();
				if (!pesquisa.equals("")) {
					configFiltro();
					pesquisarUsuarios();
				} else {
					recuperarUsuarios();
					configFiltro();
				}
			}
			return false;
		});
	}

	private void pesquisarUsuarios() {
		for (Usuario usuario : new ArrayList<>(usuarioList)){
			if (!usuario.getNome().toLowerCase().contains(pesquisa.toLowerCase())){
				usuarioList.remove(usuario);
			}
		}
		if (usuarioList.isEmpty()){
			textInfo.setText("Nenhum usuário encontrado com este nome.");
		}

		progressBar.setVisibility(View.GONE);
		usuarioAdapter.notifyDataSetChanged();
	}

	private void configFiltro() {
		if (!pesquisa.equals("")) {
			textPesquisa.setText("Pesquisa: " + pesquisa);
			llPesquisa.setVisibility(View.VISIBLE);
			ocultarTeclado();
		} else {
			textPesquisa.setText("");
			llPesquisa.setVisibility(View.GONE);
		}
	}

	private void recuperarUsuarios() {
		DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
				.child("usuarios");
		usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				if (snapshot.exists()) {
					usuarioList.clear();
					for (DataSnapshot ds : snapshot.getChildren()) {

						Usuario usuario = ds.getValue(Usuario.class);

						if (usuario != null) {
							if (!usuario.getId().equals(FirebaseHelper.getIdFirebase())) {
								usuarioList.add(usuario);
							}
						}

					}
					textInfo.setText("");
				} else {
					textInfo.setText("Nenhum usuário cadastrado.");
				}

				progressBar.setVisibility(View.GONE);
				usuarioAdapter.notifyDataSetChanged();

			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});
	}

	private void configRv() {
		rvUsuarios.setLayoutManager(new LinearLayoutManager(this));
		rvUsuarios.setHasFixedSize(true);
		usuarioAdapter = new UsuarioAdapter(usuarioList, this);
		rvUsuarios.setAdapter(usuarioAdapter);
	}

	private void configToolbar() {
		TextView textTitulo = findViewById(R.id.textTitulo);
		textTitulo.setText("Selecione o usuário");

		findViewById(R.id.ibVoltar).setOnClickListener(v -> finish());
	}

	private void ocultarTeclado() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(edtPesquisa.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private void iniciaComponentes() {
		rvUsuarios = findViewById(R.id.rvUsuarios);
		textInfo = findViewById(R.id.textInfo);
		progressBar = findViewById(R.id.progressBar);
		edtPesquisa = findViewById(R.id.edtPesquisa);
		textLimpar = findViewById(R.id.textLimpar);
		textPesquisa = findViewById(R.id.textPesquisa);
		llPesquisa = findViewById(R.id.llPesquisa);
	}

	@Override
	public void OnClickListener(Usuario usuario) {

		String idUsuario = usuario.getId();

		if (transferencia != null){

			transferencia.setIdUserDestino(idUsuario);
			Intent intent = new Intent(this, TransferenciaConfirmaActivity.class);
			intent.putExtra("transferencia", transferencia);
			intent.putExtra("usuario", usuario);
			startActivity(intent);

		}else if (cobranca != null){

			cobranca.setIdDestinatario(idUsuario);
			Intent intent = new Intent(this, CobrancaConfirmaActivity.class);
			intent.putExtra("cobranca", cobranca);
			intent.putExtra("usuario", usuario);
			startActivity(intent);
		}
	}
}