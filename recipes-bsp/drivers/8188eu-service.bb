DESCRIPTION = "systemd wlan startup"
LICENSE = "MIT"
SECTION = "console/network"
LIC_FILES_CHKSUM ="file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI = "file://wifi-start.service \
           file://10-wlan0.network \ 
           file://wpa_supplicant-wlan0.conf \   
           "

SYSTEMD_SERVICE_${PN} = "wifi-start.service"
RDEPENDS_${PN} = "systemd"
DEPENDS = "systemd"
inherit systemd
S = "${WORKDIR}"

do_install () {

        install -d ${D}${systemd_unitdir}/system
        install -c -m 0755 ${WORKDIR}/wifi-start.service ${D}${systemd_unitdir}/system

        install -d ${D}${sysconfdir}/wpa_supplicant
        install -c -m 0755 ${WORKDIR}/wpa_supplicant-wlan0.conf ${D}${sysconfdir}/wpa_supplicant

        install -d ${D}${systemd_unitdir}/network
        install -c -m 0755 ${WORKDIR}/10-wlan0.network  ${D}${systemd_unitdir}/network

}


FILES_${PN} = "${base_libdir}/systemd/system/wifi-start.service"
FILES_${PN} += "${systemd_unitdir}/network/10-wlan0.network"
FILES_${PN} += "${sysconfdir}/wpa_supplicant/wpa_supplicant-wlan0.conf"

SYSTEMD_AUTO_ENABLE = "enable"

