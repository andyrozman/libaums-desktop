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

package com.atech.library.usb.libaums.usb4java;

import com.atech.library.usb.libaums.data.LibAumsException;
import com.atech.library.usb.libaums.data.UsbMassStorageDeviceConfig;
import com.atech.library.usb.libaums.UsbMassStorageLibrary;
import com.github.mjdev.libaums.UsbCommunication;
import lombok.Getter;
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

    private static final int TRANSFER_TIMEOUT = 5000; // 21000
    DeviceHandle deviceHandle;
    @Getter
    UsbMassStorageDeviceConfig deviceConfig;
    boolean deviceIsOpenAndClaimed = false;

    public Usb4JavaUsbDeviceCommunication(UsbMassStorageDeviceConfig usbMassStorageDeviceConfig) {
        this.deviceConfig = usbMassStorageDeviceConfig;
    }

    public void openDevice() throws LibAumsException {

        if (deviceHandle != null) {
            log.error("Device {} is already opened, exiting.", deviceConfig.getReadableDeviceId());
            return;
        }

        log.debug("openDevice: opening {}", deviceConfig.getReadableDeviceId());
        Context context = UsbMassStorageLibrary.initLibrary();

        // Open device
        DeviceHandle handle = LibUsb.openDeviceWithVidPid(context, deviceConfig.getVendorId(),
                deviceConfig.getProductId());
        if (handle == null) {
            log.error("openDevice: Device {} not found, or could not be opened.", deviceConfig.getReadableDeviceId());
            System.exit(1);
        }

        this.deviceHandle = handle;

        // Claim interface
        int result = LibUsb.claimInterface(handle, deviceConfig.getInterfaceNumber());
        if (result != LibUsb.SUCCESS)
        {
            // if device is busy we try to detach kernel driver
            if (result == LibUsb.ERROR_BUSY) {
                int result2 = LibUsb.detachKernelDriver(handle, deviceConfig.getInterfaceNumber());
                if (result2 != LibUsb.SUCCESS &&
                        result2 != LibUsb.ERROR_NOT_SUPPORTED &&
                        result2 != LibUsb.ERROR_NOT_FOUND) {
                    throw LibAumsException.createWithLibUsbException("Device busy and unable to detach kernel driver",
                            result2);
                }
                log.debug("openDevice: Device {} was busy. We detached kernel driver which was successful.", deviceConfig.getReadableDeviceId());
            } else {
                throw LibAumsException.createWithLibUsbException("Unable to claim interface", result);
            }

            log.debug("openDevice: Trying to claim interface again.");
            int result3 = LibUsb.claimInterface(handle, deviceConfig.getInterfaceNumber());
            if (result3 != LibUsb.SUCCESS) {
                throw LibAumsException.createWithLibUsbException("Unable to claim interface even after detaching kernel.", result3);
            }
        }

        log.info("openDevice: Device {} opened and interface {} claimed.", deviceConfig.getReadableDeviceId(), deviceConfig.getInterfaceNumber());
        this.deviceIsOpenAndClaimed = true;
    }


    @Override
    public int bulkOutTransfer(byte[] data, int length) throws LibAumsException {

        log.debug("bulkOutTransfer (data={},length={})", getAsHex(data), length);

        ByteBuffer buffer = BufferUtils.allocateByteBuffer(data.length);
        buffer.put(data);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result = LibUsb.bulkTransfer(deviceHandle,  deviceConfig.getOutEndpointAddress(),
                buffer,
                transferred, TRANSFER_TIMEOUT);
        if (result != LibUsb.SUCCESS) {
            throw LibAumsException.createWithLibUsbException("Unable to send data: " + result, result);
        }

        int transferredCount = transferred.get();
        log.debug("bulkOutTransfer: {} bytes sent to device", transferredCount);

        return transferredCount;
    }

    @Override
    public int bulkOutTransfer(byte[] data, int offset, int length)  throws LibAumsException{
        if (offset==0) {
            return bulkOutTransfer(data, length);
        }

        log.debug("bulkOutTransfer(offset): (data={},length={},offset={})", getAsHex(data), length, offset);

        int remaining = length-offset;
        byte[] newData = new byte[length-offset];
        System.arraycopy(data, offset, newData, 0, remaining);

        ByteBuffer buffer = BufferUtils.allocateByteBuffer(newData.length);
        buffer.put(newData);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result = LibUsb.bulkTransfer(deviceHandle,  deviceConfig.getOutEndpointAddress(), buffer,
                transferred, TRANSFER_TIMEOUT);
        if (result != LibUsb.SUCCESS)
        {
            throw LibAumsException.createWithLibUsbException("Unable to send data", result);
        }

        int transferredCount = transferred.get();
        log.debug("bulkOutTransfer(offset): {} bytes sent to device", transferredCount);

        return transferredCount;
    }

    @Override
    public int bulkInTransfer(byte[] data, int length) throws LibAumsException {

        log.debug("bulkInTransfer: dataLength={},requestLength={}", data.length, length);

        ByteBuffer buffer = BufferUtils.allocateByteBuffer(length).order(
                ByteOrder.LITTLE_ENDIAN);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result = LibUsb.bulkTransfer(deviceHandle,
                deviceConfig.getInEndpointAddress(), buffer,
                transferred, TRANSFER_TIMEOUT);
        if (result != LibUsb.SUCCESS) {
            throw LibAumsException.createWithLibUsbException("Unable to read data", result);
        }

        int transferredCount = transferred.get();
        log.debug("bulkInTransfer: {} bytes read from device", transferredCount);

        if (buffer.hasArray()) {
            log.trace("bulkInTransfer: Buffer.hasArray: {}", buffer);
            System.arraycopy(buffer.array(), 0, data, 0, length);
        } else {
            buffer.get(data);
        }

        log.debug("bulkInTransfer: Data returned: {}", getAsHex(data));

        return transferredCount;
    }

    @Override
    public int bulkInTransfer(byte[] data, int offset, int length)  throws LibAumsException{
        if (offset==0) {
            return bulkInTransfer(data, length);
        }

        log.debug("bulkInTransfer(offset): dataLength={},requestLength={},offset={}", data.length, length, offset);

        ByteBuffer buffer = BufferUtils.allocateByteBuffer(length).order(
                ByteOrder.LITTLE_ENDIAN);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result = LibUsb.bulkTransfer(deviceHandle, deviceConfig.getInEndpointAddress(),
                buffer,
                transferred, TRANSFER_TIMEOUT);
        if (result != LibUsb.SUCCESS) {
            throw LibAumsException.createWithLibUsbException("Unable to read data", result);
        }

        int transferredCount = transferred.get();
        log.debug("bulkInTransfer(offset): {} bytes read from device", transferredCount);

        if (buffer.hasArray()) {
            log.trace("bulkInTransfer(offset): Buffer.hasArray: {}", buffer);
            System.arraycopy(buffer.array(), 0, data, offset, length);
        } else {
            buffer.get(data, offset, length);
        }

        log.debug("bulkInTransfer(offset): Data returned: {}", getAsHex(data));

        return transferredCount;
    }


    public void closeDevice() throws LibAumsException {
        if (deviceHandle == null) {
            log.warn("closeDevice: device {} is already closed. Exiting.", deviceConfig.getReadableDeviceId());
            return;
        }
        log.debug("closeDevice: closing device {}", deviceConfig.getReadableDeviceId());

        if (deviceIsOpenAndClaimed) {
            // Release interface
            int result = LibUsb.releaseInterface(deviceHandle, deviceConfig.getInterfaceNumber());
            if (result != LibUsb.SUCCESS) {
                throw LibAumsException.createWithLibUsbException("Unable to release interface", result);
            }
            this.deviceIsOpenAndClaimed = false;
        }

        // Close the device
        LibUsb.close(deviceHandle);
        log.info("closeDevice: device {} closed and interface released", deviceConfig.getReadableDeviceId());
        deviceHandle = null;
    }

    private String getAsHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("0x%02X ", b));
        }
        return sb.toString();
    }

    public String getReadableDeviceName() {
        return this.deviceConfig.getReadableDeviceId();
    }
}
