package com.diegolima.bancodigital.cobrar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.diegolima.bancodigital.R;
import com.diegolima.bancodigital.app.MainActivity;
import com.diegolima.bancodigital.helper.FirebaseHelper;
import com.diegolima.bancodigital.helper.GetMask;
import com.diegolima.bancodigital.model.Cobranca;
import com.diegolima.bancodigital.model.Notificacao;
import com.diegolima.bancodigital.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Collections;

public class PagarCobrancaActivity extends AppCompatActivity {

	private TextView textValor;
	private TextView textData;
	private TextView textUsuario;
	private ImageView imagemUsuario;
	private ProgressBar progressBar;

	private AlertDialog dialog;

	private Cobranca cobranca;
	private Notificacao notificacao;
	private Usuario usuarioDestino;
	private Usuario usuarioOrigem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pagar_cobranca);

		configToolbar();

		iniciaComponentes();

		getExtra();

	}

	private void getExtra(){
		notificacao = (Notificacao) getIntent().getSerializableExtra("notificacao");

		recuperaCobranca();
	}

	private void recuperaCobranca(){
		DatabaseReference cobrancaRef = FirebaseHelper.getDatabaseReference()
				.child("cobrancas")
				.child(FirebaseHelper.getIdFirebase())
				.child(notificacao.getIdOperacao());
		cobrancaRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				cobranca = snapshot.getValue(Cobranca.class);

				recuperaUsuarioDestino();
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});
	}

	// Configura a notificação
	private void configNotificacao(){
		Notificacao notificacao = new Notificacao();
		notificacao.setIdOperacao(cobranca.getId());
		notificacao.setIdDestinario(cobranca.getIdDestinatario());
		notificacao.setIdEmitente(FirebaseHelper.getIdFirebase());
		notificacao.setOperacao("COBRANCA");

		// Envia a notificação para o usuário que irá receber a cobrança
		enviarNotificacao(notificacao);

	}

	// Envia a notificação para o usuário que irá receber o pagamento
	private void enviarNotificacao(Notificacao notificacao){
		DatabaseReference noficacaoRef = FirebaseHelper.getDatabaseReference()
				.child("notificacoes")
				.child(notificacao.getIdDestinario())
				.child(notificacao.getId());
		noficacaoRef.setValue(notificacao).addOnCompleteListener(task -> {
			if(task.isSuccessful()){

				DatabaseReference updateRef = noficacaoRef
						.child("data");
				updateRef.setValue(ServerValue.TIMESTAMP);

				Toast.makeText(this, "Cobrança enviada com sucesso!", Toast.LENGTH_SHORT).show();

				Intent intent = new Intent(this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

			}else {
				progressBar.setVisibility(View.GONE);
				showDialog();
			}
		});
	}

	private void showDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(
				this, R.style.CustomAlertDialog
		);

		View view = getLayoutInflater().inflate(R.layout.layout_dialog_info, null);

		TextView textTitulo = view.findViewById(R.id.textTitulo);
		textTitulo.setText("Atenção");

		TextView mensagem = view.findViewById(R.id.textMensagem);
		mensagem.setText("Não foi possível salvar os dados, tente novamente mais tarde.");

		Button btnOK = view.findViewById(R.id.btnOK);
		btnOK.setOnClickListener(v -> dialog.dismiss());

		builder.setView(view);

		dialog = builder.create();
		dialog.show();

	}

	private void recuperaUsuarioDestino() {
		DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
				.child("usuarios")
				.child(cobranca.getIdEmitente());
		usuarioRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				usuarioDestino = snapshot.getValue(Usuario.class);
				configDados();
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});
	}

	private void recuperaUsuarioOrigem() {
		DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
				.child("usuarios")
				.child(FirebaseHelper.getIdFirebase());
		usuarioRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				usuarioDestino = snapshot.getValue(Usuario.class);
				configDados();
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});
	}

	private void configDados(){
		textUsuario.setText(usuarioDestino.getNome());
		if(usuarioDestino.getUrlImagem() != null){
			Picasso.get().load(usuarioDestino.getUrlImagem())
					.placeholder(R.drawable.loading)
					.into(imagemUsuario);
		}

		textData.setText(GetMask.getDate(cobranca.getData(), 3));
		textValor.setText(getString(R.string.text_valor, GetMask.getValor(cobranca.getValor())));

	}

	private void configToolbar(){
		TextView textTitulo = findViewById(R.id.textTitulo);
		textTitulo.setText("Confirme os dados");
	}

	private void iniciaComponentes(){
		textValor = findViewById(R.id.textValor);
		textData = findViewById(R.id.textData);
		textUsuario = findViewById(R.id.textUsuario);
		imagemUsuario = findViewById(R.id.imagemUsuario);
		progressBar = findViewById(R.id.progressBar);
	}

}