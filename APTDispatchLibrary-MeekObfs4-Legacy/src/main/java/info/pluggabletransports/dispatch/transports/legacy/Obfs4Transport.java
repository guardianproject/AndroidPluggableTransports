package info.pluggabletransports.dispatch.transports.legacy;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.runjva.sourceforge.jsocks.protocol.Socks4Proxy;
import com.runjva.sourceforge.jsocks.protocol.Socks5Proxy;
import com.runjva.sourceforge.jsocks.protocol.SocksException;
import com.runjva.sourceforge.jsocks.protocol.SocksSocket;
import com.runjva.sourceforge.jsocks.protocol.UserPasswordAuthentication;

import info.pluggabletransports.dispatch.Connection;
import info.pluggabletransports.dispatch.DispatchConstants;
import info.pluggabletransports.dispatch.Dispatcher;
import info.pluggabletransports.dispatch.Listener;
import info.pluggabletransports.dispatch.Transport;
import info.pluggabletransports.dispatch.util.TransportListener;
import info.pluggabletransports.dispatch.util.TransportManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import static info.pluggabletransports.dispatch.DispatchConstants.PT_TRANSPORTS_MEEK;
import static info.pluggabletransports.dispatch.DispatchConstants.PT_TRANSPORTS_OBFS4;
import static info.pluggabletransports.dispatch.DispatchConstants.TAG;

public class Obfs4Transport implements Transport {

    public final static String OPTION_CERT = "cert";

    private int mLocalSocksPort = -1;

    private String mPtStateDir;
    private String mCert;
    private String mIatMode;

    private final static char NUL_CHAR = '\u0000';

    private TransportManager mTransportManager;
    private final static String ASSET_KEY = "obfs4proxy";

    public final static String OPTION_IAT_MODE = "iat-mode";
    public final static String OPTION_ADDRESS = "address";
    @Override
    public void register() {
        Dispatcher.get().register(PT_TRANSPORTS_OBFS4, getClass());
    }

    @Override
    public void init(final Context context, Properties options) {

        mTransportManager = new TransportManager() {
            public  void startTransportSync (TransportListener listener)
            {
                try {


                    StringBuffer cmd = new StringBuffer();
                    cmd.append(mFileTransport.getCanonicalPath());

                    HashMap<String,String> env = new HashMap<>();

                    env.put(DispatchConstants.TOR_PT_LOG_LEVEL, "DEBUG");
                    env.put(DispatchConstants.TOR_PT_CLIENT_TRANSPORTS, DispatchConstants.PT_TRANSPORTS_OBFS4);
                    env.put(DispatchConstants.TOR_PT_MANAGED_TRANSPORT_VER, "1");
                    env.put(DispatchConstants.TOR_PT_EXIT_ON_STDIN_CLOSE, "1");
                    env.put(DispatchConstants.TOR_PT_STATE_LOCATION,context.getDir("pt-cache",Context.MODE_PRIVATE).getCanonicalPath());

                    exec(cmd.toString(), false, env, listener);



                }
                catch (Exception ioe)
                {
                    debug("Couldn't install transport: " + ioe);

                    if (listener != null)
                    listener.transportFailed("Couldn't install transport: " + ioe.getMessage());
                }
            }

        };

        mTransportManager.installTransport(context, ASSET_KEY);

        mPtStateDir = context.getDir("pt-state", Context.MODE_PRIVATE).getAbsolutePath();

        mCert = options.getProperty(OPTION_CERT);

        if (options.containsKey(OPTION_IAT_MODE))
            mIatMode = options.getProperty(OPTION_IAT_MODE);
    }

    @Override
    public Connection connect(String addr) {

        mTransportManager.startTransport(new TransportListener() {
            @Override
            public void transportStarted(int localPort) {
                mLocalSocksPort = localPort;
            }

            @Override
            public void transportFailed(String err) {
                Log.d(TAG,"error starting transport: " + err);
            }
        });

        while (mLocalSocksPort == -1)
        {
            try { Thread.sleep(500);}catch(Exception e){}
        }

        try {
            return new Obfs4Connection(addr, InetAddress.getLocalHost(), mLocalSocksPort);
        } catch (IOException e) {
            Log.e(getClass().getName(),"Error making connection",e);
            return null;
        }
    }

