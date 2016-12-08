package com.example.daniel.registro_de_opiniao;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
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
import java.util.ArrayList;
import java.util.Date;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends Activity {

    private EditText EdDescri;
    private RadioGroup RgOpiniao;
    private CheckBox CbNivel_1, CbNivel_2, CbNivel_3, CbNivel_4, CbNivel_5;
    private ImageView IvFoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EdDescri = (EditText) findViewById(R.id.EdDescri);
        RgOpiniao = (RadioGroup) findViewById(R.id.RgOpiniao);
        CbNivel_1 = (CheckBox) findViewById(R.id.CbNivel_1);
        CbNivel_2 = (CheckBox) findViewById(R.id.CbNivel_2);
        CbNivel_3 = (CheckBox) findViewById(R.id.CbNivel_3);
        CbNivel_4 = (CheckBox) findViewById(R.id.CbNivel_4);
        CbNivel_5 = (CheckBox) findViewById(R.id.CbNivel_5);
    }

    public boolean Verifica_informa(){
        if(EdDescri.getText().toString().trim().equals("")){
            return false;
        }
        if (Verifica_nivel() == 0){
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

            Opiniao_mod cria = new Opiniao_mod();
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

            cria.setNivel(Verifica_nivel());
            cria.setCaminho_da_foto(caminhoFoto);
            cria.setData(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

            Classi_DAO.salvar(cria);
            Intent abridor = new Intent(this.getApplicationContext(), Tela_principal.class);
            startActivity(abridor);
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

    public int Verifica_nivel() {

        if (CbNivel_1.isChecked() == true) {
            return 1;
        }
        if (CbNivel_2.isChecked() == true) {
            return 2;
        }
        if (CbNivel_3.isChecked() == true) {
            return 3;
        }
        if (CbNivel_4.isChecked() == true) {
            return 4;
        }
        if (CbNivel_5.isChecked() == true) {
            return 5;
        }
        return 0;
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
