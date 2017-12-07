package decoster.keepup.helper;

import android.graphics.Bitmap;
import android.location.Location;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import decoster.keepup.activity.MainActivity;
import decoster.keepup.app.AppConfig;

/**
 * Created by Decoster on 04/02/2016.
 */
public class Sender {


    //called to send coordinate to server
    public void sendCoordinate(Location location) {
        JSONObject json = createBasicJSON(location);
        try {
            json.put("type", "coordinate");
            new ExecuteTask().execute(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //called to send Image to server
    public void sendImage(Bitmap image, Location location) {
        JSONObject json =createBasicJSON(location);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageBytes = stream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        try {
            json.put("image", encodedImage);
            json.put("type", "picture");

            new ExecuteTask().execute(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    //Called to send the new journey information to server
    public  void sendNewJounrey(String name, String mode, String comment) {
        JSONObject json = createBasicJSON();
        new ExecuteTask().execute(json);
    }

    //Called when button stop is clicked
    public void sendStopJourney() {
        JSONObject json = createBasicJSON();
        try {
            json.put("type", "stop");
            new ExecuteTask().execute(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //Create BasicJSon over json made for the login
    private JSONObject createBasicJSON(Location location) {
        JSONObject json = createBasicJSON();
        try {
            json.put("time", location.getTime());
            json.put("lat", location.getLatitude());
            json.put("lng", location.getLongitude());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    //Create A jsonObject containing the user information (for login)
    private JSONObject createBasicJSON() {
        JSONObject json = new JSONObject();
        HashMap<String, String> user = MainActivity.db.getUserDetails();
        try {
            json.put("email", user.get("email"));
            json.put("uid", user.get("uid"));
            json.put("trip_name", user.get("trip_name"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}

class ExecuteTask extends AsyncTask<JSONObject, Integer, String> {
    @Override
    protected String doInBackground(JSONObject... jsons) {
        URL url = null;
        OutputStream os = null;
        HttpURLConnection connection = null;
        String result =null;
        try {
            url = new URL(AppConfig.URL_SERVER);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestMethod("POST");

            os = connection.getOutputStream();
            for (int i = 0; i < jsons.length; i++) {
                JSONObject currentJson = jsons[i];
                os.write(currentJson.toString().getBytes("UTF-8"));

            }
            os.close();

            StringBuilder sb = new StringBuilder();
            int HttpResult = connection.getResponseCode();
            if(HttpResult == HttpURLConnection.HTTP_OK){
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(),"utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }

                br.close();

                result = sb.toString();

            }else{
                result =connection.getResponseMessage();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }



        return result;
    }


}
