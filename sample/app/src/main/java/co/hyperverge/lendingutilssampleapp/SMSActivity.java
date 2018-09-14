package co.hyperverge.lendingutilssampleapp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import co.hyperverge.hvroboutils.objects.HVOperationError;
import co.hyperverge.hvroboutils.objects.HVSMSConfig;
import co.hyperverge.hvroboutils.objects.HVUtils;
import co.hyperverge.hvroboutils.workflows.sms.HVSMSManager;

public class SMSActivity extends AppCompatActivity {

    co.hyperverge.lendingutilssampleapp.PermissionManager permissionManager;
    public final String TAG = getClass().getSimpleName();
    TextView textView;
    ProgressDialog dialog;

    private final String appId = "";        //add the appId provided by HyperVerge here
    private final String appKey = "";       //add the appKey provided by HyperVerge here


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(co.hyperverge.lendingutilssampleapp.R.layout.activity_sms);

        textView = (TextView) findViewById(co.hyperverge.lendingutilssampleapp.R.id.textView);

        startSMSProcessing();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void startSMSProcessing() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("Processing.. Please wait");
        dialog.setCancelable(false);
        dialog.show();

        HVSMSConfig hvsmsConfig = new HVSMSConfig();

        String[] sources = {HVSMSManager.Source.Bank};
        hvsmsConfig.setSourceList(sources);
        hvsmsConfig.setCountry(HVUtils.Countries.VIETNAM);

        HVSMSManager.start(this, appId, appKey, hvsmsConfig, new HVSMSManager.HVSMSCallback() {
            @Override
            public void onComplete(HVOperationError error, JSONObject results) {
                if (error == null) {
                    int spacesToIndentEachLevel = 4;
                    try {
                        if (dialog != null) {
                            dialog.cancel();
                        }
                        if (results != null) {
                            textView.setText(results.toString(4));

                            }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    dialog.cancel();
                    Toast.makeText(SMSActivity.this, error.getErrMsg(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
