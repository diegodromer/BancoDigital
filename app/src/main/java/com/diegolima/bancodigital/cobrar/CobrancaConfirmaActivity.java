package com.diegolima.bancodigital.cobrar;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.squareup.picasso.Picasso;

public class CobrancaConfirmaActivity extends AppCompatActivity {

	private TextView textValor;
	private TextView textUsuario;
	private ImageView imagemUsuario;
	private ProgressBar progressBar;

	private Cobranca cobranca;

	private AlertDialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cobranca_confirma);

		configToolbar();

		iniciaComponentes();

		configDados();
	}

	public void confirmaCobranca(View view) {
		DatabaseReference cobrancaRef = FirebaseHelper.getDatabaseReference()
				.child("cobrancas")
				.child(cobranca.getIdDestinatario())
				.child(cobranca.getId());
		cobrancaRef.setValue(cobranca).addOnCompleteListener(task -> {
			if (task.isSuccessful()){
				progressBar.setVisibility(View.VISIBLE);

				DatabaseReference updateRef = cobrancaRef
						.child("data");
				updateRef.setValue(ServerValue.TIMESTAMP);

				configNotificacao();
			}else{
				showDialog();
			}
		});
	}

	private void configNotificacao() {
		Notificacao notificacao = new Notificacao();
		notificacao.setIdOperacao(cobranca.getId());
		notificacao.setIdDestinario(cobranca.getIdDestinatario());
		notificacao.setIdEmitente(FirebaseHelper.getIdFirebase());
		notificacao.setOperacao("COBRANCA");

		// Envia a notificação para o usuário que irá receber a cobrança
		enviarNotificacao(notificacao);

	}

	private void enviarNotificacao(Notificacao notificacao) {
		DatabaseReference notificacaoRef = FirebaseHelper.getDatabaseReference()
				.child("notificacoes")
				.child(notificacao.getIdDestinario())
				.child(notificacao.getId());
		notificacaoRef.setValue(notificacao).addOnCompleteListener(task -> {
			if (task.isSuccessful()){
				DatabaseReference updateRef = notificacaoRef
						.child("data");
				updateRef.setValue(ServerValue.TIMESTAMP);

				Toast.makeText(this, "Cobrança enviada com sucesso!", Toast.LENGTH_SHORT).show();

				Intent intent = new Intent(this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}else{
				progressBar.setVisibility(View.GONE);
				showDialog();
			}
		});
	}

	private void configDados() {
		Usuario usuarioDestino;

		usuarioDestino = (Usuario) getIntent().getSerializableExtra("usuario");
		cobranca = (Cobranca) getIntent().getSerializableExtra("cobranca");

		textUsuario.setText(usuarioDestino.getNome());
		if (usuarioDestino.getUrlImagem() != null) {
			Picasso.get().load(usuarioDestino.getUrlImagem()).placeholder(R.drawable.loading).into(imagemUsuario);
		}
		textValor.setText(getString(R.string.text_valor, GetMask.getValor(cobranca.getValor())));
	}

	private void showDialog() {
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

	private void configToolbar() {
		TextView textTitulo = findViewById(R.id.textTitulo);
		textTitulo.setText("Confirme os dados");
	}

	private void iniciaComponentes() {
		textValor = findViewById(R.id.textValor);
		textUsuario = findViewById(R.id.textUsuario);
		imagemUsuario = findViewById(R.id.imagemUsuario);
		progressBar = findViewById(R.id.progressBar);
	}

}