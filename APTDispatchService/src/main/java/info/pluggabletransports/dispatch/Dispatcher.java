package info.pluggabletransports.dispatch;

public class Dispatcher {

    private static Dispatcher _instance;

    private Dispatcher () {}

    public static synchronized Dispatcher get ()
    {
        if (_instance == null)
        {
            _instance = new Dispatcher();
        }

        return _instance;
    }


}
