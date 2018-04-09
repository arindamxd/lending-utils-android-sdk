
  

## HVLendingUtils Framework Documentation

  

  

  

### Introduction

  

HVLendingUtils is HyperVerge's proprietary android Utilities Framework for banking and financial services industries. It has a module for verification and validation of user's facebook profile using intelligence built over HyperVerge's inhouse deep learning face recognition system.

  

  

  

### Requirements

  

- Gradle Version: 4.4 (Recommended)

  

- Tested with Gradle Plugin for Android Studio - version 2.3.1

  

- minSdkVersion 15

  

- targetSdkVersion 27

  

  

  

### Example Project

  

- Please refer to the sample app provided in the repo to get an understanding of the implementation process.

  

- To run the app, clone/download the repo and open sample using latest version of Android Studio

  

- Open project build.gradle and replace aws_access_key and aws_secret_pass with the credentials provided by HyperVerge

  

- In `FBLoginActivity`, set the value of `appId` & `appKey` to the credentials provided by HyperVerge

  

- Build and run the app

  

  

  

### Integration Steps

  

  

  

#### 1. Add the SDK to your project:

  

- Add dependency to  HVLendingUtils SDK's maven repo.

  

- Add the following set of lines to your `app/build.gradle`

  

  

  

```
dependencies {
    compile('co.hyperverge:hv-lending-utils:1.0.0@aar', {
	    transitive=true
	})
}

```

  

- Add the following set of lines to the Project (top-level) `build.gradle`

  

  

  

```
allprojects {
    repositories {
        maven {
            url "s3://hvsdk/android/releases"
            credentials(AwsCredentials) {
                accessKey aws_access_key
                secretKey aws_secret_pass
            }
        }
    }
}

```

  

  

  

Kindly contact HyperVerge at contact@hyperverge.co for getting your `aws_access_key` and `aws_secret_pass`.

  

  

  

#### 2. Set up a Facebook App

  

- Open the Facebook Apps dashboard : https://developers.facebook.com/apps.

  

- Click on the 'Add a new App' button. Give the app name and email ID and create the app. You will be redirected to a new page.

  

- In the new page, select 'Settings' -> 'Basic' in the left navigation.

  

- Open your application's 'strings.xml' file and add these two lines. Replace '{your-app-id}' with the value found in the Facebook App dashboard.

  

```
<string name="facebook_app_id">{your-app-id}</string>
<string name="fb_login_protocol_scheme">fb{your-app-id}</string>
```

  

- Click 'Add Platform' at the bottom of the Facebook App dashboard and select 'android'.

  

- In a new tab, open 'https://developers.facebook.com/docs/facebook-login/android' and follow steps 5 and 6 to add the App's bundle identifier, default class and key hashes to the Facebook App. Please note that steps other than 5 and 6 will be handled in the HVLendingUtils module.

  

- Click Save Changes at the bottom of the App Dashboard window.

  

  

  

  

  

#### Presenting theActivity:

  

  

  

All interactions with the Facebook module and the corresponding HyperVerge server call for face match happens from an Activity called `HVFBActivity`.

  

To present the activity, call the `start` method of `HVFBActivity`(or its subclass). The variables in the following example code are described below:

  

```
HVFBActivity.start(this,HVFBActivity.class, imageUri, completionHook,
appId,appKey, new HVFBActivity.HVFBCallback() {
    @Override
    public void onComplete(HVOperationError error, JSONObject results){
        if (error == null) {
            Log.e("HVFBActivity", "Success!");
        } else {
            Log.e("HVFBActivity", "Failure!");
        }
    }
});
```

  

  

  

##### Parameters

  

These are the parameters to be set in `start` method:

  

  

  

- imageUri (String): Local file path of the face image to be used for face match.

  

- completionHook (String) - optional: The url which should be hit with results of face match and other facebook profile details. If this is set to `null` or is an empty string, the results will be returned in the onComplete method of HVFBCallback instead.

  

- appId (String): Given by HyperVerge

  

- appKey (String): Given by HyperVerge

  

