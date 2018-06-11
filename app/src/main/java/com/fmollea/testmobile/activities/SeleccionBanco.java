package com.fmollea.testmobile.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fmollea.testmobile.R;
import com.fmollea.testmobile.adapters.adapter_TextImage;
import com.fmollea.testmobile.models.Banco;
import com.fmollea.testmobile.models.Pago;
import com.fmollea.testmobile.utils.Comunicador;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.fmollea.testmobile.utils.Utiles.getUrlCardIssuers;
import static com.fmollea.testmobile.utils.Utiles.mensajeAlertDialog;
import static com.fmollea.testmobile.utils.Utiles.mensajeToast;
import static com.fmollea.testmobile.utils.Utiles.readAll;
import static com.fmollea.testmobile.utils.Utiles.verificarConexion;

public class SeleccionBanco extends AppCompatActivity{

    private ListView lvBancos;
    private String paymentMethodId;
    private List<Banco> listBancos;
    private adapter_TextImage adapter;
    private Pago pago;
    ArrayList<String> lLisNombresBancos;
    ArrayList<String> lLisUrlImagenes;
    ArrayList<Integer> lLisIdsBanco;
    ProgressDialog progressDialog;
    Comunicador comunicador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_banco);
        this.setTitle(getString(R.string.title_banco));
        //Método que hace el binding con la interfaz gráfica e inicializa los objetos
        initComponentes();
        //métodos donde se declaran los objetos de la vista
        delegar();
    }

    private void cargarDatos() {

        //creo un progressdialog
        progressDialog.setMessage("Cargando datos...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Obtengo la lista de bandos
                    listBancos = obtenerBancos();

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cargarLista();
                        chequearLista();
                        progressDialog.dismiss();
                    }
                });

            }
        }).start();
    }

    @Override
    protected void onStart(){
        super.onStart();
        cargarDatos();

    }

    @Override
    protected void onRestart() {

        super.onRestart();
        datosOk();
    }

    //si los datos ya fueron cargados se finaliza la actividad.
    private void datosOk(){
        Comunicador comunicador = new Comunicador();
        pago = (Pago) comunicador.getObjeto();
        if (pago.isDatosOk()){
            finish();
        }
    }

    //Si la tarjeta seleccionada no tiene un banco para seleccionar, nos dirigimos directamente a la proxima vista
    private void chequearLista(){
        if (listBancos.size() == 0) {
            Comunicador comunicador = new Comunicador();
            pago = (Pago) comunicador.getObjeto();
            pago.setIdBanco(0);
            comunicador.setObjeto(pago);
            Intent intent = new Intent(this, SeleccionCuotas.class);
            startActivity(intent);
            finish();
        }
    }

    private void initComponentes(){
        lvBancos = (ListView) findViewById(R.id.lvSeleccionBanco);

        //Inicializo objetos
        lLisNombresBancos = new ArrayList<>();
        lLisUrlImagenes = new ArrayList<>();
        lLisIdsBanco = new ArrayList<>();

        comunicador = new Comunicador();
        pago = new Pago();
        pago = (Pago) comunicador.getObjeto();
        paymentMethodId = pago.getIdTarjeta();
        progressDialog = new ProgressDialog(this);
    }

    private void delegar(){
        lvBancos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                seleccionarBanco(position);
            }
        });
    }

    //Obtengo los bancos
    private List<Banco> obtenerBancos() throws Exception{
        List<Banco> lListPM = new ArrayList<>();

        String url = getUrlCardIssuers(paymentMethodId);

        //Creo una nueva URL, utilizando el URL del método get de la API de mercado pago.
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonString = readAll(rd);
            Type listType = new TypeToken<ArrayList<Banco>>(){}.getType();
            //Convierto el Json obtenido en una lista de PaymentMethods.
            lListPM = new Gson().fromJson(jsonString, listType);
        }
        catch (Exception e) {
            mensajeAlertDialog(
                    this, "Error", "Ocurrió el siguiente error: " + e);
        }
        finally {
            //Me desconecto
            is.close();
        }
        return lListPM;
    }

    //se carga una lista con los bancos disponibles para la tareta seleccionada (en el caso que la tarjeta tenga bancos)
    private void cargarLista(){

        for (Banco item: listBancos) {
                lLisNombresBancos.add(item.getName());
                lLisUrlImagenes.add(item.getThumbnail());
                lLisIdsBanco.add(item.getId());
        }

        adapter = new adapter_TextImage(this, lLisNombresBancos, lLisUrlImagenes);
        lvBancos.setAdapter(adapter);
    }

    //se guarda la información del baco seleccioado y se inicia la última vista
    public void seleccionarBanco(int index){
        try {
            if (verificarConexion(this)) {

            pago.setIdBanco(lLisIdsBanco.get(index));
            pago.setNombreBanco(lLisNombresBancos.get(index));
            pago.setUrlImagenBanco(lLisUrlImagenes.get(index));
            comunicador.setObjeto(pago);

            Intent intent = new Intent(this, SeleccionCuotas.class);
            startActivity(intent);
        }
        else
            mensajeToast(this, "Debe estar conectado a internet.");

        }
        catch (Exception e){
            mensajeAlertDialog(this, "Error", "Ocurrió el siguiente error: " + e);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if (lvBancos != null) lvBancos = null;
        if (paymentMethodId != null) paymentMethodId = null;
        if (listBancos != null) listBancos = null;
        if (adapter != null) adapter = null;
        if (pago != null) pago = null;
        if (lLisNombresBancos != null){lLisNombresBancos.clear(); lLisNombresBancos = null; }
        if (lLisUrlImagenes != null){lLisUrlImagenes.clear(); lLisUrlImagenes = null; }
        if (lLisIdsBanco != null){lLisIdsBanco.clear(); lLisIdsBanco = null; }
        if (progressDialog != null )progressDialog = null;
        if (comunicador != null) comunicador = null;
    }
}
