/*
 * (C) Copyright 2014 mjahnen <jahnen@in.tum.de>
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

package com.github.mjdev.libaums;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//import android.annotation.TargetApi;
//import android.content.Context;
//import android.hardware.usb.UsbConstants;
//import android.hardware.usb.UsbDevice;
//import android.hardware.usb.UsbDeviceConnection;
//import android.hardware.usb.UsbEndpoint;
//import android.hardware.usb.UsbInterface;
//import android.hardware.usb.UsbManager;
//import android.os.Build;
import com.atech.library.usb.libaums.UsbMassStorageLibrary;
import com.atech.library.usb.libaums.data.LibAumsException;
import com.atech.library.usb.libaums.data.UsbConstants;
import com.atech.library.usb.libaums.UsbManagement;
import com.atech.library.usb.libaums.data.UsbMassStorageDeviceConfig;
import com.atech.library.usb.libaums.usb.device.ATUsbDevice;
import com.atech.library.usb.libaums.usb.device.ATUsbEndpointDescriptor;
import com.atech.library.usb.libaums.usb.device.ATUsbInterface;
import com.atech.library.usb.libaums.usb.device.ATUsbInterfaceDescriptor;
import com.atech.library.usb.libaums.usb4java.Usb4JavaManager;
import com.atech.library.usb.libaums.usb4java.Usb4JavaUsbDeviceCommunication;
import com.github.mjdev.libaums.driver.BlockDeviceDriverFactory;
import com.github.mjdev.libaums.driver.scsi.ScsiBlockDevice;
import com.github.mjdev.libaums.partition.PartitionTableFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import com.github.mjdev.libaums.driver.BlockDeviceDriver;
import com.github.mjdev.libaums.partition.Partition;
import com.github.mjdev.libaums.partition.PartitionTable;
import com.github.mjdev.libaums.partition.PartitionTableEntry;

import javax.usb.*;

import static com.atech.library.usb.libaums.data.UsbConstants.*;

/**
 * Class representing a connected USB mass storage device. You can enumerate
 * through all connected mass storage devices via
 // @link #getMassStorageDevices(Context). This method only returns supported
 * devices or if no device is connected an empty array.
 * <p>
 * After choosing a device you have to get the permission for the underlying
 //  @link android.hardware.usb.UsbDevice. The underlying
 //  @link android.hardware.usb.UsbDevice can be accessed via
 * @link #getUsbDevice()
 * <p>
 * After that you need to call {@link #setupDevice()}. This will initialize the
 * mass storage device and read the partitions (
 * {@link com.github.mjdev.libaums.partition.Partition}).
 * <p>
 * The supported partitions can then be accessed via {@link #getPartitions()}
 * and you can begin to read directories and files.
 * 
 * @author mjahnen
 * 
 */
@Slf4j
public class UsbMassStorageDevice {

	/**
	 * Usb communication which uses the newer API in Android Jelly Bean MR2 (API
	 * level 18). It just delegates the calls to the {@link UsbDeviceConnection}
	 * .
	 * 
	 * @author mjahnen
	 * 
	 */
//	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
//	private class JellyBeanMr2Communication implements UsbCommunication {
//		@Override
//		public int bulkOutTransfer(byte[] buffer, int length) {
//			return deviceConnection.bulkTransfer(outEndpoint, buffer, length, TRANSFER_TIMEOUT);
//		}
//
//		@Override
//		public int bulkOutTransfer(byte[] buffer, int offset, int length) {
//			return deviceConnection.bulkTransfer(outEndpoint, buffer, offset, length,
//					TRANSFER_TIMEOUT);
//		}
//
//		@Override
//		public int bulkInTransfer(byte[] buffer, int length) {
//			return deviceConnection.bulkTransfer(inEndpoint, buffer, length, TRANSFER_TIMEOUT);
//		}
//
//		@Override
//		public int bulkInTransfer(byte[] buffer, int offset, int length) {
//			return deviceConnection.bulkTransfer(inEndpoint, buffer, offset, length,
//					TRANSFER_TIMEOUT);
//		}
//	}

