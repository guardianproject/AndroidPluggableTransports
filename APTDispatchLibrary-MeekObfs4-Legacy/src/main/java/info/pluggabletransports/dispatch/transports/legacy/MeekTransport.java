package info.pluggabletransports.dispatch.transports.legacy;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.runjva.sourceforge.jsocks.protocol.Socks5Proxy;
import com.runjva.sourceforge.jsocks.protocol.SocksSocket;
import com.runjva.sourceforge.jsocks.protocol.UserPasswordAuthentication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Properties;

import info.pluggabletransports.dispatch.Connection;
import info.pluggabletransports.dispatch.Dispatcher;
import info.pluggabletransports.dispatch.Listener;
import info.pluggabletransports.dispatch.Transport;
import info.pluggabletransports.dispatch.util.TransportListener;
import info.pluggabletransports.dispatch.util.TransportManager;

import static info.pluggabletransports.dispatch.DispatchConstants.PT_TRANSPORTS_MEEK;
import static info.pluggabletransports.dispatch.DispatchConstants.TAG;
import static info.pluggabletransports.dispatch.transports.legacy.Obfs4Transport.OPTION_IAT_MODE;

public class MeekTransport implements Transport {

    public final static String OPTION_FRONT = "front";
    public final static String OPTION_KEY = "key";
    public final static String OPTION_URL = "url";

    private int mLocalSocksPort = 47352;

    private String mPtStateDir;
    private String mMeekFrontDomain;
    private String mMeekKey;
    private String mMeekUrl;

    private final static String NUL_CHAR = "\u0000";

    private TransportManager mTransportManager;
    private final static String ASSET_KEY = "obfs4";

    @Override
    public void register() {
        Dispatcher.get().register(PT_TRANSPORTS_MEEK, getClass());
    }

    @Override
    public void init(Context context, Properties options) {

        mTransportManager = new TransportManager() {
            public  void startTransportSync (TransportListener listener)
            {
                try {

                    String serverAddress = "172.104.48.102";
                    String serverPort = "443";

                    String localAddress = "127.0.0.1";
                    String localPort = "31059";

                    StringBuffer cmd = new StringBuffer();
                    cmd.append(mFileTransport.getCanonicalPath()).append(' ');
                    cmd.append("-s ").append(serverAddress).append(' ');
                    cmd.append("-p ").append(serverPort).append(' ');
                    cmd.append("-b ").append(localAddress).append(' ');
                    cmd.append("-l ").append(localPort).append(' ');

                    exec(cmd.toString(), false, null,listener);


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

        mMeekFrontDomain = options.getProperty(OPTION_FRONT);
        mMeekKey = options.getProperty(OPTION_KEY);
        mMeekUrl = options.getProperty(OPTION_URL);
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
            return new MeekConnection(addr, InetAddress.getLocalHost(), mLocalSocksPort);
        } catch (IOException e) {
            Log.e(getClass().getName(),"Error making connection",e);
            return null;
        }
    }

    private void exec (Runnable run)
    {
        new Thread (run).start();
    }

    @Override
    public Listener listen(String addr) {
        return null;
    }


    class MeekConnection implements Connection {

        private InetAddress mLocalAddress;
        private int mLocalPort;
        private String mRemoteAddress;
        private int mRemotePort;

        private InputStream mInputStream;
        private OutputStream mOutputStream;


        private final static char NUL_CHAR = '\u0000';

        public MeekConnection(String bridgeAddr, InetAddress localSocks, int port) throws IOException {

            String[] addressparts = bridgeAddr.split(":");
            mRemoteAddress = addressparts[0];
            mRemotePort = Integer.parseInt(addressparts[1]);
            mLocalAddress = localSocks;
            mLocalPort = port;

            initBridgeViaSocks();
        }


        @Override
        public String getProxyUsername ()
        {
            StringBuffer socksUser = new StringBuffer();

            //what do we use here for Meek?

            return socksUser.toString();
        }

        @Override
        public String getProxyPassword ()
        {
            return Character.toString(NUL_CHAR);
        }

        private void initBridgeViaSocks() throws IOException {
            //connect to SOCKS port and pass the values appropriately to configure meek
            //see: https://gitweb.torproject.org/torspec.git/tree/pt-spec.txt#n628

            StringBuffer socksUser = new StringBuffer();

            socksUser.append(OPTION_URL).append("\\=").append(mMeekUrl).append("\\;");
            socksUser.append(OPTION_FRONT).append("\\=").append(mMeekFrontDomain).append("\\;");
            socksUser.append(OPTION_KEY).append("\\=").append(mMeekKey).append("\\;");

            StringBuffer socksPass = new StringBuffer();
            socksPass.append(NUL_CHAR);

            Socks5Proxy proxy = new Socks5Proxy(mLocalAddress,mLocalPort);
            UserPasswordAuthentication auth = new UserPasswordAuthentication(socksUser.toString(),socksPass.toString());
            proxy.setAuthenticationMethod(UserPasswordAuthentication.METHOD_ID, auth);
            SocksSocket s = new SocksSocket(proxy, mRemoteAddress, mRemotePort);

            mInputStream = s.getInputStream();
            mOutputStream = s.getOutputStream();

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
            mOutputStream.write(b);
            mOutputStream.flush();
        }

        /**
         * Close socks socket
         */
        @Override
        public void close() {

            try {
                mOutputStream.close();
                mInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
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

        @Override
        public Socket getSocket(String address, int port) throws IOException {
            return null;
        }
    }
}

/**
 * 3.5. Pluggable Transport Client Per-Connection Arguments

 Certain PT transport protocols require that the client provides
 per-connection arguments when making outgoing connections.  On
 the server side, this is handled by the "ARGS" optional argument
 as part of the "SMETHOD" message.

 On the client side, arguments are passed via the authentication
 fields that are part of the SOCKS protocol.

 First the "<Key>=<Value>" formatted arguments MUST be escaped,
 such that all backslash, equal sign, and semicolon characters
 are escaped with a backslash.

 Second, all of the escaped are concatenated together.

 Example:

 shared-secret=rahasia;secrets-file=/tmp/blob

 Lastly the arguments are transmitted when making the outgoing
 connection using the authentication mechanism specific to the
 SOCKS protocol version.

 - In the case of SOCKS 4, the concatenated argument list is
 transmitted in the "USERID" field of the "CONNECT" request.

 - In the case of SOCKS 5, the parent process must negotiate
 "Username/Password" authentication [RFC1929], and transmit
 the arguments encoded in the "UNAME" and "PASSWD" fields.

 If the encoded argument list is less than 255 bytes in
 length, the "PLEN" field must be set to "1" and the "PASSWD"
 field must contain a single NUL character.
 */
