SUMMARY = "RTL8188EU kernel driver (wifi)"
DESCRIPTION = "RTL8188EU kernel driver"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://hal/hal_com.c;md5=c15d638f650a32fa55c5513496ae6ae7"

SRC_URI = "git://github.com/lwfinger/rtl8188eu.git;protocol=https;branch=master"
SRCREV = "f5d1c8df2e2d8b217ea0113bf2cf3e37df8cb716"

S = "${WORKDIR}/git"

PV = "1.0-git"

DEPENDS = "virtual/kernel"

inherit module

EXTRA_OEMAKE  = "ARCH=${ARCH}"
EXTRA_OEMAKE += "KSRC=${STAGING_KERNEL_BUILDDIR}"

do_compile () {
    unset LDFLAGS
    oe_runmake
}

do_install () {
    install -d ${D}/lib/modules/${KERNEL_VERSION}
    install -m 0755 ${B}/8188eu.ko ${D}/lib/modules/${KERNEL_VERSION}/8188eu.ko
}

