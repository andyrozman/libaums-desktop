package com.atech.library.usb.libaums.usb.device;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.usb4java.DescriptorUtils;
import org.usb4java.EndpointDescriptor;

import javax.usb.UsbEndpointDescriptor;
import java.nio.ByteBuffer;

@Data
@Accessors(fluent = true)
public class ATUsbEndpointDescriptor implements UsbEndpointDescriptor {

    byte bLength;

    byte bDescriptorType;

    byte bEndpointAddress;

    byte bmAttributes;

    short wMaxPacketSize;

    byte bInterval;

    byte bRefresh;

    byte bSynchAddress;

    ByteBuffer extra;

    int extraLength;
    
    
//    bLength                 7
//    bDescriptorType         5
//    bEndpointAddress     0x81  EP 1 IN
//    bmAttributes            2
//    Transfer Type            Bulk
//    Synch Type               None
//    Usage Type               Data
//    wMaxPacketSize     0x0040  1x 64 bytes
//    bInterval               1



    public void loadData(EndpointDescriptor endpointDescriptor) {
        bLength = endpointDescriptor.bLength();
        bDescriptorType = endpointDescriptor.bDescriptorType();
        bEndpointAddress = endpointDescriptor.bEndpointAddress();
        bmAttributes = endpointDescriptor.bmAttributes();
        wMaxPacketSize = endpointDescriptor.wMaxPacketSize();
        bInterval = endpointDescriptor.bInterval();
        bRefresh = endpointDescriptor.bRefresh();
        bSynchAddress = endpointDescriptor.bSynchAddress();
        extra = endpointDescriptor.extra();
        extraLength = endpointDescriptor.extraLength();
    }

    public String toLsUsbString(int pad) {

//    bLength                 7
//    bDescriptorType         5
//    bEndpointAddress     0x81  EP 1 IN
//    bmAttributes            2
//    Transfer Type            Bulk
//    Synch Type               None
//    Usage Type               Data
//    wMaxPacketSize     0x0040  1x 64 bytes
//    bInterval               1

        //StringUtils.move

        //UsbEndpointDescriptor

        return "XXATUsbDevice: " + toString();
    }


    public int getType() {
        // TODO getType what is this
        //DescriptorUtils.
        return 0;
    }

    public int getDirection() {
        // TODO getDirecton what is this?
        return 0;
    }
}
