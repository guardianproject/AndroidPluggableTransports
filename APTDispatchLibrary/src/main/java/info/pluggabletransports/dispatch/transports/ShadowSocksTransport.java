package info.pluggabletransports.dispatch.transports;

import android.content.Context;

import java.util.Properties;

import info.pluggabletransports.dispatch.Connection;
import info.pluggabletransports.dispatch.Listener;
import info.pluggabletransports.dispatch.Transport;
import info.pluggabletransports.dispatch.util.TransportManager;

public class ShadowSocksTransport implements Transport {

    private TransportManager mTransportManager;
    private final static String ASSET_KEY = "shadowsocks";

    @Override
    public void init(Context context, Properties options) {

        mTransportManager = new TransportManager();
        mTransportManager.installTransport(context,ASSET_KEY);
    }

    @Override
    public Connection connect(String addr) {

        mTransportManager.startTransport();

        return null;
    }

    @Override
    public Listener listen(String addr) {
        return null;
    }
}
