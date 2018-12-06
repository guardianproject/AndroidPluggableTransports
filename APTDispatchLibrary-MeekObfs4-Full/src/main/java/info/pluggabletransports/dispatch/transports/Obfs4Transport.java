package info.pluggabletransports.dispatch.transports;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;


import com.runjva.sourceforge.jsocks.protocol.Socks4Proxy;
import com.runjva.sourceforge.jsocks.protocol.Socks5Proxy;
import com.runjva.sourceforge.jsocks.protocol.SocksException;
import com.runjva.sourceforge.jsocks.protocol.SocksSocket;
import com.runjva.sourceforge.jsocks.protocol.UserPasswordAuthentication;

import org.w3c.dom.Text;

import goptbundle.Goptbundle;
import info.pluggabletransports.dispatch.Connection;
import info.pluggabletransports.dispatch.DispatchConstants;
import info.pluggabletransports.dispatch.Dispatcher;
import info.pluggabletransports.dispatch.Listener;
import info.pluggabletransports.dispatch.Transport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Properties;

import static info.pluggabletransports.dispatch.DispatchConstants.PT_TRANSPORTS_MEEK;
import static info.pluggabletransports.dispatch.DispatchConstants.PT_TRANSPORTS_OBFS4;
import static info.pluggabletransports.dispatch.DispatchConstants.TAG;

public class Obfs4Transport implements Transport {

    public final static String OPTION_CERT = "cert";
    public final static String OPTION_IAT_MODE = "iat-mode";
    public final static String OPTION_KEY = "key";
    public final static String OPTION_ADDRESS = "address";

    private int mLocalSocksPort = 47351;

    private String mPtStateDir;

    private String mObfs4Host;
    private int mObfs4Port;
    private String mObfs4Cert;
    private String mObfs4Key;

    private String mIatMode = "0";

    private final static String NUL_CHAR = "\u0000";


    @Override
    public void register() {
        Dispatcher.get().register(PT_TRANSPORTS_OBFS4, getClass());
    }

    @Override
    public void init(Context context, Properties options) {

        initObfs4SharedLibrary(context);

        mPtStateDir = context.getDir("pt-state", Context.MODE_PRIVATE).getAbsolutePath();

        if (options.containsKey(OPTION_ADDRESS))
        {
            String[] addrParts = options.getProperty(OPTION_ADDRESS).split(":");
            mObfs4Host = addrParts[0];
            mObfs4Port = Integer.parseInt(addrParts[1]);
        }

        mObfs4Cert = options.getProperty(OPTION_CERT);
        mObfs4Key = options.getProperty(OPTION_KEY);

        if (options.containsKey(OPTION_IAT_MODE))
            mIatMode = options.getProperty(OPTION_IAT_MODE);


    }

    @Override
    public Connection connect(final String addr) {

        //let's start the transport in it's own thread
        exec(new Runnable() { public void run () { Goptbundle.load(mPtStateDir); } });
        exec(new Runnable() { public void run () {

            String line = getLogLine("CMETHOD obfs4 socks5",1000);
            //         CMETHOD trebuchet socks5 127.0.0.1:19999

            if (!TextUtils.isEmpty(line))
            {
                String[] parts = line.split(" ");
                for (String part : parts) {
                    if (part.contains("127.0.0.1")) {
                        String[] addrParts = part.split(":");
                        mLocalSocksPort = Integer.parseInt(addrParts[1]);
                        break;
                    }
                }
            }

        } });


        try {
            return new Obfs4Connection(addr, InetAddress.getLocalHost(), mLocalSocksPort);
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

    private void initObfs4SharedLibrary(Context context) {

        try {
            Goptbundle.setenv(DispatchConstants.TOR_PT_LOG_LEVEL, "DEBUG");
            Goptbundle.setenv(DispatchConstants.TOR_PT_CLIENT_TRANSPORTS, DispatchConstants.PT_TRANSPORTS_OBFS4);
            Goptbundle.setenv(DispatchConstants.TOR_PT_MANAGED_TRANSPORT_VER, "1");
            Goptbundle.setenv(DispatchConstants.TOR_PT_EXIT_ON_STDIN_CLOSE, "0");
        } catch (Exception e) {
            Log.e(getClass().getName(), "Error setting env variables", e);
        }

    }

    public class Obfs4Connection implements Connection {

        private InetAddress mLocalAddress;
        private int mLocalPort;
        private String mRemoteAddress;
        private int mRemotePort;

        private InputStream mInputStream;
        private OutputStream mOutputStream;

        public Obfs4Connection(String remoteAddress, InetAddress localSocks, int port) throws IOException {

            String[] addressparts = remoteAddress.split(":");
            mRemoteAddress = addressparts[0];
            mRemotePort = Integer.parseInt(addressparts[1]);
            mLocalAddress = localSocks;
            mLocalPort = port;

        }

        public String getProxyUsername ()
        {
            StringBuffer socksUser = new StringBuffer();

            socksUser.append(OPTION_KEY).append("\\=").append(mObfs4Key).append("\\;");
            socksUser.append(OPTION_CERT).append("\\=").append(mObfs4Cert).append("\\;");
            socksUser.append(OPTION_IAT_MODE).append("\\=").append(mIatMode).append("\\;");

            return socksUser.toString();
        }

        public String getProxyPassword ()
        {
            StringBuffer socksPass = new StringBuffer();
            socksPass.append(NUL_CHAR);
            return socksPass.toString();
        }

        public Socket getSocket (String address, int port) throws SocksException, UnknownHostException {

            Socks5Proxy proxy = new Socks5Proxy(mLocalAddress,mLocalPort);
            UserPasswordAuthentication auth = new UserPasswordAuthentication(getProxyUsername(),getProxyPassword());
            proxy.setAuthenticationMethod(UserPasswordAuthentication.METHOD_ID, auth);
            SocksSocket s = new SocksSocket(proxy, address, port);
            return s;
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

            try {
                mOutputStream.close();
                mInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Goptbundle.close();
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
    }

    private static String getLogLine(String matchChars, int max){
        try {
            Process process = Runtime.getRuntime().exec("logcat -d");
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            String line;
            int i = 0;
            while ((line = bufferedReader.readLine()) != null && i++ < max) {
                if (line.contains(matchChars))
                    return line;
            }
        } catch (IOException e) {
        }
        return null;
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
        options.put(Obfs4Transport.OPTION_KEY,parts[2]);
        options.put(Obfs4Transport.OPTION_CERT,parts[3].split("=")[1]);
        options.put(Obfs4Transport.OPTION_IAT_MODE,parts[4].split("=")[1]);



    }
}
