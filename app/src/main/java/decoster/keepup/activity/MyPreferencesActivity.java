package decoster.keepup.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import decoster.keepup.R;

import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import decoster.keepup.activity.MainActivity;
import decoster.keepup.helper.SQLiteHandler;
import decoster.keepup.helper.SessionManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MyPreferencesActivity extends PreferenceActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();

    }
    /*public void logoutUser() {

        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MyPreferencesActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }*/

    public static class MyPreferenceFragment extends PreferenceFragment
    {

        private Preference myPref;
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            myPref= (Preference) findPreference("buttonLogOut");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    new AlertDialog.Builder(getActivity())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(R.string.btn_logout)
                            .setMessage(R.string.really_logout)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    MainActivity.session.setLogin(false);

                                    MainActivity.db.deleteUsers();

                                    // Launching the login activity
                                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }

                            })
                            .setNegativeButton(R.string.no, null)
                            .show();

                    return true;
                }
            });
            // SqLite database handler
            /*db = new SQLiteHandler(getActivity().getApplicationContext());

            // session manager
            session = new SessionManager(getActivity().getApplicationContext());


            session.setLogin(false);

            db.deleteUsers();

            // Launching the login activity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();*/
        }

    }

}