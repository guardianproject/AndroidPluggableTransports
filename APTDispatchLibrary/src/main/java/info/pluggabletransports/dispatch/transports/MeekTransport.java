package info.pluggabletransports.dispatch.transports;

import java.net.InetAddress;
import java.util.Properties;

import info.pluggabletransports.dispatch.Connection;
import info.pluggabletransports.dispatch.Listener;
import info.pluggabletransports.dispatch.Transport;

public class MeekTransport implements Transport {

    @Override
    public void init(Properties options) {

    }

    @Override
    public Connection connect(String addr) {
        return null;
    }

    @Override
    public Listener listen(String addr) {
        return null;
    }

    private void initMeekIPC ()
    {
        //meek_lite 0.0.2.0:2 97700DFE9F483596DDA6264C4D7DF7641E1E39CE url=https://meek.azureedge.net/ front=ajax.aspnetcdn.com

    }

    private void initMeekSharedLibrary ()
    {

    }
}
