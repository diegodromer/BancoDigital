package com.diegolima.bancodigital.transferencia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.diegolima.bancodigital.R;
import com.diegolima.bancodigital.app.MainActivity;
import com.diegolima.bancodigital.helper.FirebaseHelper;
import com.diegolima.bancodigital.helper.GetMask;
import com.diegolima.bancodigital.model.Transferencia;
import com.diegolima.bancodigital.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class TransferenciaReciboActivity extends AppCompatActivity {

	private TextView textCodigo;
	private TextView textUsuario;
	private TextView textData;
	private TextView textValor;
	private TextView textTipoTransferencia;
	private TextView textInfoTransferencia;
	private ImageView imagemUsuario;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transferencia_recibo);

		configToolbar();
		iniciaComponentes();
		recuperaTransferencia();
		configCliques();
	}

	private void configCliques(){
		findViewById(R.id.btnOK).setOnClickListener(v -> {
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		});
	}

	private void configToolbar() {
		TextView textTitulo = findViewById(R.id.textTitulo);
		textTitulo.setText("Recibo");
	}

	private void recuperaTransferencia() {
		String idTransferencia = getIntent().getStringExtra("idTransferencia");

		DatabaseReference transferenciaRef = FirebaseHelper.getDatabaseReference()
				.child("transferencias")
				.child(idTransferencia);
		transferenciaRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				Transferencia transferencia = snapshot.getValue(Transferencia.class);
				if (transferencia != null){
					recuperaUsuarioDestino(transferencia);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});
	}

	private void recuperaUsuarioDestino(Transferencia transferencia){
		DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
				.child("usuarios")
				.child(transferencia.getIdUserDestino());
		usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				Usuario usuarioDestino = snapshot.getValue(Usuario.class);
				if (usuarioDestino != null){
					configDados(transferencia, usuarioDestino);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});
	}

	private void configDados(Transferencia transferencia, Usuario usuariDestino) {
		textCodigo.setText(transferencia.getId());
		textData.setText(GetMask.getDate(transferencia.getData(), 3));
		textValor.setText(getString(R.string.text_valor, GetMask.getValor(transferencia.getValor())));

		if (usuariDestino.getUrlImagem() != null){
			Picasso.get().load(usuariDestino.getUrlImagem())
					.placeholder(R.drawable.loading)
					.into(imagemUsuario);
		}
		textUsuario.setText(usuariDestino.getNome());
	}

	private void iniciaComponentes() {
		textCodigo = findViewById(R.id.textCodigo);
		textUsuario = findViewById(R.id.textUsuario);
		textData = findViewById(R.id.textData);
		textValor = findViewById(R.id.textValor);
		imagemUsuario = findViewById(R.id.imagemUsuario);
		textTipoTransferencia = findViewById(R.id.textTipoTransferencia);
		textInfoTransferencia = findViewById(R.id.textInfoTransferencia);
	}
}