
package info.pluggabletransports.dispatch.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import info.pluggabletransports.dispatch.DispatchConstants;


public class DispatchReceiver extends BroadcastReceiver implements DispatchConstants {

    @Override
    public void onReceive(Context context, Intent intent) {
        /* sanitize the Intent before forwarding it to TorService */
        String action = intent.getAction();
        if (TextUtils.equals(action, ACTION_START)) {
            String packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME);

                Intent startTorIntent = new Intent(context, DispatchService.class);
                startTorIntent.setAction(action);
                if (packageName != null)
                    startTorIntent.putExtra(EXTRA_PACKAGE_NAME, packageName);
                context.startService(startTorIntent);

        }
    }
}
