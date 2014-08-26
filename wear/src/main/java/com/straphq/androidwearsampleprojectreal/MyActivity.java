package com.straphq.androidwearsampleprojectreal;

import android.app.Activity;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.wearable.*;
import com.google.android.gms.common.api.*;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import android.util.Log;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.straphq.wear_sdk.Strap;

import android.content.Intent;
import android.speech.RecognizerIntent;

import java.util.Date;
import java.util.List;


public class MyActivity extends Activity {

    // TODO: Dynamically pull this from strings.xml. I couldn't get that to work. -@scald
//  String strapAppID = this.getString(R.string.strap_app_id);
    String strapAppID = "36foJBS6PkBFiTMJt";
    private static Strap strap = null;

    private TextView mTextView;
    private GoogleApiClient mGoogleApiClient;

    private static final int SPEECH_REQUEST_CODE = 0;

    public void onYoTap(View theView) {
        displaySpeechRecognizer();
    }

    // Create an intent that can start the Speech Recognizer activity
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    // This callback is invoked when the Speech Recognizer returns.
// This is where you process the intent and extract the speech text from the intent.
    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);

            //Remove whitespace
            spokenText = spokenText.replaceAll("\\s+","");

            //strap.logEvent("voiceInput");

            PutDataMapRequest dataMap = PutDataMapRequest.create("/sampleapp/" + new Date().toString());
            dataMap.getDataMap().putString("voiceCommand", spokenText);
            PutDataRequest request = dataMap.asPutDataRequest();
            boolean isConnected = mGoogleApiClient.isConnected();
            PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi
                    .putDataItem(mGoogleApiClient, request);
            pendingResult.setResultCallback( new ResultCallback<DataApi.DataItemResult>() {
                @Override
                public void onResult(DataApi.DataItemResult dataItemResult) {
                    String x;
                }


            });
            // Do something with spokenText

            //mTextView.setText(spokenText);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);



        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);

                mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                        .addConnectionCallbacks(new ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle connectionHint) {
                                Log.d("TAG", "onConnected: " + connectionHint);
                                //strap.logEvent("/app/started");



                            }
                            @Override
                            public void onConnectionSuspended(int cause) {
                                Log.d("TAG", "onConnectionSuspended: " + cause);
                            }
                        })
                        .addOnConnectionFailedListener(new OnConnectionFailedListener() {
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.d("TAG", "onConnectionFailed: " + result);
                            }
                        })
                        .addApi(Wearable.API)
                        .build();

                mGoogleApiClient.connect();

                //strap = new Strap(mGoogleApiClient, getApplicationContext(), strapAppID);
                displaySpeechRecognizer();

            }

        });
    }
}
