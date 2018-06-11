package com.fmollea.testmobile.utils;

public class Comunicador {

    private static Object objeto = null;

    public static void setObjeto(Object newObjeto) {
        objeto = newObjeto;
    }

    public static Object getObjeto() {
        return objeto;
    }
}
