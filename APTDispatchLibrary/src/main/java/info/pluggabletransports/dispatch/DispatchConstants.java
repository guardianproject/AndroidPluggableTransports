package info.pluggabletransports.dispatch;

import android.content.Intent;

/**
 * Created by n8fr8 on 10/18/17.
 */

public interface DispatchConstants {

    public final static String TAG = "APTDS";

    /**
     * Pluggable Transports 2.0 Spec Constants 3.3.1.1. Common Configuration Parameters
     **/

    public final static String PT_VERSION = "ptversion"; //decimal //Specify the Pluggable Transport protocol version to use
    public final static String PT_STATE_DIRECTORY = "state"; //string path or file // Specify the directory to use to store state information required by the transports
    public final static String PT_EXIT_ON_STDIN_CLOSE = "exit-on-stdin-close"; //boolean // Set to true to force the dispatcher to close when the stdin pipe is closed

    // NOTE: -transports is parsed as a common command line flag that overrides either TOR_PT_SERVER_TRANSPORTS or TOR_PT_CLIENT_TRANSPORTS
    public final static String PT_TRANSPORTS = "transports"; //string transport name // Specify transports to enable

    // PT 2.0 specification, 3.3.1.2. Pluggable PT Client Configuration Parameters
    public final static String PT_CLIENT_UPSTREAM_PROXY = "proxy";

    // PT 2.0 specification, 3.3.1.3. Pluggable PT Server Environment Variables
    public final static String PT_SERVER_OPTIONS = "options"; //Specify the transport options for the server
    public final static String PT_SERVER_BIND_ADDR = "bindaddr"; //Specify the bind address for transparent server
    public final static String PT_SERVER_OR_PORT = "orport"; //Specify the address the server should forward traffic to in host:port format
    public final static String PT_SERVER_EXT_OR_PORT = "extorport"; //    Specify the address of a server implementing the Extended OR Port protocol, which is used for per-connection metadata
    public final static String PT_SERVER_AUTH_COOKIE = "authcookie"; //   Specify an authentication cookie, for use in authenticating with the Extended OR Port
    public final static String PT_SERVER_PROXY_LISTEN_ADDR = "proxylistenaddr"; //Specify the bind address for the local SOCKS server provided by the client

    // APT-DS implemented transports
    public final static String PT_TRANSPORTS_MEEK = "meek-lite";
    public final static String PT_TRANSPORTS_OBFS4 = "obfs4";
    public final static String PT_TRANSPORTS_SHADOWSOCKS = "ss";
    public final static String PT_TRANSPORTS_SNOWFLAKE = "snowflake";

    // goptbundle env var names
    public static final String TOR_PT_LOG_LEVEL = "TOR_PT_LOG_LEVEL";
    /**
     * If {@code tor} daemon keeps stdin open for use in termination detection (1/0)
     */
    public static final String TOR_PT_EXIT_ON_STDIN_CLOSE = "TOR_PT_EXIT_ON_STDIN_CLOSE";
    /**
     * Read the pluggable transports version from.
     */
    public static final String TOR_PT_MANAGED_TRANSPORT_VER = "TOR_PT_MANAGED_TRANSPORT_VER";
    /**
     * The directory to read the state of the pluggable transport from.
     */
    public static final String TOR_PT_STATE_LOCATION = "TOR_PT_STATE_LOCATION";
    /**
     * Comma-separated list of transports: obfs4,meek_lite,obfs2,obfs3,scramblesuit
     */
    public static final String TOR_PT_CLIENT_TRANSPORTS = "TOR_PT_CLIENT_TRANSPORTS";
    /**
     * Comma-separated list of transports: obfs4,meek_lite,obfs2,obfs3,scramblesuit
     */
    public static final String TOR_PT_SERVER_TRANSPORTS = "TOR_PT_SERVER_TRANSPORTS";
    public static final String TOR_PT_SERVER_BINDADDR = "TOR_PT_SERVER_BINDADDR";
    public static final String TOR_PT_SERVER_TRANSPORT_OPTIONS = "TOR_PT_SERVER_TRANSPORT_OPTIONS";
    /**
     * URL for the proxy to connect to in order to use the running pluggable transport
     */
    public static final String TOR_PT_PROXY = "TOR_PT_PROXY";
    public static final String TOR_PT_ORPORT = "TOR_PT_ORPORT";
    public static final String TOR_PT_AUTH_COOKIE_FILE = "TOR_PT_AUTH_COOKIE_FILE";
    public static final String TOR_PT_EXTENDED_SERVER_PORT = "TOR_PT_EXTENDED_SERVER_PORT";

    /**
     * A request to Orbot to transparently start Tor services
     */
    public final static String ACTION_START = "info.pluggabletransports.aptds.intent.action.START";
    /**
     * {@link Intent} send by Orbot with {@code ON/OFF/STARTING/STOPPING} status
     */
    public final static String ACTION_STATUS = "info.pluggabletransports.aptds.intent.action.STATUS";
    /**
     * {@code String} that contains a status constant: {@link #STATUS_ON},
     * {@link #STATUS_OFF}, {@link #STATUS_STARTING}, or
     * {@link #STATUS_STOPPING}
     */
    public final static String EXTRA_STATUS = "info.pluggabletransports.aptds.intent.extra.STATUS";
    /**
     * A {@link String} {@code packageName} for Orbot to direct its status reply
     * to, used in {@link #ACTION_START} {@link Intent}s sent to Orbot
     */
    public final static String EXTRA_PACKAGE_NAME = "info.pluggabletransports.aptds.intent.extra.PACKAGE_NAME";
    /**
     * The SOCKS proxy settings in URL form.
     */
    public final static String EXTRA_TRANSPORT_TYPE = "info.pluggabletransports.aptds.intent.extra.PROXY_TYPE";
    public final static String EXTRA_TRANSPORT_PORT = "info.pluggabletransports.aptds.intent.extra.PROXY_PORT";
    public final static String EXTRA_TRANSPORT_VPN = "info.pluggabletransports.aptds.intent.extra.PROXY_VPN";


    /**
     * All related services and daemons are stopped
     */
    public final static String STATUS_OFF = "OFF";
    /**
     * All related services and daemons have completed starting
     */
    public final static String STATUS_ON = "ON";
    public final static String STATUS_STARTING = "STARTING";
    public final static String STATUS_STOPPING = "STOPPING";

    public final static int FILE_WRITE_BUFFER_SIZE = 2048;

    public final static String DIRECTORY_BINARIES = "bin";


}
