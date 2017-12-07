package decoster.keepup.activity;

/**
 * Created by Decoster on 02/02/2016.
 */

import decoster.keepup.R;
import decoster.keepup.helper.SQLiteHandler;
import decoster.keepup.helper.Sender;
import decoster.keepup.helper.SessionManager;
import decoster.keepup.helper.TrackerService;

import java.util.HashMap;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private TextView txtName;
    private TextView txtEmail;
    private TextView txtTripName;
    private TextView txtTripStartedAt;
    private TextView txtTripMeanTransport;
    private TextView txtTripNbPicture;
    private TextView txtTripStatus;
    private static final int CAMERA_REQUEST = 1888;
    private Button play_pause;
    private Button picture;
    private Button journey_finished;
    private Button start_new;
    public static SQLiteHandler db;
    public static SessionManager session;
    private ShareActionProvider mShareActionProvider;
    private Boolean pause = true;
    private TrackerService trackerService;
    public static Sender sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        txtTripName = (TextView) findViewById(R.id.tripName);
        txtTripStartedAt = (TextView) findViewById(R.id.TripStartedAt);
        txtTripMeanTransport = (TextView) findViewById(R.id.TripMeanTransport);
        txtTripNbPicture = (TextView) findViewById(R.id.TripNbPicture);
        txtTripStatus = (TextView) findViewById(R.id.txtTripStatus);
        sender = new Sender();
        trackerService = new TrackerService(this, sender);
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        txtTripStatus.setText(R.string.pause);
        String name = user.get("name");
        String email = user.get("email");
        String trip_name = user.get("trip_name");
        String start_at = user.get("trip_started_at");
        String nb_picture = user.get("trip_picture_nb");
        String mean = user.get("trip_mean_transport");
        //user.put("trip_trip_comment", cursor.getString(9));
        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);
        if (trip_name != null && trip_name.isEmpty()) {
            //Hide pannel because no joruney avaiable
            LinearLayout journeyLayout = (LinearLayout) findViewById(R.id.tripPannel);
            journeyLayout.setVisibility(LinearLayout.GONE);
        } else {

            //Set text field
            txtTripName.setText(trip_name);
            txtTripStartedAt.setText(getResources().getString(R.string.TripStartedAt) + " " + start_at);
            txtTripMeanTransport.setText(getResources().getString(R.string.TripMeanTransport) + " " + mean);
            txtTripNbPicture.setText(getResources().getString(R.string.TripNbPicture) + " " + nb_picture);
            txtTripStatus.setText(getResources().getString(R.string.status) + " " + getResources().getString(R.string.pause));

            //Get Button for Journey Pannel
            play_pause = (Button) findViewById(R.id.pause_start);
            picture = (Button) findViewById(R.id.take_picture);
            journey_finished = (Button) findViewById(R.id.stop);

            //Get orange Button
            start_new = (Button) findViewById(R.id.start_new_journey);

            //addListener
            play_pause.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    if (pause) {

                        if (!trackerService.registerLocationService()) {
                            play_pause.setText(R.string.pause);
                            txtTripStatus.setText(getResources().getString(R.string.status) + " " +getResources().getString(R.string.emmiting));
                            pause = false;

                        }
                        else {
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.problemEnableTrackerService), Toast.LENGTH_LONG)
                                    .show();
                        }

                    } else {

                        if (!trackerService.unregisterLocationService()) {
                            play_pause.setText(R.string.start);
                            txtTripStatus.setText(getResources().getString(R.string.status) + " " +getResources().getString(R.string.pause));
                            pause = true;
                        }
                        else {
                            Toast.makeText(getApplicationContext(),
                                    getResources().getString(R.string.problemDisableTrackerService), Toast.LENGTH_LONG)
                                    .show();
                        }

                    }
                }
            });

            picture.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    takePicture();

                }
            });

            journey_finished.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.stop)
                            .setMessage(R.string.stop_msg)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LinearLayout journeyLayout = (LinearLayout) findViewById(R.id.tripPannel);
                                    journeyLayout.setVisibility(LinearLayout.GONE);
                                    sender.sendStopJourney();
                                }

                            })
                            .setNegativeButton(R.string.no, null)
                            .show();
                }
            });

            start_new.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {

                    //If journey present
                    //-delete in db
                    //-REload pannel
                    //Ask user for information
                    new AlertDialog.Builder(MainActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.start_new_journey)
                            .setMessage(R.string.confirmation)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(getApplicationContext(),
                                            RegisterJourneyActivity.class);
                                    startActivity(i);
                                }

                            })
                            .setNegativeButton(R.string.no, null)
                            .show();

                }
            });
        }


    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Locate MenuItem with ShareActionProvider

        //Handle share menu item
        MenuItem shareItem = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider)
                MenuItemCompat.getActionProvider(shareItem);
        HashMap<String, String> user = db.getUserDetails();

        setShareIntent(user.get("url"));

        return true;
    }

    //Set the share intent
    private void setShareIntent(String url) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Check this out " + url);

        mShareActionProvider.setShareIntent(intent);
    }


    private void takePicture() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final String permission = "android.permission.CAMERA";
        int res = ContextCompat.checkSelfPermission(MainActivity.this,
                permission);

        if (res != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    permission)) {
                showMessageOKCancel("You need to allow access to Camera",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[] {permission},
                                        REQUEST_CODE_ASK_PERMISSIONS);
                            }
                        });
                return;
            }
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{permission},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        startActivityForResult(cameraIntent, CAMERA_REQUEST);

    }
    /**
     * react to the user tapping/selecting an options menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                //Toast.makeText(this, "ADD!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, MyPreferencesActivity.class);
                startActivity(i);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    //end fragment

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    public void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Log.d(AppController.TAG, "test");
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            //imageView.setImageBitmap(photo);
        }
    }



}