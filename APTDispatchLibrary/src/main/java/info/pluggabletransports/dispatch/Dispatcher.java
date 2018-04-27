package info.pluggabletransports.dispatch;

import android.content.Context;

import java.util.HashMap;
import java.util.Properties;

public class Dispatcher implements DispatchConstants {

    private static Dispatcher _instance;

    private HashMap<String,Class> mTransportRegistery = new HashMap<>();

    private Dispatcher () {

        loadTransports();

    }

    public static synchronized Dispatcher get ()
    {
        if (_instance == null)
        {
            _instance = new Dispatcher();
        }

        return _instance;
    }

    private void loadTransports ()
    {
        //TODO: discover available transports here, and do what we need to get them ready
    }

    public void register (String type, Class ptClass)
    {
        mTransportRegistery.put(type,ptClass);
    }

    public Transport getTransport (Context context, String type, Properties options)
    {
        Transport transport = null;

        Class ptClass = mTransportRegistery.get(type);
        if (ptClass != null)
        {
            try {
                transport = (Transport)ptClass.newInstance();
                if (transport != null)
                    transport.init(context, options);

                return transport;

            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

       return null;
    }

}
