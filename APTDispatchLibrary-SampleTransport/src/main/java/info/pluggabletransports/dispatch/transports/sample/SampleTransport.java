package info.pluggabletransports.dispatch.transports.sample;

import android.content.Context;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;
import java.util.Properties;

import info.pluggabletransports.dispatch.Connection;
import info.pluggabletransports.dispatch.Dispatcher;
import info.pluggabletransports.dispatch.Listener;
import info.pluggabletransports.dispatch.Transport;

public class SampleTransport implements Transport {

    public final static String SAMPLE_SPECIAL_OPTION = "option1";

    @Override
    public void register() {
        Dispatcher.get().register("sample", getClass());
    }

    @Override
    public void init(Context context, Properties options) {

    }

    @Override
    public Connection connect(String addr) {
        return new SampleConnection();
    }

    @Override
    public Listener listen(String addr) {
        return null;
    }

    class SampleConnection implements Connection {

        @Override
        public int read(byte[] b, int offset, int length) throws IOException {
            return 0;
        }

        @Override
        public void write(byte[] b) throws IOException {

        }

        @Override
        public void close() {

        }

        @Override
        public InetAddress getLocalAddress() {
            return null;
        }

        @Override
        public int getLocalPort() {
            return 0;
        }

        @Override
        public InetAddress getRemoteAddress() {
            return null;
        }

        @Override
        public int getRemotePort() {
            return 0;
        }

        @Override
        public void setDeadline(Date deadlineTime) {

        }

        @Override
        public void setReadDeadline(Date deadlineTime) {

        }

        @Override
        public void setWriteDeadline(Date deadlineTime) {

        }
    }
}
