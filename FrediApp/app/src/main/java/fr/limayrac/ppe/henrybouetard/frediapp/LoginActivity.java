package fr.limayrac.ppe.henrybouetard.frediapp;

import android.app.ProgressDialog;
import android.content.Context;
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

    public void loginButtonPressed(View v) {
        String mail = userMail.getText().toString();
        String passwd = userPasswd.getText().toString();

        new PostRequest(this, mail, passwd).execute();
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

                final TextView outputView = (TextView)findViewById(R.id.textView);

                // URL myUrl = new URL("williamhenry.ddns.net/frediApp/actions/login.php");
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

                int responseCode = con.getResponseCode();

                System.out.println("\nSending 'POST' request to URL : " + myUrl);
                System.out.println("Post parameters : " + urlParameters);
                System.out.println("Response Code : " + responseCode);

                final StringBuilder output = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line = "";
                StringBuilder responseOutput = new StringBuilder();

                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                br.close();

                output.append("Response: " + System.getProperty("line.separator") + responseOutput.toString());

                LoginActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        outputView.setText(output);
                        System.out.println(output);
                        progress.dismiss();
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
