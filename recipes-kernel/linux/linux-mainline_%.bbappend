FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI += "file://defconfig"
KERNEL_DEFCONFIG_phyboard-mira-imx6-13 = "${WORKDIR}/defconfig"
