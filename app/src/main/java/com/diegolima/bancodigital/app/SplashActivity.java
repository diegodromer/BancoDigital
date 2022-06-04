package com.diegolima.bancodigital.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.diegolima.bancodigital.R;
import com.diegolima.bancodigital.autenticacao.LoginActivity;
import com.diegolima.bancodigital.helper.FirebaseHelper;

public class SplashActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		new Handler(Looper.getMainLooper()).postDelayed(this::getAutenticacao, 500);
	}

	private void getAutenticacao() {
		if (FirebaseHelper.getAutenticado()){
			startActivity(new Intent(this, MainActivity.class));
		}else{
			startActivity(new Intent(this, LoginActivity.class));
		}
		finish();
	}
}