    @Override
    public Listener listen(String addr) {
        return null;
    }


    class Obfs4Connection implements Connection {

        private InetAddress mLocalAddress;
        private int mLocalPort;
        private String mRemoteAddress;
        private int mRemotePort;

        private InputStream mInputStream;
        private OutputStream mOutputStream;

        public Obfs4Connection(String bridgeAddr, InetAddress localSocks, int port) throws IOException {

            String[] addressparts = bridgeAddr.split(":");
            mRemoteAddress = addressparts[0];
            mRemotePort = Integer.parseInt(addressparts[1]);
            mLocalAddress = localSocks;
            mLocalPort = port;

        }

        private void initBridgeViaSocks() throws IOException {
            //connect to SOCKS port and pass the values appropriately to configure meek
            //see: https://gitweb.torproject.org/torspec.git/tree/pt-spec.txt#n628

            Socket s = getSocket(mRemoteAddress, mRemotePort);
            mInputStream = new BufferedInputStream(s.getInputStream());
            mOutputStream = new BufferedOutputStream(s.getOutputStream());

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
            if (mInputStream == null)
                initBridgeViaSocks();

            return mInputStream.read(b,offset,length);
        }

        /**
         * Write to socks socket
         *
         * @param b
         * @throws IOException
         */
        @Override
        public void write(byte[] b) throws IOException {

            if (mOutputStream == null)
                initBridgeViaSocks();

            mOutputStream.write(b);
            mOutputStream.flush();
        }

        /**
         * Close socks socket
         */
        @Override
        public void close() {

            if (mOutputStream != null && mInputStream != null) {
                try {
                    mOutputStream.close();
                    mInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

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
            try {
                return InetAddress.getByName(mRemoteAddress);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public int getRemotePort() {
            return mRemotePort;
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


        private String getProxyUsername ()
        {
            StringBuffer socksUser = new StringBuffer();

            socksUser.append(OPTION_CERT).append("=").append(mCert);

            if (!TextUtils.isEmpty(mIatMode))
                socksUser.append(";").append(OPTION_IAT_MODE).append("=").append(mIatMode);

            return socksUser.toString();
        }

        private String getProxyPassword ()
        {
            return Character.toString(NUL_CHAR);
        }

        @Override
        public Socket getSocket (String remoteAddress, int remotePort) throws SocksException, UnknownHostException {

            Socks5Proxy proxy = new Socks5Proxy(mLocalAddress,mLocalPort);

            UserPasswordAuthentication auth = new UserPasswordAuthentication(getProxyUsername(),getProxyPassword());
            proxy.setAuthenticationMethod(0,null);
            proxy.setAuthenticationMethod(UserPasswordAuthentication.METHOD_ID, auth);

            SocksSocket s = new SocksSocket(proxy, remoteAddress, remotePort);

            return s;
        }
    }

    /**
     * Convenience method to set APT options from Tor style bridge line
     *
     * @param options the options instance you want to be configured
     * @param bridgeLine a configuration line as provided by https://bridges.torproject.org
     */
    public static void setPropertiesFromBridgeString (Properties options, String bridgeLine)
    {

        // obfs4 174.128.247.178:443 818AAAC5F85DE83BF63779E578CA32E5AEC2115E cert=ApWvCPD2uhjeAgaeS4Lem5PudwHLkmeQfEMMGoOkDJqZoeCq9bzLf/q/oGIggvB0b0VObg iat-mode=0

        String[] parts = bridgeLine.split(" ");

        options.put(Obfs4Transport.OPTION_ADDRESS,parts[1]);
        options.put(Obfs4Transport.OPTION_CERT,parts[3].split("=")[1]);
        options.put(Obfs4Transport.OPTION_IAT_MODE,parts[4].split("=")[1]);


    }
}
