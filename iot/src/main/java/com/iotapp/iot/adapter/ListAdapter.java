package com.iotapp.iot.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.iotapp.iot.R;
import com.iotapp.iot.controller.ChatController;
import com.iotapp.iot.modal.ChatVO;

import java.util.ArrayList;

/**
 * Created by kundankumar on 26/09/16.
 */
public class ListAdapter extends BaseAdapter {

    private Context mContext;
    private  ArrayList<ChatVO> chatList;
    String user;
    SharedPreferences sharedPrefs;
    public ListAdapter(Context ctx, ArrayList<ChatVO> chatList) {
        mContext = ctx;
        this.chatList = chatList;
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        user = sharedPrefs.getString("prefUsername", "NULL");
    }

    public void setChatList(ArrayList<ChatVO> chatList){
        this.chatList = chatList;
    }

    public int getCount() {
        return chatList.size();
    }

    public Object getItem(int paramInt) {
        return chatList.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return 0L;
    }

    static class ViewHolder {

        LinearLayout layoutleft;
        LinearLayout layoutright;
        TextView txtdateandtime;
        TextView txtmessage;
        TextView txtnameleft;
        TextView txtnameright;
        ImageView imageView;
    }

    public View getView(int paramInt, View paramView,
                        ViewGroup paramViewGroup) {
        final ViewHolder holder;
        View view = paramView;
        if (view == null) {
            LayoutInflater in = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = in.inflate(R.layout.conversation_layout, null);
            holder = new ViewHolder();
            assert view != null;
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.layoutleft = ((LinearLayout) view
                .findViewById(R.id.layoutleft));
        holder.layoutleft.setVisibility(8);
        holder.layoutright = ((LinearLayout) view
                .findViewById(R.id.layoutright));
        holder.layoutright.setVisibility(8);
        holder.txtnameleft = ((TextView) view
                .findViewById(R.id.txtusernameleft));
        holder.txtnameright = ((TextView) view
                .findViewById(R.id.txtusernameright));
        holder.imageView = ((ImageView) view.findViewById(R.id.viewImage));

        holder.txtmessage = ((TextView) view.findViewById(R.id.txtmessage));
        String time = (chatList.get(paramInt))
                .getDate();

        String msg = (chatList.get(paramInt))
                .getMsg();
        String img = (chatList.get(paramInt))
                .getImage();

        if ((chatList.get(paramInt)).getName()
                .contains(user)) {
            holder.layoutright.setVisibility(0);
            holder.txtnameright
                    .setText(( chatList
                            .get(paramInt)).getName());

            // holder.txtdateandtime.setGravity(5);
            holder.txtmessage.setGravity(5);
        } else {
            holder.layoutleft.setVisibility(0);
            holder.txtnameleft.setText(( chatList
                    .get(paramInt)).getName());

        }
        if (msg.length() > 0) {
            holder.txtmessage.setText(msg + "     " + time);
            holder.imageView.setVisibility((View.GONE));
        } else if (img.length() > 0) {

            new ChatController().setPic(img, holder.imageView);
            img = "";
            holder.imageView.setVisibility((View.VISIBLE));
            holder.txtmessage.setText(time);
        }

        return view;

    }

}
