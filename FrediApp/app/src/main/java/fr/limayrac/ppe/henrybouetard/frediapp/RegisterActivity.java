package fr.limayrac.ppe.henrybouetard.frediapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Handler;

    public class RegisterActivity extends AppCompatActivity {
    // Link Graphical objects from the UI
    EditText tuserMail;
    EditText tuserMail2;
    EditText tuserPasswd;
    EditText tuserPasswd2;
    EditText tuserNom;
    EditText tuserPrenom;
    EditText tuserRue;
    EditText tuserCP;
    EditText tuserVille;
    TextView tuserOUT;

    private ProgressDialog progress;

    private static final String TAG_LOGIN_STATUS = "status";
    private static final String TAG_LOGIN_MESSAGE = "message";


    public void registerButtonPressed(View v) {
        String mail = tuserMail.getText().toString();
        String mail2 = tuserMail2.getText().toString();
        String passwd = tuserPasswd.getText().toString();
        String passwd2 = tuserPasswd2.getText().toString();
        String nom = tuserNom.getText().toString();
        String prenom = tuserPrenom.getText().toString();
        String rue = tuserRue.getText().toString();
        String cp = tuserCP.getText().toString();
        String ville = tuserVille.getText().toString();
        new PostRequest(this, nom, prenom, rue, cp, ville, mail, passwd, mail2, passwd2).execute();
    }

    // Classe PostRequest ========================================================
    private class PostRequest extends AsyncTask<String, Void, Void> {
        private final Context context;
        private final String nom;
        private final String prenom;
        private final String rue;
        private final String cp;
        private final String ville;
        private final String mail;
        private final String passwd;
        private final String mail2;
        private final String passwd2;

        public PostRequest(Context c, String pnom, String pprenom, String prue, String pcp, String pville, String pmail, String ppasswd,String pmail2, String ppasswd2) {
            this.context = c;
            this.mail = pmail;
            this.passwd = ppasswd;
            this.nom = pnom;
            this.prenom = pprenom;
            this.ville = pville;
            this.cp = pcp;
            this.rue = prue;
            this.mail2 = pmail2;
            this.passwd2 = ppasswd2;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                final TextView outputView = (TextView)findViewById(R.id.tuserOUT);

                URL myUrl = new URL("http://williamhenry.ddns.net/frediApp/actions/register.php");
                //URL myUrl = new URL("http://williamhenry.ddns.net/emsservices/actionsdao/userLoginDao.php");

                HttpURLConnection con = (HttpURLConnection)myUrl.openConnection();
                String urlParameters = "AdresseMail=" + mail + "&AdresseMail2=" + mail2  + "&motDePasse=" + passwd  + "&motDePasse2=" + passwd2 + "&Nom=" + nom + "&Prenom=" + prenom + "&Rue=" + rue + "&CP=" + cp + "&Ville=" + ville;
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

                // TODO parse JSON
                final StringBuilder output = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line = "";
                StringBuilder responseOutput = new StringBuilder();

                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }

                br.close();

                JSONObject returnedJSON = parseJSON(responseOutput.toString());

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

                // register.this.runOnUiThread(new Runnable() {

                //  @Override
                //  public void run() {
                //
                //AlertDialog.Builder builder = new AlertDialog.Builder(register.this);

                RegisterActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.dismiss();
                        if(finalStatus.contains("success"))
                        {
                            tuserOUT.setTextColor(Color.parseColor("#00CC00"));
                            tuserOUT.setText(finalStatus + " : " + finalMessage);
                            // TOTO intent Login
                            Intent loginView = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(loginView);
                        }
                        else {
                            tuserOUT.setText(finalStatus + " : " + finalMessage);
                            tuserOUT.setTextColor(Color.parseColor("#CC0000"));
                        }
                        System.out.println(finalStatus + " " + finalMessage);
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
    // FIN DE CLASSE ===================================================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialising object by using their id's
        tuserMail = (EditText)findViewById(R.id.userMail);
        tuserPasswd = (EditText)findViewById(R.id.userPass);
        tuserMail2 = (EditText)findViewById(R.id.userMail2);
        tuserPasswd2 = (EditText)findViewById(R.id.userPass2);
        tuserCP = (EditText)findViewById(R.id.userCP);
        tuserNom = (EditText)findViewById(R.id.userNom);
        tuserPrenom = (EditText)findViewById(R.id.userPrenom);
        tuserVille = (EditText)findViewById(R.id.userVille);
        tuserRue = (EditText)findViewById(R.id.userRue);
        tuserOUT = (TextView)findViewById(R.id.tuserOUT);
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
