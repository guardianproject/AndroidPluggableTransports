package info.pluggabletransports.dispatch.util;

public interface TransportListener {

    public void transportStarted (int localPort);

    public void transportFailed (String err);
}
