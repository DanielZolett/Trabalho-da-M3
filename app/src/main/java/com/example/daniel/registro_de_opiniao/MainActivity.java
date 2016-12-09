package com.example.daniel.registro_de_opiniao;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.daniel.registro_de_opiniao.Modelo.Opiniao_mod;
import com.example.daniel.registro_de_opiniao.Opiniao_DAO.Classi_DAO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends Activity {

    private EditText EdDescri;
    private RadioGroup RgOpiniao;
    private RatingBar ratingBar;
    private ImageView IvFoto;
    private Opiniao_mod cria;
    private RequestQueue filaRequisicoes;
    private ProgressDialog pbLocalizacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EdDescri = (EditText) findViewById(R.id.EdDescri);
        RgOpiniao = (RadioGroup) findViewById(R.id.RgOpiniao);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar2);
        this.filaRequisicoes = Volley.newRequestQueue(this);
    }

    public boolean Verifica_informa(){
        if(EdDescri.getText().toString().trim().equals("")){
            return false;
        }
        if (caminhoFoto == null){
            return false;
        }
        return true;

    }

    public void Salvar(View click) {
        ArrayList<Opiniao_mod> lista = Classi_DAO.obterLista();

        if (Verifica_informa() == false){
            Toast.makeText(this.getApplicationContext(), "prencher todos os campos/deve conter foto", LENGTH_SHORT).show();
        }else {
            cria = new Opiniao_mod();
            cria.setDescricao(EdDescri.getText().toString());

            switch (RgOpiniao.getCheckedRadioButtonId()) {
                case R.id.RbEntra:
                    cria.setTipo("Entrada");
                    break;
                case R.id.RbPrin:
                    cria.setTipo("Prato principal");
                    break;
                case R.id.RbSobre:
                    cria.setTipo("Sobremesa");
                    break;
                case R.id.RbLanch:
                    cria.setTipo("Lanche");
                    break;
                default:
                    Toast informa = Toast.makeText(getApplicationContext(), "selecione algo", Toast.LENGTH_LONG);
                    informa.show();
            }

            cria.setNivel(ratingBar.getNumStars());
            cria.setCaminho_da_foto(caminhoFoto);
            cria.setData(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

            obterLocal();
        }
    }

    private void obterLocal(){
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissoes = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissoes, 1);
        } else {

            obterCoordenada();
        }
    }

    private LocationManager locationManager;
    private LocationListener locationListener;

    private void obterCoordenada(){
        pbLocalizacao = new ProgressDialog(this);
        pbLocalizacao.setMessage("Obtendo sua localizacao");
        pbLocalizacao.setIndeterminate(true);
        pbLocalizacao.setCancelable(false);
        pbLocalizacao.show();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("GPS desligado. deseja ligar?")
                    .setCancelable(false)
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Nao", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            pbLocalizacao.dismiss();
            alert.show();
        }
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("GPS", "CHEGOU DADO DE LOCALIZAÇÃO");
                Log.d("GPS", "LAT: " + location.getLatitude());
                Log.d("GPS", "LON: " + location.getLongitude());
                Log.d("GPS", "ACC: " + location.getAccuracy());

                String url = "http://maps.googleapis.com/maps/api/geocode/json?latlng=__LAT__,__LONG__&sensor=true";
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                url = url.replace("__LAT__", String.valueOf(location.getLatitude()));
                url = url.replace("__LONG__", String.valueOf(location.getLongitude()));

                JsonObjectRequest jsObjRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                //Toast.makeText(WeatherActivity.this.getApplicationContext(), "Request DONE!", Toast.LENGTH_LONG).show();
                                try {
                                    //
                                    JSONArray results = response.getJSONArray("results");
                                    JSONArray address_components = results.getJSONObject(0).getJSONArray("address_components");
                                    for (int i = 0; i < address_components.length(); i++) {
                                        JSONObject component = address_components.getJSONObject(i);
                                        String long_name = component.getString("long_name");
                                        JSONArray mtypes = component.getJSONArray("types");
                                        String Type = mtypes.getString(0);
                                        if (Type.equalsIgnoreCase("locality")) {
                                            Toast.makeText(MainActivity.this,"Cidade: "+long_name, Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    pbLocalizacao.dismiss();
                                    finalizarCadastro();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                volleyError.printStackTrace();
                                pbLocalizacao.dismiss();
                            }
                        });

                filaRequisicoes.add(jsObjRequest);
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    String[] permissoes = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
                    ActivityCompat.requestPermissions(MainActivity.this, permissoes, 1);
                }
                locationManager.removeUpdates(locationListener);
                pbLocalizacao.dismiss();
            }

            @Override
            public void onStatusChanged(String s, int status, Bundle bundle) {
                switch (status) {

                    case LocationProvider.AVAILABLE:
                        Log.d("GPS", "GPS LIGOU");
                        break;
                    case LocationProvider.OUT_OF_SERVICE:
                        Log.d("GPS", "GPS FORA DE SERVIÇO");
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        Log.d("GPS", "GPS TEMPORARIAMENTE INDISPONÍVEL");
                        break;
                }
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.d("GPS", "GPS LIGADO");
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.d("GPS", "GPS DESLIGADO");
            }
        };
        Log.d("GPS", "INICIO");
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    @SuppressWarnings({"ResourceType"})
    protected void onPause() {
        super.onPause();
        if (locationManager != null)
            locationManager.removeUpdates(locationListener);
    }

    private void finalizarCadastro() {
        Classi_DAO.salvar(cria);
        Intent abridor = new Intent(this.getApplicationContext(), Tela_principal.class);
        startActivity(abridor);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1){
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permissão concedida
                obterLocal();
                return;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                ImageView imgFotografia = (ImageView) findViewById(R.id.IvFoto);
                File arquivoImagem = new File(caminhoFoto);
                imgFotografia.setImageURI(Uri.fromFile(arquivoImagem));
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
    }

    private String caminhoFoto = null;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        caminhoFoto = image.getAbsolutePath();
        return image;
    }

    public void Tira_foto(View v) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.daniel.registro_de_opiniao", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 3);
            }
        }
    }
}
