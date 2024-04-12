package com.atech.library.usb.libaums;

import com.atech.library.usb.libaums.usb.device.ATUsbDevice;
import com.atech.library.usb.libaums.usb4java.Usb4JavaManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by andy on 12.04.2024.
 */
public class UsbMassStorageLibraryTest {

    @Test
    public void getDeviceList() throws Exception{
        List<ATUsbDevice> deviceList = Usb4JavaManager.getDeviceList();
        Assert.assertNotNull(deviceList);
        UsbMassStorageLibrary.disposeLibrary();
    }


}