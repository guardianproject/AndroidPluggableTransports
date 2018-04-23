package info.pluggeabletransports.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import info.pluggabletransports.dispatch.Dispatcher;

public class SampleClientActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_client);
    }

    public void init ()
    {
        Dispatcher.get();
    }
}
