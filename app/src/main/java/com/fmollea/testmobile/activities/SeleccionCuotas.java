package com.fmollea.testmobile.activities;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.fmollea.testmobile.R;
import com.fmollea.testmobile.adapters.adapter_Simple;
import com.fmollea.testmobile.models.Pago;
import com.fmollea.testmobile.utils.Comunicador;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.model.Installment;
import com.mercadopago.model.PayerCost;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import static com.fmollea.testmobile.utils.Utiles.getUrlInstallments;
import static com.fmollea.testmobile.utils.Utiles.mensajeAlertDialog;
import static com.fmollea.testmobile.utils.Utiles.mensajeToast;
import static com.fmollea.testmobile.utils.Utiles.readAll;
import static com.fmollea.testmobile.utils.Utiles.verificarConexion;

public class SeleccionCuotas extends AppCompatActivity {

    private ListView lvMensajeCuotas;
    private Installment mObjInstallment;
    private ArrayList<String> lLisMensajeCuotas;
    private Pago pago;
    private adapter_Simple adaptador;
    private Comunicador comunicador;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_cuotas);
        this.setTitle(getString(R.string.title_cuotas));
        //Método que hace el binding con la interfaz gráfica e inicializa los objetos
        initComponentes();
        //métodos donde se declaran los objetos de la vista
        delegar();
    }

    private void cargarDatos() {

        progressDialog.setMessage("Cargando datos...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Obtengo la información de la forma de pago.
                    mObjInstallment = ObtenerMediosDePago();
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


    private void initComponentes(){

        lvMensajeCuotas = (ListView) findViewById(R.id.lvlistaCuotas);


        mObjInstallment = new Installment();
        lLisMensajeCuotas = new ArrayList<>();
        comunicador = new Comunicador();
        pago = new Pago();
        pago = (Pago) comunicador.getObjeto();
        progressDialog = new ProgressDialog(this);

    }

    private void delegar(){
        lvMensajeCuotas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                seleccionarCuotas(position);
            }
        });
    }

    private void seleccionarCuotas(int position){
        if (verificarConexion(this)) {
            pago.setMensajeCuotas(lLisMensajeCuotas.get(position));
            pago.setDatosOk(true);
            comunicador.setObjeto(pago);
            finish();
        }
        else
            mensajeToast(this, "Debe estar conectado a internet.");
    }

    //Obtengo el objeto Installments para poder obtener los mensajes recomendados.
    private Installment ObtenerMediosDePago() throws Exception{
        Installment installment = new Installment();
        Gson gson = new Gson();
        ArrayList<Installment> enums = new ArrayList<>();
        String url = getUrlInstallments(pago.getIdTarjeta(), pago.getMonto(), pago.getIdBanco());

        //Creo una nueva URL, utilizando el URL del método get de la API de mercado pago.
        InputStream is = new URL(url).openStream();

        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonString = readAll(rd);
            //Convierto el Json obtenido en un installment.
            jsonString = jsonString.replaceAll("payer_costs", "payerCosts");
            jsonString = jsonString.replaceAll("recommended_message", "recommendedMessage");

            Type collectionType = new TypeToken<ArrayList<Installment>>(){}.getType();
            enums =  gson.fromJson(jsonString, collectionType);
            installment = enums.get(0);
        }
        catch (Exception e) {
            mensajeAlertDialog(this, "Error", "Ocurrió el siguiente error: " + e);
        }
        finally {
            //Me desconecto
            is.close();
        }
        return installment ;
    }

    private void cargarLista(){
        for (PayerCost item: mObjInstallment.getPayerCosts())
                lLisMensajeCuotas.add(item.getRecommendedMessage());

        adaptador = new adapter_Simple(this, lLisMensajeCuotas);
        lvMensajeCuotas.setAdapter(adaptador);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if (lvMensajeCuotas != null) lvMensajeCuotas = null;
        if (mObjInstallment != null) mObjInstallment = null;
        if (lLisMensajeCuotas !=  null ) { lLisMensajeCuotas.clear(); lLisMensajeCuotas = null; }
        if(pago != null) pago = null;
        if (adaptador != null) adaptador = null;
        if (comunicador != null) comunicador = null;
        if (progressDialog != null) progressDialog = null;

    }
}
