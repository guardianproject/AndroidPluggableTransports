package info.pluggabletransports.dispatch.transports.sample;

import android.content.Context;

import java.util.Properties;

import info.pluggabletransports.dispatch.Connection;
import info.pluggabletransports.dispatch.Dispatcher;
import info.pluggabletransports.dispatch.Listener;
import info.pluggabletransports.dispatch.Transport;

public class SampleTransport implements Transport {

    {
        Dispatcher.get().register("sample",getClass());
    }

    @Override
    public void init(Context context, Properties options) {

    }

    @Override
    public Connection connect(String addr) {
        return null;
    }

    @Override
    public Listener listen(String addr) {
        return null;
    }
}
