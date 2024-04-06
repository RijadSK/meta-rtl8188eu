<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#license">License</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project

This project shows Yocto meta layer for integrating Wifi USB driver of Realtek RTL8188EUS (highly integrated single-chip 802.11n WLAN network USB interface controller) into Linux Image of phyBoard Mira.

In case of Wifi USB stick, I have tested it with **TL-WN725N 150Mbit/s-WLAN-Nano-USB-Adapter** as on the following link: https://www.tp-link.com/de/home-networking/adapter/tl-wn725n/

In case of embedded board, I have used **phyBoard Mira** for testing, but it should work for other embedded boards with USB port without big changes.
Link with description of phyBoard Mira is here: https://www.phytec.de/produkte/single-board-computer/phyboard-mira-imx6/


<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- GETTING STARTED -->
## Getting Started

Here I will show instructions that would work in case of phyBoard Mira board, and what might change for others.

### Prerequisites

Hardware prerequisites for integrating this Yocto meta layer:
* Embedded board similar to phyBoard Mira
* WiFi USB stick which uses RTL8188EU chip, like for example TL-WN725N
* Something which can bring generated zImage, oftree, barebox and rootfs to your embedded board.

  I used **USB-C to Gigabit LAN-adapter** from ISY, but microSD Card should work as well
* USB to Serial Adapter Cable for serial communication with embedded board 

Software prerequisites for integrating this Yocto meta layer:
* **Virtual machine** for development with phyBoard Mira as from this link: https://download.phytec.de/Products/i.MX6/SO-547v5_PD22.1.0/Virtual_Machine/

  It contains everything we need for quick start


<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Integration

First we create a folder for the BSP.

Enter the following commands:
```
sudo mkdir ‐p /opt/PHYTEC_BSPs/phyBOARD‐MIRA/
sudo chmod ‐R 777 /opt/PHYTEC_BSPs/
cd /opt/PHYTEC_BSPs/phyBOARD‐MIRA/
```
Download the **phyLinux script** and set execution privileges:
```
wget ftp://ftp.phytec.de/pub/Software/Linux/Yocto/Tools/phyLinux
chmod +x ./phyLinux
```
The phyLinux script is a basic management tool for PHYTEC Yocto BSP releases. It is mainly a
tool to get started with the BSP structure. You can get all the BSP sources without the need
of interacting with repo or git.

Start the phyLinux script:
```
./phyLinux init
```
During the execution of the init command, you need to choose your processor platform
(iMX6), **PHYTEC's BSP release number (PD20.1.0)** and the hardware you are working on
(in my case phyboard-mira-imx6-3).

After you downloaded all the meta data with phyLinux, you have to set up the shell
environment variables. This needs to be done every time you open a new shell for starting
builds. We use the shell script provided by poky in its default configuration.

