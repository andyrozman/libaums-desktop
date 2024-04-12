package com.atech.library.usb.libaums.data;

import org.usb4java.LibUsbException;

import java.io.IOException;

/**
 * Created by andy on 11.04.2024.
 */
public class LibAumsException extends Exception {

    public LibAumsException(String message) {
        super(message);
    }

    public LibAumsException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public static LibAumsException createWithLibUsbException(String message, int result) {
        return new LibAumsException(message, new LibUsbException(message, result));
    }

    public static LibAumsException createWithIOException(String message) {
        return new LibAumsException(message, new IOException(message));
    }

}
