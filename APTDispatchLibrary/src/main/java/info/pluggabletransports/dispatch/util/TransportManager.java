package info.pluggabletransports.dispatch.util;

import android.content.Context;
import android.util.Log;

import com.jrummyapps.android.shell.CommandResult;
import com.jrummyapps.android.shell.Shell;

import java.io.File;

public abstract class TransportManager {

    public File mFileTransport = null;
    private Thread mTransportThread = null;

    public File installTransport (Context context, String assetKey)
    {
        BinaryInstaller bi = new BinaryInstaller(context,context.getFilesDir());

        String arch = System.getProperty("os.arch");
        if (arch.contains("arm"))
            arch = "arm";
        else
            arch = "x86";

        try {
            mFileTransport = bi.installResource(arch, assetKey, false);
        }
        catch (Exception ioe)
        {
            debug("Couldn't install transport: " + ioe);
        }

        return mFileTransport;
    }

    public void startTransport ()
    {
        if (mFileTransport != null) {
            debug("Transport installed:  " + mFileTransport.getAbsolutePath());
            mTransportThread = new Thread () {
                public void run ()
                {
                    startTransportSync();
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


    public abstract void startTransportSync ();

    public int exec (String cmd, boolean wait) throws Exception
    {
        CommandResult shellResult = Shell.run(cmd);

        String output = shellResult.getStdout();

        debug("CMD: " + cmd + "; SUCCESS=" + shellResult.isSuccessful());

        if (!shellResult.isSuccessful()) {
            throw new Exception("Error: " + shellResult.exitCode + " ERR=" + shellResult.getStderr() + " OUT=" + shellResult.getStdout());
        }

        return shellResult.exitCode;
    }

    public void debug(String msg) {
        Log.d(getClass().getName(), msg);
    }

}
