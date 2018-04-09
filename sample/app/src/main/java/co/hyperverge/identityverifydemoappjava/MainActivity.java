package co.hyperverge.identityverifydemoappjava;

import android.content.Intent;
import android.media.FaceDetector;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import co.hyperverge.hypersnapsdk.HyperSnapSDK;
import co.hyperverge.hypersnapsdk.activities.FaceCaptureActivity;
import co.hyperverge.hypersnapsdk.listeners.CaptureCompletionHandler;
import co.hyperverge.hypersnapsdk.objects.CaptureError;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Button takeSelfieButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        takeSelfieButton = (Button) findViewById(R.id.takeSelfieButton);

        takeSelfieButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.takeSelfieButton){
            FaceCaptureActivity.start(this, new CaptureCompletionHandler() {
                @Override
                public void onResult(CaptureError error, JSONObject result) {
                    if(error != null){
                        Log.e("LandingActivity", error.getError() + " :: " + error.getErrMsg());
                    }else{
                        try {
                            JSONObject json= (JSONObject) new JSONTokener(result.toString()).nextValue();
                            String imageUri = json.getString("imageUri");

                            showFbLoginPage(imageUri);
                        }catch(JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void showFbLoginPage(String imageUri){
        Intent intent = new Intent(this, FBLoginActivity.class);
        intent.putExtra("imageUri", imageUri);
        startActivity(intent);
    }

}
