package com.diegolima.bancodigital.transferencia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.diegolima.bancodigital.R;
import com.diegolima.bancodigital.helper.FirebaseHelper;
import com.diegolima.bancodigital.helper.GetMask;
import com.diegolima.bancodigital.model.Extrato;
import com.diegolima.bancodigital.model.Notificacao;
import com.diegolima.bancodigital.model.Transferencia;
import com.diegolima.bancodigital.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class TransferenciaConfirmaActivity extends AppCompatActivity {

	private TextView textValor;
	private TextView textUsuario;
	private ImageView imagemUsuario;

	private Usuario usuarioDestino;
	private Usuario usuarioOrigem;
	private Transferencia transferencia;

	private AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transferencia_confirma);

		configToolbar();

		iniciaComponentes();

		configDados();

		recuperaUsuarioOrigem();

	}

	private void recuperaUsuarioOrigem(){
		DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
				.child("usuarios")
				.child(transferencia.getIdUserOrigem());
		usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				usuarioOrigem = snapshot.getValue(Usuario.class);
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});
	}

	private void enviaNotificacao(String idOperacao){
		Notificacao notificacao = new Notificacao();
		notificacao.setOperacao("TRANSFERENCIA");
		notificacao.setIdDestinario(usuarioDestino.getId());
		notificacao.setIdEmitente(usuarioOrigem.getId());
		notificacao.setIdOperacao(idOperacao);
		notificacao.enviar();
	}

	public void confirmarTransferencia(View view){
		if(transferencia != null){
			if(usuarioOrigem.getSaldo() >= transferencia.getValor()){

				usuarioOrigem.setSaldo(usuarioOrigem.getSaldo() - transferencia.getValor());
				usuarioOrigem.atualizarSaldo();

				usuarioDestino.setSaldo(usuarioDestino.getSaldo() + transferencia.getValor());
				usuarioDestino.atualizarSaldo();

				// Origem
				salvarExtrato(usuarioOrigem, "SAIDA");

				// Destino
				salvarExtrato(usuarioDestino, "ENTRADA");

			}else {
				showDialog("Saldo insuficiente.");
			}
		}
	}

	private void salvarExtrato(Usuario usuario, String tipo){

		Extrato extrato = new Extrato();
		extrato.setOperacao("TRANSFERENCIA");
		extrato.setValor(transferencia.getValor());
		extrato.setTipo(tipo);

		DatabaseReference extratoRef = FirebaseHelper.getDatabaseReference()
				.child("extratos")
				.child(usuario.getId())
				.child(extrato.getId());
		extratoRef.setValue(extrato).addOnCompleteListener(task -> {
			if(task.isSuccessful()){

				DatabaseReference updateExtrato = extratoRef
						.child("data");
				updateExtrato.setValue(ServerValue.TIMESTAMP);

				salvarTransferencia(extrato);

			}else {
				showDialog("Não foi possível efetuar o deposito, tente mais tarde.");
			}
		});

	}

	private void salvarTransferencia(Extrato extrato){

		transferencia.setId(extrato.getId());

		DatabaseReference transferenciaRef = FirebaseHelper.getDatabaseReference()
				.child("transferencias")
				.child(transferencia.getId());
		transferenciaRef.setValue(transferencia).addOnCompleteListener(task -> {
			if(task.isSuccessful()){

				DatabaseReference updateTransferencia = transferenciaRef
						.child("data");
				updateTransferencia.setValue(ServerValue.TIMESTAMP);

				if(extrato.getTipo().equals("ENTRADA")){

					enviaNotificacao(extrato.getId());

					Intent intent = new Intent(this, TransferenciaReciboActivity.class);
					intent.putExtra("idTransferencia", transferencia.getId());
					startActivity(intent);
				}

			}else {
				showDialog("Não foi possível completar a transferência.");
			}
		});
	}

	private void showDialog(String msg){
		AlertDialog.Builder builder = new AlertDialog.Builder(
				this, R.style.CustomAlertDialog
		);

		View view = getLayoutInflater().inflate(R.layout.layout_dialog_info, null);

		TextView textTitulo = view.findViewById(R.id.textTitulo);
		textTitulo.setText("Atenção");

		TextView mensagem = view.findViewById(R.id.textMensagem);
		mensagem.setText(msg);

		Button btnOK = view.findViewById(R.id.btnOK);
		btnOK.setOnClickListener(v -> dialog.dismiss());

		builder.setView(view);

		dialog = builder.create();
		dialog.show();

	}

	private void configDados(){
		usuarioDestino = (Usuario) getIntent().getSerializableExtra("usuario");
		transferencia = (Transferencia) getIntent().getSerializableExtra("transferencia");

		textUsuario.setText(usuarioDestino.getNome());
		if(usuarioDestino.getUrlImagem() != null){
			Picasso.get().load(usuarioDestino.getUrlImagem())
					.placeholder(R.drawable.loading)
					.into(imagemUsuario);
		}
		textValor.setText(getString(R.string.text_valor, GetMask.getValor(transferencia.getValor())));

	}

	private void configToolbar(){
		TextView textTitulo = findViewById(R.id.textTitulo);
		textTitulo.setText("Confirme os dados");
	}

	private void iniciaComponentes(){
		textValor = findViewById(R.id.textValor);
		textUsuario = findViewById(R.id.textUsuario);
		imagemUsuario = findViewById(R.id.imagemUsuario);
	}

}