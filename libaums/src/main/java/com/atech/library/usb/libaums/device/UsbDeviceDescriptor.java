package com.atech.library.usb.libaums.device;

import lombok.Data;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import java.util.List;

/**
 * Created by andy on 10.04.2024.
 */
@Data
public class UsbDeviceDescriptor {

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
    List<UsbConfigurationDescriptor> configurationDescriptors;

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
    }
}
