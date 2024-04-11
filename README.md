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

The library can be included into your project like this: NOT AVAILABLE YET

```
compile 'com.github.mjdev:libaums:0.3'
```
There are plenty examples inside the project how to use usb4java (copied from usb4java samples), and I will add some examples on how to use library itself, as soon as I am so far.

### How to use (2)

I added special class where you can configure what part of library you need, so you can load Block Device and/or you can load Partitions. By default we load booth, but this can be changed like this:

```java
// turn off partition loading
UsbMassStorageLibrarySettings.LOAD_PARTITIONS = false;
// turn off block device loading
UsbMassStorageLibrarySettings.LOAD_PARTITIONS = false;
```




### Status

Just started on this project in April 2024, and it will take me some time to get it to working state. 




=======
Original README from 

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





