package com.example.brom.listviewjsonapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
// Create ArrayLists from the raw data above and use these lists when populating your ListView.



// Create a new class, Mountain, that can hold your JSON data

// Create a ListView as in "Assignment 1 - Toast and ListView"

// Retrieve data from Internet service using AsyncTask and the included networking code

// Parse the retrieved JSON and update the ListView adapter

// Implement a "refresh" functionality using Android's menu system


public class MainActivity extends AppCompatActivity {

    private String[] mountainNames = {""};
    private String[] mountainLocations = {""};
    private int[] mountainHeights ={};
    private ArrayList<String> listData;
    private ArrayAdapter<Mountain> mountainAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new FetchData().execute();//Hämtar datan
        listData=new ArrayList<>(Arrays.asList(mountainNames)); //Listar den hämtade datan i mountainNames fältet
        mountainAdapter=new ArrayAdapter<Mountain>(this,R.layout.my_item,R.id.my_item);

        ListView my_listview=(ListView) findViewById(R.id.mountain_listview); //Skapar en listview
        my_listview.setAdapter(mountainAdapter); //Listview ska innehålla data från mountainAdapter

        my_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() { //Toast som bestämmer vilken information som ska printas beroende på variabeln i (senare skapad for-loop)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String temp = mountainAdapter.getItem(i).info();
                Toast.makeText(getApplicationContext(), temp, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){ //Lägger knappen i menyn
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){ // Kör fetch vid klick av refresh knappen
        int id = item.getItemId();

        if(id == R.id.action_refresh){
            mountainAdapter.clear(); //Rensar listan vid refresh så gamla versioner rensas
            new FetchData().execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class FetchData extends AsyncTask<Void,Void,String>{
        @Override

        protected String doInBackground(Void... params) {
            // These two variables need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a Java string.
            String jsonStr = null;

            try {
                // Construct the URL for the Internet service
                URL url = new URL("http://wwwlab.iit.his.se/brom/kurser/mobilprog/dbservice/admin/getdataasjson.php?type=brom"); //URL

                // Create the request to the PHP-service, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                jsonStr = buffer.toString();
                return jsonStr;
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in
                // attempting to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("Network error", "Error closing stream", e);
                    }
                }
            }
        }
        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
            Log.d("brom","DataFetched:"+o);
            // This code executes after we have received our data. The String object o holds
            // the un-parsed JSON string or is null if we had an IOException during the fetch.

            // Implement a parsing code that loops through the entire JSON and creates objects
            // of our newly created Mountain class.
            try {

                JSONArray toastArray = new JSONArray(o);
                for (int i = 0; i < toastArray.length(); i++) { //for-loop som bestämmer värdet på i för användning av toast tidigare i programmet
                    Log.d("brom", "element 0:" + toastArray.get(i).toString());
                    JSONObject container = toastArray.getJSONObject(i);
                    //loggar data om bergen i objectet container
                    Log.d("brom", container.getString("name"));
                    Log.d("brom", container.getString("location"));
                    Log.d("brom", "" + container.getInt("size"));

                    Mountain m = new Mountain(container.getString("name"), container.getString("location"), container.getInt("size"));
                    Log.d("brom", m.toString());
                    mountainAdapter.add(m); //Lägger till berget i mountainadapter som sedan visas i listview
                }
            }
            catch (JSONException e) {
                Log.e("brom","E:"+e.getMessage());
            }
        }
    }
}

