package com.example.dounn.menutendina.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dounn.menutendina.A10_HomePage;
import com.example.dounn.menutendina.A24_Recensione;
import com.example.dounn.menutendina.A25_RecensioniUtente;
import com.example.dounn.menutendina.A27_PaginaElemento;
import com.example.dounn.menutendina.A29_Risposte;
import com.example.dounn.menutendina.Database.Notifica;
import com.example.dounn.menutendina.Database.Store;
import com.example.dounn.menutendina.OtherView.ImageViewLoading;
import com.example.dounn.menutendina.R;

import java.util.ArrayList;

/**
 * Created by lucadiliello on 16/06/2017.
 */

public class NotificheAdapter extends RecyclerView.Adapter<NotificheAdapter.ViewHolder> {

    private ArrayList<Notifica> notifiche;
    Context context;

    public NotificheAdapter(Context context, ArrayList<Notifica> notifiche) {
        this.context = context;
        this.notifiche = notifiche;
    }

    public void update(ArrayList<Notifica> e) {
        notifiche = e;
        notifyDataSetChanged();
    }

    public Notifica getItem(int position) {
        return notifiche.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notifica_layout, parent, false);
        final ViewHolder res = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = res.getAdapterPosition();
                if(position != RecyclerView.NO_POSITION) {
                    Notifica not = getItem(position);
                    Intent i;
                    switch(not.getTipo()) {

                        case NuovaRecensioneOggettoCheSeguo:
                            i = new Intent(context, A27_PaginaElemento.class);
                            i.putExtra("focus", not.getRecensione().getId());
                            Store.add("elemento", not.getRecensione().getElemento());
                            break;
                        case NuovaRecensioneUtenteCheSeguo:
                            i = new Intent(context, A25_RecensioniUtente.class);
                            i.putExtra("focus", not.getRecensione().getId());
                            Store.add("elemento", not.getRecensione().getId());
                            break;
                        case NuovaRispostaMiaDomanda:
                            i = new Intent(context, A29_Risposte.class);
                            i.putExtra("id_domanda", not.getRisposta().getIdDomanda());
                            i.putExtra("focus", not.getRisposta().getId());
                            break;
                        case NuovoVotoMiaRecensione:
                            i = new Intent(context, A24_Recensione.class);
                            Store.add("recensione", not.getVoto().getRecensione());
                            i.putExtra("focus", 1);
                            break;
                        case MiaMigliorRisposta:
                            i = new Intent(context, A29_Risposte.class);
                            i.putExtra("id_domanda", not.getRisposta().getIdDomanda());
                            i.putExtra("focus", -2);
                            break;
                        default:
                            i = new Intent(context, A10_HomePage.class);
                            break;
                    }
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            }
        });

        return res;
    }

    @Override
    public void onBindViewHolder(NotificheAdapter.ViewHolder viewHolder, int i) {
        Notifica not = getItem(i);

        viewHolder.notificaTx.setText(not.toString());
        viewHolder.notificaData.setText(context.getResources().getString(R.string.Data) + " " + not.reduceData());
        viewHolder.notificaImg.setFotoPath(not.getFoto());
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return notifiche.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageViewLoading notificaImg;
        TextView notificaData;
        TextView notificaTx;

        public ViewHolder(View convertView) {
            super(convertView);
            notificaImg = (ImageViewLoading) convertView.findViewById(R.id.notifica_img);
            notificaData = (TextView) convertView.findViewById(R.id.notifica_data);
            notificaTx = (TextView) convertView.findViewById(R.id.notifica_tx);

        }
    }
}