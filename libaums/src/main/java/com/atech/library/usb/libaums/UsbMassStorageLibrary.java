package com.atech.library.usb.libaums;

import com.atech.library.usb.libaums.data.LibAumsException;
import lombok.Getter;
import org.usb4java.Context;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

public class UsbMassStorageLibrary {

    public static boolean LOAD_PARTITIONS = true; // loading partitions by default

    @Getter
    public static boolean libraryInitialized = false;

    @Getter
    static Context context;

    public static synchronized Context initLibrary() throws LibAumsException {
        if (!libraryInitialized) {
            context = new Context();

            // Initialize the libusb context
            int result = LibUsb.init(context);
            if (result < 0) {
                context = null;
                throw LibAumsException.createWithLibUsbException("Unable to initialize libusb", result);
            }

            libraryInitialized = true;
        }

        return context;
    }


    public static void disposeLibrary() {
        if (libraryInitialized) {
            // Deinitialize the libusb context
            LibUsb.exit(context);
            libraryInitialized = false;
            context = null;
        }
    }

}
