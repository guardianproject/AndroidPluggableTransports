package info.pluggeabletransports.sample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Properties;

import info.pluggabletransports.dispatch.Connection;
import info.pluggabletransports.dispatch.DispatchConstants;
import info.pluggabletransports.dispatch.Dispatcher;
import info.pluggabletransports.dispatch.Transport;
import info.pluggabletransports.dispatch.transports.MeekTransport;
import info.pluggabletransports.dispatch.transports.sample.SampleTransport;

public class SampleClientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_client);

        // The Very Basic
        new AsyncTask<Void, Void, Void>() {
            protected void onPreExecute() {
                // Pre Code
            }

            protected Void doInBackground(Void... unused) {
                // Background Code
                initMeekTransport();
                initSampleTransport();
                return null;
            }

            protected void onPostExecute(Void unused) {
                // Post Code
            }
        }.execute();

    }

    private void initMeekTransport() {
        new MeekTransport().register();

        Properties options = new Properties();
        String remoteAddress = "185.152.65.180:9001";// a public Tor guard to test

        options.put(MeekTransport.OPTION_URL,"https://meek.azureedge.net/"); //the public Tor Meek endpoint
        options.put(MeekTransport.OPTION_FRONT, "ajax.aspnetcdn.com"); //the domain fronting address to use
        options.put(MeekTransport.OPTION_KEY, "97700DFE9F483596DDA6264C4D7DF7641E1E39CE"); //the key that is needed for this endpoint

        init(DispatchConstants.PT_TRANSPORTS_MEEK, remoteAddress, options);

    }

    private void initSampleTransport() {
        new SampleTransport().register();

        String bridgeAddress = "somecrazyaddress";
        Properties options = new Properties();
        options.put(SampleTransport.SAMPLE_SPECIAL_OPTION, "thesecret");

        Connection conn = init("sample", bridgeAddress, options);

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
    }

    public Connection init(String type, String bridgeAddress, Properties options) {
        Transport transport = Dispatcher.get().getTransport(this, type, options);

        if (transport != null) {
            Connection conn = transport.connect(bridgeAddress);

            return conn;

        }

        return null;
    }

    private void setSocksProxy(InetAddress localSocks, int socksPort) {
        //do what you need here to proxy
    }
}
