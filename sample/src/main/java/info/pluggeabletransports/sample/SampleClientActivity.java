package info.pluggeabletransports.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import info.pluggabletransports.dispatch.Connection;
import info.pluggabletransports.dispatch.DispatchConstants;
import info.pluggabletransports.dispatch.Dispatcher;
import info.pluggabletransports.dispatch.Transport;

public class SampleClientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_client);

        try {
            InetAddress bridgeAddr = Inet4Address.getByName("some.endpoint.domain.fronting.com");
            init(bridgeAddr);
        }
        catch (UnknownHostException uhe)
        {
            Log.e(getClass().getName(),"Error finding host",uhe);
        }
    }

    public void init (InetAddress bridgeAddr)
    {
        Transport transport = Dispatcher.get().getTransport(DispatchConstants.PT_TRANSPORTS_MEEK);

        if (transport != null)
        {
            Connection conn = transport.connect(bridgeAddr);

            //now use the connection, either as a proxy, or to read and write bytes directly

        }
    }
}
