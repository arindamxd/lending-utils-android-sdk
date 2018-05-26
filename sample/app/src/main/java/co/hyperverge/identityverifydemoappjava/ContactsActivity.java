package co.hyperverge.identityverifydemoappjava;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import co.hyperverge.hvroboutils.objects.HVOperationError;
import co.hyperverge.hvroboutils.objects.HVUtils;
import co.hyperverge.hvroboutils.workflows.contacts.HVContactManager;
import co.hyperverge.hvroboutils.workflows.sms.HVSMSManager;


public class ContactsActivity extends AppCompatActivity {
    PermissionManager permissionManager;
    public final String TAG = getClass().getSimpleName();
    TextView textView;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        textView = (TextView) findViewById(R.id.textView);

        startContactProcessing();
        startSMSProcessing();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void startContactProcessing() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Processing.. Please wait");
        dialog.setCancelable(false);
        dialog.show();


        HVContactManager.start(this, "", "", HVUtils.Countries.VIETNAM, new HVContactManager.HVContactCallback() {
            @Override
            public void onComplete(HVOperationError error, JSONObject results) {
                if (error == null) {
                    Log.d(TAG, "JSON Result : " + results.toString());
                    int spacesToIndentEachLevel = 2;
                    try {
                        if (dialog != null) {
                            dialog.cancel();
                        }
                        textView.setText(results.toString(spacesToIndentEachLevel));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    dialog.cancel();
                    Toast.makeText(ContactsActivity.this, error.getErrMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void startSMSProcessing() {
        HVSMSManager.start(this, "", "", new HVSMSManager.HVSMSCallback() {
            @Override
            public void onComplete(HVOperationError error, JSONObject results) {
                if (error == null) {
                    Log.d(TAG, "JSON Result : " + results.toString());
                    Toast.makeText(ContactsActivity.this, "Finished SMS processing", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
