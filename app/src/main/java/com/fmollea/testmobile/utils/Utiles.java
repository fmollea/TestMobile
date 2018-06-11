package com.fmollea.testmobile.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.widget.Toast;

import java.io.IOException;
import java.io.Reader;

public class Utiles {

    //public key
    public static String publicKey = "444a9ef5-8a6b-429f-abdf-587639155d88";
    //Links
    public static String urlBase = "https://api.mercadopago.com/v1/payment_methods";
    public static String urlCardIssuers = "/card_issuers";
    public static String urlInstallments = "/installments";
    //Parametros
    public static String paramPublicKey = "?public_key=";
    public static String paramCardIssuers = "&payment_method_id=";
    public static String paramAmount = "&amount=";
    public static String paramIssuerId = "&issuer.id=";

    public static  String getUrlPaymentMethods(){
        return urlBase +  paramPublicKey + publicKey;
    }

    public static String getUrlCardIssuers(String pPaymentMethodId){
        return urlBase + urlCardIssuers + paramPublicKey + publicKey + paramCardIssuers + pPaymentMethodId;
    }

    public static String getUrlInstallments(String pPaymentMethodId, double pAmount, Integer pIssuerId ){
        String url;

        if (pIssuerId == 0) {
            url = urlBase + urlInstallments + paramPublicKey + publicKey + paramAmount + pAmount +
                    paramCardIssuers + pPaymentMethodId + paramIssuerId + "null";
        }
        else {
            url = urlBase + urlInstallments + paramPublicKey + publicKey + paramAmount + pAmount +
                    paramCardIssuers + pPaymentMethodId + paramIssuerId + pIssuerId;
        }

        return url;
    }



    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static void mensajeAlertDialog(Context pContext, String pTitulo, String pMensaje) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(pContext);
        dlgAlert.setTitle(pTitulo);
        dlgAlert.setMessage(pMensaje);
        dlgAlert.setCancelable(true);
        dlgAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dialog.cancel();
            } });
        dlgAlert.create();
        dlgAlert.show();
    }

    public static void mensajeToast(Context pContext, String pMensaje){
        Toast toastMsg = Toast.makeText(pContext, pMensaje, Toast.LENGTH_SHORT);
        toastMsg.setGravity(Gravity.CENTER, 0,0);
        toastMsg.show();
    }

    public static boolean verificarConexion(Context pContext){

        ConnectivityManager infoNetwork = (ConnectivityManager)pContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = infoNetwork.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        boolean isWiFi = activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;

        return isConnected || isWiFi;
    }

}
