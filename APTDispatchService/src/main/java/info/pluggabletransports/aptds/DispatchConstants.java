package info.pluggabletransports.aptds;

import android.content.Intent;

/**
 * Created by n8fr8 on 10/18/17.
 */

public interface DispatchConstants {

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
    public final static String EXTRA_SOCKS_PROXY = "info.pluggabletransports.aptds.intent.extra.SOCKS_PROXY";
    public final static String EXTRA_SOCKS_PROXY_HOST = "info.pluggabletransports.aptds.intent.extra.SOCKS_PROXY_HOST";
    public final static String EXTRA_SOCKS_PROXY_PORT = "info.pluggabletransports.aptds.intent.extra.SOCKS_PROXY_PORT";
    /**
     * The HTTP proxy settings in URL form.
     */
    public final static String EXTRA_HTTP_PROXY = "info.pluggabletransports.aptds.intent.extra.HTTP_PROXY";
    public final static String EXTRA_HTTP_PROXY_HOST = "info.pluggabletransports.aptds.intent.extra.HTTP_PROXY_HOST";
    public final static String EXTRA_HTTP_PROXY_PORT = "info.pluggabletransports.aptds.intent.extra.HTTP_PROXY_PORT";

    /**
     * All tor-related services and daemons are stopped
     */
    public final static String STATUS_OFF = "OFF";
    /**
     * All tor-related services and daemons have completed starting
     */
    public final static String STATUS_ON = "ON";
    public final static String STATUS_STARTING = "STARTING";
    public final static String STATUS_STOPPING = "STOPPING";

}
