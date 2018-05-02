package info.pluggabletransports.dispatch.transports;

import android.content.Context;

import java.util.Properties;

import info.pluggabletransports.dispatch.Connection;
import info.pluggabletransports.dispatch.Dispatcher;
import info.pluggabletransports.dispatch.Listener;
import info.pluggabletransports.dispatch.Transport;
import info.pluggabletransports.dispatch.util.TransportManager;

import static info.pluggabletransports.dispatch.DispatchConstants.PT_TRANSPORTS_SHADOWSOCKS;

public class ShadowSocksTransport implements Transport {

    private final static String ASSET_KEY = "shadowsocks";
    private TransportManager mTransportManager;

    @Override
    public void register() {
        Dispatcher.get().register(PT_TRANSPORTS_SHADOWSOCKS, getClass());
    }

    @Override
    public void init(Context context, Properties options) {

        mTransportManager = new TransportManager();
        mTransportManager.installTransport(context, ASSET_KEY);
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
