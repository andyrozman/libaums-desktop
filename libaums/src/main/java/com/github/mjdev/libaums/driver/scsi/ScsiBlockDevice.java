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

package com.github.mjdev.libaums.driver.scsi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;


import com.atech.library.usb.libaums.data.LibAumsException;
import com.github.mjdev.libaums.UsbCommunication;
import com.github.mjdev.libaums.driver.BlockDeviceDriver;
import com.github.mjdev.libaums.driver.scsi.commands.CommandBlockWrapper;
import com.github.mjdev.libaums.driver.scsi.commands.CommandBlockWrapper.Direction;
import com.github.mjdev.libaums.driver.scsi.commands.CommandStatusWrapper;
import com.github.mjdev.libaums.driver.scsi.commands.ScsiInquiry;
import com.github.mjdev.libaums.driver.scsi.commands.ScsiInquiryResponse;
import com.github.mjdev.libaums.driver.scsi.commands.ScsiRead10;
import com.github.mjdev.libaums.driver.scsi.commands.ScsiReadCapacity;
import com.github.mjdev.libaums.driver.scsi.commands.ScsiReadCapacityResponse;
import com.github.mjdev.libaums.driver.scsi.commands.ScsiTestUnitReady;
import com.github.mjdev.libaums.driver.scsi.commands.ScsiWrite10;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is responsible for handling mass storage devices which follow the
 * SCSI standard. This class communicates with the mass storage device via the
 * different SCSI commands.
 * 
 * @author mjahnen
 * @see com.github.mjdev.libaums.driver.scsi.commands
 */

@Slf4j
public class ScsiBlockDevice implements BlockDeviceDriver {

	private UsbCommunication usbCommunication;
	private ByteBuffer outBuffer;
	private byte[] cswBuffer;

	private int blockSize;
	private int lastBlockAddress;

	public ScsiBlockDevice(UsbCommunication usbCommunication) {
		this.usbCommunication = usbCommunication;
		outBuffer = ByteBuffer.allocate(31);
		cswBuffer = new byte[CommandStatusWrapper.SIZE];
	}

	/**
	 * Issues a SCSI Inquiry to determine the connected device. After that it is
	 * checked if the unit is ready. Logs a warning if the unit is not ready.
	 * Finally the capacity of the mass storage device is read.
	 * 
	 * @throws IOException
	 *             If initialing fails due to an unsupported device or if
	 *             reading fails.
	 * @see com.github.mjdev.libaums.driver.scsi.commands.ScsiInquiry
	 * @see com.github.mjdev.libaums.driver.scsi.commands.ScsiInquiryResponse
	 * @see com.github.mjdev.libaums.driver.scsi.commands.ScsiTestUnitReady
	 * @see com.github.mjdev.libaums.driver.scsi.commands.ScsiReadCapacity
	 * @see com.github.mjdev.libaums.driver.scsi.commands.ScsiReadCapacityResponse
	 */
	@Override
	public void init() throws LibAumsException {
		ByteBuffer inBuffer = ByteBuffer.allocate(36);
		ScsiInquiry inquiry = new ScsiInquiry((byte) inBuffer.array().length);
		transferCommand(inquiry, inBuffer);
		// TODO support multiple luns!
		ScsiInquiryResponse inquiryResponse = ScsiInquiryResponse.read(inBuffer);
		log.debug("inquiry response: " + inquiryResponse);

		if (inquiryResponse.getPeripheralQualifier() != 0
				|| inquiryResponse.getPeripheralDeviceType() != 0) {
			throw LibAumsException.createWithIOException("unsupported PeripheralQualifier or PeripheralDeviceType");
		}

		ScsiTestUnitReady testUnit = new ScsiTestUnitReady();
		if (!transferCommand(testUnit, null)) {
			log.warn( "unit not ready!");
		}

		ScsiReadCapacity readCapacity = new ScsiReadCapacity();
		transferCommand(readCapacity, inBuffer);
		ScsiReadCapacityResponse readCapacityResponse = ScsiReadCapacityResponse.read(inBuffer);
		blockSize = readCapacityResponse.getBlockLength();
		lastBlockAddress = readCapacityResponse.getLogicalBlockAddress();

		log.debug("Block size: " + blockSize);
		log.debug("Last block address: " + lastBlockAddress);
	}

