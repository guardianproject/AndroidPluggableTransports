package info.pluggeabletransports.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import info.pluggabletransports.dispatch.Connection;
import info.pluggabletransports.dispatch.DispatchConstants;
import info.pluggabletransports.dispatch.Dispatcher;
import info.pluggabletransports.dispatch.Transport;

public class SampleClientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_client);

        String bridgeAddr = "some.endpoint.domain.fronting.com";
        init (bridgeAddr);
    }

    public void init (String bridgeAddr)
    {
        Transport transport = Dispatcher.get().getTransport(DispatchConstants.PT_TRANSPORTS_MEEK);

        if (transport != null)
        {
            Connection conn = transport.connect(bridgeAddr);
        }
    }
}
