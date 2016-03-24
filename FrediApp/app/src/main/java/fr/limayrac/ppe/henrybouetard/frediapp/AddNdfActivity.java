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
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AddNdfActivity extends AppCompatActivity {

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ndf);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private class PostRequest extends AsyncTask<String, Void, Void> {

        private final Context context;

        public PostRequest(Context c) {
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

                final TextView outputView = (TextView) findViewById(R.id.txtOutput);

                // URL myUrl = new URL("williamhenry.ddns.net/frediApp/actions/login.php");
                URL myUrl = new URL("http://williamhenry.ddns.net/frediApp/actions/retrieveLigue.php");


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

                output.append("Response: " + System.getProperty("line.separator") + responseOutput.toString());

                AddNdfActivity.this.runOnUiThread(new Runnable() {

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
    }
}
