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
import org.apache.commons.lang3.StringUtils;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import java.util.List;

/**
 * Created by andy on 10.04.2024.
 */
@Data
@Accessors(fluent = true)
public class ATUsbDeviceDescriptor implements javax.usb.UsbDeviceDescriptor {

    byte bLength;
    byte bDescriptorType;
    short bcdUSB;
    byte bDeviceClass;
    byte bDeviceSubClass;
    byte bDeviceProtocol;
    byte bMaxPacketSize0;
    short idVendor;
    String vendor;
    short idProduct;
    short bcdDevice;
    byte iManufacturer;
    String manufacturer;
    byte iProduct;
    String product;
    byte iSerialNumber;
    String serialNumber;
    byte bNumConfigurations;

    String description;
    List<ATUsbConfigurationDescriptor> configurationDescriptors;

    public String getVendorHex() {
        return String.format("%04x", idVendor);
    }

    public String getProductHex() {
        return String.format("%04x", idProduct);
    }

    public void loadData(DeviceDescriptor descriptor) {
        this.bLength = descriptor.bLength();
        this.bDescriptorType = descriptor.bDescriptorType();
        this.bcdUSB = descriptor.bcdUSB();
        this.bDeviceClass = descriptor.bDeviceClass();
        this.bDeviceSubClass = descriptor.bDeviceSubClass();
        this.bDeviceProtocol = descriptor.bDeviceProtocol();
        this.bMaxPacketSize0 = descriptor.bMaxPacketSize0();
        this.idVendor = descriptor.idVendor();
        this.idProduct = descriptor.idProduct();
        this.bcdDevice = descriptor.bcdDevice();
        this.iManufacturer = descriptor.iManufacturer();
        this.iProduct = descriptor.iProduct();
        this.iSerialNumber = descriptor.iSerialNumber();
        this.bNumConfigurations = descriptor.bNumConfigurations();
    }

    public void loadDataWithHandle(DeviceDescriptor descriptor, DeviceHandle handle) {
        this.description = descriptor.dump(handle);

        this.manufacturer = LibUsb.getStringDescriptor(handle, descriptor.iManufacturer());
        this.product = LibUsb.getStringDescriptor(handle, descriptor.iProduct());
        this.serialNumber = LibUsb.getStringDescriptor(handle, descriptor.iSerialNumber());
        //this.vendor = LibUsb.getStringDescriptor(handle, descriptor.idVendor());

        //descriptor.



    }


    public String toLsUsbString(int pad) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.repeat(" ", pad+2) + description);
        for (ATUsbConfigurationDescriptor configurationDescriptor : configurationDescriptors) {
            stringBuilder.append(StringUtils.repeat(" ", pad+2) + configurationDescriptor.toLsUsbString(pad+2));
        }
        return stringBuilder.toString();
    }

}
