package com.atech.library.usb.libaums.device;

import lombok.Data;
import org.usb4java.ConfigDescriptor;
import org.usb4java.DescriptorUtils;
import org.usb4java.Interface;

import java.nio.ByteBuffer;

/**
 * Created by andy on 10.04.2024.
 */
@Data
public class UsbConfigurationDescriptor {

    byte bLength;

    byte bDescriptorType;

    short wTotalLength;

    byte bNumInterfaces;

    byte bConfigurationValue;

    byte iConfiguration;

    byte bmAttributes;

    byte bMaxPower;

    //public native Interface[] iface();

    ByteBuffer extra;

    int extraLength;

    public void loadData(ConfigDescriptor descriptor) {
        bLength = descriptor.bLength();
        bDescriptorType = descriptor.bDescriptorType();
        wTotalLength = descriptor.wTotalLength();
        bNumInterfaces = descriptor.bNumInterfaces();
        bConfigurationValue = descriptor.bConfigurationValue();
        iConfiguration = descriptor.iConfiguration();
        bmAttributes = descriptor.bmAttributes();
        bMaxPower =  descriptor.bMaxPower();

        Interface[] iface = descriptor.iface();

        for (Interface anInterface : iface) {

        }


        //public native Interface[] iface();

        extra = descriptor.extra();
        extraLength = descriptor.extraLength();

    }




//    bLength                 9
//    bDescriptorType         2
//    wTotalLength       0x0020
//    bNumInterfaces          1
//    bConfigurationValue     1
//    iConfiguration          5 Mass Storage Device
//    bmAttributes         0xc0
//    Self Powered
//    MaxPower              100mA

}
