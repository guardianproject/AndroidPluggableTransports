package info.pluggabletransports.dispatch;

import java.io.IOException;
import java.util.Date;

public interface Connection {
    public byte[] read(int i) throws IOException;
    public void write(byte[] b) throws IOException;
    public void close();
    public String getLocalAddress();
    public String getRemoteAddress();
    public void setDeadline(Date deadlineTime);
    public void setReadDeadline(Date deadlineTime);
    public void setWriteDeadline(Date deadlineTime);
}