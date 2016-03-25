package com.example.lf032813.gestion_ndf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;


public class Gestion_ndf extends AppCompatActivity {

    private ProgressDialog progress;

    private int trajet;
    private int idLigne;

    private static final String TAG_LOGIN_STATUS = "status";
    private static final String TAG_LOGIN_MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_ndf);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //code david
        //public void ndf_enter(View v){
        //Intent goToNdf = new Intent(this, NdfActivity.class);
        //startActivity(goToNdf);
    //}

        String[] mStrings = {
                "AAAAAAAA", "BBBBBBBB", "CCCCCCCC", "DDDDDDDD", "EEEEEEEE",
                "FFFFFFFF", "GGGGGGGG", "HHHHHHHH", "IIIIIIII", "JJJJJJJJ",
                "KKKKKKKK", "LLLLLLLL", "MMMMMMMM", "NNNNNNNN", "OOOOOOOO",
                "PPPPPPPP", "QQQQQQQQ", "RRRRRRRR", "SSSSSSSS", "TTTTTTTT",
                "UUUUUUUU", "VVVVVVVV", "WWWWWWWW", "XXXXXXXX", "YYYYYYYY",
                "ZZZZZZZZ"
        };
        final ListView Listview_ndf = (ListView) findViewById(R.id.listView_Gestion_Ndf);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mStrings);

        Listview_ndf.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_gestion_ndf, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendPostRequest(View View) {
        new PostClass(this).execute();
    }

    private class PostClass extends AsyncTask<String, Void, Void> {

        private final Context context;

        public PostClass(Context c){

            this.context = c;
        }

        protected void onPreExecute(){
            progress= new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {

                URL url = new URL("http://williamhenry.ddns.net/FrediApp/actions/login.php");

                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                String urlParameters = "";
                connection.setRequestMethod("POST");
                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                connection.setDoOutput(true);
                DataOutputStream dStream = new DataOutputStream(connection.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();

                int responseCode = connection.getResponseCode();

                System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Post parameters : " + urlParameters);
                System.out.println("Response Code : " + responseCode);

                final StringBuilder output = new StringBuilder("Request URL " + url);
                //output.append(System.getProperty("line.separator") + "Request Parameters " + urlParameters);
                //output.append(System.getProperty("line.separator")  + "Response Code " + responseCode);
                //output.append(System.getProperty("line.separator")  + "Type " + "POST");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = "";
                final StringBuilder responseOutput = new StringBuilder();
                System.out.println("output===============" + br);


                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }


                br.close();


                JSONObject returnedJSON = parseLoginJSON(responseOutput.toString());

                String status = null;
                String message = null;

                try {
                    status = returnedJSON.getString(TAG_LOGIN_STATUS);
                    message = returnedJSON.getString(TAG_LOGIN_MESSAGE);
                } catch (JSONException e) {
                    e.getMessage();
                }

                final String finalStatus = status;
                final String finalMessage = message;


                Gestion_ndf.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();

                        if (finalStatus.contains("success")) {
                            Intent addNdf = new Intent(Gestion_ndf.this, NdfActivity.class);
                            startActivity(addNdf);
                        } else {
                            // Popup alert to show decrypted message
                            // Instantiate an AlertDialog.Builder with its constructor
                            AlertDialog.Builder builder = new AlertDialog.Builder(Gestion_ndf.this);

                            // Chain together various setter methods to set the dialog characteristics
                            builder.setMessage(finalMessage)
                                    .setTitle(finalStatus);

                            // Adding buttons
                            builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            // Get the AlertDialog from create()
                            AlertDialog dialog = builder.create();

                            // Show the aAlertDialog
                            dialog.show();
                        }
                    }
                });


                //output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + System.getProperty("line.separator") + responseOutput.toString());

                Gestion_ndf.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        System.out.println(responseOutput.toString());
                        progress.dismiss();
                    }
                });

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute() {
            progress.dismiss();
        }
    }

    private JSONObject parseLoginJSON(String json) {

        JSONObject jsonObj = null;

        if (json != null) {
            try {
                jsonObj = new JSONObject(json);
            } catch (JSONException exc) {
                exc.getMessage();
                return null;
            }
        } else {
            return null;
        }

        return jsonObj;
    }
}