	/**
	 * On Android API level lower 18 (Jelly Bean MR2) we cannot specify a start
	 * offset in the source/destination array. Because of that we have to use
	 * this workaround, where we have to copy the data every time offset is non
	 * zero.
	 * 
	 * @author mjahnen
	 * 
	 */
//	private class HoneyCombMr1Communication implements UsbCommunication {
//		@Override
//		public int bulkOutTransfer(byte[] buffer, int length) {
//			return deviceConnection.bulkTransfer(outEndpoint, buffer, length, TRANSFER_TIMEOUT);
//		}
//
//		@Override
//		public int bulkOutTransfer(byte[] buffer, int offset, int length) {
//			if (offset == 0)
//				return deviceConnection.bulkTransfer(outEndpoint, buffer, length, TRANSFER_TIMEOUT);
//
//			byte[] tmpBuffer = new byte[length];
//			System.arraycopy(buffer, offset, tmpBuffer, 0, length);
//			return deviceConnection.bulkTransfer(outEndpoint, tmpBuffer, length,
//					TRANSFER_TIMEOUT);
//		}
//
//		@Override
//		public int bulkInTransfer(byte[] buffer, int length) {
//			return deviceConnection.bulkTransfer(inEndpoint, buffer, length, TRANSFER_TIMEOUT);
//		}
//
//		@Override
//		public int bulkInTransfer(byte[] buffer, int offset, int length) {
//			if (offset == 0)
//				return deviceConnection.bulkTransfer(inEndpoint, buffer, length, TRANSFER_TIMEOUT);
//
//			byte[] tmpBuffer = new byte[length];
//			int result = deviceConnection.bulkTransfer(inEndpoint, tmpBuffer, length,
//					TRANSFER_TIMEOUT);
//			System.arraycopy(tmpBuffer, 0, buffer, offset, length);
//			return result;
//		}
//	}



	private static int TRANSFER_TIMEOUT = 21000;


	UsbMassStorageDeviceConfig usbMassStorageDeviceConfig;

	@Getter
	private ScsiBlockDevice blockDevice;
	private PartitionTable partitionTable;
	private List<Partition> partitions = new ArrayList<>();

	// this two parameters will be overwritten with values
	private boolean loadPartitionTable = true;

	Usb4JavaUsbDeviceCommunication communication;

	/**
	 * Construct a new {@link com.github.mjdev.libaums.UsbMassStorageDevice}.
	 * The given parameters have to actually be a mass storage device, this is
	 * not checked in the constructor!
	 * 
	 * @param usbMassStorageDeviceConfig
	 */
	private UsbMassStorageDevice(UsbMassStorageDeviceConfig usbMassStorageDeviceConfig) {
		this.usbMassStorageDeviceConfig = usbMassStorageDeviceConfig;
		this.loadPartitionTable = UsbMassStorageLibrary.LOAD_PARTITIONS;
	}

	/**
	 * This method iterates through all connected USB devices and searches for
	 * mass storage devices.
	 * 
	 // @param context
	 *            Context to get the @link UsbManager
	 * @return An array of suitable mass storage devices or an empty array if
	 *         none could be found.
	 */
	public static UsbMassStorageDevice[] getMassStorageDevices() throws LibAumsException {
		List<UsbMassStorageDevice> result = new ArrayList<>();

		List<UsbMassStorageDeviceConfig> massStorageDevices =
				getListOfAttachedUsbMassStorageDevices();

		for (UsbMassStorageDeviceConfig massStorageDevice : massStorageDevices) {
			result.add(new UsbMassStorageDevice(massStorageDevice));
		}

		return result.toArray(new UsbMassStorageDevice[0]);
	}

	public static UsbMassStorageDevice getMassStorageDevice(UsbMassStorageDeviceConfig config) {
		return new UsbMassStorageDevice(config);
	}

