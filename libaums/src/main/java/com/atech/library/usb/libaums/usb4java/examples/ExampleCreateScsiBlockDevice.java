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

package com.atech.library.usb.libaums.usb4java.examples;


import com.atech.library.usb.libaums.UsbMassStorageLibrary;
import com.atech.library.usb.libaums.data.LibAumsException;
import com.atech.library.usb.libaums.data.UsbMassStorageDeviceConfig;
import com.github.mjdev.libaums.UsbMassStorageDevice;
import com.github.mjdev.libaums.driver.scsi.ScsiBlockDevice;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import java.util.List;

/**
 * Created by andy on 12.04.2024.
 */
@Slf4j
public class ExampleCreateScsiBlockDevice {

    public ExampleCreateScsiBlockDevice() {
        String PATTERN = "%d{HH:mm:ss,SSS} %5p [%c{1}:%L] - %m%n";
        UsbMassStorageLibrary.LOAD_PARTITIONS = false;
        //createConsoleAppender(Level.ALL, PATTERN);
        Logger.getRootLogger().removeAllAppenders();
        Logger.getRootLogger().addAppender(createConsoleAppender(Level.ALL, PATTERN));
        Logger.getLogger("com").setLevel(Level.DEBUG);
        Logger.getLogger("github").setLevel(Level.DEBUG);
    }

    public static ConsoleAppender createConsoleAppender(Level threshold, String pattern) {
        ConsoleAppender console = new ConsoleAppender();
        console.setLayout(new PatternLayout(pattern));
        console.setThreshold(threshold);
        console.activateOptions();
        return console;
    }



    private void findDevice() {
        try {
            List<UsbMassStorageDeviceConfig> listOfAttachedUsbMassStorageDevices = UsbMassStorageDevice.getListOfAttachedUsbMassStorageDevices();

            log.info("Found {} devices: ", listOfAttachedUsbMassStorageDevices.size());

            for (UsbMassStorageDeviceConfig device : listOfAttachedUsbMassStorageDevices) {
                log.info("{}", device);
            }


        } catch (LibAumsException e) {
            log.error("Error getting device list." + e.getMessage(), e);
        }
    }

    @SneakyThrows
    public void connectToVerio() {
        UsbMassStorageDevice massStorageDevice = null;
        try {
            UsbMassStorageDeviceConfig config = UsbMassStorageDeviceConfig.builder()
                    .vendorId((short) 10086)
                    .productId((short) 4)
                    .interfaceNumber(0)
                    .inEndpointAddress((byte) -127)
                    .outEndpointAddress((byte) 1)
                    .readableDeviceId("2766:0004")
                    .build();

            log.debug("Config: {}", config);
//        vendorId=10086,productId=4,interfaceNumber=0,inEndpointAddress=-127,outEndpointAddress=1,readableDeviceId=2766:0004

            massStorageDevice = UsbMassStorageDevice.getMassStorageDevice(config);
            massStorageDevice.init();

            ScsiBlockDevice blockDevice = massStorageDevice.getBlockDevice();

            log.info("Block Device for OT Verio: {}", blockDevice);


        } catch (Exception ex) {
            log.error("Problem connecting: {}", ex.getMessage(), ex);
        } finally {
            if (massStorageDevice!=null) {
                massStorageDevice.close();
            }
        }
    }

    @SneakyThrows
    public void readMicroSDreader() {
        UsbMassStorageDevice massStorageDevice = null;
        try {
            UsbMassStorageDeviceConfig config = UsbMassStorageDeviceConfig.builder()
                    .vendorId((short) 5325)
                    .productId((short) 4626)
                    .interfaceNumber(0)
                    .inEndpointAddress((byte) -127)
                    .outEndpointAddress((byte) 2)
                    .readableDeviceId("14cd:1212")
                    .build();

            log.debug("Config: {}", config);
//        vendorId=10086,productId=4,interfaceNumber=0,inEndpointAddress=-127,outEndpointAddress=1,readableDeviceId=2766:0004

            massStorageDevice = UsbMassStorageDevice.getMassStorageDevice(config);
            massStorageDevice.init();

            ScsiBlockDevice blockDevice = massStorageDevice.getBlockDevice();

            log.info("Block Device For MicroSD: {}", blockDevice);


        } catch (Exception ex) {
            log.error("Problem connecting: {}", ex.getMessage(), ex);
        } finally {
            if (massStorageDevice!=null) {
                massStorageDevice.close();
            }
        }

    }

    public void dispose() {
        UsbMassStorageLibrary.disposeLibrary();
    }


    public static void main(String[] args) {

        ExampleCreateScsiBlockDevice flexi = new ExampleCreateScsiBlockDevice();
        flexi.connectToVerio();
        //flexi.findDevice();
        //flexi.readMicroSDreader();

        flexi.dispose();
    }


}
