package info.pluggabletransports.dispatch.service;

import android.app.Application;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.File;

import info.pluggabletransports.dispatch.DispatchConstants;
import info.pluggabletransports.dispatch.util.ResourceInstaller;

/**
 * Created by n8fr8 on 10/18/17.
 */

public class DispatchService extends Service implements DispatchConstants {
    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(new IncomingIntentRouter(intent)).start();

        return super.onStartCommand(intent, flags, startId);

    }

    private class IncomingIntentRouter implements Runnable
    {
        Intent mIntent;

        public IncomingIntentRouter (Intent intent)
        {
            mIntent = intent;
        }

        public void run() {

            String action = mIntent.getAction();

            if (action != null) {
                if (action.equals(ACTION_START)) {

                    String transportType = mIntent.getStringExtra(EXTRA_TRANSPORT_TYPE);
                    boolean transportVPN = mIntent.getBooleanExtra(EXTRA_TRANSPORT_VPN, false);

                    //launch transport here
                    int transportPort = startTransport (transportType);

                    if (transportVPN)
                        startVPN(transportPort);

                    replyWithStatus(mIntent,STATUS_ON,transportType,transportPort);
                }
                else if (action.equals(ACTION_STATUS)) {
                   // replyWithStatus(mIntent,STATUS_ON,"http",1234);
                }
            }
        }
    }

    private int startTransport (String type)
    {
        int port = -1;

       //call the dispatcher here?

        return port;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

   /*
     * Send Orbot's status in reply to an
     * {@link DispatchConstants#ACTION_START} {@link Intent}, targeted only to
     * the app that sent the initial request. If the user has disabled auto-
     * starts, the reply {@code ACTION_START Intent} will include the extra
     */
    private void replyWithStatus(Intent startRequest, String status, String type, int port) {

        String packageName = startRequest.getStringExtra(EXTRA_PACKAGE_NAME);

        Intent reply = new Intent(ACTION_STATUS);
        reply.putExtra(EXTRA_STATUS, status);
        reply.putExtra(EXTRA_TRANSPORT_TYPE, type);
        reply.putExtra(EXTRA_TRANSPORT_PORT, port);

        if (packageName != null)
        {
            reply.setPackage(packageName);
            sendBroadcast(reply);
        }


    }

    private void startVPN (int socks)
    {
        Intent intentVpn = new Intent(this,DispatchVPN.class);
        intentVpn.setAction("start");
        intentVpn.putExtra("socks",socks);
        startService(intentVpn);
    }

    public void stopVPN ()
    {
        Intent intentVpn = new Intent(this,DispatchVPN.class);
        intentVpn.setAction("stop");
        startService(intentVpn);
    }
}
