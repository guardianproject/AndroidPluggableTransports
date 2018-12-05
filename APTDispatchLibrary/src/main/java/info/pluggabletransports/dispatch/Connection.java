package info.pluggabletransports.dispatch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Date;

public interface Connection {

    public Socket getSocket (String address, int port) throws IOException;

    public int read(byte[] b, int offset, int length) throws IOException;

    public void write(byte[] b) throws IOException;

    public void close();

    public InetAddress getLocalAddress();

    public int getLocalPort();

    public InetAddress getRemoteAddress();

    public int getRemotePort();

    public void setDeadline(Date deadlineTime);

    public void setReadDeadline(Date deadlineTime);

    public void setWriteDeadline(Date deadlineTime);
}