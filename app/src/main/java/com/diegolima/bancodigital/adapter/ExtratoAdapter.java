package com.diegolima.bancodigital.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.diegolima.bancodigital.R;
import com.diegolima.bancodigital.model.Extrato;

import java.util.List;

public class ExtratoAdapter extends RecyclerView.Adapter<ExtratoAdapter.MyViewHolder> {

	private List<Extrato> extratoList;
	private Context context;

	public ExtratoAdapter(List<Extrato> extratoList, Context context) {
		this.extratoList = extratoList;
		this.context = context;
	}

	@NonNull
	@Override
	public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_extrato, parent, false);
		return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
		Extrato extrato = extratoList.get(position);

		// DEPOSITO
		// TRANSFERENCIA

		String icon = extrato.getOperacao().substring(0, 1);
		holder.textIcon.setText(icon);
	}

	@Override
	public int getItemCount() {
		return extratoList.size();
	}

	static class MyViewHolder extends RecyclerView.ViewHolder {

		TextView textIcon, textOperacao, textData, textValor;

		public MyViewHolder(@NonNull View itemView) {
			super(itemView);

			textIcon = itemView.findViewById(R.id.textIcon);
			textOperacao = itemView.findViewById(R.id.textOperacao);
			textData = itemView.findViewById(R.id.textData);
			textValor = itemView.findViewById(R.id.textValor);
		}
	}
}
