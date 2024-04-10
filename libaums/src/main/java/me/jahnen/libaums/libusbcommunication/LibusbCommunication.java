package me.jahnen.libaums.libusbcommunication;

//import android.hardware.usb.UsbDevice
//import android.hardware.usb.UsbDeviceConnection
//import android.hardware.usb.UsbEndpoint
//import android.hardware.usb.UsbInterface
//import android.hardware.usb.UsbManager
//import android.util.Log
//import me.jahnen.libaums.core.ErrNo;
//import me.jahnen.libaums.core.usb.PipeException;
//import me.jahnen.libaums.core.usb.UsbCommunication;
//import me.jahnen.libaums.core.usb.UsbCommunication.Companion.TRANSFER_TIMEOUT;
//import me.jahnen.libaums.core.usb.UsbCommunicationCreator;

import com.github.mjdev.libaums.UsbCommunication;
import org.usb4java.DeviceHandle;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;

import javax.usb.UsbDevice;
import javax.usb.UsbEndpoint;
import javax.usb.UsbInterface;
import java.io.IOException;
import java.nio.ByteBuffer;


public class LibusbCommunication implements UsbCommunication {

    private final UsbDevice usbDevice;
    private final UsbInterface usbInterface;
    private final UsbEndpoint outEndpoint;
    private final UsbEndpoint inEndpoint;

    public LibusbCommunication(//UsbManager usbManager,
                               UsbDevice usbDevice,
                               UsbInterface usbInterface,
                               UsbEndpoint outEndpoint,
                               UsbEndpoint inEndpoint) {

        this.usbDevice = usbDevice;
        this.usbInterface = usbInterface;
        this.outEndpoint = outEndpoint;
        this.inEndpoint = inEndpoint;

        initLibUsbWithDevice();
    }

    private void initLibUsbWithDevice() {

//        // Initialize the libusb context
//        int result = LibUsb.init(null);
//        if (result != LibUsb.SUCCESS)
//        {
//            throw new LibUsbException("Unable to initialize libusb", result);
//        }
//
//        // Open test device (Samsung Galaxy Nexus)
//        DeviceHandle handle = LibUsb.openDeviceWithVidPid(null, usbDevice.getV  VENDOR_ID,
//                PRODUCT_ID);
//        if (handle == null)
//        {
//            System.err.println("Test device not found.");
//            System.exit(1);
//        }
//
//        // Claim the ADB interface
//        result = LibUsb.claimInterface(handle, INTERFACE);
//        if (result != LibUsb.SUCCESS)
//        {
//            throw new LibUsbException("Unable to claim interface", result);
//        }


        //        System.loadLibrary("libusbcom")
//
//        deviceConnection = usbManager.openDevice(usbDevice)
//            ?: throw IOException("deviceConnection is null!")
//
//        val res = nativeInit(deviceConnection!!.fileDescriptor, libUsbHandleArray)
//        if (res != 0) {
//            throw LibusbException("libusb init failed", LibusbError.fromCode(res))
//        }
//
//        val claim = deviceConnection!!.claimInterface(usbInterface, true)
//        if (!claim) {
//            throw ErrNoIOException("could not claim interface!")
//        }
////        val ret = nativeClaimInterface(libUsbHandle, usbInterface.id)
////        if (ret < 0) {
////            throw IOException("libusb returned $ret in claim interface")
////        }


    }

    @Override
    public int bulkOutTransfer(byte[] buffer, int length) {
        return 0;
    }

    @Override
    public int bulkOutTransfer(byte[] buffer, int offset, int length) {
        return 0;
    }

    @Override
    public int bulkInTransfer(byte[] buffer, int length) {
        return 0;
    }

