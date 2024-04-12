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

package com.atech.library.usb.libaums;

import com.atech.library.usb.libaums.data.LibAumsException;
import lombok.Getter;
import org.usb4java.Context;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;



public class UsbMassStorageLibrary {

    public static boolean LOAD_PARTITIONS = true; // loading partitions by default
    public static boolean debugModeOnLowLevel = false;

    @Getter
    public static boolean libraryInitialized = false;

    @Getter
    static Context context;

    public static synchronized Context initLibrary() throws LibAumsException {
        if (!libraryInitialized) {
            context = new Context();

            // Initialize the libusb context
            int result = LibUsb.init(context);
            if (result < 0) {
                context = null;
                throw LibAumsException.createWithLibUsbException("Unable to initialize libusb", result);
            }

            if (debugModeOnLowLevel) {
                result = LibUsb.setOption(context, LibUsb.OPTION_LOG_LEVEL, LibUsb.LOG_LEVEL_DEBUG);
                if (result != LibUsb.SUCCESS) {
                    throw LibAumsException.createWithLibUsbException("failed to set log level", result);
                }
            }

            libraryInitialized = true;
        }

        return context;
    }


    public static void disposeLibrary() {
        if (libraryInitialized) {
            // Deinitialize the libusb context
            LibUsb.exit(context);
            libraryInitialized = false;
            context = null;
        }
    }

}