	/**
	 * Transfers the desired command to the device. If the command has a data
	 * phase the parameter <code>inBuffer</code> is used to store or read data
	 * to resp. from it. The direction of the data phase is determined by
	 * {@link com.github.mjdev.libaums.driver.scsi.commands.CommandBlockWrapper #getDirection()}
	 * .
	 * <p>
	 * Return value is true if the status of the command status wrapper is
	 * successful (
	 * {@link com.github.mjdev.libaums.driver.scsi.commands.CommandStatusWrapper #getbCswStatus()}
	 * ).
	 * 
	 * @param command
	 *            The command which should be transferred.
	 * @param inBuffer
	 *            The buffer used for reading or writing.
	 * @return True if the transaction was successful.
	 * @throws IOException
	 *             If something fails.
	 */
	private boolean transferCommand(CommandBlockWrapper command, ByteBuffer inBuffer)
			throws LibAumsException {
		byte[] outArray = outBuffer.array();
		outBuffer.clear();
		Arrays.fill(outArray, (byte) 0);

		command.serialize(outBuffer);
		int written = usbCommunication.bulkOutTransfer(outArray, outArray.length);
		if (written != outArray.length) {
			log.error( "Writing all bytes on command " + command + " failed!");
		}

		int transferLength = command.getdCbwDataTransferLength();
		int read = 0;
		if (transferLength > 0) {
			byte[] inArray = inBuffer.array();

			if (command.getDirection() == Direction.IN) {
				do {
					int tmp = usbCommunication.bulkInTransfer(inArray, read + inBuffer.position(),
							inBuffer.remaining() - read);
					if (tmp == -1) {
						throw LibAumsException.createWithIOException("reading failed!");
					}
					read += tmp;
				} while (read < transferLength);

				if (read != transferLength) {
					throw LibAumsException.createWithIOException("Unexpected command size (" + read + ") on response to "
							+ command);
				}
			} else {
				written = 0;
				do {
					int tmp = usbCommunication.bulkOutTransfer(inArray,
							written + inBuffer.position(), inBuffer.remaining() - written);
					if (tmp == -1) {
						throw LibAumsException.createWithIOException("writing failed!");
					}
					written += tmp;
				} while (written < transferLength);

				if (written != transferLength) {
					throw LibAumsException.createWithIOException("Could not write all bytes: " + command);
				}
			}
		}

		// expecting csw now
		read = usbCommunication.bulkInTransfer(cswBuffer, cswBuffer.length);
		if (read != CommandStatusWrapper.SIZE) {
			log.error( "Unexpected command size while expecting csw");
		}

		CommandStatusWrapper csw = CommandStatusWrapper.read(ByteBuffer.wrap(cswBuffer));
		if (csw.getbCswStatus() != CommandStatusWrapper.COMMAND_PASSED) {
			log.error( "Unsuccessful Csw status: " + csw.getbCswStatus());
		}

		if (csw.getdCswTag() != command.getdCbwTag()) {
			log.error( "wrong csw tag!");
		}

		return csw.getbCswStatus() == CommandStatusWrapper.COMMAND_PASSED;
	}

	/**
	 * This method reads from the device at the specific device offset. The
	 * devOffset specifies at which block the reading should begin. That means
	 * the devOffset is not in bytes!
	 */
	@Override
	public synchronized void read(long devOffset, ByteBuffer dest) throws LibAumsException {
		//long time = System.currentTimeMillis();
		// TODO try to make this more efficient by for example only allocating
		// blockSize and making it global
		ByteBuffer buffer;
		if (dest.remaining() % blockSize != 0) {
			log.debug("WARNING: we have to round up size to next block sector");
			int rounded = blockSize - dest.remaining() % blockSize + dest.remaining();
			buffer = ByteBuffer.allocate(rounded);
			buffer.limit(rounded);
		} else {
			buffer = dest;
		}

		ScsiRead10 read = new ScsiRead10((int) devOffset, buffer.remaining(), blockSize);
		//log.debug( "reading: " + read);
		transferCommand(read, buffer);

		if (dest.remaining() % blockSize != 0) {
			System.arraycopy(buffer.array(), 0, dest.array(), dest.position(), dest.remaining());
		}

		dest.position(dest.limit());

		//log.debug( "read time: " + (System.currentTimeMillis() - time));
	}

	/**
	 * This method writes from the device at the specific device offset. The
	 * devOffset specifies at which block the writing should begin. That means
	 * the devOffset is not in bytes!
	 */
	@Override
	public synchronized void write(long devOffset, ByteBuffer src) throws LibAumsException {
		//long time = System.currentTimeMillis();
		// TODO try to make this more efficient by for example only allocating
		// blockSize and making it global
		ByteBuffer buffer;
		if (src.remaining() % blockSize != 0) {
			log.debug("WARNING: we have to round up size to next block sector");
			int rounded = blockSize - src.remaining() % blockSize + src.remaining();
			buffer = ByteBuffer.allocate(rounded);
			buffer.limit(rounded);
			System.arraycopy(src.array(), src.position(), buffer.array(), 0, src.remaining());
		} else {
			buffer = src;
		}

		ScsiWrite10 write = new ScsiWrite10((int) devOffset, buffer.remaining(), blockSize);
		//log.debug( "writing: " + write);
		transferCommand(write, buffer);

		src.position(src.limit());

		//log.debug( "write time: " + (System.currentTimeMillis() - time));
	}

	@Override
	public int getBlockSize() {
		return blockSize;
	}

	public String toString() {
		return String.format("ScsiBlockDevice [device=%s,blockSize=%d,lastBlockAddress=%d]",
			this.usbCommunication.getReadableDeviceName(),
			blockSize,
			lastBlockAddress);
	}
}
