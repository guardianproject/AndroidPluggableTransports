package info.pluggabletransports.dispatch;

public class Dispatcher implements DispatchConstants {

    private static Dispatcher _instance;

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

    public Transport getTransport (String type)
    {
        Transport transport = null;
        return transport;
    }

}
