package info.pluggabletransports.dispatch;

import java.net.InetAddress;

//​ ​ It​ ​ provides​ ​ a ​ ​ way​ ​ to​ ​ make​ ​ outgoing​ ​ transport​ ​ connections​ ​ and​ ​ to​ ​ accept
//​ ​ incoming​ ​ transport​ ​ connections.
public interface Transport {

//​ ​ Create​ ​ outgoing​ ​ transport​ ​ connection;​ The​ ​ Dial​ ​ method​ ​ implements​ ​ the​ ​ Client​ ​ Factory​ ​ abstract​ ​ interface.
    public abstract Connection connect (InetAddress addr);

//​ ​ Create​ ​ listener​ ​ for​ ​ incoming​ ​ transport​ ​ connection; The​ ​ Listen​ ​ method​ ​ implements​ ​ the​ ​ Server​ ​ Factory​ ​ abstract​ i
    public abstract Listener listen (InetAddress addr);

}