	public static List<UsbMassStorageDeviceConfig> getListOfAttachedUsbMassStorageDevices() throws LibAumsException {

		List<UsbMassStorageDeviceConfig> outList = new ArrayList<>();

		List<ATUsbDevice> deviceList = Usb4JavaManager.getDeviceList();

		for (ATUsbDevice device : deviceList) {
			log.info("found usb device: " + device);

			//int interfaceCount = device.getInterfaceCount();
			//for (int i = 0; i < interfaceCount; i++) {
			ATUsbInterface usbInterfaceRoot = device.getInterface(0); //read mass storage not HID interface. HID interface is 1
			log.info( "found usb interface: " + usbInterfaceRoot);

			for (ATUsbInterfaceDescriptor usbInterface : usbInterfaceRoot.altsettings()) {

				// we currently only support SCSI transparent command set with
				// bulk transfers only!
				if (usbInterface.bInterfaceClass() != USB_CLASS_MASS_STORAGE ||
					usbInterface.bInterfaceSubClass() != MASS_STORAGE_SUBCLASS_SCSI ||
					usbInterface.bInterfaceProtocol() != MASS_STORAGE_PROTOCOL_BBB_BULK_ONLY) {
					log.info("Device interface not suitable ! Found class={},subclass={},protocol={}), required=8/6/80 (Mass Storage/SCSI/Bulk-Only)",
							usbInterface.bInterfaceClass(),
							usbInterface.bInterfaceSubClass(),
							usbInterface.bInterfaceProtocol());
					continue;
				}

				// Every mass storage device has exactly two endpoints
				// One IN and one OUT endpoint
				int endpointCount = usbInterface.bNumEndpoints();
				if (endpointCount != 2) {
					log.warn("interface endpoint count != 2");
				}

				ATUsbEndpointDescriptor outEndpoint = null;
				ATUsbEndpointDescriptor inEndpoint = null;
				for (int j = 0; j < endpointCount; j++) {
					ATUsbEndpointDescriptor endpoint = usbInterface.getEndpoint(j);
					log.info( "found usb endpoint: " + endpoint);
					if (endpoint.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
						if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
							outEndpoint = endpoint;
						} else {
							inEndpoint = endpoint;
						}
					}
				}

				if (outEndpoint == null || inEndpoint == null) {
					log.error("Not all needed endpoints found!");
					continue;
				}

				// TODO UsbMassStorageDeviceConfig filling - missing endpoints
				outList.add(UsbMassStorageDeviceConfig.builder()
								.vendorId(device.getUsbDeviceDescriptor().idVendor())
								.productId(device.getUsbDeviceDescriptor().idProduct())
								.interfaceNumber(usbInterface.bInterfaceNumber())
								.inEndpointAddress(inEndpoint.bEndpointAddress())
								.outEndpointAddress(outEndpoint.bEndpointAddress())
						.build());

			}

		}

		return outList;
	}



	/**
	 * Initializes the mass storage device and determines different things like
	 * for example the MBR or the file systems for the different partitions.
	 * 
	 * @throws IOException
	 *             If reading from the physical device fails.
	 * @throws IllegalStateException
	 *             If permission to communicate with the underlying
	 *             {@link UsbDevice} is missing.
	 * //@see #getUsbDevice()
	 */
	public void init() throws LibAumsException {
		setupDevice();
	}

	/**
	 * Sets the device up. Claims interface and initiates the device connection.
	 *
	 * Initializes the {@link #blockDevice} and reads the partitions (if so configured).
	 * 
	 * @throws LibAumsException
	 *             If reading from the physical device fails.
	 * @see #init()
	 */
	private void setupDevice() throws LibAumsException {
		log.info("Init device {}", usbMassStorageDeviceConfig.getReadableDeviceId());
		communication = new Usb4JavaUsbDeviceCommunication(this.usbMassStorageDeviceConfig);
		communication.openDevice();

		log.info("Create Block Device for {}", usbMassStorageDeviceConfig.getReadableDeviceId());
		blockDevice = BlockDeviceDriverFactory.createBlockDevice(communication);
		blockDevice.init();

		if (loadPartitionTable) {
			log.info("Create Partition Table for {}", usbMassStorageDeviceConfig.getReadableDeviceId());
			partitionTable = PartitionTableFactory.createPartitionTable(blockDevice);
			initPartitions();
		}
	}

	/**
	 * Fills {@link #partitions} with the information received by the
	 * {@link #partitionTable}.
	 * 
	 * @throws IOException
	 *             If reading from the {@link #blockDevice} fails.
	 */
	private void initPartitions() throws LibAumsException {
		Collection<PartitionTableEntry> partitionEntrys = partitionTable.getPartitionTableEntries();

		for (PartitionTableEntry entry : partitionEntrys) {
			Partition partition = Partition.createPartition(entry, blockDevice);
			if (partition != null) {
				partitions.add(partition);
			}
		}
	}

	/**
	 * Releases the @link android.hardware.usb.UsbInterface and closes the
	 * @link android.hardware.usb.UsbDeviceConnection. After calling this
	 * method no further communication is possible. That means you can not read
	 * or write from or to the partitions returned by {@link #getPartitions()}.
	 */
	public void close() throws LibAumsException {
		this.communication.closeDevice();
	}

	/**
	 * Returns the available partitions of the mass storage device. You have to
	 * call {@link #init()} before calling this method!
	 * 
	 * @return List of partitions.
	 */
	public List<Partition> getPartitions() {
		return partitions;
	}

	/**
	 * This returns the @link android.hardware.usb.UsbDevice which can be used
	 * to request permission for communication.
	 * 
	 // @return Underlying @link android.hardware.usb.UsbDevice used for
	 *         communication.
	 */
//	public UsbDevice getUsbDevice() {
//		return usbDevice;
//	}


}
