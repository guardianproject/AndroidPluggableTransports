package info.pluggabletransports.dispatch.compat;

import android.annotation.TargetApi;
import android.os.Build;
import android.system.ErrnoException;
import android.util.Log;

import java.lang.reflect.Method;

public class OsCompat {

    private static final String TAG = "OsCompat";

    public static String getenv(String name) {
        if (Build.VERSION.SDK_INT >= 21) {
            return getenvOs(name);
        } else if (Build.VERSION.SDK_INT >= 18) {
            return getenvLibcore(name);
        } else {
            return getenvReflection(name);
        }
    }

    public static void setenv(String name, String value) {
        setenv(name, value, true);
    }

    public static void setenv(String name, String value, boolean overwrite) {
        if (Build.VERSION.SDK_INT >= 21) {
            setenvOs(name, value, overwrite);
        } else if (Build.VERSION.SDK_INT >= 18) {
            setenvLibcore(name, value, overwrite);
        } else {
            setenvReflection(name, value, overwrite);
        }
    }

    public static void unsetenv(String name) {
        if (Build.VERSION.SDK_INT >= 21) {
            unsetenvOs(name);
        } else if (Build.VERSION.SDK_INT >= 18) {
            unsetenvLibcore(name);
        } else {
            unsetenvReflection(name);
        }
    }

    /**
     * Moved into a separate class rather than just a method, so that phones
     * running older than {@link Build.VERSION_CODES#LOLLIPOP android-21} will
     * not attempt to load this class at runtime. Otherwise, using the
     * {@link android.system.Os#setenv} method will cause  a {@code VerifyError}
     * to be thrown at runtime when this {@code OsCompat} class is first used.
     */
    private static class AndroidSystemOs21 {

        @TargetApi(21)
        String getenv(String name) {
            return android.system.Os.getenv(name);
        }

        @TargetApi(21)
        void setenv(String name, String value, boolean overwrite) {
            try {
                android.system.Os.setenv(name, value, overwrite);
            } catch (ErrnoException e) {
                e.printStackTrace();
            }
        }

        @TargetApi(21)
        void unsetenv(String name) {
            try {
                android.system.Os.unsetenv(name);
            } catch (ErrnoException e) {
                e.printStackTrace();
            }
        }
    }

    @TargetApi(21)
    private static String getenvOs(String name) {
        return new AndroidSystemOs21().getenv(name);
    }

    private static String getenvLibcore(String name) {
        try {
            Object os = Class.forName("libcore.io.Libcore").getField("os").get(null);
            Method getenv = os.getClass().getMethod("getenv", String.class);
            return (String) getenv.invoke(os, name);
        } catch (Exception e) {
            // Should catch more specific exceptions than just "Exception" here, but there are
            // some which come from libcore.io.Libcore, which we don't have access to at compile time.
            Log.e(TAG, "Could not getenv " + name);
            e.printStackTrace();
        }
        return null;
    }

    private static String getenvReflection(String name) {
        // TODO
        throw new UnsupportedOperationException("implement me! ");
    }

    @TargetApi(21)
    private static void setenvOs(String name, String value, boolean overwrite) {
        new AndroidSystemOs21().setenv(name, value, overwrite);
    }

    private static void setenvLibcore(String name, String value, boolean overwrite) {
        try {
            Object os = Class.forName("libcore.io.Libcore").getField("os").get(null);
            Method setenv = os.getClass().getMethod("setenv", String.class, String.class, Boolean.class);
            setenv.invoke(os, name, value, overwrite);
        } catch (Exception e) {
            // Should catch more specific exceptions than just "Exception" here, but there are
            // some which come from libcore.io.Libcore, which we don't have access to at compile time.
            Log.e(TAG, "Could not setenv " + name + " to " + value);
            e.printStackTrace();
        }
    }

    private static void setenvReflection(String name, String value, boolean overwrite) {
        // TODO https://stackoverflow.com/questions/318239/how-do-i-set-environment-variables-from-java
        throw new UnsupportedOperationException("implement me!");
    }

    @TargetApi(21)
    private static void unsetenvOs(String name) {
        new AndroidSystemOs21().unsetenv(name);
    }

    private static String unsetenvLibcore(String name) {
        try {
            Object os = Class.forName("libcore.io.Libcore").getField("os").get(null);
            Method unsetenv = os.getClass().getMethod("unsetenv", String.class);
            return (String) unsetenv.invoke(os, name);
        } catch (Exception e) {
            // Should catch more specific exceptions than just "Exception" here, but there are
            // some which come from libcore.io.Libcore, which we don't have access to at compile time.
            Log.e(TAG, "Could not unsetenv " + name);
            e.printStackTrace();
        }
        return null;
    }

    private static String unsetenvReflection(String name) {
        // TODO
        throw new UnsupportedOperationException("implement me! ");
    }

}
