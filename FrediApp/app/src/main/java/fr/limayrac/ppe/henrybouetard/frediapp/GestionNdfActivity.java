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
import java.util.List;

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
    public static final String TAG_ANNEE = "Annee";

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

        listNdf = (ListView)findViewById(R.id.listNdf);
        new PostRequest(this, idDem).execute();

    }

    public void addButtonPressed(View v) {
        Intent addNdf = new Intent(this, AddNdfActivity.class);
        startActivity(addNdf);
    }

    public void logoutBtnPressed(View v) {
        Intent backLogin = new Intent(this, LoginActivity.class);
        startActivity(backLogin);

        preferencesEditor = preferencesSettings.edit();
        preferencesEditor.putBoolean("isUserLoggedIn", false);
        preferencesEditor.commit();
    }

    private class PostRequest extends AsyncTask<String, Void, Void> {

        private final Context context;
        private List<NoteDeFrais> listNdfs;
        private int idDemandeur;

        public PostRequest(Context c, int idDem) {
            this.context = c;
            this.idDemandeur = idDem;
            this.listNdfs = new ArrayList<NoteDeFrais>();
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {

            final String finalStatus;
            final String finalMessage;

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

                listNdfs = parseJSON(responseOutput.toString());
                System.out.println("Listndf count: " + listNdfs.size());

                if (!listNdfs.isEmpty()) {
                    finalStatus = "success";
                    finalMessage = "Data downloaded";
                } else {
                    finalStatus = "error";
                    finalMessage = "No ndf to display or user not found";
                }

                GestionNdfActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if (finalStatus.contains("success")) {

                            System.out.println("Data downloaded correctly");
                            List<String> listStr = new ArrayList<String>();
                            for (NoteDeFrais uneNdf : listNdfs) {
                                String str = uneNdf.getTrajet() + "\n" + uneNdf.formatYearFr();
                                listStr.add(str);
                            }

                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(GestionNdfActivity.this, android.R.layout.simple_list_item_1, listStr);
                            GestionNdfActivity.this.listNdf.setAdapter(dataAdapter);

                            progress.dismiss();

                        } else {

                            progress.dismiss();
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

        public List<NoteDeFrais> parseJSON(String json) {
            JSONObject jsonObject;
            List<NoteDeFrais> listNdfs = new ArrayList<>();

            if (json != null) {
                try {
                    jsonObject = new JSONObject(json);
                    String status = jsonObject.getString(TAG_LOGIN_STATUS);

                    if (status.contains("success")) {

                        JSONArray ndfs = jsonObject.getJSONArray(TAG_LOGIN_USER_INFOS);

                        for (int i = 0; i < ndfs.length(); i++) {
                            JSONObject ndf = ndfs.getJSONObject(i);
                            int idNdf = ndf.getInt(TAG_ID);
                            String date = ndf.getString(TAG_DATE);
                            String trajet = ndf.getString(TAG_TRAJET);
                            int distance = ndf.getInt(TAG_KM);
                            double coutP = ndf.getDouble(TAG_COUTP);
                            double coutR = ndf.getDouble(TAG_COUTR);
                            double coutH = ndf.getDouble(TAG_COUTH);
                            int idDem = ndf.getInt(TAG_IDDEM);
                            int idMotif = ndf.getInt(TAG_MOTIF);
                            int annee = ndf.getInt(TAG_ANNEE);
                            NoteDeFrais uneNdf = new NoteDeFrais(idNdf, date, trajet, distance, coutP, coutR, coutH, idDem, idMotif, annee);
                            listNdfs.add(uneNdf);
                        }
                    }

                } catch (JSONException exc) {
                    exc.getMessage();
                }
            }

            return listNdfs;
        }

        private String parseJSONStatus(String json) {

            JSONObject jsonObj;
            String status = "error";

            if (json != null) {
                try {
                    jsonObj = new JSONObject(json);
                    status = jsonObj.getString(TAG_LOGIN_STATUS);
                } catch (JSONException exc) {
                    exc.getMessage();
                }
            }

            return status;
        }
    }

}
