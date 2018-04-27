package info.pluggabletransports.dispatch.transports;

import android.content.Context;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.Properties;

import info.pluggabletransports.dispatch.Connection;
import info.pluggabletransports.dispatch.Dispatcher;
import info.pluggabletransports.dispatch.Listener;
import info.pluggabletransports.dispatch.Transport;
import info.pluggabletransports.dispatch.util.TransportManager;
import iobfs4proxy.Iobfs4proxy;

import static info.pluggabletransports.dispatch.DispatchConstants.PT_TRANSPORTS_MEEK;

public class MeekTransport implements Transport, Runnable {

    public final static String OPTION_FRONT = "front";
    public final static String OPTION_KEY = "key";
    public final static String OPTION_URL = "url";

    private final static String ASSET_KEY = "obfs4proxy";

    private final static int DEFAULT_MEEK_SOCKS_PORT = 47352;

    {
        Dispatcher.get().register(PT_TRANSPORTS_MEEK,getClass());
    }

    @Override
    public void init(Context context, Properties options) {

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
