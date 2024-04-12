package com.atech.library.usb.libaums;

import com.atech.library.usb.libaums.data.UsbMassStorageDeviceConfig;
import com.atech.library.usb.libaums.usb.device.ATUsbDevice;
import com.atech.library.usb.libaums.usb4java.Usb4JavaManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.usb4java.*;

import java.util.*;

/**
 * Created by andy on 10.04.2024.
 */
@Slf4j
public class UsbManagement {





    public void getUsbDeviceDetails(UsbMassStorageDeviceConfig usbDeviceSettings) {

        // Initialize the libusb context
        int result = LibUsb.init(null);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to initialize libusb", result);
        }

        // Open test device (Samsung Galaxy Nexus)
        DeviceHandle handle = LibUsb.openDeviceWithVidPid(null, usbDeviceSettings.getVendorId(),
                usbDeviceSettings.getProductId());
        if (handle == null)
        {
            System.err.println("Test device not found.");
            System.exit(1);
        }

        Device device = LibUsb.getDevice(handle);

        // TODO discover device


        // Close the device
        LibUsb.close(handle);

        // Deinitialize the libusb context
        LibUsb.exit(null);

    }

    public void openDevice(UsbMassStorageDeviceConfig usbDeviceSettings) {

    }


    @SneakyThrows
    public static void main(String[] args) {

        //Usb4JavaManager usbManagement = new Usb4JavaManager();

        List<ATUsbDevice> fullDeviceList = Usb4JavaManager.getDeviceList();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //System.out.println(gson.toJson(fullDeviceList));

        System.out.println("test output: ==================================================");

        ATUsbDevice atUsbDevice = fullDeviceList.get(0);

        System.out.println(atUsbDevice.toLsUsbString());

    }


}
