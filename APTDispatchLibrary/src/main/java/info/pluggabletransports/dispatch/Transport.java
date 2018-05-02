package info.pluggabletransports.dispatch;

import android.content.Context;

import java.util.Properties;

//​ ​ It​ ​ provides​ ​ a ​ ​ way​ ​ to​ ​ make​ ​ outgoing​ ​ transport​ ​ connections​ ​ and​ ​ to​ ​ accept
//​ ​ incoming​ ​ transport​ ​ connections.
public interface Transport {

    /**
     * key, value pairs based on PT 2.0 spec options, or custom values for the particular transport
     **/
    public abstract void init(Context context, Properties options);

    /**
     * Addr can be a host:port or other value dependent upon the transport needs
     **/
//​ ​ Create​ ​ outgoing​ ​ transport​ ​ connection;​ The​ ​ Dial​ ​ method​ ​ implements​ ​ the​ ​ Client​ ​ Factory​ ​ abstract​ ​ interface.
    public abstract Connection connect(String addr);

    /**
     * Addr is likely a localhost:port to bind a listen server socket to, but could be anything based on the transport
     **/
//​ ​ Create​ ​ listener​ ​ for​ ​ incoming​ ​ transport​ ​ connection; The​ ​ Listen​ ​ method​ ​ implements​ ​ the​ ​ Server​ ​ Factory​ ​ abstract​ i
    public abstract Listener listen(String addr);

    /**
     * Used to register with the Dispatcher
     **/
    public abstract void register();

}
