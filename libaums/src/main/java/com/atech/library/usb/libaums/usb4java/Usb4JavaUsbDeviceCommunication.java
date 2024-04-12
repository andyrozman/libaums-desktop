package com.atech.library.usb.libaums.usb4java;

import com.atech.library.usb.libaums.data.LibAumsException;
import com.atech.library.usb.libaums.data.UsbMassStorageDeviceConfig;
import com.atech.library.usb.libaums.UsbMassStorageLibrary;
import com.github.mjdev.libaums.UsbCommunication;
import lombok.extern.slf4j.Slf4j;
import org.usb4java.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

/**
 * Created by andy on 11.04.2024.
 */
@Slf4j
public class Usb4JavaUsbDeviceCommunication implements UsbCommunication {

    DeviceHandle deviceHandle;
    UsbMassStorageDeviceConfig deviceConfig;

    public Usb4JavaUsbDeviceCommunication(UsbMassStorageDeviceConfig usbMassStorageDeviceConfig) {
        this.deviceConfig = usbMassStorageDeviceConfig;
    }

    public void openDevice() throws LibAumsException {
        log.info("openDevice {}:{}")
        Context context = UsbMassStorageLibrary.initLibrary();

        // Open device
        DeviceHandle handle = LibUsb.openDeviceWithVidPid(context, deviceConfig.getVendorId(),
                deviceConfig.getProductId());
        if (handle == null) {
            System.err.println("Test device not found.");
            System.exit(1);
        }

        this.deviceHandle = handle;

        // Claim interface
        int result = LibUsb.claimInterface(handle, deviceConfig.getInterfaceNumber());
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to claim interface", result);
        }
    }


    @Override
    public int bulkOutTransfer(byte[] buffer, int length) {
        // TODO implement Usb4JavaUsbDeviceCommunication

        //			return deviceConnection.bulkTransfer(outEndpoint, buffer, length, TRANSFER_TIMEOUT);


        return 0;
    }

    @Override
    public int bulkOutTransfer(byte[] buffer, int offset, int length) {
        // TODO implement Usb4JavaUsbDeviceCommunication

//			if (offset == 0)
//				return deviceConnection.bulkTransfer(outEndpoint, buffer, length, TRANSFER_TIMEOUT);
//
//			byte[] tmpBuffer = new byte[length];
//			System.arraycopy(buffer, offset, tmpBuffer, 0, length);
//			return deviceConnection.bulkTransfer(outEndpoint, tmpBuffer, length,
//					TRANSFER_TIMEOUT);

        return 0;
    }

    @Override
    public int bulkInTransfer(byte[] buffer, int length) {

        LibUsb.bulkTransfer(deviceHandle, )

        // TODO implement Usb4JavaUsbDeviceCommunication
        //			return deviceConnection.bulkTransfer(inEndpoint, buffer, length, TRANSFER_TIMEOUT);

        return 0;
    }

    @Override
    public int bulkInTransfer(byte[] buffer, int offset, int length) {
        // TODO implement Usb4JavaUsbDeviceCommunication

        //			if (offset == 0)
//				return deviceConnection.bulkTransfer(inEndpoint, buffer, length, TRANSFER_TIMEOUT);
//
//			byte[] tmpBuffer = new byte[length];
//			int result = deviceConnection.bulkTransfer(inEndpoint, tmpBuffer, length,
//					TRANSFER_TIMEOUT);
//			System.arraycopy(tmpBuffer, 0, buffer, offset, length);
//			return result;

        // read()

        return 0;
    }


    /**
     * Writes some data to the device.
     *
     * @param handle
     *            The device handle.
     * @param data
     *            The data to send to the device.
     */
    public static void write(DeviceHandle handle, byte[] data)
    {
//        ByteBuffer buffer = BufferUtils.allocateByteBuffer(data.length);
//        buffer.put(data);
//        IntBuffer transferred = BufferUtils.allocateIntBuffer();
//        int result = LibUsb.bulkTransfer(handle, OUT_ENDPOINT, buffer,
//                transferred, TIMEOUT);
//        if (result != LibUsb.SUCCESS)
//        {
//            throw new LibUsbException("Unable to send data", result);
//        }
//        System.out.println(transferred.get() + " bytes sent to device");
    }

    /**
     * Reads some data from the device.
     *
     * @param handle
     *            The device handle.
     * @param size
     *            The number of bytes to read from the device.
     * @return The read data.
     */
    public static ByteBuffer read(DeviceHandle handle, int size)
    {
//        ByteBuffer buffer = BufferUtils.allocateByteBuffer(size).order(
//                ByteOrder.LITTLE_ENDIAN);
//        IntBuffer transferred = BufferUtils.allocateIntBuffer();
//        int result = LibUsb.bulkTransfer(handle, IN_ENDPOINT, buffer,
//                transferred, TIMEOUT);
//        if (result != LibUsb.SUCCESS)
//        {
//            throw new LibUsbException("Unable to read data", result);
//        }
//        System.out.println(transferred.get() + " bytes read from device");
//        return buffer;
        return null;

    }



    public void closeDevice() throws LibAumsException {
        // Release the ADB interface
        int result = LibUsb.releaseInterface(deviceHandle, deviceConfig.getInterfaceNumber());
        if (result != LibUsb.SUCCESS) {
            throw LibAumsException.createWithLibUsbException("Unable to release interface", result);
        }

        // Close the device
        LibUsb.close(deviceHandle);
    }
}
