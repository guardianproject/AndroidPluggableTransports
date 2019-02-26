package info.pluggabletransports.dispatch.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.jrummyapps.android.shell.CommandResult;
import com.jrummyapps.android.shell.Shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

public abstract class TransportManager {

    public File mFileTransport = null;
    private Thread mTransportThread = null;

    public File installTransport (Context context, String assetKey)
    {
        BinaryInstaller bi = new BinaryInstaller(context,context.getFilesDir());

        String arch = System.getProperty("os.arch");
        if (arch.contains("x86"))
            arch = "x86";
        else
            arch = "arm";

        try {
            mFileTransport = bi.installResource(arch, assetKey, false);
        }
        catch (Exception ioe)
        {
            debug("Couldn't install transport: " + ioe);
        }

        return mFileTransport;
    }

    public void startTransport (final TransportListener listener)
    {
        if (mFileTransport != null) {
            debug("Transport installed:  " + mFileTransport.getAbsolutePath());
            mTransportThread = new Thread () {
                public void run ()
                {
                    startTransportSync(listener);
                }
            };
            mTransportThread.start();
        }
    }

    public void stopTransport ()
    {
        if (mTransportThread != null && mTransportThread.isAlive())
            mTransportThread.interrupt();
    }


    public abstract void startTransportSync (TransportListener listener);

    public void exec (String cmd, boolean wait, Map<String,String> env,TransportListener listener) throws Exception
    {

        //get system stream
        logStream(System.in);

        final Process shellResult = Shell.runWithEnv(cmd, env);

        debug("CMD: " + cmd);

        logStream(shellResult.getErrorStream());

        BufferedReader br = new BufferedReader(new InputStreamReader(shellResult.getInputStream()));

        String line = null;

        while(mTransportThread.isAlive())
        {
            line = br.readLine();
            debug(line);

            if (line.contains("socks5")) {
                String[] parts = line.split(" ");
                for (String part : parts) {
                    if (part.contains("127.0.0.1")) {
                        String[] addrParts = part.split(":");
                        listener.transportStarted(Integer.parseInt(addrParts[1]));

                    }
                }
            }
        }

    }

    public void debug(String msg) {
        Log.d(getClass().getName(), msg);
    }

    public void debug(String msg, Exception e) {
        Log.e(getClass().getName(), msg,e);
    }

    private void exec (Runnable run)
    {
        new Thread (run).start();
    }

    private void logStream (final InputStream is)
    {
        exec(new Runnable() {
            @Override
            public void run() {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                try {

                    String line = br.readLine();

                    while (line != null) {

                        if (!TextUtils.isEmpty(line))
                            debug("SYSTEM: " + line);

                        line = br.readLine();
                    }
                }
                catch (IOException e)
                {
                    debug("error reading errorstream",e);
                }
            }
        });
    }
}
