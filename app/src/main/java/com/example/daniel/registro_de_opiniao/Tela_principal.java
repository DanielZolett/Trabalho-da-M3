package com.example.daniel.registro_de_opiniao;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.daniel.registro_de_opiniao.Adapter.Adapter_opniao;
import com.example.daniel.registro_de_opiniao.Modelo.Opiniao_mod;
import com.example.daniel.registro_de_opiniao.Opiniao_DAO.Classi_DAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class Tela_principal extends Activity {

    private Button btCadastro;
    ArrayList<JsonObjectRequest> filaRequisicoes;
    private String cidade;
    private Location location;
    private LocationManager locationmaneger;
    private Address endereco;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_principal);

        double latitude = 0.0;
        double longitutde = 0.0;

        ArrayList<Opiniao_mod> lista = Classi_DAO.obterLista();

        RecyclerView rvListaCadastro = (RecyclerView) findViewById(R.id.rvListaCadastro);

        RecyclerView.LayoutManager formaApresentacao = new LinearLayoutManager(this.getApplicationContext(), LinearLayoutManager.VERTICAL,false);
        rvListaCadastro.setLayoutManager(formaApresentacao);
        Adapter_opniao adaptador = new Adapter_opniao(this.getApplicationContext(), lista);
        rvListaCadastro.setAdapter(adaptador);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
        } else {
            locationmaneger = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            location = locationmaneger.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if (location != null){

            longitutde = location.getLongitude();
            latitude = location.getLatitude();
        }

        try{
            endereco = buscarEndereco(latitude,longitutde);
            //cidade = endereco.getLocality();

        } catch (IOException e){
            Log.i("GPS",e.getMessage());
        }
    }

    public void Cadastrar (View origem){
        Intent abridor = new Intent(this.getApplicationContext(), MainActivity.class);
        startActivity(abridor);
    }

    public Address buscarEndereco(double latitude, double longitude) throws IOException{

        Geocoder geocoder;
        Address address = null;
        List<Address> addresses;

        geocoder = new Geocoder(getApplicationContext());

        addresses = geocoder.getFromLocation(latitude,longitude,1);
        if(addresses.size()>0){
            address = addresses.get(0);
        }
        return address;
    }



}
