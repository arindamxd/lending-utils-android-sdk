package co.hyperverge.identityverifydemoappjava;

import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import co.hyperverge.hvroboutils.workflows.fb.activities.HVFBActivity;
import co.hyperverge.hvroboutils.objects.HVOperationError;

public class FBLoginActivity extends AppCompatActivity implements View.OnClickListener {

    String imageUri = "";
    String completionHook = "https://requestbin.fullcontact.com/q026mtq0";
    private final String appId = "";            //add the appId provided by HyperVerge here
    private final String appKey = "";           //add the appKey provided by HyperVerge here
    Button loginToFacebookButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fblogin);
        imageUri = getIntent().getStringExtra("imageUri");

        loginToFacebookButton = (Button) findViewById(R.id.loginToFacebookButton);

        loginToFacebookButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.loginToFacebookButton) {

            HVFBChild.start(this,HVFBChild.class, imageUri, completionHook,
                    appId,appKey, new HVFBActivity.HVFBCallback() {
                @Override
                public void onComplete(HVOperationError error, JSONObject results) {
                    String message = "";

                    if (error == null) {
                        Log.e("HVFBActivity", "Callback: Success!");

                        message = "Operation completed successfully";
                        Log.d("HVFBActivity", results.toString());

                    } else {
                        Log.e("HVFBActivity", "Callback: Failure!");
                        message = "Error!\n" + error.getErrMsg();
                    }

                    showAlert(message);



                }
            });

        }
    }

    private void showAlert(String title){
        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(title)
                .setNeutralButton("Okay", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
