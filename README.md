![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/andyrozman/libaums-usb4java/maven.yml)
[![](https://jitpack.io/v/com.atech-software/libaums-usb4java.svg)](https://jitpack.io/#com.atech-software/libaums-usb4java)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# libaums-usb4java - Library to access USB Mass Storage Devices (through LibUsb api via usb4java project)


This is fork of norani/libaums, which seems to be (more) java predeccessor of magnusja/libaums (this 
one is much more android specific and mostly written in kotlin). 

Any android references were removed, since I need this library to work in java on desktop 
computers. Instead of Android USB Stack, we will be using LibUsb thorugh usb4java project.

You can find norani/libaums here https://github.com/norani/libaums-develop, magnusja/libaums here https://github.com/magnusja/libaums and 
more about usb4java here https://github.com/usb4java/usb4java or on their website http://usb4java.org/index.html

## My contribution
You can find original disclaimer little bit down the page. My contribution is sort of middle layer which can help us when querying USB stack (devices description and such) and glue part that connect usb4java layer with libaums implementation.

### How to use

The library can be included into your project like this: 

```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependency>
    <groupId>com.atech-software</groupId>
    <artifactId>libaums-usb4java</artifactId>
    <version>0.4.0</version>
</dependency>
```
There are plenty examples inside the project how to use usb4java (copied from usb4java samples), and I will add some examples on how to use library itself, as soon as I am so far.

### How to use (2)

I added special class where you can configure what part of library you need, so you can either load Partitions or not. By default we load partitions, but this can be disabled:

```java
// turn off partition loading
UsbMassStorageLibrary.LOAD_PARTITIONS = false;
```

We can get list of all applicable devices (all Mass Storage Devices that support SCSI commands (subclass 6) 
with protocol Bulk-Only (80)):

```java
List<UsbMassStorageDeviceConfig> configs = UsbMassStorageDevice.getListOfAttachedUsbMassStorageDevices();
```

There is also one additional method here where you can filter by either idVendorString(s) (must be uppercase) or idProductString(s). Inputs are Sets 

```java
List<UsbMassStorageDeviceConfig> configs = UsbMassStorageDevice.getListOfAttachedUsbMassStorageDevices((Sets.newHashSet("27A6"), null));
```


We can get all instances of UsbMassStorageDevice by calling:

```java
UsbMassStorageDevice[] list = UsbMassStorageDevice.getMassStorageDevices(); 
```

We can get specific instance if can call this, with instance of UsbMassStorageDeviceConfig: 

```java
UsbMassStorageDeviceConfig config = new UsbMassStorageDeviceConfig();
UsbMassStorageDevice device = UsbMassStorageDevice.getMassStorageDevice(config);
```

Once we have instance we need to initialize device, which will open device and claim its interface:

```java
// see previous step we have instance of UsbMassStorageDevice called device
device.init();
```

After device is initialized we can work with it get partitions, get blockDevice:



When we are done with specific device we need to close it:

```java
device.close();
```

When we are done with library we need to call this, to dispose of LibUsb instance (instance is created automatically, but needs to be disposed manually):

```java
UsbMassStorageLibrary.disposeLibrary()
```

If you intend to work with SCSI (only part that I tested so far, although everything else should work):

```java

ScsiBlockDevice blockDevice = device.getBlockDevice();

// when writing to device we have to know where to write (3=would be what some would asume lba3)
long deviceOffset = 3; 

// write to block device (createPacket() will be your method that creates packet you wish to send)
byte[] packet = createPacket();  
        
blockDevice.write(deviceOffset, buffer);

// read from block device (in this case expected answer is 10 bytes
ByteBuffer readBuffer = ByteBuffer.allocate(10);

blockDevice.read(deviceOffset, readBuffer);

```

You can see some basic examples in ExampleCreateScsiBlockDevice.


### Logging

Library has Slf4j (Simple Log Framework for Java) integrated. I would recommend that you enable following packages for DEBUG while you are developing, but turn them to INFO for production systems and set your own logging to follow slf4j.

```
com.atech.library.usb.libaums=DEBUG
com.github.mjdev.libaums=DEBUG
```


### Status

This project was extended and changed (to usb4java), so everything that worked in original project should work here. 
I have tested SCSI block reading and writing and it works, so 1st release (0.4.0) should be fully working. If you find something wrong open Issue or in worst case scenario fix it and create PR and I will merge it (if I think its OK) and make new release. 

There is no plans at the moment to add extensions that were introduced in magnusja/libaums, although I started some work on that in next_gen branch.  
 


### Original README 

Libaums - Library to access USB Mass Storage Devices  
License: Apache 2.0 (see license.txt for details)

Author: Magnus Jahnen, jahnen at in.tum.de  
Advisor: Nils Kannengießer, nils.kannengiesser at tum.de  
Supervisor: Prof. Uwe Baumgarten, baumgaru at in.tum.de  


Technische Universität München (TUM)  
Lehrstuhl/Fachgebiet für Betriebssysteme  
www.os.in.tum.de  

The library was developed by Mr. Jahnen as part of his bachelor's thesis in 2014. It's a sub-topic of the research topic "Secure Copy Protection for Mobile Apps" by Mr. Kannengießer. The full thesis document can be downloaded [here](https://www.os.in.tum.de/fileadmin/w00bdp/www/Lehre/Abschlussarbeiten/Jahnen-thesis.pdf).

We would appreciate an information email, when you plan to use the library in your projects.





