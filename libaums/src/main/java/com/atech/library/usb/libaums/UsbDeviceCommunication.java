package com.atech.library.usb.libaums;

import com.atech.library.usb.libaums.data.UsbMassStorageDeviceConfig;
import com.github.mjdev.libaums.UsbCommunication;
import org.usb4java.BufferUtils;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Created by andy on 10.04.2024.
 */
public class UsbDeviceCommunication implements UsbCommunication {

    private final UsbMassStorageDeviceConfig deviceSettings;
    DeviceHandle handle;
    private static int TIMEOUT = 5000;

    public UsbDeviceCommunication(UsbMassStorageDeviceConfig deviceSettings) {
        this.deviceSettings = deviceSettings;
        initDeviceWithLibUsb();
    }

    private void initDeviceWithLibUsb() {
        // Initialize the libusb context
        int result = LibUsb.init(null);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to initialize libusb", result);
        }

        // Open device
        DeviceHandle handle = LibUsb.openDeviceWithVidPid(null, deviceSettings.getVendorId(),
                deviceSettings.getProductId());
        if (handle == null)
        {
            System.err.println("Test device not found.");
            System.exit(1);
        }

        // Claim interface
        result = LibUsb.claimInterface(handle, deviceSettings.getInterfaceNumber());
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to claim interface", result);
        }
    }


    @Override
    public int bulkOutTransfer(byte[] data, int length) {
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(length);
        buffer.put(data, 0, length);
        //put(byte[] src, int offset, int length)
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result = LibUsb.bulkTransfer(handle, deviceSettings.getOutEndpoint(), buffer,
                transferred, TIMEOUT);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to send data", result);
        }
        System.out.println(transferred.get() + " bytes sent to device");


        return result;
    }

    @Override
    public int bulkOutTransfer(byte[] buffer, int offset, int length) {
        int newSize = buffer.length-offset;
        byte[] outBuffer = new byte[newSize];

        System.arraycopy(buffer, offset, outBuffer, 0, newSize);

        return bulkOutTransfer(outBuffer, length);
    }

    @Override
    public int bulkInTransfer(byte[] data, int length) {
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(length).order(
                ByteOrder.LITTLE_ENDIAN);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result = LibUsb.bulkTransfer(handle, deviceSettings.getInEndpoint(), buffer,
                transferred, TIMEOUT);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to read data", result);
        }
        System.out.println(transferred.get() + " bytes read from device");
        data = buffer.array();

        return result;
    }

    @Override
    public int bulkInTransfer(byte[] buffer, int offset, int length) {
        int newSize = buffer.length-offset;
        byte[] outBuffer = new byte[newSize];

        System.arraycopy(buffer, offset, outBuffer, 0, newSize);

        return bulkInTransfer(outBuffer, length);
    }

    public void closeDevice() {
        // Release the ADB interface
        int result = LibUsb.releaseInterface(handle, deviceSettings.getInterfaceNumber());
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to release interface", result);
        }

        // Close the device
        LibUsb.close(handle);

//        // Deinitialize the libusb context
//        LibUsb.exit(null);

    }
}
