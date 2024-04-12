package com.atech.library.usb.libaums.data;

import lombok.Builder;
import lombok.Data;

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
}
