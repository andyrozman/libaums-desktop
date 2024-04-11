package com.atech.library.usb.libaums.device;

import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.usb.*;
import javax.usb.event.UsbDeviceListener;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by andy on 10.04.2024.
 *
 * This is sort of mix between javax.usb.UsbDevice and android UsbDevice, with implementation only when needed, since when working with actual
 * devices we won't be using this, but we will write/read through UsbCommunication interface
 *
 */
@Data
@Accessors(fluent = true)
@Slf4j
public class ATUsbDevice implements javax.usb.UsbDevice {
    int busNumber;
    int address;

    Integer portNumber;
    int speed;
    String speedString;

    ATUsbDeviceDescriptor descriptor;

    public String getBusNumberString() {
        return String.format("%03d", busNumber);
    }

    public String getAddressString() {
        return String.format("%03d", address);
    }

    @Override
    public UsbPort getParentUsbPort() throws UsbDisconnectedException {
        return null;
    }

    @Override
    public boolean isUsbHub() {
        return false;
    }

    @Override
    public String getManufacturerString() throws UsbException, UnsupportedEncodingException, UsbDisconnectedException {
        return this.descriptor.manufacturer();
    }

//    private ATUsbDeviceDescriptor getMyDescriptor() {
//        return (ATUsbDeviceDescriptor)this.getUsbDeviceDescriptor();
//    }

    @Override
    public String getSerialNumberString() throws UsbException, UnsupportedEncodingException, UsbDisconnectedException {
        return this.descriptor.serialNumber;
    }

    @Override
    public String getProductString() throws UsbException, UnsupportedEncodingException, UsbDisconnectedException {
        return this.descriptor.product;
    }

    @Override
    public Object getSpeed() {
        return null;
    }

    @Override
    public List getUsbConfigurations() {
        return null;
    }

    @Override
    public UsbConfiguration getUsbConfiguration(byte b) {
        return null;
    }

    @Override
    public boolean containsUsbConfiguration(byte b) {
        return false;
    }

    @Override
    public byte getActiveUsbConfigurationNumber() {
        return 0;
    }

    @Override
    public UsbConfiguration getActiveUsbConfiguration() {
        return null;
    }

    @Override
    public boolean isConfigured() {
        return false;
    }

    @Override
    public ATUsbDeviceDescriptor getUsbDeviceDescriptor() {
        return this.descriptor;
    }

//    @Override
//    public javax.usb.UsbDeviceDescriptor getUsbDeviceDescriptor() {
//        log.error("Not implemented: createUsbControlIrp");
//        return this.;
//    }

    @Override
    public UsbStringDescriptor getUsbStringDescriptor(byte b) throws UsbException, UsbDisconnectedException {
        log.error("Not implemented: createUsbControlIrp");
        return null;
    }

    @Override
    public String getString(byte b) throws UsbException, UnsupportedEncodingException, UsbDisconnectedException {
        log.error("Not implemented: createUsbControlIrp");
        return null;
    }

    @Override
    public void syncSubmit(UsbControlIrp usbControlIrp) throws UsbException, IllegalArgumentException, UsbDisconnectedException {
        log.error("Not implemented: createUsbControlIrp");
    }

    @Override
    public void asyncSubmit(UsbControlIrp usbControlIrp) throws UsbException, IllegalArgumentException, UsbDisconnectedException {
        log.error("Not implemented: createUsbControlIrp");
    }

    @Override
    public void syncSubmit(List list) throws UsbException, IllegalArgumentException, UsbDisconnectedException {
        log.error("Not implemented: createUsbControlIrp");
    }

    @Override
    public void asyncSubmit(List list) throws UsbException, IllegalArgumentException, UsbDisconnectedException {
        log.error("Not implemented: createUsbControlIrp");
    }

    @Override
    public UsbControlIrp createUsbControlIrp(byte b, byte b1, short i, short i1) {
        log.error("Not implemented: createUsbControlIrp");
        return null;
    }

    @Override
    public void addUsbDeviceListener(UsbDeviceListener usbDeviceListener) {
        log.error("Not implemented: createUsbControlIrp");
    }

    @Override
    public void removeUsbDeviceListener(UsbDeviceListener usbDeviceListener) {
        log.error("Not implemented: createUsbControlIrp");
    }

    public int getInterfaceCount() {
        return this.descriptor.configurationDescriptors().get(0).bNumInterfaces;
    }

    public ATUsbInterface getInterface(int i) {
        return this.descriptor.configurationDescriptors.get(0).interfaces.get(i);
    }

    public String toLsUsbString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("Bus %03d Device %03d: ID %04x:%04x %s %s\n", this.busNumber, this.address, this.descriptor.idVendor, this.descriptor.idProduct, this.descriptor.manufacturer, this.descriptor.product));
//        stringBuilder.append(String.format("%s:%s.%s (%s)", this.getBusNumberString(), this.getAddressString(), portNumber, speedString));
        stringBuilder.append(StringUtils.repeat(' ', 2) + descriptor.toLsUsbString(2));
        return stringBuilder.toString();
    }

}
