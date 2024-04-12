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
//        for (ATUsbInterface anInterface : interfaces) {
//            stringBuilder.append(anInterface.toLsUsbString(0));
//        }
        return stringBuilder.toString();
    }

}
