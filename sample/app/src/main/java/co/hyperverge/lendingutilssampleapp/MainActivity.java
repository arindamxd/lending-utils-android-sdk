package co.hyperverge.lendingutilssampleapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import co.hyperverge.hypersnapsdk.activities.FaceCaptureActivity;
import co.hyperverge.hypersnapsdk.listeners.CaptureCompletionHandler;
import co.hyperverge.hypersnapsdk.objects.CaptureError;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Button takeSelfieButton;
    Button contactButton;
    Button smsButton;

    public final String TAG = getClass().getSimpleName();
    PermissionManager permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takeSelfieButton = (Button) findViewById(R.id.takeSelfieButton);
        contactButton = (Button) findViewById(R.id.contactButton);
        smsButton = (Button) findViewById(R.id.smsButton);

        takeSelfieButton.setOnClickListener(this);
        contactButton.setOnClickListener(this);
        smsButton.setOnClickListener(this);
        permissionManager = new PermissionManager();
        boolean isPermissionAdded = permissionManager.checkAndRequestPermissions(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.checkResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        }

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.takeSelfieButton) {
            FaceCaptureActivity.start(this, new CaptureCompletionHandler() {
                @Override
                public void onResult(CaptureError error, JSONObject result) {
                    if (error != null) {
                        Log.e("LandingActivity", error.getError() + " :: " + error.getErrMsg());
                    } else {
                        try {
                            JSONObject json = (JSONObject) new JSONTokener(result.toString()).nextValue();
                            String imageUri = json.getString("imageUri");

                            showFbLoginPage(imageUri);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        if (v.getId() == R.id.contactButton) {
            Intent intent = new Intent(this, ContactsActivity.class);
            startActivity(intent);
        }

        if (v.getId() == R.id.smsButton) {
            Intent intent = new Intent(this, SMSActivity.class);
            startActivity(intent);
        }
    }

    private void showFbLoginPage(String imageUri) {
        Intent intent = new Intent(this, FBLoginActivity.class);
        intent.putExtra("imageUri", imageUri);
        startActivity(intent);
    }

}
