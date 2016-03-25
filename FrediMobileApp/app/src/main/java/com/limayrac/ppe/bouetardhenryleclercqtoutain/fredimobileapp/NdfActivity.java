package com.limayrac.ppe.bouetardhenryleclercqtoutain.fredimobileapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class NdfActivity extends AppCompatActivity {

    EditText inputDate;
    EditText inputTrajet;
    EditText inputMotif;
    EditText inputnbkm;
    EditText inputCtTrajet;
    EditText inputCtPeage;
    EditText inputCtHebergement;
    EditText inputCtRepas;
    EditText inputCtTotal;

    Spinner list_ligue;

    Date itDate;
    String itTrajet;
    String itMotif;
    int itNbkm;
    int itCtTrajet;
    int itCtPeage;
    int itCtHeberg;
    int itCtRepas;
    int itCtTotal;


    // JSON Node names
    private static final String TAG_LOGIN_STATUS = "status";
    private static final String TAG_LOGIN_MESSAGE = "message";


    private ProgressDialog progress;

    private String frediurl;

    public NdfActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ndf_);
        inputDate = (EditText) findViewById(R.id.inputDate);
        inputTrajet = (EditText) findViewById(R.id.inputTrajet);
        inputMotif = (EditText) findViewById(R.id.inputMotif);
        inputnbkm = (EditText) findViewById(R.id.inputNbkm);
        inputCtTrajet = (EditText) findViewById(R.id.inputCttrajet);
        inputCtPeage = (EditText) findViewById(R.id.inputCtpeage);
        inputCtHebergement = (EditText) findViewById(R.id.inputCtheberg);
        inputCtRepas = (EditText) findViewById(R.id.inputCtrepas);
        inputCtTotal = (EditText) findViewById(R.id.inputCttotal);

        list_ligue = (Spinner) findViewById(R.id.list_ligue);
        String[] list = {"Ligue de Taekwondo","Ligue de Football", "Ligue de Badminton", "Ligue d'Echec", "Ligue de Boxe"};


        ArrayAdapter<String> adp1=new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,list);
        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        list_ligue.setAdapter(adp1);

        frediurl = "";


        /*if(inputCtTrajet.getText().toString() != "") {
            //itDate = ;
            itTrajet = inputTrajet.getText().toString();
            itMotif = inputMotif.getText().toString();
            itNbkm = Integer.parseInt(inputnbkm.getText().toString());
            itCtTrajet = Integer.parseInt(inputCtTrajet.getText().toString());
            itCtPeage = Integer.parseInt(inputCtPeage.getText().toString());
            itCtHeberg = Integer.parseInt(inputCtHebergement.getText().toString());
            itCtRepas = Integer.parseInt(inputCtRepas.getText().toString());
            itCtTotal = Integer.parseInt(inputCtTotal.getText().toString());
        }*/
        String urlParameters = "date="+itDate+"&trajet="+itTrajet+"&motif="+itMotif+
                "&nbkm="+itNbkm+"&cttrajet="+itCtTrajet+"&ctpeage="+itCtPeage+"&ctheberg="
                +itCtHeberg+"&cttotal="+itCtTotal+"&idligue=";
    }

    private class PostRequest extends AsyncTask<String, Void, Void> {

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                

        public PostRequest(Context c, String mail, String passwd) {
            this.context = c;
            this.mail = mail;
            this.passwd = passwd;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            try {


                URL myUrl = new URL("http://192.168.1.29/frediApp/actions/login.php");
                //URL myUrl = new URL("http://williamhenry.ddns.net/frediApp/actions/login.php");


                HttpURLConnection con = (HttpURLConnection) myUrl.openConnection();
                String urlParameters = "AdresseMail=" + mail + "&motDePasse=" + passwd;
                con.setRequestMethod("POST");
                con.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                con.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                con.setDoOutput(true);

                DataOutputStream dStream = new DataOutputStream(con.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();

                int responseCode = con.getResponseCode();

                System.out.println("\nSending 'POST' request to URL : " + myUrl);
                System.out.println("Post parameters : " + urlParameters);
                System.out.println("Response Code : " + responseCode);

                final StringBuilder output = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line = "";
                StringBuilder responseOutput = new StringBuilder();

                while ((line = br.readLine()) != null) {
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

                NdfActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();

                        if (finalStatus == "success") {

                        } else {
                            // Popup alert to show decrypted message
                            // Instantiate an AlertDialog.Builder with its constructor
                            AlertDialog.Builder builder = new AlertDialog.Builder(NdfActivity.this);

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

            } catch (MalformedURLException e) {
                // TODO Generate Exception
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Generate Exception
                e.printStackTrace();
            }

            return null;
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
}

