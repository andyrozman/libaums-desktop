package com.atech.library.usb.libaums.usb4java;

import javax.usb.*;
import java.util.ArrayList;
import java.util.List;

public class Usb4JavaManager {

    static UsbServices services;

    public static List<UsbDevice> getUsbDevices() throws UsbException {
        if (services==null) {
             services = UsbHostManager.getUsbServices();
        }

        return discoverDevices(services.getRootUsbHub());
    }

    public static List<UsbDevice> discoverDevices(UsbHub usbHub) {
        List<UsbDevice> deviceList = new ArrayList<>();
        for (Object attachedUsbDevice : usbHub.getAttachedUsbDevices()) {
            UsbDevice device = (UsbDevice) attachedUsbDevice;

            if (device.isUsbHub()) {
                deviceList.addAll(discoverDevices((UsbHub)device));
            } else {
                deviceList.add(device);
            }
        }
        return deviceList;
    }


}
