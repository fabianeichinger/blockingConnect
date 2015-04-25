package example.blockingconnect;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.fitness.Fitness;

public class GooglePlayServicesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(greenView());
    }

    @Override
    protected void onStart() {
        super.onStart();

        blockingTest();
    }

    private View greenView() {
        View v = new View(this);
        v.setBackgroundColor(Color.GREEN);
        return v;
    }

    private void blockingTest() {
        final HandlerThread bcThread = new HandlerThread("blockingConnectThread");
        bcThread.start();
        final Handler bcHandler = new Handler(bcThread.getLooper());

        final HandlerThread cbThread = new HandlerThread("callbackThread");
        cbThread.start();
        final Handler cbHandler = new Handler(cbThread.getLooper());

        final GoogleApiClient apiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(Fitness.SCOPE_ACTIVITY_READ)
                .setHandler(cbHandler)
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        Log.d(Thread.currentThread().getName(), connectionResult.toString());
                    }
                })
                .build();

        bcHandler.post(new Runnable() {
            @Override
            public void run() {
                ConnectionResult blockingResult = apiClient.blockingConnect();
                Log.d(Thread.currentThread().getName(), blockingResult.toString());
            }
        });
    }
}
