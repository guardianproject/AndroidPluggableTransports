package libcore.io;

import android.system.ErrnoException;

public interface Os {
    public String getenv(String name);

    public void setenv(String name, String value, boolean overwrite) throws ErrnoException;

    public void unsetenv(String name) throws ErrnoException;
}