- HVFBCallback - It is a callback with one method - `onComplete`. It is called when the facebook login and our processing is successful or when an error has occured in either of the steps. The values of `error` and  `result` received by the callback determine whether the call was a success or failure.

  

The `onComplete` method has two parameters.

  

- error: It is of type `HVOperationError`. It has an error code and an error message. The various error codes are described later. It is set to `null` if the whole process is successful.

  

- result: It is of type `JSONObject`. It has results of the server call. It is `null` when there is an error. If the `completionHook` is set, the result would be an empty Json Object. Otherwise it contains the results of the profile verification. The result structure and the payload structure for the completion hook are discussed later.

  

  

#### Customizing HVFBActivity View

  

`HVFBActivity` has a simple progress bar at the center of the view. If any customization is required, you could inherit this Activity and add your own UI elements to it and replace `HVFBActivity` in  the argument `HVFBActivity.class` with the subclass' name (all the other fields will be the same).

  

```

HVFBActivity.start(this,MyCustomActivity.class, imageUri, completionHook,

appId,appKey, new HVFBActivity.HVFBCallback() {

	...

});

```

  

where `MyCustomActivity` is a subclass of `HVFBActivity`

  
#### Completion Hook Payload Structure
If completionHook is passed with the start method, then a POST request with JSON payload will be made by the HyperVerge server to the hook url with the result of the Facebook profile verification and other profile information. The payload for this request will have following structure:

```
{
	statusCode: <Number, 200 if successful>,
	result: <Object, discussed in the next section. Will be present only in case of success>,
	error: <Object, Has detail about the error. Present only in case statusCode is not 200>
}
```
  


#### Result structure
The result dictionary (returned by SDK/posted to completion hook) has the following format.

```
"result": {
    "matchedImage": {
        "timeValidationPassed": <Boolean - digitally qualified profile>,
        "createdTime": <Number - Epoch time of the oldest matched image, exists only when time validation is passed>,
        "id": <String - ID of the oldest matched image, exists only when time validation is passed>,
        "url": <String - URL of the above image>
        },
        
    "info": {
        "firstImageActivity": <Number - Epoch time of the oldest image uploaded/tagged image of the user>,
        "id": <String, Facebook User ID>,
        "name": <String>,
        "mostCoTaggedUsers": <Array of user objects who have been tagged in photos with this user the most>
        .
        .
        .
        }
    }
}
  ```

  Depending on the permissions given by the user and information available in the profile, additional fields might be present in the 'info' object of the result. Eg: First Name, Last Name, Email ID, Tagged Places, Devices, Location etc.





  

#### Error Codes

  

  

  

Descriptions of the error codes returned in the callback are given here.

  

  

  

|Error Code|Description|Explanation|Action|
|----------|-----------|-----------|------|
|1|Input Error|Occurs when input provided to the framework is not correct.|Check if all the parameters provided are proper and as per the documentation|
|2|Network Error|Occurs when the internet is either non-existant, very patchy or the call is taking too long to complete|Check internet and try again.|
|3|Internal Server|Occurs when there is an internal error at the server.|Notify HyperVerge|
|4|Internal SDK Error|Occurs when an unexpected error has happened with the framework.|Notify HyperVerge|
|5|Invalid Image Path|Occurs when the image path sent to the framework is invalid|Validate the imageUri and retry|
|6|Face Not Found|Occurs when no face is found in the image sent to the framework.|Make sure the face is present in the frame. Ensure proper lighting and minimal movement of camera or face while capturing.|
|7|Multiple Faces Found|Occurs when multiple faces are found in the image sent to the framework.|Try again. Make sure only one face is present in the frame|
|101|Inadequate FB Permissions|Occurs when the user has not provided minimum permissions(user_photos, public_profile) in Facebook login|Try Facebook login again|
|102|Facebook login cancelled by user|Occurs when user clicks on the cancel button in the Facebook login page|Try again|
|103|Facebook login failed|Occurs when error is returned by Facebook |Analyze the error message|

### Contact Us
If you are interested in integrating this SDK, please do send us a mail at contact@hyperverge.co explaining your use case. We will give you the appId, appKey, aws_access_key & aws_secret_pass so that you can try it out.
