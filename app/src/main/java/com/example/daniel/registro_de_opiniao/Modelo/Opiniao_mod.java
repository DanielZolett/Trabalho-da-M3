package com.example.daniel.registro_de_opiniao.Modelo;

/**
 * Created by daniel on 07/12/2016.
 */

public class Opiniao_mod {

    private String tipo;
    private String descricao;
    private int nivel;
    private String data;
    private String caminho_da_foto;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCaminho_da_foto() {
        return caminho_da_foto;
    }

    public void setCaminho_da_foto(String caminho_da_foto) {
        this.caminho_da_foto = caminho_da_foto;
    }
}
