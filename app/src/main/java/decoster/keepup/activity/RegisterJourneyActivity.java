package decoster.keepup.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import decoster.keepup.R;

/**
 * Created by Decoster on 06/02/2016.
 */
public class RegisterJourneyActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button sendButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_journey);
        sendButton  = (Button) findViewById(R.id.send);
        sendButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                EditText txtName = (EditText) findViewById(R.id.name_new_journey);
                EditText txtComment = (EditText) findViewById(R.id.comment_new_journey);
                Spinner spinnerMode = (Spinner) findViewById(R.id.mode_spinner);
                String name = txtName.getText().toString();
                String comment = txtComment.getText().toString();
                String mode =spinnerMode.getSelectedItem().toString();
                MainActivity.sender.sendNewJounrey(name, mode, comment);
                MainActivity.db.putNewJourney(name, mode, comment);
                finish();
            };
        });

    }
}
