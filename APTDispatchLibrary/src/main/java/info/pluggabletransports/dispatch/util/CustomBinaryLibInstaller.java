package info.pluggabletransports.dispatch.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeoutException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static info.pluggabletransports.dispatch.DispatchConstants.FILE_WRITE_BUFFER_SIZE;


public class CustomBinaryLibInstaller {


    File installFolder;
    Context context;

    File fileBinary;

    public CustomBinaryLibInstaller(Context context, File installFolder)
    {
        this.installFolder = installFolder;
        this.context = context;
    }

    public File getFileBinary ()
    {
        return fileBinary;
    }


    //
    /*
     * Extract the Tor resources from the APK file using ZIP
     *
     * @File path to the Tor executable
     */
    public File installResources (String binaryName) throws IOException, TimeoutException
    {

        fileBinary = new File(installFolder, binaryName);

        if (!installFolder.exists())
            installFolder.mkdirs();


        File fileNativeDir = new File(getNativeLibraryDir(context));
        fileBinary = new File(fileNativeDir,binaryName + ".so");

        if (fileBinary.exists())
        {
            if (fileBinary.canExecute())
                return fileBinary;
            else
            {
                setExecutable(fileBinary);

                if (fileBinary.canExecute())
                    return fileBinary;
            }
        }

        if (fileBinary.exists()) {
            InputStream is = new FileInputStream(fileBinary);
            streamToFile(is, fileBinary, false, true);
            setExecutable(fileBinary);

            if (fileBinary.exists() && fileBinary.canExecute())
                return fileBinary;
        }

        //let's try another approach
        fileBinary = new File(installFolder, binaryName);
        //fileTor = NativeLoader.initNativeLibs(context,fileTor);
        CustomNativeLoader.initNativeLibs(context,fileBinary);

        setExecutable(fileBinary);

        if (fileBinary != null && fileBinary.exists() && fileBinary.canExecute())
            return fileBinary;

        return null;
    }


    // Return Full path to the directory where native JNI libraries are stored.
    private static String getNativeLibraryDir(Context context) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        return appInfo.nativeLibraryDir;
    }




    /*
     * Write the inputstream contents to the file
     */
    private static boolean streamToFile(InputStream stm, File outFile, boolean append, boolean zip) throws IOException

    {
        byte[] buffer = new byte[FILE_WRITE_BUFFER_SIZE];

        int bytecount;

        OutputStream stmOut = new FileOutputStream(outFile.getAbsolutePath(), append);
        ZipInputStream zis = null;

        if (zip)
        {
            zis = new ZipInputStream(stm);
            ZipEntry ze = zis.getNextEntry();
            stm = zis;

        }

        while ((bytecount = stm.read(buffer)) > 0)
        {

            stmOut.write(buffer, 0, bytecount);

        }

        stmOut.close();
        stm.close();

        if (zis != null)
            zis.close();


        return true;

    }



    private void setExecutable(File fileBin) {
        fileBin.setReadable(true);
        fileBin.setExecutable(true);
        fileBin.setWritable(false);
        fileBin.setWritable(true, true);
    }

    private static File[] listf(String directoryName) {

        // .............list file
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();

        if (fList != null)
            for (File file : fList) {
                if (file.isFile()) {
                    Log.d("listf",file.getAbsolutePath());
                } else if (file.isDirectory()) {
                    listf(file.getAbsolutePath());
                }
            }

        return fList;
    }
}

