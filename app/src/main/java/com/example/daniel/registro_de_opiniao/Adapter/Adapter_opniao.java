package com.example.daniel.registro_de_opiniao.Adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.os.Parcel;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.daniel.registro_de_opiniao.Modelo.Opiniao_mod;
import com.example.daniel.registro_de_opiniao.Opiniao_DAO.Classi_DAO;
import com.example.daniel.registro_de_opiniao.R;

import java.io.File;
import java.util.ArrayList;

public class Adapter_opniao extends RecyclerView.Adapter {

    private ArrayList<Opiniao_mod> listaDeCadastro;
    private Context context;

    public Adapter_opniao(Context c, ArrayList<Opiniao_mod> p) {
        this.listaDeCadastro = p;
        this.context = c;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_adapter_opniao, parent, false);
        PessoaViewHolder retorno = new PessoaViewHolder(view);
        return retorno;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PessoaViewHolder caixinha = (PessoaViewHolder) holder;
        Opiniao_mod p = listaDeCadastro.get(position);
        caixinha.seAtualiza(p);
    }

    @Override
    public int getItemCount() {
        return listaDeCadastro.size();
    }

    public class PessoaViewHolder extends RecyclerView.ViewHolder {

        private ImageView IvPrato;
        private TextView Tvordem;
        private TextView Tvdescri;
        private TextView Tvavaliado;

        public PessoaViewHolder(View itemView) {
            super(itemView);

            this.IvPrato = (ImageView) itemView.findViewById(R.id.IvPrato);
            this.Tvordem = (TextView) itemView.findViewById(R.id.Tvordem);
            this.Tvdescri = (TextView) itemView.findViewById(R.id.Tvdescri);
            this.Tvavaliado = (TextView) itemView.findViewById(R.id.Tvavaliado);

        }

        public void seAtualiza(Opiniao_mod quemDevoMostrar) {
            Tvordem.setText(quemDevoMostrar.getTipo());
            Tvdescri.setText(quemDevoMostrar.getDescricao());
            Tvavaliado.setText("Avaliado com " + quemDevoMostrar.getNivel() + " em " + quemDevoMostrar.getData());

            if(quemDevoMostrar.getCaminho_da_foto()!= null) {
                File arquivoImagem = new File(quemDevoMostrar.getCaminho_da_foto());
                if (arquivoImagem.exists()) {
                    IvPrato.setImageURI(Uri.fromFile(arquivoImagem));
                }
            }
        }

    }
}
