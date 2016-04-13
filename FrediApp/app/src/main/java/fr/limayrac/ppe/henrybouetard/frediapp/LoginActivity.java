package fr.limayrac.ppe.henrybouetard.frediapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

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

public class LoginActivity extends AppCompatActivity {

    // Link Graphical objects from the UI
    EditText userMail;
    EditText userPasswd;

    private ProgressDialog progress;

    private SharedPreferences preferencesSettings;
    private SharedPreferences.Editor preferencesEditor;

    private static final int PREFERENCES_MODE_PRIVATE = 0;
    public static final String PREFS_NAME = "MyPrefsFile";

    // JSON Node names
    private static final String TAG_LOGIN_STATUS = "status";
    private static final String TAG_LOGIN_MESSAGE = "message";
    private static final String TAG_LOGIN_USER_INFOS = "infos";

    public void loginButtonPressed(View v) {
        String mail = userMail.getText().toString();
        String passwd = userPasswd.getText().toString();

        new PostRequest(this, mail, passwd).execute();
    }

    public void registerButtonPressed(View v) {
        Intent registerActivity = new Intent(this, RegisterActivity.class);
        startActivity(registerActivity);
    }


    private class PostRequest extends AsyncTask<String, Void, Void> {

        private final Context context;
        private final String mail;
        private final String passwd;

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

                //URL myUrl = new URL("http://192.168.1.29/frediApp/actions/login.php");
                URL myUrl = new URL("http://williamhenry.ddns.net/frediApp/actions/login.php");


                HttpURLConnection con = (HttpURLConnection)myUrl.openConnection();
                String urlParameters = "AdresseMail=" + mail + "&motDePasse=" + passwd;
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

                JSONObject returnedJSON = parseLoginJSON(responseOutput.toString());

                String status = null;
                String message = null;
                int id = 888888;

                try {
                    status = returnedJSON.getString(TAG_LOGIN_STATUS);
                    message = returnedJSON.getString(TAG_LOGIN_MESSAGE);
                    id = returnedJSON.getInt(TAG_LOGIN_USER_INFOS);
                } catch (JSONException e) {
                    e.getMessage();
                }

                final String finalStatus = status;
                final String finalMessage = message;
                final int finalId = id;

                LoginActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        progress.dismiss();

                        if (finalStatus.contains("success")) {
                            Intent gestionNdf = new Intent(LoginActivity.this, GestionNdfActivity.class);
                            startActivity(gestionNdf);
                            finish();

                            System.out.println("finalId: " + finalId);

                            preferencesSettings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                            preferencesEditor = preferencesSettings.edit();

                            preferencesEditor.putInt("idDem", finalId);
                            preferencesEditor.putBoolean("isUserLoggedIn", true);
                            preferencesEditor.commit();

                        } else {
                            // Popup alert to show decrypted message
                            // Instantiate an AlertDialog.Builder with its constructor
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

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

        protected void onPostRequestExecute() {
            progress.dismiss();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialising object by using their id's
        userMail = (EditText)findViewById(R.id.txtMail);
        userPasswd = (EditText)findViewById(R.id.txtPassword);

        preferencesSettings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Boolean loggedIn = preferencesSettings.getBoolean("isUserLoggedIn", false);

        if (loggedIn) {
            Intent goGestNdf = new Intent(this, GestionNdfActivity.class);
            startActivity(goGestNdf);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