Type: 
```
source sources/poky/oe‐init‐build‐env build_minimal_stick
```
The current working directory of the shell should change to **build_minimal_stick/** and you are now ready
to build your first images.

Go to **conf/** directory and add new file **sanity.conf** in this directory with following contents:
```
# Sanity checks for common user misconfigurations
#
# See sanity.bbclass
#
# Expert users can confirm their sanity with "touch conf/sanity.conf"
BB_MIN_VERSION = "1.42.0"

SANITY_ABIFILE = "${TMPDIR}/abi_version"

POKY_CONF_VERSION  = "1"
LAYER_CONF_VERSION = "1"
SITE_CONF_VERSION  = "1"

INHERIT += "sanity"
```

Now open file **local.conf** in this conf/ directory. Add these contents near the beginning of this file (by changing existing lines or uncommenting them):
```
DL_DIR = "${TOPDIR}/../../downloads"
SSTATE_DIR = "${TOPDIR}/../../sstate-cache"
TMP_DIR = "{TOPDIR}/tmp"
```
Change files related to license handling:
```
LICENSE_FLAGS_WHITELIST += "license-freescale_v12-march-2016_firmware-imx"
ACCEPT_FSL_EULA = "1"
```
Change these lines in **local.conf** file (I added nano just for ease of usage on embedded board later):
```
IMAGE_INSTALL_append = " nano tree kernel-module-r8188eu \
                         linux-firmware-rtl8188 \
                         dhcp-client \
                         iw \
                         wpa-supplicant \
                         wireless-regdb-static \
                       "
IMAGE_INSTALL_remove += " packagegroup-userland "

# Autoload WiFi driver on boot
KERNEL_MODULE_AUTOLOAD += "r8188eu"
```
Add this line at the end of local.conf file to save some space on hard-disk:
```
INHERIT += "rm_work"
```

Now open file **bblayers.conf** in this conf directory. You will need to add this meta layer in this file. In my case, bblayers.conf looked like this at the end:
```
BBPATH = "${TOPDIR}"
BBFILES ?= ""

OEROOT := "/opt/PHYTEC_BSPs/phyBOARD-MIRA/sources/poky"
BBLAYERS  ?= " \
  ${OEROOT}/meta \
  ${OEROOT}/meta-poky \
  ${OEROOT}/../meta-openembedded/meta-oe \
  ${OEROOT}/../meta-openembedded/meta-networking \
  ${OEROOT}/../meta-openembedded/meta-python \
  ${OEROOT}/../meta-openembedded/meta-multimedia \
  ${OEROOT}/../meta-openembedded/meta-filesystems \
  ${OEROOT}/../meta-openembedded/meta-perl \
  "

BBLAYERS += "\
  ${OEROOT}/../meta-phytec \
  ${OEROOT}/../meta-qt5 \
  ${OEROOT}/../meta-rauc \
  ${OEROOT}/../meta-security \
  ${OEROOT}/../meta-virtualization \
  ${OEROOT}/../meta-yogurt \
  ${OEROOT}/../meta-rtl8188eu \
  "
```

Leave generated build_minimal_stick folder. If you list directories of your current folder with **ls**, it would look like this:
```
phytec-dev-ca      sources      HOWTO
python             tools        build_minimal_stick
phyLinux           ReleaseNotes
```
Now go to directory sources and clone this github repository:
```
git clone git@github.com:RijadSK/meta-rtl8188eu.git
```
  Well, I have hard-coded settings of my WLAN in **meta-rtl8188eu/recipes-bsp/drivers/files/wpa_supplicant-wlan0.conf**. Those WLAN settings would not be helpful to you. 

You would either like to change these settings or to manually type in these commands after your embedded board boots:
```
wpa_passphrase “<SSID>” “<WiFi Password>” >> /etc/wpa_supplicant/wpa_supplicant-wlano.conf
wpa_supplicant -B -i wlan0 -D wext -c /etc/wpa_supplicant/wpa_supplicant-wlan0.conf
```

In order to enable Linux kernel driver for RTL8188EU chip, we have to change Linux kernel settings as well.

In **recipes-kernel/linux/files/defconfig** are settings which we are applying during generating of Linux image: 
```
Networking support > Networking options (following should be enabled):
    [*] TCP/IP networking
        [*] IP: kernel level autoconfiguration
            [*] IP: DHCP support
            [*] IP: BOOTP support
            [*] IP: RARP support
        <*> The IPv6 protocol
Networking support > Wireless
    <M> cfg80211 - wireless configuration API
        [*] cfg80211 wireless extensions compatibility
    <M> Generic IEEE 802.11 Networking Stack (mac80211)
    [*] Minstrel (for rate control in 802.11)
Device Drivers> Network device support > Wireless LAN
    [*] Realtek devices
        < > Realtek rtlwifi family of devices
            < > Realtek RTL8192CU/RTL8188CU USB Wireless Network Adapter
            [ ] Debugging output for rtlwifi driver family
        < > RTL8723AU/RTL8188[CR]U/RTL8192[12]CU (mac80211) support
            [ ] Include support for untested Realtek 8xxx USB devices (EXPERIMENTAL)
Device Drivers > USB support, make sure USB Host is enabled:
    <*> Support for Host-side USB
    [*] Enable USB persist by default
Device Drivers > Staging Drivers
    <M> Realtek RTL8188EU Wireless LAN NIC driver
        [ ] Realtek RTL8188EU AP mode (NEW)
[*] Enable loadable module support  --->
    [*]   Module signature verification
        [*]   Require modules to be validly signed
        [*]   Automatically sign all modules
        Which hash algorithm should modules be signed with? (SHA-256)
```
If you are not using phyBoard Mira board, then you would need to change file **recipes-kernel/linux/linux-mainline_%.bbappend** so that these settings apply to your Linux image.

You can also generate these kernel settings from defconfig file yourself with command ``` bitbake -c menuconfig virtual/kernel ```.

Go back to our generated build_minimal_stick directory and type:
```
bitbake phytec-headless-bundle
```
All build artifacts will be generated in ``` build_minimal_stick/deploy/ ``` directory.

At the end, you should find **oftree**, **zImage** and **phytec-headless-image-phyboard-mira-imx6-3.ubifs** in your generated deploy/ directory.
  I have used TFTP-Server **Tftpd64** on Windows for downloading generated artifacts to embedded board (see https://pjo2.github.io/tftpd64/).
  I have used PuTTY for serial communication with my board.


<p align="right">(<a href="#readme-top">back to top</a>)</p>

## Usage

  Plug in your Wifi USB stick into board.

  After booting of your board, you can connect to your WLAN as follows:
```
wpa_passphrase “<SSID>” “<WiFi Password>” >> /etc/wpa_supplicant/wpa_supplicant-wlano.conf
wpa_supplicant -B -i wlan0 -D wext -c /etc/wpa_supplicant/wpa_supplicant-wlan0.conf
```
  I have hard-coded my WLAN settings in this layer, so it happens automatically for me.

  You can check if you have connected to WLAN by typing ``` ifconfig ```

  You can try to download contents of example website to check if everything is working as expected:
  
``` wget -qO - http://example.com ```


<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- LICENSE -->
## License

  Distributed under the MIT License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

