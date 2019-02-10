package com.chriz.lightingcontrol.main;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chriz.lightingcontrol.database.Contract;
import com.chriz.lightingcontrol.lightBulb.LightBulb;
import com.chriz.lightingcontrol.lightBulb.LightBulbActivity;
import com.chriz.lightingcontrol.R;

/**
 * This class adds, changes and deletes lightbulbs from the database.
 * Here the user can see all his lighbulbs that he added to his device and is able to choose,
 * whether to turn them on/off or change the color or their names and so on.
 *
 * TODO: Needs major reworking in terms of code readability! Extract subclasses and functions
 */
public class LightBulbListAdapter extends RecyclerView.Adapter<LightBulbListAdapter.LightBulbListItemHolder> {
    public static final String IP_MESSAGE = "com.example.chriz.lightingcontrol.lightBulb.IP";

    private static final String TAG = LightBulbListAdapter.class.getSimpleName();
    private static final String[] projection = new String[] {Contract.CONTENT_PATH}; //table

    private LayoutInflater mInflater;
    private Context mContext;

    private String mQueryUri = Contract.CONTENT_URI.toString(); // base uri
    private String mSelectionClause = null;
    private String mSelectionArgs[] = null;
    private String mSortOrder = "ASC";

    LightBulbListAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public LightBulbListItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mItemView = mInflater.inflate(R.layout.lightbulb_list_item, viewGroup, false);
        return new LightBulbListItemHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final LightBulbListItemHolder lightBulbHolder, final int position) {

        Cursor cursor = mContext.getContentResolver().query(Uri.parse(
                mQueryUri), null, null, null, mSortOrder);

        final LightBulb lightBulb = retrieveLightBulb(cursor, position);
        if (lightBulb == null) {
            Log.d(TAG, "Could not retrieve a lightbulb from the cursor");
            return;
        }

        lightBulbHolder.lightbulbColorChangeButton.setOnClickListener(new LightBulbListItemOnClickListener(lightBulb) {
            @Override
            public void onClick(View v) {
                int position = lightBulbHolder.getLayoutPosition();

                Intent intent = new Intent(mContext, LightBulbActivity.class);
                intent.putExtra(IP_MESSAGE, lightBulb.getIpAddress());
                mContext.startActivity(intent);
            }
        });

        lightBulbHolder.lightbulbEditButton.setOnClickListener(new LightBulbEditPopupViewOnClickListener(lightBulb) {
            @Override
            public void onClick(View v) {
                final View popupView = mInflater.inflate(R.layout.lightbulb_edit_popup_window, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, -2, -2, true);
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                final EditText txt = popupView.findViewById(R.id.lightbulbNameEditText);
                txt.setText(lightBulb.getName());
                TextView ip = popupView.findViewById(R.id.lightbulbIpTextView);
                ip.setText(lightBulb.getIpAddress());
                popupView.findViewById(R.id.positiveButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(Contract.LightBulbs.KEY_ID, lightBulb.getID());
                        contentValues.put(Contract.LightBulbs.KEY_IP, lightBulb.getIpAddress());
                        contentValues.put(Contract.LightBulbs.KEY_NAME, txt.getText().toString());
                        mSelectionArgs = new String[] {Integer.toString(lightBulb.getID())};
                        mContext.getContentResolver().update(Contract.CONTENT_URI, contentValues, Contract.CONTENT_PATH, mSelectionArgs);
                        notifyItemChanged(position);
                        popupWindow.dismiss();
                    }
                });
                popupView.findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        AlertDialog dialog = builder.setMessage("Delete")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int mPosition = lightBulbHolder.getAdapterPosition();
                                        mSelectionArgs = new String[] {Integer.toString(lightBulb.getID())};
                                        int deleted = mContext.getContentResolver().delete(Contract.CONTENT_URI,
                                                Contract.CONTENT_PATH, mSelectionArgs);
                                        if (deleted > 0) {
                                            notifyItemRemoved(mPosition);
                                            notifyItemRangeChanged(mPosition, getItemCount());
                                            popupWindow.dismiss();
                                        } else {
                                            Log.d(TAG, "cant delete");
                                        }
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                }).create();
                        dialog.show();
                    }
                });
            }
        });

        lightBulbHolder.lightbulbNameText.setText(lightBulb.getName());
    }

    @Override
    public int getItemCount() {
        Cursor cursor = mContext.getContentResolver().query(
                Contract.ROW_COUNT_URI, new String[] {"count(*) AS count"},
                mSelectionClause, mSelectionArgs, mSortOrder);
        if (cursor == null) {
            return -1;
        }
        try {
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count;
        } catch (Exception e) {
            Log.d("LBA", "EXCEPTION getItemCount: " + e);
            return -1;
        }
    }

    private LightBulb retrieveLightBulb(Cursor cursor, int position) {
        LightBulb lightBulb = null;
        if (cursor != null) {
            if (cursor.moveToPosition(position)) {
                int indexIp = cursor.getColumnIndex(Contract.LightBulbs.KEY_IP);
                String ip = cursor.getString(indexIp);
                int indexId = cursor.getColumnIndex(Contract.LightBulbs.KEY_ID);
                int id = cursor.getInt(indexId);
                int indexName = cursor.getColumnIndex(Contract.LightBulbs.KEY_NAME);
                String name = cursor.getString(indexName);
                lightBulb = new LightBulb(id, ip, name);
            } else {
                Log.d(TAG, "No light found in the database.");
            }

            cursor.close();
        }
        return lightBulb;
    }

    class LightBulbListItemHolder extends RecyclerView.ViewHolder {
        private LightBulbListAdapter adapter;
        private TextView lightbulbNameText;
        private Button lightbulbEditButton;
        private ImageView lightbulbImageView;
        private ImageButton lightbulbColorChangeButton;
        private ImageButton lightbulbOnOffButton;

        LightBulbListItemHolder(View itemView, LightBulbListAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            lightbulbNameText = itemView.findViewById(R.id.lightbulbNameText);
            lightbulbEditButton = itemView.findViewById(R.id.lightbulbEditButton);
            lightbulbImageView = itemView.findViewById(R.id.lightbulbImageView);
            lightbulbColorChangeButton = itemView.findViewById(R.id.lightbulbColorChangeButton);
            lightbulbOnOffButton = itemView.findViewById(R.id.lightbulbOnOffButton);
        }
    }

    class LightBulbListItemOnClickListener implements View.OnClickListener {
        private LightBulb lightBulb;

        LightBulbListItemOnClickListener(LightBulb lightBulb) {
            this.lightBulb = lightBulb;
        }

        @Override
        public void onClick(View view) {

        }
    }

    class LightBulbEditPopupViewOnClickListener implements View.OnClickListener {
        private LightBulb lightBulb;

        LightBulbEditPopupViewOnClickListener(LightBulb lightBulb) {
            this.lightBulb = lightBulb;
        }

        @Override
        public void onClick(View v) {

        }
    }
}
