package com.fmollea.testmobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.fmollea.testmobile.R;

import java.util.ArrayList;

public class adapter_Simple extends BaseAdapter {

    Context context;
    ArrayList<String> nombres;
    LayoutInflater inflater;

    public adapter_Simple(Context pContext, ArrayList<String> pNombres) {
        context = pContext;
        nombres = pNombres;
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

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.item, parent, false);

        txtTexto = (TextView) itemView.findViewById(R.id.text);

        txtTexto.setText(nombres.get(position));

        return itemView;
    }

}
