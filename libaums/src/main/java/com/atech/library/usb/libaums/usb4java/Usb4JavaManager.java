package com.atech.library.usb.libaums.usb4java;

import com.atech.library.usb.libaums.UsbMassStorageLibrary;
import com.atech.library.usb.libaums.data.LibAumsException;
import com.atech.library.usb.libaums.usb.device.ATUsbConfigurationDescriptor;
import com.atech.library.usb.libaums.usb.device.ATUsbDevice;
import com.atech.library.usb.libaums.usb.device.ATUsbDeviceDescriptor;
import lombok.extern.slf4j.Slf4j;
import org.usb4java.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class Usb4JavaManager {

    /**
     * getFullDeviceList - returns list of all devices, including hubs
     * @return list of devices
     */
    public static List<ATUsbDevice> getFullDeviceList() throws LibAumsException {
        return getFullDeviceList(null);
    }


    /**
     * getDeviceList - returns list of all real devices (excluding hubs)
     *
     * @return list of real devices
     */
    public static List<ATUsbDevice> getDeviceList() throws LibAumsException {
        return getFullDeviceList(9); // 9 = Hubs
    }


    /**
     * getDeviceList - returns list of all devices, with excludeClass list
     *
     * @param excludeClass list of exclude
     *
     * @return list of real devices
     */
    public static List<ATUsbDevice> getFullDeviceList(int...excludeClass) throws LibAumsException {

        UsbMassStorageLibrary.initLibrary();

        Set<Integer> excludesForClass = new HashSet<>();

        for (int aClass : excludeClass) {
            excludesForClass.add(aClass);
        }

        List<ATUsbDevice> outList = new ArrayList<>();

        // Read the USB device list
        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(UsbMassStorageLibrary.getContext(), list);

        if (result < 0) {
            throw LibAumsException.createWithLibUsbException("Unable to get device list", result);
        }

        try {
            // Iterate over all devices and list them
            for (Device device: list) {
                ATUsbDevice usbDevice = getDevice(device, true, null, excludesForClass);
                if (usbDevice!=null)
                    outList.add(usbDevice);
            }
        } finally {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }

        return outList;
    }

    /**
     * Get Device
     * @param device Device instance
     * @param details do we need to retrive details
     * @param handle DeviceHandle (can be null)
     * @param excludesForClass excludeClass Set
     * @return
     */
    public static ATUsbDevice getDevice(Device device, boolean details, DeviceHandle handle, Set<Integer> excludesForClass) {
        ATUsbDevice usbDevice = new ATUsbDevice();
        usbDevice.address(LibUsb.getDeviceAddress(device));
        usbDevice.busNumber(LibUsb.getBusNumber(device));
        DeviceDescriptor descriptor = new DeviceDescriptor();
        int result = LibUsb.getDeviceDescriptor(device, descriptor);

        if (result < 0) {
            throw new LibUsbException("Unable to read device descriptor", result);
        }

        if (excludesForClass.contains(Integer.valueOf(descriptor.bDeviceClass()))) {
            return null;
        }

        ATUsbDeviceDescriptor deviceDescriptor = new ATUsbDeviceDescriptor();
        deviceDescriptor.loadData(descriptor);

        if (handle == null) {
            handle = new DeviceHandle();
            result = LibUsb.open(device, handle);
            if (result < 0) {
                log.warn("Unable to open device: {}. "
                                + "Continuing without device handle.",
                        LibUsb.strError(result));
                handle = null;
            }
        }

        deviceDescriptor.loadDataWithHandle(descriptor, handle);

        if (details) {
            // Dump port number if available
            int portNumber = LibUsb.getPortNumber(device);
            if (portNumber != 0) {
                log.info("Connected to port: " + portNumber);
                usbDevice.portNumber(portNumber);
            }

            // Dump parent device if available
//            final Device parent = LibUsb.getParent(device);
//            if (parent != null)
//            {
//                final int parentAddress = LibUsb.getDeviceAddress(parent);
//                final int parentBusNumber = LibUsb.getBusNumber(parent);
//                System.out.println(String.format("Parent: %03d/%03d",
//                        parentBusNumber, parentAddress));
//            }

            // Dump the device speed
            int speed = LibUsb.getDeviceSpeed(device);
            usbDevice.speed(speed);
            usbDevice.speedString(DescriptorUtils.getSpeedName(speed));
            log.debug("Speed: " + usbDevice.getSpeed());

            deviceDescriptor.configurationDescriptors(
                    getConfigurationDescriptors(device, deviceDescriptor.bNumConfigurations(), handle));
        }

        usbDevice.descriptor(deviceDescriptor);
        log.info(String.format("Bus %03d, Device %03d: Vendor %04x, Product %04x%n",
                usbDevice.busNumber(), usbDevice.address(), descriptor.idVendor(),
                descriptor.idProduct()));

        return usbDevice;
    }

    public static List<ATUsbConfigurationDescriptor> getConfigurationDescriptors(final Device device,
                                                                                 final int numConfigurations,
                                                                                 DeviceHandle handle)
    {
        List<ATUsbConfigurationDescriptor> list = new ArrayList<>();
        for (byte i = 0; i < numConfigurations; i += 1)
        {
            final ConfigDescriptor descriptor = new ConfigDescriptor();
            final int result = LibUsb.getConfigDescriptor(device, i, descriptor);
            if (result < 0) {
                throw new LibUsbException("Unable to read config descriptor",
                        result);
            }

            try
            {
//                System.out.println(descriptor.dump().replaceAll("(?m)^",
//                        "  "));
                ATUsbConfigurationDescriptor usbConfigDescriptor = new ATUsbConfigurationDescriptor();
                usbConfigDescriptor.loadData(descriptor);

                list.add(usbConfigDescriptor);
            }
            finally
            {
                // Ensure that the config descriptor is freed
                LibUsb.freeConfigDescriptor(descriptor);
            }
        }
        return list;
    }





//    static UsbServices services;
//
//    public static List<UsbDevice> getUsbDevices() throws UsbException {
//        if (services==null) {
//             services = UsbHostManager.getUsbServices();
//        }
//
//        return discoverDevices(services.getRootUsbHub());
//    }
//
//    public static List<UsbDevice> discoverDevices(UsbHub usbHub) {
//        List<UsbDevice> deviceList = new ArrayList<>();
//        for (Object attachedUsbDevice : usbHub.getAttachedUsbDevices()) {
//            UsbDevice device = (UsbDevice) attachedUsbDevice;
//
//            if (device.isUsbHub()) {
//                deviceList.addAll(discoverDevices((UsbHub)device));
//            } else {
//                deviceList.add(device);
//            }
//        }
//        return deviceList;
//    }


}
