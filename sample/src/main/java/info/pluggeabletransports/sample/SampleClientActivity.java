package info.pluggeabletransports.sample;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Properties;

import helloproxy.Helloproxy;
import info.pluggabletransports.dispatch.Connection;
import info.pluggabletransports.dispatch.DispatchConstants;
import info.pluggabletransports.dispatch.Dispatcher;
import info.pluggabletransports.dispatch.Transport;
import info.pluggabletransports.dispatch.transports.legacy.MeekTransport;
import info.pluggabletransports.dispatch.transports.legacy.Obfs4Transport;

public class SampleClientActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_client);


    }

    public void connectClicked (View view)
    {

        // The Very Basic
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                // Pre Code
            }

            protected String doInBackground(Void... unused) {
                // Background Code

              //  return initMeekTransport();

                return initObfs4Transport();
                //   initSampleTransport();

            }

            protected void onPostExecute(String log) {
                // Post Code
                if (log != null) {
                    Log.d("SAMPLEPT","Result: " + log);
                    ((TextView) findViewById(R.id.response)).setText(log);
                }
                else
                    ((TextView)findViewById(R.id.response)).setText("An error occured");
            }
        }.execute();
    }

    private int initUpstreamProxy (final int localHttpPort, int ptSocksPort, String username, String password) throws UnsupportedEncodingException {
        final StringBuffer socksProxy = new StringBuffer();
        socksProxy.append("socks5://");
        socksProxy.append(URLEncoder.encode(username,"UTF-8"));
        socksProxy.append(":");
        socksProxy.append(URLEncoder.encode(password,"UTF-8"));
        socksProxy.append('@');
        socksProxy.append("127.0.0.1:");
        socksProxy.append(ptSocksPort);

        Log.d("Proxy","proxy" + socksProxy.toString());

        new Thread () {
            public void run ()

            {
                Helloproxy.startProxy(":" + localHttpPort, socksProxy.toString());
            }
        }.start();

        return localHttpPort;
    }

    /**
     * You will need to run your own Meek server endpoint for this sample to work
     * Learn how to do that here: https://trac.torproject.org/projects/tor/wiki/doc/meek#Howtorunameek-serverbridge
     */
    private String initMeekTransport() {
        new MeekTransport().register();

        String address = "0.0.2.0:2";
        Properties options = new Properties();
        options.put(MeekTransport.OPTION_URL,"https://meek.azureedge.net/"); //a public Meek endpoint
        options.put(MeekTransport.OPTION_FRONT, "ajax.aspnetcdn.com"); //the domain fronting address to use for Azure
        options.put(MeekTransport.OPTION_KEY, "97700DFE9F483596DDA6264C4D7DF7641E1E39CE"); //the unique meek key that is needed for this endpoint

        Connection ptConn = null;
        Transport transport = Dispatcher.get().getTransport(this, DispatchConstants.PT_TRANSPORTS_MEEK, options);
        if (transport != null)
            ptConn = transport.connect(address);

        if (ptConn != null) {
            //now use the connection, either as a proxy, or to read and write bytes directly
            if (ptConn.getLocalAddress() != null && ptConn.getLocalPort() != -1) {
                try {
                    ptConn.write("Hello".getBytes());
                    byte[] resp = new byte[1];
                    ptConn.read(resp,0,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return null;
    }

    /**
     * You will need to run your own Obfs4 server bridge for this to work.
     * Learn more at: https://tor.stackexchange.com/questions/6370/how-to-run-an-obfs4-bridge
     * and http://manpages.ubuntu.com/manpages/xenial/man1/obfs4proxy.1.html
     * @return
     */
    private String initObfs4Transport() {
        new Obfs4Transport().register();

        Properties options = new Properties();

        /**
        //these values come from the public obfs4 endpoint you are running; you can't use Tor's OBFS4 bridges, you need your own
        options.put(Obfs4Transport.OPTION_ADDRESS,"xxx.xxx.xxx.xxx:1234"); //the host:port where the bridge is running
        options.put(Obfs4Transport.OPTION_CERT,"your obfs4 cert value goes here"); //looks like: ApWvCPD2uhjeAgaeS4Lem5PudwHLkmeQfEMMGoOkDJqZoeCq9bzLf/q/oGIggvB0b0VObg
         **/
        String address = "72.14.182.23:8888";//"208.80.154.224:80"; //wikipedia!

        String torBridgeLine = "obfs4 72.14.182.23:8888 key-not-used cert=x7i6lumoDE5ApW28e8rwqwCwDLhYYYQu8c0ut6vmc9e+P2VV4YQgtN9F+TzSbHJCrD+dLw iat-mode=0";
        Obfs4Transport.setPropertiesFromBridgeString(options,torBridgeLine);

        Transport transport = Dispatcher.get().getTransport(this, DispatchConstants.PT_TRANSPORTS_OBFS4, options);
        if (transport != null) {
            Connection ptConn = transport.connect(address);// transport.connect(options.getProperty(Obfs4Transport.OPTION_ADDRESS));

            if (ptConn != null) {

                boolean useHttpToSocks5Upstream = true;

                if (useHttpToSocks5Upstream) {

                    String urlString = "https://wikipedia.org";

                    //use this if your Obfs4 bridge is connected to a backend SOCKS5 server

                    int localHttpPort = 8989;
                    try {
                        initUpstreamProxy(localHttpPort, ptConn.getLocalPort(), ptConn.getProxyUsername(), ptConn.getProxyPassword());
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    //now here you can make URLConnection requests with HTTP proxy set
                    Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", localHttpPort));
                    try {
                        URLConnection conn = new URL(urlString).openConnection(proxy);
                        conn.getContent();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {

                        //otherwise you can send and receive raw bytes to whatever socket/port your obfs4 is connected to

                        ptConn.write("GET /index.html HTTP/1.0".getBytes());
                        byte[] resp = new byte[1000];
                        ptConn.read(resp, 0, resp.length);
                        ptConn.close();
                        String log = new String(resp);
                        return log;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        }

        return null;
    }

    /**
    private void initSampleTransport() {
        new SampleTransport().register();

        String remoteAddress = "somecrazyaddress";
        Properties options = new Properties();
        options.put(SampleTransport.SAMPLE_SPECIAL_OPTION, "thesecret");

        Connection conn = null;
        Transport transport = Dispatcher.get().getTransport(this, "sample", options);

        if (transport != null)
            conn = transport.connect(remoteAddress);

        if (conn != null) {
            //now use the connection, either as a proxy, or to read and write bytes directly
            if (conn.getLocalAddress() != null && conn.getLocalPort() != -1)
                setSocksProxy(conn.getLocalAddress(), conn.getLocalPort());
            else {

                //or read and write bytes directly!
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    baos.write("GET https://somewebsite.org/TheProject.html HTTP/1.0".getBytes());
                    conn.write(baos.toByteArray());

                    byte[] buffer = new byte[1024 * 64];
                    int read = conn.read(buffer, 0, buffer.length);
                    String response = new String(buffer);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }**/

}
