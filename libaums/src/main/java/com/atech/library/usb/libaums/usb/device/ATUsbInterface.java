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
import org.usb4java.Interface;
import org.usb4java.InterfaceDescriptor;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(fluent = true)
public class ATUsbInterface  {

    //public native InterfaceDescriptor[] altsetting();

    int padNumber = 4;

    List<ATUsbInterfaceDescriptor> altsettings;

    int numAltsetting;

    public void loadData(Interface interfaceInstance) {
        this.numAltsetting = interfaceInstance.numAltsetting();

        InterfaceDescriptor[] altsettingI = interfaceInstance.altsetting();

        altsettings = new ArrayList<>();

        for (InterfaceDescriptor interfaceDescriptor : altsettingI) {
            ATUsbInterfaceDescriptor atInterfaceDescriptor = new ATUsbInterfaceDescriptor(null);
            atInterfaceDescriptor.loadData(interfaceDescriptor);
            altsettings.add(atInterfaceDescriptor);
        }
    }

    public String toLsUsbString(int pad) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(StringUtils.leftPad("numAltSetting: " + numAltsetting, 4));


        return "Interface: " + toString();

        //StringUtils.leftPad()
    }




}
