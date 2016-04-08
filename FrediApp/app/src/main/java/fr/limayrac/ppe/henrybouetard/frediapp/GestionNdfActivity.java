package fr.limayrac.ppe.henrybouetard.frediapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

public class GestionNdfActivity extends AppCompatActivity {

    ListView listNdf;
    String[] mStrings = {
            "AAAAAAAA", "BBBBBBBB", "CCCCCCCC", "DDDDDDDD", "EEEEEEEE",
            "FFFFFFFF", "GGGGGGGG", "HHHHHHHH", "IIIIIIII", "JJJJJJJJ",
            "KKKKKKKK", "LLLLLLLL", "MMMMMMMM", "NNNNNNNN", "OOOOOOOO",
            "PPPPPPPP", "QQQQQQQQ", "RRRRRRRR", "SSSSSSSS", "TTTTTTTT",
            "UUUUUUUU", "VVVVVVVV", "WWWWWWWW", "XXXXXXXX", "YYYYYYYY",
            "ZZZZZZZZ"
    };

    ArrayList <NoteDeFrais> notesFrais = new ArrayList<NoteDeFrais>();
    ArrayList <String> listTraj = new ArrayList<>();
    String[] ndfTraj;

    private ProgressDialog progress;

    private static final String TAG_LOGIN_STATUS = "status";
    private static final String TAG_LOGIN_MESSAGE = "message";
    private static final String TAG_LOGIN_USER_INFOS = "infos";
    public static final String PREFS_NAME = "MyPrefsFile";

    public static final String TAG_ID = "idLigne";
    public static final String TAG_DATE = "date";
    public static final String TAG_TRAJET = "trajet";
    public static final String TAG_COUTP = "coutPeage";
    public static final String TAG_COUTR = "coutRepas";
    public static final String TAG_COUTH = "coutHebergement";
    public static final String TAG_KM = "km";
    public static final String TAG_IDDEM = "idDemandeur";
    public static final String TAG_MOTIF = "idMotif";
    public static final String TAG_ANNEE = "idAnnee";

    private SharedPreferences preferencesSettings;
    private SharedPreferences.Editor preferencesEditor;

    private static final int PREFERENCES_MODE_PRIVATE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_ndf);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preferencesSettings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int idDem = preferencesSettings.getInt("idDem", 777777);

        if (idDem != 777777) {
            System.out.println("idDem: " + idDem);
        } else {
            System.out.println("Value not found");
        }

        new PostRequest(this, idDem).execute();

        listNdf = (ListView)findViewById(R.id.listNdf);

        for (NoteDeFrais note: notesFrais) {
            String motif = note.getTrajet();
            listTraj.add(motif);
        }

        if (notesFrais.size() > 0) {

            ndfTraj = new String[notesFrais.size()];
            for (int i = 0; i < notesFrais.size(); i++) {
                NoteDeFrais aNdf = notesFrais.get(i);
                String motif = aNdf.getTrajet();
                ndfTraj[i] = motif;
            }

            System.out.println("ndfTraj: " + ndfTraj.length);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ndfTraj);
            listNdf.setAdapter(adapter);
        }

    }

    public void addButtonPressed(View v) {
        Intent addNdf = new Intent(this, AddNdfActivity.class);
        startActivity(addNdf);
    }

    private class PostRequest extends AsyncTask<String, Void, Void> {

        private Context context;
        private int idDemandeur;

        public PostRequest(Context c, int idDem) {
            this.context = c;
            this.idDemandeur = idDem;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {


            try {
                URL myUrl = new URL("http://williamhenry.ddns.net/frediApp/actions/retrieveNdf.php");
                HttpURLConnection con = (HttpURLConnection)myUrl.openConnection();
                String urlParameters = "idDemandeur=" + idDemandeur;
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

                JSONObject returnedJSON = parseJSON(responseOutput.toString());

                JSONArray allNdf;

                String status = null;
                String message = null;

                try {
                    status = returnedJSON.getString(TAG_LOGIN_STATUS);
                    message = returnedJSON.getString(TAG_LOGIN_MESSAGE);
                    allNdf = returnedJSON.getJSONArray(TAG_LOGIN_USER_INFOS);

                    for (int i = 0; i< allNdf.length(); i++) {
                        JSONObject ndf = allNdf.getJSONObject(i);
                        int id = ndf.getInt(TAG_ID);
                        String date = ndf.getString(TAG_DATE);
                        String trajet = ndf.getString(TAG_TRAJET);
                        int km = ndf.getInt(TAG_KM);
                        double coutP = ndf.getDouble(TAG_COUTP);
                        double coutR = ndf.getDouble(TAG_COUTR);
                        double coutH = ndf.getDouble(TAG_COUTH);
                        int idDem = ndf.getInt(TAG_IDDEM);
                        int idMotif = ndf.getInt(TAG_MOTIF);
                        int idAnnee = ndf.getInt(TAG_ANNEE);

                        NoteDeFrais aNdf = new NoteDeFrais(id, date, trajet, km, coutP, coutR, coutH, idDem, idMotif, idAnnee);
                        GestionNdfActivity.this.notesFrais.add(aNdf);
                    }

                } catch (JSONException e) {
                    e.getMessage();
                }

                final String finalStatus = status;
                final String finalMessage = message;

                GestionNdfActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();

                        if (finalStatus.contains("success")) {

                            System.out.println("Data downloaded correctly");

                        } else {
                            // Popup alert to show decrypted message
                            // Instantiate an AlertDialog.Builder with its constructor
                            AlertDialog.Builder builder = new AlertDialog.Builder(GestionNdfActivity.this);

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
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        protected void onPostRequestExecute() {
            progress.dismiss();
        }

        private JSONObject parseJSON(String json) {

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
