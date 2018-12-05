package info.pluggeabletransports.sample;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.Properties;

import info.pluggabletransports.dispatch.Connection;
import info.pluggabletransports.dispatch.DispatchConstants;
import info.pluggabletransports.dispatch.Dispatcher;
import info.pluggabletransports.dispatch.Transport;
import info.pluggabletransports.dispatch.transports.MeekTransport;
import info.pluggabletransports.dispatch.transports.sample.SampleTransport;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class SampleClientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_client);

        // The Very Basic
        new AsyncTask<Void, Void, String>() {
            protected void onPreExecute() {
                // Pre Code
            }

            protected String doInBackground(Void... unused) {
                // Background Code
                initMeekTransport();
             //   initSampleTransport();
                return null;
            }

            protected void onPostExecute(String log) {
                // Post Code
                if (log != null)
                    ((TextView)findViewById(R.id.response)).setText(log);

            }
        }.execute();

    }

    private String initMeekTransport() {
        new MeekTransport().register();

        String remoteAddress = "en.wikipedia.org:443";// a public Tor guard to test
        String requestUrl = "https://en.wikipedia.org/index.html";

        Properties options = new Properties();
        options.put(MeekTransport.OPTION_URL,"https://myprivatemeek.azureedge.net/"); //a public Meek endpoint
        options.put(MeekTransport.OPTION_FRONT, "ajax.aspnetcdn.com"); //the domain fronting address to use for Azure
        options.put(MeekTransport.OPTION_KEY, "88880DFE9F483596DDA6264C4D7DF7641E1E39CE"); //the unique meek key that is needed for this endpoint

        Connection conn = init(DispatchConstants.PT_TRANSPORTS_MEEK, remoteAddress, options);

        if (conn != null) {
            //now use the connection, either as a proxy, or to read and write bytes directly
            if (conn.getLocalAddress() != null && conn.getLocalPort() != -1) {

                OkHttpClient client = new OkHttpClient();

                // Create request for remote resource
                Request request = new Request.Builder()
                        .url(requestUrl)
                        .build();

                // Execute the request and retrieve the response.
                Response response = null;
                try {
                    response = client.newCall(request).execute();

                    // Deserialize HTTP response to concrete type.
                    ResponseBody body = response.body();

                    Log.d("Sample","Got page via Meek: " + body);

                    return body.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else {

                //or read and write bytes directly!
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    String httpGet = "GET " + requestUrl + " HTTP/1.0";
                    baos.write(httpGet.getBytes());
                    conn.write(baos.toByteArray());

                    byte[] buffer = new byte[1024 * 64];
                    int read = conn.read(buffer, 0, buffer.length);
                    String response = new String(buffer);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
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

    public Connection init(String type, String remoteAddress, Properties options) {
        Transport transport = Dispatcher.get().getTransport(this, type, options);

        if (transport != null) {
            Connection conn = transport.connect(remoteAddress);

            return conn;

        }

        return null;
    }

    private void setSocksProxy(InetAddress localSocks, int socksPort) {
        //do what you need here to proxy
    }
}
