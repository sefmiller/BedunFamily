package com.gb.globalfamily;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static com.gb.globalfamily.R.layout.item_country;

class CountrySpinnerAdapter extends ArrayAdapter implements Filterable {
    private ArrayList<String> countriesNameArray;
    private final LayoutInflater mLayoutInflater;
    private final CountriesFetcher.CountryList mCountries;
    private CountryFilter countryFilter;
    private final ArrayList<String> filterCountriesList;



    public CountrySpinnerAdapter(Context context, ArrayList<String> countries) {
        super(context, R.layout.item_country, countries);
        mCountries = CountriesFetcher.getCountries(context);
        mLayoutInflater = LayoutInflater.from(context);
        countriesNameArray = countries;
        filterCountriesList = countries;
    }

    /**
     * List item view
     *
     * @param position    position of item
     * @param convertView View of item
     * @param parent      parent view of item's view
     * @return covertView
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(item_country, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mImageView = convertView.findViewById(R.id.intl_phone_edit__country__item_image);
            viewHolder.mNameView = convertView.findViewById(R.id.intl_phone_edit__country__item_name);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mNameView.setText(countriesNameArray.get(position));
        System.out.println(viewHolder.mNameView.getText().toString());
        int index = mCountries.indexOfName(viewHolder.mNameView.getText().toString());
        Country aCountry = mCountries.get(index);
        viewHolder.mImageView.setImageResource(getFlagResource(aCountry));
        return convertView;
    }



    /**
     * Fetch flag resource by Country
     *
     * @param country Country
     * @return int of resource | 0 value if not exists
     */
    private int getFlagResource(Country country) {
        return getContext().getResources().getIdentifier("country_" + country.getIso().toLowerCase(), "drawable", getContext().getPackageName());
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if(countryFilter == null)
            countryFilter = new CountryFilter();
        return countryFilter;
    }

    @Override
    public int getCount() {
        return countriesNameArray.size();
    }

    @Override
    public String getItem(int position) {
        return countriesNameArray.get(position);
    }
    /**
     * View holder for caching
     */
    private static class ViewHolder {
        public ImageView mImageView;
        public TextView mNameView;
    }

    class CountryFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                ArrayList<String> tempList = new ArrayList<>();

                for (String name : filterCountriesList) {
                    if (name.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(name);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
                System.out.println("performFiltering:SearchFoundCount "+ tempList.size());
                System.out.println("performFiltering:ActualSize "+ filterCountriesList.size());

            } else {
                filterResults.count = filterCountriesList.size();
                filterResults.values = filterCountriesList;
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            countriesNameArray = (ArrayList<String>) results.values;
            notifyDataSetChanged();

        }
    }
}
