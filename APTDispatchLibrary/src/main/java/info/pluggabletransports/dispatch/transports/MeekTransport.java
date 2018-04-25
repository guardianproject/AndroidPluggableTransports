package info.pluggabletransports.dispatch.transports;

import android.content.Context;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.Properties;

import info.pluggabletransports.dispatch.Connection;
import info.pluggabletransports.dispatch.Listener;
import info.pluggabletransports.dispatch.Transport;
import info.pluggabletransports.dispatch.util.TransportManager;
import iobfs4proxy.Iobfs4proxy;

public class MeekTransport implements Transport {

    public final static String OPTION_FRONT = "front";
    public final static String OPTION_KEY = "key";
    public final static String OPTION_URL = "url";

    private final static String ASSET_KEY = "obfs4proxy";

    private TransportManager mTransportManager;

    @Override
    public void init(Context context, Properties options) {

        initMeekIPC(context);
        initMeekSharedLibrary();
    }

    @Override
    public Connection connect(String addr) {

        //for the IPC version
        mTransportManager.startTransport();

        //for the in-process library
        //calls obfs4 in the same thread, woot!
        //Iobfs4proxy.main();

        return new MeekConnection();
    }

    @Override
    public Listener listen(String addr) {
        return null;
    }

    private void initMeekIPC (Context context)
    {
        //meek_lite 0.0.2.0:2 97700DFE9F483596DDA6264C4D7DF7641E1E39CE url=https://meek.azureedge.net/ front=ajax.aspnetcdn.com
        mTransportManager = new TransportManager();
        mTransportManager.installTransport(context,ASSET_KEY);
    }

    private void initMeekSharedLibrary ()
    {
        //nothing to do here
    }

    class MeekConnection implements Connection {

        @Override
        public int read(byte[] b, int offset, int length) throws IOException {
            return 0;
        }

        @Override
        public void write(byte[] b) throws IOException {

        }

        @Override
        public void close() {

        }

        @Override
        public InetAddress getLocalAddress() {
            return null;
        }

        @Override
        public int getLocalPort() {
            return 0;
        }

        @Override
        public InetAddress getRemoteAddress() {
            return null;
        }

        @Override
        public int getRemotePort() {
            return 0;
        }

        @Override
        public void setDeadline(Date deadlineTime) {

        }

        @Override
        public void setReadDeadline(Date deadlineTime) {

        }

        @Override
        public void setWriteDeadline(Date deadlineTime) {

        }
    }
}
