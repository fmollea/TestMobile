package com.fmollea.testmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fmollea.testmobile.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class adapter_TextImage extends BaseAdapter {

    Context context;
    ArrayList<String> nombres;
    ArrayList<String> urlImagenes;
    LayoutInflater inflater;

    public adapter_TextImage(Context pContext, ArrayList<String> pNombres, ArrayList<String> pImagenes) {
        context = pContext;
        nombres = pNombres;
        urlImagenes = pImagenes;
    }

    @Override
    public int getCount() {
        return nombres.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        TextView txtTexto;
        ImageView imgImagen;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.item_con_imagen, parent, false);

        txtTexto = (TextView) itemView.findViewById(R.id.text);
        imgImagen = (ImageView) itemView.findViewById(R.id.image);

        txtTexto.setText(nombres.get(position));
        Picasso.get().load(urlImagenes.get(position)).into(imgImagen);

        return itemView;
    }

}
