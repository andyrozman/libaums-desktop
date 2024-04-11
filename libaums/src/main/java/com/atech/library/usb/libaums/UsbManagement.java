package com.atech.library.usb.libaums;

import com.atech.library.usb.libaums.device.ATUsbConfigurationDescriptor;
import com.atech.library.usb.libaums.device.ATUsbDevice;
import com.atech.library.usb.libaums.device.ATUsbDeviceDescriptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.usb4java.*;

import java.util.*;

/**
 * Created by andy on 10.04.2024.
 */
public class UsbManagement {

    public static List<ATUsbDevice> getFullDeviceList() {
        return getFullDeviceList(null);
    }


    public static List<ATUsbDevice> getDeviceList() {
        return getFullDeviceList(9); // 9 = Hubs
    }


    public static List<ATUsbDevice> getFullDeviceList(int...excludeClass) {
        // Create the libusb context
        Context context = new Context();

        //Set<Integer> collect = Arrays.stream(excludeClass)..collect(Collectors.toSet());
        Set<Integer> excludesForClass = new HashSet<>();

        for (int aClass : excludeClass) {
            excludesForClass.add(aClass);
        }

        List<ATUsbDevice> outList = new ArrayList<>();

        // Initialize the libusb context
        int result = LibUsb.init(context);
        if (result < 0)
        {
            throw new LibUsbException("Unable to initialize libusb", result);
        }

        // Read the USB device list
        DeviceList list = new DeviceList();
        result = LibUsb.getDeviceList(context, list);

        if (result < 0) {
            throw new LibUsbException("Unable to get device list", result);
        }

        try
        {
            // Iterate over all devices and list them
            for (Device device: list)
            {
                ATUsbDevice usbDevice = getDevice(device, true, null, excludesForClass);

//
//                UsbDevice usbDevice = new UsbDevice();
//                usbDevice.setAddress(LibUsb.getDeviceAddress(device));
//                usbDevice.setBusNumber(LibUsb.getBusNumber(device));
//                DeviceDescriptor descriptor = new DeviceDescriptor();
//                result = LibUsb.getDeviceDescriptor(device, descriptor);
//                if (result < 0)
//                {
//                    throw new LibUsbException(
//                            "Unable to read device descriptor", result);
//                }
//
//                if (excludesForClass.contains(Integer.valueOf(descriptor.bDeviceClass()))) {
//                    continue;
//                }
//
//                UsbDeviceDescriptor deviceDescriptor = new UsbDeviceDescriptor();
//                deviceDescriptor.loadData(descriptor);
//
//                DeviceHandle handle = new DeviceHandle();
//                result = LibUsb.open(device, handle);
//                if (result < 0)
//                {
//                    System.out.println(String.format("Unable to open device: %s. "
//                                    + "Continuing without device handle.",
//                            LibUsb.strError(result)));
//                    handle = null;
//                }
//
//                deviceDescriptor.loadDataWithHandle(descriptor, handle);
//
//
////                deviceDescriptor.usbClassName = DescriptorUtils.getUSBClassName(descriptor.bDeviceClass());
//
//                usbDevice.setDescriptor(deviceDescriptor);
//                System.out.format(
//                        "Bus %03d, Device %03d: Vendor %04x, Product %04x%n",
//                        usbDevice.getBusNumber(), usbDevice.getAddress(), descriptor.idVendor(),
//                        descriptor.idProduct());


                outList.add(usbDevice);
            }
        }
        finally
        {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }

        // Deinitialize the libusb context
        LibUsb.exit(context);

        return outList;

    }

    public static ATUsbDevice getDevice(Device device, boolean details, DeviceHandle handle, Set<Integer> excludesForClass) {

        ATUsbDevice usbDevice = new ATUsbDevice();
        usbDevice.address(LibUsb.getDeviceAddress(device));
        usbDevice.busNumber(LibUsb.getBusNumber(device));
        DeviceDescriptor descriptor = new DeviceDescriptor();
        int result = LibUsb.getDeviceDescriptor(device, descriptor);
        if (result < 0)
        {
            throw new LibUsbException(
                    "Unable to read device descriptor", result);
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
                System.out.println(String.format("Unable to open device: %s. "
                                + "Continuing without device handle.",
                        LibUsb.strError(result)));
                handle = null;
            }
        }

        deviceDescriptor.loadDataWithHandle(descriptor, handle);

        if (details) {
            // TODO

            // Dump port number if available
            int portNumber = LibUsb.getPortNumber(device);
            if (portNumber != 0) {
                System.out.println("Connected to port: " + portNumber);
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
            System.out.println("Speed: " + usbDevice.getSpeed());

            deviceDescriptor.configurationDescriptors(
                    getConfigurationDescriptors(device, deviceDescriptor.bNumConfigurations(), handle));

        }

        usbDevice.descriptor(deviceDescriptor);
        System.out.format(
                "Bus %03d, Device %03d: Vendor %04x, Product %04x%n",
                usbDevice.busNumber(), usbDevice.address(), descriptor.idVendor(),
                descriptor.idProduct());

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
            if (result < 0)
            {
                throw new LibUsbException("Unable to read config descriptor",
                        result);
            }
            try
            {
                System.out.println(descriptor.dump().replaceAll("(?m)^",
                        "  "));
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




    public void getUsbDeviceDetails(UsbDeviceSettings usbDeviceSettings) {

        // Initialize the libusb context
        int result = LibUsb.init(null);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to initialize libusb", result);
        }

        // Open test device (Samsung Galaxy Nexus)
        DeviceHandle handle = LibUsb.openDeviceWithVidPid(null, usbDeviceSettings.getVendorId(),
                usbDeviceSettings.getProductId());
        if (handle == null)
        {
            System.err.println("Test device not found.");
            System.exit(1);
        }

        Device device = LibUsb.getDevice(handle);

        // TODO discover device


        // Close the device
        LibUsb.close(handle);

        // Deinitialize the libusb context
        LibUsb.exit(null);

    }

    public void openDevice(UsbDeviceSettings usbDeviceSettings) {

    }


    public static void main(String[] args) {

        UsbManagement usbManagement = new UsbManagement();

        List<ATUsbDevice> fullDeviceList = usbManagement.getDeviceList();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //System.out.println(gson.toJson(fullDeviceList));

        System.out.println("test output: ==================================================");

        ATUsbDevice atUsbDevice = fullDeviceList.get(0);

        System.out.println(atUsbDevice.toLsUsbString());

    }


}