    @Override
    public int bulkInTransfer(byte[] buffer, int offset, int length) {
        return 0;
    }

//    // used to save heap address of libusb device handle
//    private var libUsbHandleArray = longArrayOf(0)
//    private val libUsbHandle: Long
//        get() = libUsbHandleArray[0]
//    private var deviceConnection: UsbDeviceConnection?
//    private var closed = false
//
//    init {
//        System.loadLibrary("libusbcom")
//
//        deviceConnection = usbManager.openDevice(usbDevice)
//            ?: throw IOException("deviceConnection is null!")
//
//        val res = nativeInit(deviceConnection!!.fileDescriptor, libUsbHandleArray)
//        if (res != 0) {
//            throw LibusbException("libusb init failed", LibusbError.fromCode(res))
//        }
//
//        val claim = deviceConnection!!.claimInterface(usbInterface, true)
//        if (!claim) {
//            throw ErrNoIOException("could not claim interface!")
//        }
////        val ret = nativeClaimInterface(libUsbHandle, usbInterface.id)
////        if (ret < 0) {
////            throw IOException("libusb returned $ret in claim interface")
////        }
//    }

//    private external fun nativeInit(fd: Int, handle: LongArray): Int
//    private external fun nativeClaimInterface(handle: Long, interfaceNumber: Int): Int
//    private external fun nativeClose(handle: Long, interfaceNumber: Int)
//    private external fun nativeReset(handle: Long): Int
//    private external fun nativeClearHalt(handle: Long, interfaceNumber: Int): Int
//    private external fun nativeBulkTransfer(handle: Long, endpointAddress: Int, data: ByteArray, offset: Int, length: Int, timeout: Int): Int
//    private external fun nativeControlTransfer(handle: Long, requestType: Int, request: Int, value: Int, index: Int, buffer: ByteArray, length: Int, timeout: Int): Int

//    public int bulkOutTransfer(src: ByteBuffer) {
//        require(!closed) { "device is closed" }
//
//        val transferred = nativeBulkTransfer(
//            libUsbHandle, outEndpoint.address, src.array(), src.position(), src.remaining(),
//            TRANSFER_TIMEOUT
//        )
//        when {
//            transferred == LibusbError.PIPE.code -> throw PipeException()
//            transferred < 0 -> throw LibusbException(
//                "libusb control transfer failed", LibusbError.fromCode(transferred)
//            )
//        }
//        src.position(src.position() + transferred)
//        return transferred
//    }
//
//    override fun bulkInTransfer(dest: ByteBuffer): Int {
//        require(!closed) { "device is closed" }
//
//        val transferred = nativeBulkTransfer(
//            libUsbHandle, inEndpoint.address, dest.array(), dest.position(), dest.remaining(),
//            TRANSFER_TIMEOUT
//        )
//        when {
//            transferred == LibusbError.PIPE.code -> throw PipeException()
//            transferred < 0 -> throw LibusbException(
//                "libusb control transfer failed", LibusbError.fromCode(transferred)
//            )
//        }
//        dest.position(dest.position() + transferred)
//        return transferred
//    }
//
//    override fun controlTransfer(requestType: Int, request: Int, value: Int, index: Int, buffer: ByteArray, length: Int): Int {
//        require(!closed) { "device is closed" }
//
//        val ret = nativeControlTransfer(libUsbHandle, requestType, request, value, index, buffer, length, TRANSFER_TIMEOUT)
//        if (ret < 0) {
//            throw LibusbException("libusb control transfer failed", LibusbError.fromCode(ret))
//        }
//        return ret
//    }
//
//    override fun resetDevice() {
//        require(!closed) { "device is closed" }
//
//        if (!deviceConnection!!.releaseInterface(usbInterface)) {
//            Log.w(TAG, "Failed to release interface, errno: ${ErrNo.errno} ${ErrNo.errstr}")
//        }
//
//        val ret = nativeReset(libUsbHandle)
//        // if LIBUSB_ERROR_NOT_FOUND might need reenumeration
//        Log.d(TAG, "libusb reset returned $ret: ${LibusbError.fromCode(ret).message}")
//
//        var counter = 3
//        while (!deviceConnection!!.claimInterface(usbInterface, true) && counter >= 0) {
//            if (counter == 0) {
//                throw ErrNoIOException(
//                    "Could not claim interface, errno: ${ErrNo.errno} ${ErrNo.errstr}"
//                )
//            }
//            Thread.sleep(800)
//            counter--
//        }
//    }
//
//    override fun clearFeatureHalt(endpoint: UsbEndpoint) {
//        require(!closed) { "device is closed" }
//
//        val ret = nativeClearHalt(libUsbHandle, endpoint.address)
//        Log.d(TAG, "libusb clearFeatureHalt returned $ret: ${LibusbError.fromCode(ret).message}")
//    }
//
//    override fun close() {
//        require(!closed) { "device is already closed" }
//
//        try {
//            deviceConnection!!.releaseInterface(usbInterface)
//            nativeClose(libUsbHandle, usbInterface.id)
//            deviceConnection!!.close()
//        } finally {
//            closed = true
//        }
//    }



}

//public static class LibusbCommunicationCreator extends UsbCommunicationCreator {
//    override fun create(usbManager: UsbManager, usbDevice: UsbDevice, usbInterface: UsbInterface, outEndpoint: UsbEndpoint, inEndpoint: UsbEndpoint): UsbCommunication? {
//        return LibusbCommunication(usbManager, usbDevice, usbInterface, outEndpoint, inEndpoint)
//    }
//}
