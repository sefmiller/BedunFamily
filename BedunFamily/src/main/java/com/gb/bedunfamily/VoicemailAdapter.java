package com.gb.bedunfamily;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gb.bedunfamily.Model.Refugee;
import com.gb.bedunfamily.Model.Voicemail;

import java.util.ArrayList;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.gb.bedunfamily.MainActivity.dbHelper;
import static com.gb.bedunfamily.MainActivity.mCountries;


/**
 * Adapter for inbox/sentbox screens. handles loading the voicemails as inbox items with details of the refugee attached to the voicemail.
 * on click determines if the user can reply to the voicemail.
 */
public class VoicemailAdapter extends RecyclerView.Adapter<VoicemailAdapter.MyViewHolder>  {

    private final ArrayList<Refugee> dataSet;
    private final ArrayList<Integer> keySet;
    private final ArrayList<Voicemail> vmData;
    private final Context context;

    /**
     * The view holder initialises the layout of an item view in the adapter. handles behaviour such as on click.
     */
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final RelativeLayout relativeLayout;
        private final TextView line1;
        private final TextView line2;
        private final TextView line3;
        private final TextView dateView;
        private final CircleImageView natView;

        private Refugee ref;
        private String refId;


        /**
         * @param itemView an item in the list
         */
        public MyViewHolder(View itemView) {
            super(itemView);
            this.relativeLayout = itemView.findViewById(R.id.relativeLayout2);
            line1 = itemView.findViewById(R.id.inbox_line1);
            line2 = itemView.findViewById(R.id.inbox_line2);
            line3 = itemView.findViewById(R.id.textView);
            dateView = itemView.findViewById(R.id.date_view);
            natView = itemView.findViewById(R.id.profile_image);


            relativeLayout.setOnClickListener(this);
        }

        /**
         * @param v view if the searchable refugee is the receiver of the voicemail, loads SentVoicemailFragment.
         *          Else, either loads RecievedVoicemailFragment, where the user can send a reply, or if the users are
         *          already matched, loads SentVoicemailFragment, where the user can only listen to the voicemail.
         */
        @Override
        public void onClick(View v) {

            refId = line3.getText().toString();

            if(vmData.get(0).getRecieverId().equals(refId)){
                android.support.v4.app.Fragment myFragment = new MainActivity.SentVoicemailFragment();
                loadFragment(myFragment);
            }
            else{
                android.support.v4.app.Fragment frag = new MainActivity.RecievedVoicemailFragment();
                //inbox
                String recId = vmData.get(getAdapterPosition()).getRecieverId();
                String senId = vmData.get(getAdapterPosition()).getSenderId();
                String idString = recId+senId;
                int id = Integer.parseInt(idString);
                System.out.println(id + "ha");
                System.out.println(dbHelper.getRemovedFromSearchMap().keySet());
                for (int key: dbHelper.getRemovedFromSearchMap().keySet()) {
                    if (key == id) {
                        ArrayList<String> tempArray = dbHelper.getRemovedFromSearchMap().get(key);
                        System.out.println(tempArray + "YEAH YEAH");
                            if (tempArray.get(2).equals("Match") || tempArray.get(2).equals("NoMatch")) {
                                System.out.println("here: " + "ha");
                                frag = new MainActivity.SentVoicemailFragment();
                        }
                    }
                }
                System.out.println(frag);
                dbHelper.setSearchableRefugee(ref);
                loadFragment(frag);
                }
            }

        /**
         * @param myFragment a fragment to be loaded to the mainactivity screen. Sets title in toolbar to the searchable refugee
         *                    name. passes id of the searchable refugee to the fragment. Replaces current fragment with fragment
         *                   provided in the method signature.
         */
        private void loadFragment(Fragment myFragment) {
            MainActivity activity = (MainActivity) itemView.getContext();
            activity.getmTitle().setText(ref.getName());
            Bundle data = new Bundle();
            data.putString("refId",refId);
            myFragment.setArguments(data);
            android.support.v4.app.FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.fadein, R.anim.fadeout);
            ft.replace(R.id.content_frame, myFragment);
            ft.addToBackStack(null).commit();
        }
    }

    /**
     * @param data array of refugees received / sent
     * @param keySet Map keys to identify and get refugee received or sent.
     * @param vmdata array of voicemail objects sent or received
     * @param context Application context.
     */
    public VoicemailAdapter(ArrayList<Refugee> data, ArrayList<Integer> keySet, ArrayList<Voicemail> vmdata, Context context) {
        this.context = context;
        dataSet = data;
        this.keySet = keySet;
        this.vmData = vmdata;
        System.out.println(keySet.toString());

    }

    /**
     * consctructor. initialises item layout
     * @param parent parent viewgroup
     * @param viewType view type
     * @return returns created viewholder
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inbox_item, parent, false);
            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;

    }

    /**
     * @param holder  initialised item view holder
     * @param position position of item
     *                 sets item view so each refugee in array is displayed in the item view holder.
     */
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.ref = dataSet.get(position);
            String lineString = holder.ref.getName() + "  (" + holder.ref.getNickname() + ")";
            String secondLineString = holder.ref.getLoc() + " , " + holder.ref.getPlaceOfBirth() + ", " + holder.ref.getAgeGroup();
            int index = mCountries.indexOfName(holder.ref.getNationality());
            Country aCountry = mCountries.get(index);
            holder.natView.setImageResource(getFlagResource(aCountry));
            holder.line1.setText(lineString);
            holder.line2.setText(secondLineString);
            holder.line3.setText(keySet.get(position).toString());
            holder.dateView.setText(vmData.get(position).getDate());
    }

    /**
     * Fetch flag resource by Country
     *
     * @param country Country
     * @return int of resource | 0 value if not exists
     */
    private int getFlagResource(Country country) {
        return context.getResources().getIdentifier("country_" + country.getIso().toLowerCase(), "drawable", context.getPackageName());
    }
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
