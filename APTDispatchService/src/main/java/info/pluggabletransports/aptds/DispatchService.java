package info.pluggabletransports.aptds;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

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
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Send Orbot's status in reply to an
     * {@link DispatchConstants#ACTION_START} {@link Intent}, targeted only to
     * the app that sent the initial request. If the user has disabled auto-
     * starts, the reply {@code ACTION_START Intent} will include the extra
     */
    private void replyWithStatus(Intent startRequest, int status, int socksPort, int httpPort) {
        String packageName = startRequest.getStringExtra(EXTRA_PACKAGE_NAME);

        Intent reply = new Intent(ACTION_STATUS);
        reply.putExtra(EXTRA_STATUS, status);

        if (socksPort != -1) {
            reply.putExtra(EXTRA_SOCKS_PROXY, "socks://127.0.0.1:" + socksPort);
            reply.putExtra(EXTRA_SOCKS_PROXY_HOST, "127.0.0.1");
            reply.putExtra(EXTRA_SOCKS_PROXY_PORT, socksPort);
        }

        if (httpPort != -1) {
            reply.putExtra(EXTRA_HTTP_PROXY, "http://127.0.0.1" + httpPort);
            reply.putExtra(EXTRA_HTTP_PROXY_HOST, "127.0.0.1");
            reply.putExtra(EXTRA_HTTP_PROXY_PORT, httpPort);
        }

        if (packageName != null)
        {
            reply.setPackage(packageName);
            sendBroadcast(reply);
        }
        else
        {
            LocalBroadcastManager.getInstance(this).sendBroadcast(reply);

        }

    }
}
