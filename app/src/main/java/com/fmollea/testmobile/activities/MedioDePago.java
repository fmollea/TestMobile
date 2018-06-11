package com.fmollea.testmobile.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fmollea.testmobile.R;
import com.fmollea.testmobile.models.Pago;
import com.fmollea.testmobile.utils.Comunicador;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.constants.PaymentTypes;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import com.fmollea.testmobile.adapters.adapter_TextImage;

import static com.fmollea.testmobile.utils.Utiles.getUrlPaymentMethods;
import static com.fmollea.testmobile.utils.Utiles.mensajeAlertDialog;
import static com.fmollea.testmobile.utils.Utiles.mensajeToast;
import static com.fmollea.testmobile.utils.Utiles.readAll;
import static com.fmollea.testmobile.utils.Utiles.verificarConexion;

public class MedioDePago extends AppCompatActivity{

    private ListView lvMediosDePago;
    private List<PaymentMethod> mListPaymentMethods;
    private adapter_TextImage adapter;
    private ArrayList<String> lLisNombresTarjetas;
    private ArrayList<String> lLisUrlImagenes ;
    private ArrayList<String> lListIds;
    private ProgressDialog progressDialog;
    private Comunicador comunicador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medio_de_pago);
        this.setTitle(getString(R.string.title_tarjeta));
        //Método que hace el binding con la interfaz gráfica e inicializa los objetos
        initComponentes();
        //métodos donde se declaran los objetos de la vista
        delegar();

    }

    private void cargarDatos() {

        //se crea un progressdialog
        progressDialog.setMessage("Cargando datos...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        //se crea un thread.
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Obtengo los métodos de pagos,
                    mListPaymentMethods = ObtenerMediosDePago();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cargarLista();
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
        DatosOk();
    }

    //Si todos los datos fueron cargados, se finaliza la vista para ir al mainactivity
    private void DatosOk(){
        Pago pago = (Pago) comunicador.getObjeto();
        if (pago.isDatosOk()){
            finish();
        }
    }

    //se hace el binding e inicialización de los objetos.
    private void initComponentes(){
        lvMediosDePago = (ListView) findViewById(R.id.lvMediosDePago);

        lLisNombresTarjetas = new ArrayList<>();
        lLisUrlImagenes = new ArrayList<>();
        lListIds = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        comunicador = new Comunicador();
    }

    private void delegar(){
        lvMediosDePago.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                SeleccionarTarjeta(position);
            }
        });
    }

    //se carga una lista con las tarjetas disponibles
    private void cargarLista(){

        for (PaymentMethod item: mListPaymentMethods) {
            if (item.getPaymentTypeId().equals(PaymentTypes.CREDIT_CARD)) {
                lLisNombresTarjetas.add(item.getName());
                lLisUrlImagenes.add(item.getThumbnail());
                lListIds.add(item.getId());
            }
        }

        adapter = new adapter_TextImage(this, lLisNombresTarjetas, lLisUrlImagenes);
        lvMediosDePago.setAdapter(adapter);
    }

    //Obtengo los medios de pago,
    private List<PaymentMethod> ObtenerMediosDePago() throws Exception{
        List<PaymentMethod> lListPM = new ArrayList<>();

        String url = getUrlPaymentMethods();

        //Creo una nueva URL, utilizando el URL del método get de la API de mercado pago.
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonString = readAll(rd);
            jsonString = jsonString.replaceAll("payment_type_id", "paymentTypeId");
            Type listType = new TypeToken<ArrayList<PaymentMethod>>(){}.getType();
            //Convierto el Json obtenido en una lista de PaymentMethods.
            lListPM = new Gson().fromJson(jsonString, listType);
        }
        catch (Exception e) {
            mensajeAlertDialog(this, "Error", "Ocurrió el siguiente error: " + e);
        }
        finally {
            //Me desconecto
            is.close();
        }
        return lListPM;
    }

    public void SeleccionarTarjeta(int index) {
        try {
            if (verificarConexion(this)) {
                Comunicador comunicador = new Comunicador();
                Pago pago = new Pago();
                pago = (Pago) comunicador.getObjeto();
                pago.setIdTarjeta(lListIds.get(index));
                pago.setUrlImagenTarjeta(lLisUrlImagenes.get(index));
                pago.setNombreTarjeta(lLisNombresTarjetas.get(index));
                comunicador.setObjeto(pago);

                Intent intent = new Intent(this, SeleccionBanco.class);
                startActivity(intent);
            }
            else
                mensajeToast(this, "Debe estar conectado a internet.");
        }catch (Exception e){
            mensajeAlertDialog(this, "Error", "Ocurrió el siguiente error: " + e);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if (lvMediosDePago != null) lvMediosDePago = null;
        if (mListPaymentMethods != null) {mListPaymentMethods.clear(); mListPaymentMethods = null;}
        if (adapter != null ) adapter = null;
        if (lLisNombresTarjetas != null) {lLisNombresTarjetas.clear(); lLisNombresTarjetas = null;}
        if (lLisUrlImagenes != null) {lLisUrlImagenes.clear(); lLisUrlImagenes = null;}
        if (lListIds != null) {lListIds.clear(); lListIds = null;}
        if (progressDialog != null)progressDialog = null;
        if (comunicador != null) comunicador = null;

    }
}
