package com.atech.library.usb.libaums.device;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.usb4java.DescriptorUtils;
import org.usb4java.Interface;
import org.usb4java.InterfaceDescriptor;

import javax.usb.UsbInterface;
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
