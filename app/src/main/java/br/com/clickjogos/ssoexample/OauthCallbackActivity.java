package br.com.clickjogos.ssoexample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class OauthCallbackActivity extends Activity {
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth_callback);

        final TextView uid = (TextView) findViewById(R.id.uid);
        final TextView provider = (TextView) findViewById(R.id.provider);
        final TextView nickname = (TextView) findViewById(R.id.nickname);
        final TextView birthday = (TextView) findViewById(R.id.birthdate);
        final TextView email = (TextView) findViewById(R.id.email);
        final TextView gender = (TextView) findViewById(R.id.gender);
        final TextView avatarThumbUrl = (TextView) findViewById(R.id.avatarThumbUrl);
        final TextView avatarBigUrl = (TextView) findViewById(R.id.avatarBigUrl);
        final ImageView avatarThumb = (ImageView) findViewById(R.id.avatarThumb);
        final ImageView avatarBig = (ImageView) findViewById(R.id.avatarBig);

        if (savedInstanceState == null) {
            Bundle bundle = getIntent().getExtras();
            userId = bundle.getString("uid");
        }

        new Thread() {
            @Override
            public void run() {
                try {
                    String input = getUser();
                    final JSONObject response = new JSONObject(input);
                    final JSONObject user = (JSONObject) response.get("info");

                    new DownloadImageTask(avatarThumb).execute(user.getString("avatar_thumb"));
                    new DownloadImageTask(avatarBig).execute(user.getString("avatar_big"));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                uid.setText("Uid: " + response.getString("uid"));
                                provider.setText("Provider: " + response.getString("provider"));
                                nickname.setText("Nickname: " + user.getString("nickname"));
                                birthday.setText("Birthdate: " + user.getString("birthdate"));
                                email.setText("E-mail: " + user.getString("email"));
                                gender.setText("Gender: " + user.getString("gender"));
                                avatarThumbUrl.setText("Avatar Thumb Url: " + user.getString("avatar_thumb"));
                                avatarBigUrl.setText("Avatar Big Url: " + user.getString("avatar_big"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public String getUser() {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(Constants.OAUTH_URL + "/user/" + Constants.KEY + "/" + Constants.SECRET + "/" + userId);

        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                System.out.println("Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;

            try {
                InputStream inputStream = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }
}