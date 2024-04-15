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
import java.util.Set;

import com.atech.library.usb.libaums.UsbMassStorageLibrary;
import com.atech.library.usb.libaums.data.LibAumsException;
import com.atech.library.usb.libaums.data.UsbConstants;
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

	UsbMassStorageDeviceConfig usbMassStorageDeviceConfig;

	@Getter
	private ScsiBlockDevice blockDevice;
	private PartitionTable partitionTable;
	private List<Partition> partitions = new ArrayList<>();

	// this two parameters will be overwritten with values
	private boolean loadPartitionTable = true;

	private Usb4JavaUsbDeviceCommunication communication;
	private boolean connectedToDevice;

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
		return getListOfAttachedUsbMassStorageDevices(null, null);
	}

	/**
	 * getListOfAttachedUsbMassStorageDevices() filtered with vendor or product (if both are null no filtering will be done)
	 * @param filterVendor - Set of Vendors as String (in uppercase letter, so something like 3344 or 1D6B)
	 * @param filterProduct - Set of Products as String (uppercase)
	 * @return
	 * @throws LibAumsException
	 */
	public static List<UsbMassStorageDeviceConfig> getListOfAttachedUsbMassStorageDevices(Set<String> filterVendor,
																						  Set<String> filterProduct) throws LibAumsException {
		List<UsbMassStorageDeviceConfig> outList = new ArrayList<>();

		List<ATUsbDevice> deviceList = Usb4JavaManager.getDeviceList();

		for (ATUsbDevice device : deviceList) {
			log.debug("Found usb device: {} - {}", device.getReadableId() , device.getManufacturerAndProductName());

			if (filterVendor!=null || filterProduct!=null) {
				if (filterVendor!=null && !filterVendor.contains(device.getVendorId())) {
					log.debug("    Device filtered out.");
					continue;
				}

				if (filterProduct!=null && !filterProduct.contains(device.getProductId())) {
					log.debug("    Device filtered out.");
					continue;
				}
			}

			//int interfaceCount = device.getInterfaceCount();
			//for (int i = 0; i < interfaceCount; i++) {
			ATUsbInterface usbInterfaceRoot = device.getInterface(0); //read mass storage not HID interface. HID interface is 1
			//log.debug("found usb interface: " + usbInterfaceRoot);

			for (ATUsbInterfaceDescriptor usbInterface : usbInterfaceRoot.altsettings()) {

				// we currently only support SCSI transparent command set with
				// bulk transfers only!
				if (usbInterface.bInterfaceClass() != USB_CLASS_MASS_STORAGE ||
					usbInterface.bInterfaceSubClass() != MASS_STORAGE_SUBCLASS_SCSI ||
					usbInterface.bInterfaceProtocol() != MASS_STORAGE_PROTOCOL_BBB_BULK_ONLY) {
					log.debug("    Device interface not suitable ! Found class={},subclass={},protocol={}), required=8/6/80 (Mass Storage/SCSI/Bulk-Only)",
							usbInterface.bInterfaceClass(),
							usbInterface.bInterfaceSubClass(),
							usbInterface.bInterfaceProtocol());
					continue;
				}

				// Every mass storage device has exactly two endpoints
				// One IN and one OUT endpoint
				int endpointCount = usbInterface.bNumEndpoints();
				if (endpointCount != 2) {
					log.debug("    Interface endpoint count != 2");
				}

				ATUsbEndpointDescriptor outEndpoint = null;
				ATUsbEndpointDescriptor inEndpoint = null;
				for (int j = 0; j < endpointCount; j++) {
					ATUsbEndpointDescriptor endpoint = usbInterface.getEndpoint(j);
					log.debug("    Found usb endpoint: " + endpoint);
					if (endpoint.getTransferType() == UsbConstants.USB_ENDPOINT_XFER_BULK) {
						if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
							outEndpoint = endpoint;
						} else {
							inEndpoint = endpoint;
						}
					}
				}

				if (outEndpoint == null || inEndpoint == null) {
					log.debug("    Not all needed endpoints found!");
					continue;
				}

				UsbMassStorageDeviceConfig config = UsbMassStorageDeviceConfig.builder()
						.vendorId(device.getUsbDeviceDescriptor().idVendor())
						.productId(device.getUsbDeviceDescriptor().idProduct())
						.interfaceNumber(usbInterface.bInterfaceNumber())
						.inEndpointAddress(inEndpoint.bEndpointAddress())
						.outEndpointAddress(outEndpoint.bEndpointAddress())
						.manufacturer(device.getManufacturerString())
						.product(device.getProductString())
						.build();

				log.info("Found relevant usb device: {} - {}", device.getReadableId() , device.getManufacturerAndProductName());

				outList.add(config);
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
		log.debug("Init device {}", usbMassStorageDeviceConfig.getReadableDeviceId());
		communication = new Usb4JavaUsbDeviceCommunication(this.usbMassStorageDeviceConfig);
		communication.openDevice();

		this.connectedToDevice = true;

		log.debug("Create Block Device for {}", usbMassStorageDeviceConfig.getReadableDeviceId());
		blockDevice = BlockDeviceDriverFactory.createBlockDevice(communication);
		blockDevice.init();

		if (loadPartitionTable) {
			log.debug("Create Partition Table for {}", usbMassStorageDeviceConfig.getReadableDeviceId());
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
		if (this.connectedToDevice) {
			this.communication.closeDevice();
		}
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


}
