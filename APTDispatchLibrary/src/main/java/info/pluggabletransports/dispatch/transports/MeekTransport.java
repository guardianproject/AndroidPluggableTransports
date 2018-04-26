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

public class MeekTransport implements Transport, Runnable {

    public final static String OPTION_FRONT = "front";
    public final static String OPTION_KEY = "key";
    public final static String OPTION_URL = "url";

    private final static String ASSET_KEY = "obfs4proxy";

    private TransportManager mTransportManager;

    private final static int DEFAULT_MEEK_SOCKS_PORT = 47352;

    @Override
    public void init(Context context, Properties options) {

       // initMeekIPC(context);
        initMeekSharedLibrary();
    }

    @Override
    public Connection connect(String addr) {

        //for the IPC version
        //mTransportManager.startTransport();

        new Thread(this).start();

        try {
            return new MeekConnection(InetAddress.getLocalHost(), DEFAULT_MEEK_SOCKS_PORT);
        } catch (IOException e)
        {
            return null;
        }
    }

    public void run ()
    {

        //TODO: how do we pass in variables?

        //for the in-process library
        //calls obfs4 in the same thread, woot!
        Iobfs4proxy.main();
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

        private InetAddress mLocalAddress;
        private int mLocalPort;

        public MeekConnection (InetAddress localSocks, int port)
        {
            //init connection to local socks port
            mLocalAddress = localSocks;
            mLocalPort = port;
        }

        /**
         * Read from socks socket
         *
         * @param b
         * @param offset
         * @param length
         * @return
         * @throws IOException
         */
        @Override
        public int read(byte[] b, int offset, int length) throws IOException {
            return 0;
        }

        /**
         * Write to socks socket
         *
         * @param b
         * @throws IOException
         */
        @Override
        public void write(byte[] b) throws IOException {

        }

        /**
         * Close socks socket
         */
        @Override
        public void close() {

        }

        @Override
        public InetAddress getLocalAddress() {
            return mLocalAddress;
        }

        @Override
        public int getLocalPort() {
            return mLocalPort;
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
