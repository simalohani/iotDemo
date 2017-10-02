package com.iotapp.iot.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.amulyakhare.textdrawable.TextDrawable;
import com.iotapp.iot.R;
import com.iotapp.iot.activity.HomeActivity;
import com.iotapp.iot.fragment.ChatFragment;
import com.iotapp.iot.fragment.MapFragment;
import com.iotapp.iot.fragment.ScmAdminFragment;
import com.iotapp.iot.fragment.ScmFragment;
import com.iotapp.iot.utility.FontUtil;
import com.iotapp.iot.utility.SvgUtil;
import com.iotapp.iot.utility.Util;


public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {
    String[] titles;
    TypedArray icons;
    Context context;

    // The default constructor to receive titles,icons and context from MainActivity.
    public NavigationDrawerAdapter(String[] titles, TypedArray icons, Context context) {

        this.titles = titles;
        this.icons = icons;
        this.context = context;
    }

    /**
     * This is called�every time�when we need a new ViewHolder and a new ViewHolder is required for every item in RecyclerView.
     * Then this ViewHolder is passed to onBindViewHolder to display items.
     */

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (viewType == 1) {
            View itemLayout = layoutInflater.inflate(R.layout.drawer_item_layout, null);
            return new ViewHolder(itemLayout, viewType, context);
        } else if (viewType == 0) {
            View itemHeader = layoutInflater.inflate(R.layout.header_layout, null);
            return new ViewHolder(itemHeader, viewType, context);
        }


        return null;
    }

    /**
     * This method is called by RecyclerView.Adapter to display the data at the specified position.�
     * This method should update the contents of the itemView to reflect the item at the given position.
     * So here , if position!=0 it implies its a row_item and we set the title and icon of the view.
     */

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (position != 0) {
            holder.devider.setVisibility(View.GONE);
            holder.navTitle.setTextColor(context.getResources().getColor(R.color.black_87));
            holder.navTitle.setTypeface(FontUtil.getInstance().getFont(FontUtil.MEDIUM));
            holder.navTitle.setText(titles[position - 1]);
            //holder.navIcon.setImageResource(icons.getResourceId(position - 1, -1));
            SvgUtil svgUtil = new SvgUtil(context);
            if(position==3){
                //svgUtil.setIcon(R.raw.ic_truck_black, holder.navIcon, (float)0.76);
            }
            else if(position==1){
                //svgUtil.setIcon(R.drawable.ic_task_black, holder.navIcon, (float)0.76);
            }
            else if(position ==2){
                //svgUtil.setIcon(R.raw.ic_bill_black,holder.navIcon,(float)0.76);
            }
        } else {

            holder.Name.setText("Kundan");
            TextDrawable drawable;
            drawable = Util.getInstance().getTextDrawable("K", context);
            holder.profile.setImageDrawable(drawable);
            holder.Name.setTypeface(FontUtil.getInstance().getFont(FontUtil.MEDIUM));


        }

    }

    /**
     * It returns the total no. of items . We +1 count to include the header view.
     * So , it the total count is 5 , the method returns 6.
     * This 6 implies that there are 5 row_items and 1 header view with header at position zero.
     */

    @Override
    public int getItemCount() {
        return titles.length + 1;
    }

    /**
     * This methods returns 0 if the position of the item is '0'.
     * If the position is zero its a header view and if its anything else
     * its a row_item with a title and icon.
     */

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 0;
        else return 1;
    }

    /**
     * Its a inner class to NavigationDrawerAdapter Class.
     * This ViewHolder class implements View.OnClickListener to handle click events.
     * If the itemType==1 ; it implies that the view is a single row_item with TextView and ImageView.
     * This ViewHolder describes an item view with respect to its place within the RecyclerView.
     * For every item there is a ViewHolder associated with it .
     */

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView navTitle;
        ImageView navIcon;
        View devider;
        ImageView profile;
        TextView Name;
        TextView email;
        Context context;

        public ViewHolder(View drawerItem, int itemType, Context context) {
            super(drawerItem);

            this.context = context;
            drawerItem.setOnClickListener(this);
            if (itemType == 1) {
                navTitle = (TextView) itemView.findViewById(R.id.tv_NavTitle);
                navIcon = (ImageView) itemView.findViewById(R.id.iv_NavIcon);
                devider = (View) itemView.findViewById(R.id.devider);

            } else if (itemType == 0) {
                Name = (TextView) itemView.findViewById(R.id.name);         // Creating Text View object from header.xml for name
                email = (TextView) itemView.findViewById(R.id.email);       // Creating Text View object from header.xml for email
                profile = (ImageView) itemView.findViewById(R.id.circleView);// Creating Image view object from header.xml for profile pic

            }
        }

        /**
         * This defines onClick for every item with respect to its position.
         */

        @Override
        public void onClick(View v) {

            HomeActivity homeActivity = (HomeActivity) context;
            homeActivity.drawerLayout.closeDrawers();
            FragmentTransaction fragmentTransaction = homeActivity.getSupportFragmentManager().beginTransaction();

            String user = PreferenceManager.getDefaultSharedPreferences(v.getContext()).getString("prefUsername", "NULL");

            switch (getPosition()) {
                case 1:
                    Fragment chat = ChatFragment.newInstance("Chat", "B");
                    fragmentTransaction.replace(R.id.container, chat);
                    fragmentTransaction.commit();
                    break;
                case 2:
                    Fragment mapFragment = MapFragment.newInstance("Map", null);
                    fragmentTransaction.replace(R.id.container, mapFragment);
                    fragmentTransaction.commit();
                    break;
                case 4:
                    Fragment scmFragment =null;
                    if(user.equalsIgnoreCase("admin")){
                        scmFragment = ScmAdminFragment.newInstance("Beer Game", "C");
                    }else{
                        scmFragment = ScmFragment.newInstance("Beer Game", "C");
                    }
                    fragmentTransaction.replace(R.id.container, scmFragment);
                    fragmentTransaction.commit();
                    break;
            }
        }
    }


}
