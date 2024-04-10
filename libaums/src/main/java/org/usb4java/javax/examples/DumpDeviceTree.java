/*
 * Copyright (C) 2013 Klaus Reimer <k@ailis.de>
 * See LICENSE.txt for licensing information.
 */

package org.usb4java.javax.examples;

import lombok.SneakyThrows;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.usb.*;

/**
 * Dumps a tree of all found USB devices.
 *
 * @author Klaus Reimer <k@ailis.de>
 */
public class DumpDeviceTree
{
    /**
     * Dumps the specified device and its sub devices.
     *
     * @param device
     *            The USB device to dump.
     * @param level
     *            The indentation level.
     */
    @SneakyThrows
    public static void dump(UsbDevice device, int level)
    {
        for (int i = 0; i < level; i += 1)
            System.out.print("  ");
        System.out.println(device.toString() + " " + getUsbName(device) );
        if (device.isUsbHub())
        {
            final UsbHub hub = (UsbHub) device;
            for (UsbDevice child: (List<UsbDevice>) hub.getAttachedUsbDevices())
            {
                dump(child, level + 1);
            }
        }
    }

    private static String getUsbName(final UsbDevice device)
            //throws UnsupportedEncodingException, UsbException
    {
        try {
            // Read the string descriptor indices from the device descriptor.
            // If they are missing then ignore the device.
            final UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
            final byte iManufacturer = desc.iManufacturer();
            final byte iProduct = desc.iProduct();
            if (iManufacturer == 0 || iProduct == 0) return "-";

            // if device.is
            // Dump the device name
            return device.getString(iManufacturer) + " "
                    + device.getString(iProduct);
        } catch(Exception ex) {
            ex.printStackTrace();
            return "x";
        }
    }

    /**
     * Main method.
     *
     * @param args
     *            Command-line arguments (Ignored).
     * @throws UsbException
     *             When USB communication fails.
     */
    public static void main(String[] args) throws UsbException
    {
        UsbServices services = UsbHostManager.getUsbServices();
        dump(services.getRootUsbHub(), 0);
    }
}