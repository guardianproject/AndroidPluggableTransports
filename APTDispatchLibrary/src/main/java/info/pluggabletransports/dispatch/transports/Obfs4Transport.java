package info.pluggabletransports.dispatch.transports;

import java.net.InetAddress;

import info.pluggabletransports.dispatch.Connection;
import info.pluggabletransports.dispatch.Listener;
import info.pluggabletransports.dispatch.Transport;

public class Obfs4Transport implements Transport {
    @Override
    public Connection connect(InetAddress addr) {
        return null;
    }

    @Override
    public Listener listen(InetAddress addr) {
        return null;
    }
}
