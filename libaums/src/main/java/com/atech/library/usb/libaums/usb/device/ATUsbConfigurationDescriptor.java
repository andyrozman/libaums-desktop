package com.atech.library.usb.libaums.usb.device;

import lombok.Data;
import lombok.experimental.Accessors;
import org.usb4java.ConfigDescriptor;
import org.usb4java.Interface;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy on 10.04.2024.
 */
@Data
@Accessors(fluent = true)
public class ATUsbConfigurationDescriptor implements javax.usb.UsbConfigurationDescriptor {

    byte bLength;

    byte bDescriptorType;

    short wTotalLength;

    byte bNumInterfaces;

    byte bConfigurationValue;

    byte iConfiguration;

    byte bmAttributes;

    byte bMaxPower;

    //public native Interface[] iface();
    List<ATUsbInterface> interfaces;

    ByteBuffer extra;

    int extraLength;

    String description;

    public void loadData(ConfigDescriptor descriptor) {
        bLength = descriptor.bLength();
        bDescriptorType = descriptor.bDescriptorType();
        wTotalLength = descriptor.wTotalLength();
        bNumInterfaces = descriptor.bNumInterfaces();
        bConfigurationValue = descriptor.bConfigurationValue();
        iConfiguration = descriptor.iConfiguration();
        bmAttributes = descriptor.bmAttributes();
        bMaxPower =  descriptor.bMaxPower();

        description = descriptor.dump();

        Interface[] iface = descriptor.iface();
        interfaces = new ArrayList<>();

        for (Interface anInterface : iface) {
            ATUsbInterface atUsbInterfaceDescriptor = new ATUsbInterface();
            atUsbInterfaceDescriptor.loadData(anInterface);
            interfaces.add(atUsbInterfaceDescriptor);
        }

        extra = descriptor.extra();
        extraLength = descriptor.extraLength();
    }

    public String toLsUsbString(int pad) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(description);
        for (ATUsbInterface anInterface : interfaces) {
            stringBuilder.append(anInterface.toLsUsbString(0));
        }
        return stringBuilder.toString();
    }

}
