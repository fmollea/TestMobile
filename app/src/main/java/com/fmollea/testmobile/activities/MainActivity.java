package com.fmollea.testmobile.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.fmollea.testmobile.R;
import com.fmollea.testmobile.models.Pago;
import com.fmollea.testmobile.utils.Comunicador;
import com.squareup.picasso.Picasso;

import static com.fmollea.testmobile.utils.Utiles.verificarConexion;
import static com.fmollea.testmobile.utils.Utiles.mensajeAlertDialog;
import static com.fmollea.testmobile.utils.Utiles.mensajeToast;

public class MainActivity extends AppCompatActivity {

    private EditText eMonto;
    private FloatingActionButton fabAceptar;
    private Pago pago;
    private Comunicador comunicador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        setContentView(R.layout.activity_main);

        //Se hace el binding e inicialización de los objetos
        initComponentes();
        //eventos  de los objetos
        delegar();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        DatosOk(); //Método que chequea si los datos fueron cargados o no.
    }

    //En este método se inicializan los componentes y objetos.
    private void initComponentes(){
        eMonto = (EditText) findViewById(R.id.eMonto);
        fabAceptar = (FloatingActionButton) findViewById(R.id.fabAceptar);
        //Inicializo el objeto Pago
        pago = new Pago();
        comunicador = new Comunicador();
    }

    protected void delegar(){
        fabAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    ingresarMonto();

            }
        });
    }

    private void DatosOk(){
        pago = (Pago) comunicador.getObjeto();
        //Si los datos fueron cargados, entonces muestro un dialog.
        if (pago.isDatosOk()){
            MostrarDatosDlg();
        }
    }

    //Método que toma el monto ingresado por el usuario
    private void ingresarMonto(){
        try {
            if (verificarConexion(this)) {
                if (eMonto.getText().toString().isEmpty()) {
                    eMonto.setError(getString(R.string.error_CampoVacio));
                } else {
                    double lMonto = Double.parseDouble(eMonto.getText().toString());
                    pago.setMonto(lMonto);
                    ingresarMedioDePago(); // se llama a la actividad para seleccionar el medio de pago.
                }
            }
            else
                mensajeToast(this, "Debe estar conectado a internet.");
            }
            catch(Exception e){
                mensajeAlertDialog(this, "Error", "Ocurrió el siguiente error: " + e);
            }
    }

    private void ingresarMedioDePago(){

        try {
            Comunicador comunicador = new Comunicador();
            comunicador.setObjeto(pago);
            Intent intent = new Intent(this, MedioDePago.class);
            startActivity(intent);

        }catch (Exception e){
            mensajeAlertDialog(this, "Error", "Ocurrió el siguiente error: " + e);
        }
    }

    //Metodo que muestra un dialog con los datos cargados.
    private void MostrarDatosDlg(){
        final Dialog dlg = new Dialog(this);
        dlg.setContentView(R.layout.dialog_datos_ingresados);
        dlg.setTitle("Datos ingresados");

        EditText eNombreTarjeta = (EditText) dlg.findViewById(R.id.eTarjeta);
        EditText eNombreBanco = (EditText) dlg.findViewById(R.id.eBanco);
        EditText eMensajeCuotas = (EditText) dlg.findViewById(R.id.eCuotas);
        ImageView imgTarjeta = (ImageView) dlg.findViewById(R.id.imgTarjeta);
        ImageView imgBanco = (ImageView) dlg.findViewById(R.id.imgBanco);
        FloatingActionButton fabAceptar = (FloatingActionButton) dlg.findViewById(R.id.fabAceptar);

        try {
            pago = (Pago) comunicador.getObjeto();
        }
        catch (Exception e){
            mensajeAlertDialog(this, "Error", "Ocurrió el siguiente error: " + e);
        }

        eNombreTarjeta.setText(pago.getNombreTarjeta());
        eMensajeCuotas.setText(pago.getMensajeCuotas());
        Picasso.get().load(pago.getUrlImagenTarjeta()).into(imgTarjeta);

        //chequeo si hay datos o no del banco de la tarjeta.
        if (pago.getIdBanco() == 0){
            eNombreBanco.setVisibility(View.GONE);
            imgBanco.setVisibility(View.GONE);
        }
        else {
            eNombreBanco.setText(pago.getNombreBanco());
            Picasso.get().load(pago.getUrlImagenBanco()).into(imgBanco);
        }

        fabAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pago = new Pago();
                comunicador = new Comunicador();
                dlg.dismiss();
                eMonto.setText("");
                mensajeToast(dlg.getContext(), "Su pago fue ingresado con éxito");
            }
        });

        dlg.show();
    }
}
