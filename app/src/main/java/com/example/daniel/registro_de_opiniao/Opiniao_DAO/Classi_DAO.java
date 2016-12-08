package com.example.daniel.registro_de_opiniao.Opiniao_DAO;

import com.example.daniel.registro_de_opiniao.Modelo.Opiniao_mod;

import java.util.ArrayList;

/**
 * Created by daniel on 07/12/2016.
 */

public class Classi_DAO {

    private static ArrayList<Opiniao_mod> listaCadastro;


    private static void inicializarLista(){
        if(Classi_DAO.listaCadastro == null){
            Classi_DAO.listaCadastro = new ArrayList<>();
        }
    }

    public static void salvar(Opiniao_mod cadastro){
        inicializarLista();
        listaCadastro.add(cadastro);
    }

    public static ArrayList<Opiniao_mod> obterLista(){
        inicializarLista();
        return  listaCadastro;
    }
}
