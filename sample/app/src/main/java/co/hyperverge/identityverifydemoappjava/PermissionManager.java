package co.hyperverge.identityverifydemoappjava;

        import android.app.Activity;
        import android.support.v4.app.ActivityCompat;
        import android.widget.Toast;

        import java.util.ArrayList;

/**
 * Created by sanchit on 08/08/17.
 */

public class PermissionManager {

    public static void checkAndGetPermission(Activity activity, String missingPermission, int requestId){
        // Assume thisActivity is the current activity

        ArrayList<String> toBeRequestedPermissions = new ArrayList<>();
        ArrayList<String> rationalePermissions = new ArrayList<>();
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    missingPermission)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                rationalePermissions.add(missingPermission);

            } else {

                // No explanation needed, we can request the permission.
                toBeRequestedPermissions.add(missingPermission);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }


        if(toBeRequestedPermissions.size() > 0) {
            ActivityCompat.requestPermissions(activity,
                    toBeRequestedPermissions.toArray(new String[0]),
                    requestId);
        }
        if(rationalePermissions.size() > 0){
            String permissionsTxt = "";
            for(String perm: rationalePermissions){
                String[] permSplit = perm.split("\\.");
                permissionsTxt += permSplit[permSplit.length - 1] + ", ";
            }

            permissionsTxt = permissionsTxt.substring(0, permissionsTxt.length() - 2);
            Toast.makeText(activity, "Please give " + permissionsTxt + " permissions by going to Settings", Toast.LENGTH_LONG).show();
        }
    }
}
