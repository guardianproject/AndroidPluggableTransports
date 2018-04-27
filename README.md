# Android Pluggable Transports Dispatch Service

This project is for implementing and providing access to transports compliant with the Pluggable Transports 2.x specification to any Android app that wishes to utilize them. It is built from knowledge and code initially developed through the work on Orbot (Tor for Android) use of Pluggable Transports, VPN features, and inter-application APIs (NetCipher). This project also will utilize the Operator Foundation's Shapeshifter-Transports library in order to provide access to common pluggable transports implemented in Go.

* Pluggable Transports site: http://pluggabletransports.info/
* PT 2.x draft spec: http://tinyurl.com/ptv2draft
* Shapeshifter-Transports: https://github.com/OperatorFoundation/shapeshifter-transports
* Orbot: Tor for Android: https://github.com/n8fr8/orbot

Here is a basic example of how to use the Dispatcher, to retrieve a Transport instance, which then can be used to make a connection to a specific "bridge" endpoint:

        Transport transport = Dispatcher.get().getTransport(this, type, options);
        if (transport != null)
        {
            Connection conn = transport.connect(bridgeAddress);
            //now use the connection, either as a proxy, or to read and write bytes directly
            if (conn.getLocalAddress() != null && conn.getLocalPort() != -1)
                setSocksProxy (conn.getLocalAddress(), conn.getLocalPort());
        }
  
  
In some cases, the Connection instance returned can be used to setup a general purpose SOCKS proxy. In other cases, you will have to use the read() and write() methods of the Connection instance to transmit data over the transport.

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write("GET https://somewebsite.org/TheProject.html HTTP/1.0".getBytes());
        conn.write(baos.toByteArray());

        byte[] buffer = new byte[1024*64];
        int read = conn.read(buffer,0,buffer.length);
        String response = new String(buffer);

The core library is the 'APTDispatchLibrary', but you also need to include a specific library for the transports you wish to bundle and utilize in your app. For instance 'APTDispatchLibrary-MeekObfs4-Full' would include the obfs4proxy library for x86 and arm devices in both 32 and 64-bit. Alternatively, you can just include ''APTDispatchLibrary-MeekObfs4-ARM' for a smaller 'armeabi' only build, reducing the size from 9MB to 2MB.

This project also demonstrates how to implement and register new Transport types, through the 'APTDispatchLibrary-SampleTransport' project. A Pluggable Transport can be implemented entirely in Java, or utilize any other language supported by the Android SDK or NDK. Transport register by making this simple call

    {
        Dispatcher.get().register("sample",getClass());
    }

This then allows any app using the library to get an instance of the transport like so:

        SampleTransport transport = (SampleTransport)Dispatcher.get().getTransport(this, "sample", options);
        
        


Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Step 2. Add the dependency

	dependencies {
	        compile 'com.github.guardianproject:AndroidPluggableTransportsDispatcher:-SNAPSHOT'
	}


