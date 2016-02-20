package com.hackday.inmobi.inmobihackday.activities;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.hackday.inmobi.inmobihackday.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationSearchActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_LAT = "intent_extra_lat";
    public static final String INTENT_EXTRA_LONG = "intent_extra_long";
    public static final String INTENT_EXTRA_ADDRESS_STRING = "intent_extra_address_string";

    private List<Address> addresses;
    private List<String> addressStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);
        EditText editText = (EditText) findViewById(R.id.autoCompleteTextView);
        String destination = "Shoppers stop, bangalore";
        editText.setText(destination);
        editText.setSelection(editText.getText().length());

        (new AddressFetcher()).execute(destination);

        ((ListView) findViewById(R.id.suggestionsList)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent result = new Intent();
                result.putExtra(INTENT_EXTRA_ADDRESS_STRING, addressStrings.get(position));
                result.putExtra(INTENT_EXTRA_LAT, addresses.get(position).getLatitude());
                result.putExtra(INTENT_EXTRA_LONG, addresses.get(position).getLongitude());
                LocationSearchActivity.this.setResult(Activity.RESULT_OK, result);
                LocationSearchActivity.this.finish();
            }
        });
    }

    private void populateSuggestionsList(List<Address> addresses) {
        this.addresses = addresses;

        addressStrings = new ArrayList<>();
        for(Address address: addresses) {
            String addressString = address.getAddressLine(0);
            for (int index = 1 ; index <= address.getMaxAddressLineIndex() ; index++) {
                addressString = addressString + ", " + address.getAddressLine(index);
            }
            addressStrings.add(addressString);
        }

        ListView listView = (ListView) findViewById(R.id.suggestionsList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, addressStrings);
        listView.setAdapter(adapter);
    }

    class AddressFetcher extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... params) {

            List<Address> addresses;
            Geocoder geocoder = new Geocoder(LocationSearchActivity.this, Locale.getDefault());
            try {
                addresses = geocoder.getFromLocationName(params[0], 10);
                return addresses;
            } catch (IOException e) {
                // do nothing
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {
            super.onPostExecute(addresses);
            LocationSearchActivity.this.populateSuggestionsList(addresses);
        }
    }
}
