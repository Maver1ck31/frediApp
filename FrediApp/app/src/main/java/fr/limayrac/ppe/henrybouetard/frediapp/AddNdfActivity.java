package fr.limayrac.ppe.henrybouetard.frediapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AddNdfActivity extends AppCompatActivity {

    private ProgressDialog progress;

    Spinner lesMotifs;
    EditText txtTrajet;
    EditText txtKm;
    EditText txtCoutP;
    EditText txtCoutH;
    EditText txtCoutR;

    // JSON Node names
    private static final String TAG_LOGIN_STATUS = "status";
    private static final String TAG_LOGIN_MESSAGE = "message";
    private static final String TAG_MOTIF_ID = "idMotif";
    private static final String TAG_MOTIF_LIBELLE = "libelle";

    private SharedPreferences preferencesSettings;
    private SharedPreferences.Editor preferencesEditor;

    private static final int PREFERENCES_MODE_PRIVATE = 0;
    public static final String PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ndf);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lesMotifs = (Spinner) findViewById(R.id.spMotif);
        txtTrajet = (EditText) findViewById(R.id.txtTrajetNdf);
        txtKm = (EditText) findViewById(R.id.txtDistanceNdf);
        txtCoutP = (EditText) findViewById(R.id.txtCoutP);
        txtCoutH = (EditText) findViewById(R.id.txtCoutH);
        txtCoutR = (EditText) findViewById(R.id.txtCoutR);

        new PostRequestListMotifs(this).execute();
    }

    public void nextButtonPressed(View v) {

        int id = Integer.parseInt(lesMotifs.getSelectedItem().toString().split(":")[0]);
        String libelle = lesMotifs.getSelectedItem().toString().split(":")[1];

        String trajetNdf = txtTrajet.getText().toString();
        int distanceNdf = Integer.parseInt(txtKm.getText().toString());
        float coupP = Float.parseFloat(txtCoutP.getText().toString());
        float coutH = Float.parseFloat(txtCoutH.getText().toString());
        float coutR = Float.parseFloat(txtCoutR.getText().toString());

        preferencesSettings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferencesEditor = preferencesSettings.edit();

        preferencesEditor.putString("trajetNdf",trajetNdf);
        preferencesEditor.putInt("distanceNdf", distanceNdf);
        preferencesEditor.putFloat("coutPNdf", coupP);
        preferencesEditor.putFloat("coutHNdf", coutH);
        preferencesEditor.putFloat("coutRNdf", coutR);
        preferencesEditor.putInt("idMotif", id);

        preferencesEditor.commit();

        Intent addDateNdf = new Intent(this, AddNdfDateActivity.class);
        startActivity(addDateNdf);
    }

    private class PostRequestListMotifs extends AsyncTask<String, Void, Void> {

        private final Context context;
        private List<Motif> listMotifs = new ArrayList<Motif>();

        public PostRequestListMotifs(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading...");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            try {
                // TODO add URL parameter
                URL myUrl = new URL("http://williamhenry.ddns.net/frediApp/actions/retrieveMotif.php");
                HttpURLConnection con = (HttpURLConnection) myUrl.openConnection();
                String urlParameters = "";
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

                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                br.close();

                this.listMotifs = parseJSON(responseOutput.toString());
                
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            AddNdfActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    List<String> str = new ArrayList<String>();
                    for(Motif mot : listMotifs) {
                        String st = mot.getId() + ":" + mot.getMotif();
                        str.add(st);
                    }

                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(AddNdfActivity.this, android.R.layout.simple_list_item_1, str);

                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    AddNdfActivity.this.lesMotifs.setAdapter(dataAdapter);


                    progress.dismiss();
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }

        public List<Motif> parseJSON(String json) {
            JSONObject jsonObject = null;
            List<Motif> listMotifs = new ArrayList<Motif>();

            if (json != null) {
                try {
                    jsonObject = new JSONObject(json);
                    String status = jsonObject.getString(TAG_LOGIN_STATUS);
                    JSONArray motifs = jsonObject.getJSONArray(TAG_LOGIN_MESSAGE);

                    for (int i = 0; i < motifs.length(); i++) {
                        JSONObject motif = motifs.getJSONObject(i);
                        int idMotif = motif.getInt(TAG_MOTIF_ID);
                        String libelle = motif.getString(TAG_MOTIF_LIBELLE);
                        Motif unMotif = new Motif(libelle, idMotif);
                        listMotifs.add(unMotif);
                    }

                } catch (JSONException exc) {
                    exc.getMessage();
                }
            }

            return listMotifs;
        }


    }
}
