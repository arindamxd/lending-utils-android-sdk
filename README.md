  

# HVLendingUtils Framework Documentation


## Overview

HVLendingUtils is HyperVerge's proprietary android Utilities Framework for banking and financial services industries. It has three modules.
- **Facebook Module** for verification and validation of user's facebook profile using intelligence built over HyperVerge's inhouse deep learning face recognition system.
- **SMS Module** for verifying income and other details of the user from their SMSes.
- **Contacts Module** for finding top contacts of the user for the purpose of collections or guarantee.

### Requirements

- Gradle Version: 4.4 (Recommended)
- Tested with Gradle Plugin for Android Studio - version 3.1.0
- minSdkVersion 15
- targetSdkVersion 27

### ChangeLog

You can find the ChangeLog in the [CHANGELOG.md](CHANGELOG.md) file

## Table of contents

- [Overview](#overview)
	- [Requirements](#requirements)
	- [ChangeLog](#changelog)
- [Table of contents](#table-of-contents)
- [Example Project](#example-project)
- [Integration Steps](#integration-steps)
	- [Adding the SDK to your project](#adding-the-sdk-to-your-project)
	- [App Permissions](#app-permissions)
	- [Using Details from Facebook](#using-details-from-facebook)
		- [Setting up a Facebook App](#setting-up-a-facebook-app)
		- [Launching the Activity](#launching-the-activity)
			- [Parameters](#parameters)
		- [Customizing HVFBActivity View](#customizing-hvfbactivity-view)
		- [Completion Hook Payload Structure](#completion-hook-payload-structure)
		- [Result Structure](#facebook-result-structure)
	- [Using Details from SMS](#using-details-from-sms)
		- [Initiating SMS Processing](#initiating-sms-processing)
			- [Parameters](#parameters)
		- [Result Structure](#sms-result-structure)
	- [Using Details from Contact Book](#using-details-from-contact-book)
		- [Initiating Contact Processing](#initiating-contact-processing)
			- [Parameters](#parameters)
		- [Result Structure](#contacts-result-structure)
			- [1. Starred Contacts](#1-starred-contacts)
			- [2. Top Contacts](#2-top-contacts)
			- [3. Special Contacts](#3-special-contacts)
			- [4. Suspicion Validation](#4-suspicion-validation)
		- [HVContactConfig](#hvcontactconfig)
- [Error Codes](#error-codes)
- [Contact Us](#contact-us)


## Example Project

Please refer to the sample app provided in the repo to get an understanding of the implementation process. To run the app:

- Clone/download the repo and open sample using latest version of Android Studio
- Open project build.gradle and replace aws_access_key and aws_secret_pass with the credentials provided by HyperVerge
- In `FBLoginActivity`, `SMSActivity` and `ContactsActivity` set the value of `appId` & `appKey` to the credentials provided by HyperVerge
- Build and run the app

## Integration Steps

### Adding the SDK to your project

Add dependency to  HVLendingUtils SDK's maven repo.

- Add the following set of lines to your `app/build.gradle`


```
dependencies {
    compile('co.hyperverge:hv-lending-utils:2.2.0@aar', {
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

### App Permissions
-  The SDK requires different permissions for different modules.

1. Facebook Module: Camera
2. SMS Module: Read SMS
3. Contacts Module: Read Call Logs, Read SMS, Read Contacts

Kindly note that for android v23 (Marshmallow) and above, you need to handle the runtime permissions inside your app.

### Using Details from Facebook

This sub-section explains the integration for verifying authenticity of the user from their published details on Facebook.

#### Setting up a Facebook App

- Open the Facebook Apps dashboard : https://developers.facebook.com/apps
- Click on the 'Add a new App' button
- Give the app name and email ID and create the app. You will be redirected to a new page
- In the new page, select 'Settings' -> 'Basic' in the left navigation
- Open your application's 'strings.xml' file and add these two lines. Replace '{your-app-id}' with the value found in the Facebook App dashboard

```
<string name="facebook_app_id">{your-app-id}</string>
<string name="fb_login_protocol_scheme">fb{your-app-id}</string>
```

- Click 'Add Platform' at the bottom of the Facebook App dashboard and select 'android'.
- In a new tab, open 'https://developers.facebook.com/docs/facebook-login/android' and follow steps 5 and 6 from the page to add the App's bundle identifier, default class and key hashes to the Facebook App. Please note that steps other than 5 and 6 will be handled in the HVLendingUtils module.
- Click Save Changes at the bottom of the App Dashboard window.

#### Launching the Activity  

All interactions with the Facebook module and the corresponding HyperVerge server call for face match happens from an Activity called `HVFBActivity`.

To launch the activity, call the `start` method of `HVFBActivity`(or its subclass). The variables in the following example code are described below:

```
HVFBActivity.start(this, HVFBActivity.class, imageUri, completionHook,
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

- HVFBCallback: It is a callback with one method - `onComplete`. It is called when the facebook login and our processing is successful or when an error has occurred in either of the steps. The values of `error` and  `result` received by the callback determine whether the call was a success or failure.

The `onComplete` method has two parameters.

- error: It is of type `HVOperationError`. It has an error code and an error message. The various error codes are described later. It is set to `null` if the whole process is successful.
- result: It is of type `JSONObject`. It has results of the server call. It is `null` when there is an error. If the `completionHook` is set, the result would be an empty Json Object. Otherwise it contains the results of the profile verification. The result structure and the payload structure for the completion hook are discussed later.

#### Customizing HVFBActivity View

`HVFBActivity` has a simple progress bar at the center of the view. If any customization is required, you could inherit this Activity and add your own UI elements to it and replace `HVFBActivity` in  the argument `HVFBActivity.class` with the subclass' name (all the other fields will be the same).

```
MyCustomActivity.start(this, MyCustomActivity.class, imageUri,
completionHook, appId, appKey, new HVFBActivity.HVFBCallback() {
	...
});
```
where `MyCustomActivity` is a subclass of `HVFBActivity`

#### Completion Hook Payload Structure
If completionHook is passed with the start method, then a POST request with JSON payload will be made by the HyperVerge server to the hook url with the result of the Facebook profile verification and other profile information. The payload for this request will have following structure:

```
{
	"statusCode": <Number, 200 if successful>,
	"result": <Object, discussed in the next section. Will be present only in case of success>,
	"error": <Object, Has detail about the error. Present only in case statusCode is not 200>
}
```

#### Result Structure  <a name="facebook-result-structure"></a>
The result JSON (returned by SDK/posted to completion hook) has the following format.

```
{
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
	"last_name":<String>,
	"short_name":<String>,
	"is_verified":<0 or 1 - This field indicates whether the person's profile is verified manually by facebook>,
	"verified":<0 or 1 - Indicates whether the account has been verified by the user via SMS etc>
        "mostCoTaggedUsers": <Array of user objects who have been tagged in photos with this user the most>,
	"tagged_places": <Dictionary with an array of few tagged places and corresponding paging to get rest of the tagged places>
	"age_range": <Dictionary with minimum and maximum age of the user>
	"birthday": <String>
	"email":<String>
	"friends:<Dictionary with an array of few friends(names and ids), corresponding paging to get rest of the friends and total friend count.
	"gender":<String>
	"hometown": <Dictionary with place id and name>
	"location": <Dictionary with place id and name>
        }
    }
}
```

Depending on the permissions given by the user and information available in the profile, additional fields might be present in the 'info' object of the result. Eg: Devices, significant_other etc.

### Using Details from SMS

This sub-section explains the integration for verifying income and other details of the user from their SMSes.

#### Initiating SMS Processing

`HVSMSManager` has a simple function to initiate processing of SMSes. 
```
HVSMSManager.start(this, appId, appKey, hvsmsConfig,
new HVSMSManager.HVSMSCallback() {
    @Override
    public void onComplete(HVOperationError error, JSONObject results){
        if (error == null) {
            Log.e("SMSManager", "Success!");
        } else {
            Log.e("SMSManager", "Failure!");
        }
    }
});
```

##### Parameters

These are the parameters to be set in `start` method:

- appId (String): Given by HyperVerge

- appKey (String): Given by HyperVerge

- hvSMSConfig (Object of type HVSMSConfig): This object has the following variables :
	- sourceList (Array of Strings): Type of messages that should be considered. Eg.: Bank, Insurance, Telco, etc. The SDK currently supports only `HVSMSManager.Sources.Bank`. Default value of the array is `{HVSMSManager.Sources.Bank}`
	- numberOfMonths (Integer): Number of months to be considered for processing. Default value is 12 months.
	- country (String): Country of the user. Default value is `HVUtils.Countries.Vietnam`
	
- HVSMSCallback - It is a callback with one method - `onComplete`. It is called when the processing is successful or when an error has occured. The values of `error` and  `result` received by the callback determine whether the call was a success or failure.

The `onComplete` method has two parameters.

- error: It is of type `HVOperationError`. It has an error code and an error message. The various error codes are described later. It is set to `null` if the whole process is successful.
- result: It is of type `JSONObject`. It has results of the processing. It is `null` when there is an error. The result structure is discussed below.

#### Result Structure <a name="sms-result-structure"></a>

```
{
    "sourcelist" : ["bank"],
    "time
    "salaries" : [
        {
            "amount" : <long - Value in corresponding currency eg:VND, INR>,
            "time" : <String - Milliseconds since The Epoch>,
            "bank-name" : <Sring - Name of the bank>
        },
        …
    ],
    "accounts" : [
        {
            "bank-name" : <String - Name of the bank>,
            "account-identifier" : <String - Unique identifier for the account>,
            "global-min-balance" : <long - Lowest balance in the time period>,
            "global-max-balance" : <long - Highest balance in the time period>,
            "avg-min-monthly-balance" : <long - Average of minimum monthly balances in the time period>,
            "avg-max-monthly-balance" : <long - Average of maximum monthly balances in the time period>,
            "transactions" : [
                {
                    "amount" : <long - Value in corresponding currency eg:VND, INR>,
                    "debit-or-credit" : <String - 'debit' or 'credit'>,
                    "time" : <String - Milliseconds since The Epoch>,
                    "closing-balance" : <long - Account balance after the transaction>
                },
                …
            ]    
        },
        …
    ]
}
```

### Using Details from Contact Book
This sub-section explains the integration for finding top contacts of the user for the purpose of collections or guarantee.

#### Initiating Contact Processing

`HVContactManager` has a start function to initiate processing of Top Contacts.   

```
HVContactConfig hvContactConfig = new HVContactConfig();

HVContactManager.start(this, appId, appKey, countryCode, hvContactConfig,
new HVContactManager.HVContactCallback() {
    @Override
    public void onComplete(HVOperationError error, JSONObject results){
        if (error == null) {
            Log.e("ContactManager", "Success!");
        } else {
            Log.e("ContactManager", "Failure!");
        }
    }
});
```
##### Parameters

These are the parameters to be set in `start` method:

- appId (String): Given by HyperVerge

- appKey (String): Given by HyperVerge

- countryCode (String): Use `HVUtils.Countries.VIETNAM` for contacts in Vietnam, for example.

- hvContactConfig: Object of type `HVContactConfig`. This object has variables to be considered for `special-contacts` and `suspicion-validation` sections in the result - discussed in the next section.

- HVContactCallback - It is a callback with one method - `onComplete`. It is called when the processing is successful or when an error has occurred. The values of `error` and  `result` received by the callback determine whether the call was a success or failure.

The `onComplete` method has two parameters.

- error: It is of type `HVOperationError`. It has an error code and an error message. The various error codes are described later. It is set to `null` if the whole process is successful.
- result: It is of type `JSONObject`. It has results of the processing. It is `null` when there is an error. The result structure is discussed in the next section.

#### Result Structure <a name="contacts-result-structure"></a>

```
{
    "starred-contacts" : [
        {
                "name" : "value",
                "phone-number" : "value",
                "details" : "value"

        }
        ...
    ],
    "top-contacts" : {
        "incoming-calls-count" : [
                {
                        "name" : "value",
                        "phone-number" : "value",
                        "details" : "value"
               },
                ...     
        ],
        "incoming-calls-duration" : [
                {
                        "name" : "value",
                        "phone-number" : "value",
                        "details" : "value"
               },
                ...     
        ],
        "outgoing-calls-count" : [
                {
                        "name" : "value",
                        "phone-number" : "value",
                        "details" : "value"
                },
                ...     
        ],
        "outgoing-calls-duration" : [
                {
                        "name" : "value",
                        "phone-number" : "value",
                        "details" : "value"
                },
                ...     
        ],
        "incoming-sms" : [
                {
                        "name" : "value",
                        "phone-number" : "value",
                        "details" : "value"
                },
                ...     
        ],
        "outgoing-sms" : [
                {
                        "name" : "value",
                        "phone-number" : "value",
                        "details" : "value"
                },
                ...     
        ],
        "overall" : [        
                {
                        "name" : "value",
                        "phone-number" : "value",
                        "details" : "value"
                },
                ...
        ]
    },
    "special-contacts" : [       
        {
                "name" : "value",
                "phone-number" : "value",
                "details" : "value"
        },
        ...
    ],
    "suspicion-validation" : [         
        {
                "number-of-contacts-check" : true,
                "call-log-count-check": true,
                "oldest-call-log-check" : true
        }
        ...
    ],

}
```

The result has 4 sections:

##### 1. Starred Contacts
All the contacts starred by the user.


##### 2. Top Contacts
This section has 7 sub sections.
- `overall`: Top 5 contacts based on an overall score assigned to each contact depending on incoming and outgoing calls, incoming and outgoing SMSes and some messaging apps.
- `incoming-calls-count`, `outgoing-calls-count`: Top 5 contacts based on number of incoming/outgoing calls in the call history present in the device.
- `incoming-calls-duration`, `outgoing-calls-duration`: Top 5 contacts based on total duration of incoming/outgoing calls in the call history present in the device.
- `incoming-sms`,`outgoing-sms`: Top 5 contacts based on number of incoming/outgoing SMSes.

##### 3. Special Contacts
This list is created considering the following three variables in the `HVContactConfig` object.
  1. `fullMatchNames` - Array of Strings(e.g.: mom, dad etc) - The SDK checks for contacts that match these values. Case and white space insensitive. By default its an empty array.
  2. `partialMatchNames` - Array of Strings (e.g.: emojis) -  The SDK checks for contacts that contains these values. By default its an empty array.
  3. `lastNameOfUser` - String. The SDK finds contacts that contains this last name. Default value of the string is null.

  There is a cap of 10 contacts per sub category in this section.


##### 4. Suspicion Validation
This is a list of validations keys to check for suspicious customers. If the flag is true, there is no suspicion.
- `number-of-contacts-check`: Checks if the number of contacts saved is over a threshold (default 20). It can be changed using `minContactsCount` in the `HVContactConfig` object.
- `call-log-count-check`: Checks if the number of logs in the call history is over a threshold(default 50). It can be changed using `minCallLogCount` in the `HVContactConfig` object.
- `oldest-call-log-check`: Checks if the oldest call found on the device is before a threshold number of day(default 30 days ago). It can be changed using `minOldestCallLog` in the `HVContactConfig` object.


Each contact returned in the result has a `details` field associated with it. This is a JSON with information relevant to the corresponding section. In the top-contacts section, it is one of `times-contacted` (overall section), `incoming-calls-count`, `incoming-calls-duration`, `outgoing-calls-count`,  `outgoing-calls-duration`, `incoming-sms-count` or `outgoing-sms-count`. The `special-contacts` and `starred-contacts` sections have all these fields returned. Please note that the durations are in minutes.


#### HVContactConfig

As mentioned in the previous section, HVContactConfig has the following variables:

  For Special Contacts:
    `fullMatchNames` - array of Strings [default - empty]
    `partialMatchNames` - array of Strings [default - empty]
    `lastNameOfUser` - String [default - null]

  For Suspicion Validation:
    `minContactsCount` - Integer [default - 20]
    `minCallLogCount` - Integer [default - 50]
    `minOldestCallLog` - Integer (number of days) [default - 30]

  These parameters can be set using the corresponding setters. Example,

  ```
  HVContactConfig config = new HVContactConfig();
  String fullMatchNames[] = {"mom","dad"};
  config.setFullMatchNames(fullMatchNames);
  config.setMinContactsCount(30);
  ```



## Error Codes

Descriptions of the error codes returned in the callback are given here.

|Error Code|Description|Explanation|Action|
|----------|-----------|-----------|------|
|1|Input Error|Occurs when input provided to the framework is not correct.|Check if all the parameters provided are proper and as per the documentation|
|2|Network Error|Occurs when the internet is either non-existent, very patchy or the call is taking too long to complete|Check internet and try again|
|3|Internal Server|Occurs when there is an internal error at the server|Notify HyperVerge|
|4|Internal SDK Error|Occurs when an unexpected error has happened with the framework|Notify HyperVerge|
|5|Invalid Image Path|Occurs when the image path sent to the framework is invalid|Validate the imageUri and retry|
|6|Face Not Found|Occurs when no face is found in the image sent to the framework|Make sure the face is present in the frame. Ensure proper lighting and minimal movement of camera or face while capturing|
|7|Multiple Faces Found|Occurs when multiple faces are found in the image sent to the framework|Try again. Make sure only one face is present in the frame|
|8|Android Permissions Not Granted|Occurs when one or more permissions required by the SDK are missing|Ask the user to give permissions and try again|
|101|Inadequate FB Permissions|Occurs when the user has not provided minimum permissions(user_photos, public_profile) in Facebook login|Try Facebook login again|
|102|Facebook login cancelled by user|Occurs when user clicks on the cancel button in the Facebook login page|Try again|
|103|Facebook login failed|Occurs when error is returned by Facebook |Analyse the error message|

## Contact Us
If you are interested in integrating this SDK, please do send us a mail at contact@hyperverge.co explaining your use case. We could give you the necessary credentials for a trail.
