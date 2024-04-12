/*
 * (C) Copyright 2024 Andy Rozman <andy.rozman@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.atech.library.usb.libaums.usb.device;

import lombok.Data;
import lombok.experimental.Accessors;
import org.usb4java.EndpointDescriptor;
import org.usb4java.LibUsb;

import javax.usb.UsbEndpointDescriptor;
import java.nio.ByteBuffer;

import static com.atech.library.usb.libaums.data.UsbConstants.USB_DIR_IN;
import static com.atech.library.usb.libaums.data.UsbConstants.USB_DIR_OUT;

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


    public int getTransferType() {
        return bmAttributes & LibUsb.TRANSFER_TYPE_MASK;
    }

    public int getDirection() {
        return ((bEndpointAddress & LibUsb.ENDPOINT_IN) == 0) ? USB_DIR_OUT : USB_DIR_IN;
    }
}
