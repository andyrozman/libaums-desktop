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

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Data
@Builder
public class UsbMassStorageDeviceConfig {
    private short vendorId;
    private short productId;
    private int interfaceNumber; // this is interfaceNumber or bInterfaceNumber
    private byte inEndpointAddress;
    private byte outEndpointAddress;
    private String readableDeviceId;

    public String vendorIdAsString() {
        return String.format("%04x", vendorId);
    }

    public String productIdAsString() {
        return String.format("%04x", productId);
    }


    public void updateReadableDeviceId() {
        this.readableDeviceId = String.format("%04x:%04x", vendorId, productId);
    }

    public void setProductId(short productId) {
        this.productId = productId;
        updateReadableDeviceId();
    }

    public void setVendorId(short vendorId) {
        this.vendorId = vendorId;
        updateReadableDeviceId();
    }

    public String readableDeviceId() {
        updateReadableDeviceId();
        return this.readableDeviceId;
    }

    public String toString() {
        updateReadableDeviceId();
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }


}
