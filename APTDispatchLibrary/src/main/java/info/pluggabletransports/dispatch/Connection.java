package info.pluggabletransports.dispatch;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;

public interface Connection {
    public int read(byte[] b, int offset, int length) throws IOException;
    public void write(byte[] b) throws IOException;
    public void close();
    public InetAddress getLocalAddress();
    public InetAddress getRemoteAddress();
    public void setDeadline(Date deadlineTime);
    public void setReadDeadline(Date deadlineTime);
    public void setWriteDeadline(Date deadlineTime);
}