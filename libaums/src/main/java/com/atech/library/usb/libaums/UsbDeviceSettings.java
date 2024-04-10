package com.atech.library.usb.libaums;

import lombok.Builder;
import lombok.Data;

/**
 * Created by andy on 10.04.2024.
 */
@Data
@Builder
public class UsbDeviceSettings {
    private short vendorId;
    private short productId;
    private int interfaceNumber;
    private byte inEndpoint;
    private byte outEndpoint;
}
