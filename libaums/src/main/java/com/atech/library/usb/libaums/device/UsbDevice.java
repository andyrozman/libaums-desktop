package com.atech.library.usb.libaums.device;

import com.atech.library.usb.libaums.device.UsbDeviceDescriptor;
import lombok.Data;

/**
 * Created by andy on 10.04.2024.
 */
@Data
public class UsbDevice {
    int busNumber;
    int address;

    Integer portNumber;
    int iSpeed;
    String speed;

    UsbDeviceDescriptor descriptor;

    public String getBusNumberString() {
        return String.format("%03d", busNumber);
    }

    public String getAddressString() {
        return String.format("%03d", address);
    }

}
