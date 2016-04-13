package fr.limayrac.ppe.henrybouetard.frediapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.DatePicker;

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

public class AddNdfDateActivity extends AppCompatActivity {

    private ProgressDialog progress;
    private SharedPreferences preferencesSettings;
    public static final String PREFS_NAME = "MyPrefsFile";

    // JSON Node names
    private static final String TAG_LOGIN_STATUS = "status";
    private static final String TAG_LOGIN_MESSAGE = "message";

    // Link UI elements
    DatePicker dateNdf;

    String trajet;
    int distance;
    int idMotif;
    int idDem;
    int annee;
    float coutP;
    float coutH;
    float coutR;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ndf_date);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dateNdf = (DatePicker) findViewById(R.id.datePicker);
        annee = dateNdf.getYear();
        date = annee + "-" + dateNdf.getMonth() + "-" + dateNdf.getDayOfMonth();

        preferencesSettings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        trajet = preferencesSettings.getString("trajetNdf", "Error");
        distance = preferencesSettings.getInt("distanceNdf", 0);
        coutP = preferencesSettings.getFloat("coutPNdf", 0);
        coutH = preferencesSettings.getFloat("coutHNdf", 0);
        coutR = preferencesSettings.getFloat("coutRNdf", 0);
        idMotif = preferencesSettings.getInt("idMotif", 0);
        idDem = preferencesSettings.getInt("idDem", 0);

        System.out.println("tra: " + trajet + "\tdist: " + distance + "\tidMotif: " + idMotif
                + "\tidDem: " + idDem + "\ncoutP: " + coutP + "\ncoutH: " + coutH + "\ncoutR: " + coutR);
    }

    public void btnAddPressed(View v) {
        date = dateNdf.getYear() + "-" + dateNdf.getMonth() + "-" + dateNdf.getDayOfMonth();
        new PostRequestAdd(this).execute();
    }

    private class PostRequestAdd extends AsyncTask<String, Void, Void> {

        private final Context context;

        public PostRequestAdd(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            try {

                //final TextView outputView = (TextView) findViewById(R.id.txtOutput);

                // URL myUrl = new URL("williamhenry.ddns.net/frediApp/actions/login.php");
                URL myUrl = new URL("http://williamhenry.ddns.net/frediApp/actions/insertNdf.php");


                final HttpURLConnection con = (HttpURLConnection) myUrl.openConnection();
                String urlParameters = "date=" + date + "&trajet=" + trajet +"&km=" + distance
                        + "&coutPeage=" + coutP + "&coutRepas=" + coutR + "&coutHebergement=" + coutH
                        + "&idDemandeur=" + idDem + "&idMotif=" + idMotif + "&annee=" + annee;
                con.setRequestMethod("POST");
                con.setRequestProperty("USER-AGENT", "Mozilla/5.0");
                con.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
                con.setDoOutput(true);

                DataOutputStream dStream = new DataOutputStream(con.getOutputStream());
                dStream.writeBytes(urlParameters);
                dStream.flush();
                dStream.close();

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

                AddNdfDateActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();
                        if (finalStatus.contains("success")) {
                            // Instantiate an AlertDialog.Builder with its constructor
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

                            // Chain together various setter methods to set the dialog characteristics
                            builder.setMessage(finalMessage)
                                    .setTitle(finalStatus);

                            // Adding buttons
                            builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent gestNdf = new Intent(context, GestionNdfActivity.class);
                                    startActivity(gestNdf);
                                    finish();
                                }
                            });

                            // Get the AlertDialog from create()
                            AlertDialog dialog = builder.create();

                            // Show the aAlertDialog
                            dialog.show();

                        } else {
                            // Instantiate an AlertDialog.Builder with its constructor
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);

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
