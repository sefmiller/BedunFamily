package com.gb.bedunfamily;

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

/**
 * Adapter for handling correct behaviour of phone dialog, so all countries, flas and dial codes are displayed.
 * Filterable, so user can search and narrow down results to find country quicker
 */
class PhoneCountrySpinnerAdapter extends ArrayAdapter implements Filterable {
    private final CountriesFetcher.CountryList mCountries;
    private ArrayList<String> countriesNameArray;
    private final LayoutInflater mLayoutInflater;
    private final ArrayList<String> filterCountriesList;
    private CountryFilter countryFilter;


    /**
     * @param context app context
     * @param countries arraylist of countries
     *                  constructer initialises arraylist of countries, filtered country names and country objects. inflates
     *                  item layout to display each country as an item in a listview.
     */
    public PhoneCountrySpinnerAdapter(Context context, ArrayList<String> countries) {
        super(context, R.layout.item_country, countries);
        mLayoutInflater = LayoutInflater.from(context);
        mCountries = CountriesFetcher.getCountries(context);
        countriesNameArray = countries;
        filterCountriesList = countries;
    }

    /**
     * List item view
     *sets elements of the each item to display the elements of a country: dialcode, flag, name.
     * @param position    position of item
     * @param convertView View of item
     * @param parent      parent view of item's view
     * @return convertView
     */
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.item_country_phone, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mImageView = convertView.findViewById(R.id.intl_phone_edit__country__item_image);
            viewHolder.mNameView = convertView.findViewById(R.id.intl_phone_edit__country__item_name);
            viewHolder.mDialCode = convertView.findViewById(R.id.intl_phone_edit__country__item_dialcode);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mNameView.setText(countriesNameArray.get(position));
        int index = mCountries.indexOfName(viewHolder.mNameView.getText().toString());
        Country aCountry = mCountries.get(index);
        viewHolder.mImageView.setImageResource(getFlagResource(aCountry));
        viewHolder.mDialCode.setText(String.format("+%s", aCountry.getDialCode()));
        return convertView;
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
     * Fetch flag resource for the Country provided in the method signature.
     *
     * @param country Country
     * @return int of resource | 0 value if not exists
     */
    private int getFlagResource(Country country) {
        return getContext().getResources().getIdentifier("country_" + country.getIso().toLowerCase(), "drawable", getContext().getPackageName());
    }

    /**
     * @return Filter
     * gets country filter to filter countries
     */
    @NonNull
    @Override
    public Filter getFilter() {
        if(countryFilter == null)
            countryFilter = new PhoneCountrySpinnerAdapter.CountryFilter();
        return countryFilter;
    }

    /**
     * View holder for caching
     */
    private static class ViewHolder {
        public ImageView mImageView;
        public TextView mNameView;
        public TextView mDialCode;
    }

    /**
     * Filters country names. for each country name in the filtercountrieslist, if the name contains
     * the user inputted charecter sequence, added to filter results.
     */
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

            } else {
                filterResults.count = filterCountriesList.size();
                filterResults.values = filterCountriesList;
            }

            return filterResults;
        }

        /**
         * @param constraint user inputted character sequence
         * @param results filtered results.
         *                recieves character sequence and sets the countries name array to the filtered results.
         *               notifies adapter to display the filtered results in the countries listview
         */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            countriesNameArray = (ArrayList<String>) results.values;
            notifyDataSetChanged();

        }
    }
}
