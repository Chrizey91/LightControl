package com.chriz.lightingcontrol.lightningSearch;

import android.content.ContentValues;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chriz.lightingcontrol.database.Contract;
import com.chriz.lightingcontrol.R;
import com.chriz.lightingcontrol.Utils;
import com.chriz.lightingcontrol.communication.CommunicationsFactory;
import com.chriz.lightingcontrol.communication.Communicator;

import java.util.LinkedList;

public class LightSearchAdapter extends RecyclerView.Adapter<LightSearchAdapter.LightSearchHolder> {
    private static final String TAG = LightSearchAdapter.class.getSimpleName();

    private LayoutInflater mInflater;
    private LinkedList<String> ips;
    private Context mContext;

    LightSearchAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.ips = new LinkedList<>();
        this.mContext = context;

        // We obtain our IP 'aaa.bbb.ccc.ddd' and then search for the lamps by trying to connect to
        // all possible combinations of 'ddd' (0-255).
        // Currently, the firmware is programmed to send "LAMPI" over TCP as soon as someone
        // connects.
        // If we receive such a message, a lamp is found.
        String ipAddress = Utils.getIPAddress(true);
        Log.d(TAG, ipAddress);
        String[] ip = ipAddress.split("\\.");
        for (int i = 0; i < 256; i++) {
            final String mIp = ip[0] + "." + ip[1] + "." + ip[2] + "." + i;
            CommunicationsFactory.createReliableButSlowCommunicator(context, mIp, 443, true)
                                 .receiveAnswer(new Communicator.OnReceiveAnswerListener() {
                @Override
                public void onReceiveAnswer(String answer) {
                    if (answer.contains("LAMPI")) {
                        Log.d(TAG, "FOUND A LAMP");
                        addLightIP(mIp);
                    }
                }
            }).execute();
        }
    }

    @NonNull
    @Override
    public LightSearchHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mItemView = mInflater.inflate(R.layout.lightbulb_item, viewGroup, false);
        return new LightSearchHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull LightSearchHolder lightSearchHolder, int i) {
        lightSearchHolder.lightbulbItemView.setText(ips.get(i));
    }

    @Override
    public int getItemCount() {
        return ips.size();
    }

    private synchronized void addLightIP(final String ip) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                ips.add(ip);
                notifyDataSetChanged();
            }
        });
    }

    class LightSearchHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView lightbulbItemView;
        final LightSearchAdapter mAdapter;

        LightSearchHolder(View itemView, LightSearchAdapter adapter) {
            super(itemView);
            lightbulbItemView = itemView.findViewById(R.id.lightbulb);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ContentValues values = new ContentValues();
            values.put(Contract.LightBulbs.KEY_IP, lightbulbItemView.getText().toString());
            values.put(Contract.LightBulbs.KEY_NAME, "No Name");
            mContext.getContentResolver().insert(Contract.CONTENT_URI, values);
        }
    }
}
