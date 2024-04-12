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
package com.atech.library.usb.libaums.data;

import org.usb4java.LibUsbException;

import java.io.IOException;

/**
 * Created by andy on 11.04.2024.
 */
public class LibAumsException extends Exception {

    public LibAumsException(String message) {
        super(message);
    }

    public LibAumsException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public static LibAumsException createWithLibUsbException(String message, int result) {
        return new LibAumsException(message, new LibUsbException(message, result));
    }

    public static LibAumsException createWithIOException(String message) {
        return new LibAumsException(message, new IOException(message));
    }

}
