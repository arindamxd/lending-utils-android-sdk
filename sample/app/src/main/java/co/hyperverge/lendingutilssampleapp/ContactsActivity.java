package co.hyperverge.lendingutilssampleapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import co.hyperverge.hvroboutils.objects.HVContactConfig;
import co.hyperverge.hvroboutils.objects.HVOperationError;
import co.hyperverge.hvroboutils.objects.HVUtils;
import co.hyperverge.hvroboutils.workflows.contacts.HVContactManager;
import co.hyperverge.hvroboutils.workflows.sms.HVSMSManager;


public class ContactsActivity extends AppCompatActivity {
    PermissionManager permissionManager;
    public final String TAG = getClass().getSimpleName();
    TextView textView;
    ProgressDialog dialog;

    private final String appId = "";
    private final String appKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        textView = (TextView) findViewById(R.id.textView);

        startContactProcessing();

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

        HVContactConfig config = new HVContactConfig();
        String fullMatchNames[] = {"mom","dad","papa"};
        config.setFullMatchNames(fullMatchNames);
        String partialMatchNames[] = {"\uD83D\uDE01", "\uD83D\uDE03", "\uD83D\uDE18", "\uD83D\uDE0E", "\uD83D\uDE0D", "\uD83D\uDC93"};
        //Grinning, smilingface-openeyes, kissy-with-heart,cool, Heart-eyes, beating-heart
        config.setPartialMatchNames(partialMatchNames);

        HVContactManager.start(this, appId, appKey, HVUtils.Countries.INDIA,config, new HVContactManager.HVContactCallback() {
            @Override
            public void onComplete(HVOperationError error, JSONObject results) {
                if (error == null) {
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

}
