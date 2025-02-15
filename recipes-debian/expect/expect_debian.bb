# base recipe poky/meta/recipes-devtools/expect/expect_5.45.4.bb
# base branch warrior
# base commit 51223035e66c5b27799b15a0f8ebe75d24d3971b

SUMMARY = "tool for automating interactive applications according to a script"
DESCRIPTION = "Expect is a tool for automating interactive applications according to a script. \
Following the script, Expect knows what can be expected from a program and what \
the correct response should be. Expect is also useful for testing these same \
applications. And by adding Tk, you can also wrap interactive applications in \
X11 GUIs. An interpreted language provides branching and high-level control \
structures to direct the dialogue. In addition, the user can take control and \
interact directly when desired, afterward returning control to the script. \
"
HOMEPAGE = "http://sourceforge.net/projects/expect/"
LICENSE = "PD"
SECTION = "devel"

LIC_FILES_CHKSUM = "file://license.terms;md5=fbf2de7e9102505b1439db06fc36ce5c"

require recipes-debian/sources/expect.inc
inherit autotools update-alternatives debian-package

DEPENDS += " tcl"
RDEPENDS_${PN} = "tcl"

DEBIAN_UNPACK_DIR = "${WORKDIR}/${BPN}${PV}"

FILESPATH_append = ":${COREBASE}/meta/recipes-devtools/expect/expect"
SRC_URI += "file://0001-configure.in.patch \
            file://0002-tcl.m4.patch \
            file://0001-expect-install-scripts-without-using-the-fixline1-tc.patch \
            file://0001-exp_main_sub.c-Use-PATH_MAX-for-path.patch \
           "

UPSTREAM_CHECK_URI = "http://sourceforge.net/projects/expect/files/Expect/"
UPSTREAM_CHECK_REGEX = "/Expect/(?P<pver>(\d+[\.\-_]*)+)/"

S = "${WORKDIR}/${BPN}${PV}"

do_install_append() {
	install -d ${D}${libdir}
        install -m 0755 ${D}${libdir}/expect${PV}/libexpect*.so   ${D}${libdir}/
        install -m 0755 ${S}/fixline1           ${D}${libdir}/expect${PV}/
        install -m 0755 ${S}/example/*          ${D}${libdir}/expect${PV}/
        rm ${D}${libdir}/expect${PV}/libexpect*.so
        sed -e 's|$dir|${libdir}|' -i ${D}${libdir}/expect${PV}/pkgIndex.tcl
}

# Apparently the public Tcl headers are only in /usr/include/tcl8.6
# when building for the target.
TCL_INCLUDE_PATH = ""
TCL_INCLUDE_PATH_class-target = "--with-tclinclude=${STAGING_INCDIR}/tcl8.6"

EXTRA_OECONF += "--with-tcl=${STAGING_LIBDIR} \
                 --enable-shared \
                 --enable-threads \
                 --disable-rpath \
                 ${TCL_INCLUDE_PATH} \
                "
EXTRA_OEMAKE_install = " 'SCRIPTS=' "

ALTERNATIVE_${PN}  = "mkpasswd"
ALTERNATIVE_LINK_NAME[mkpasswd] = "${bindir}/mkpasswd"
# Use lower priority than busybox's mkpasswd (created when built with CONFIG_CRYPTPW)
ALTERNATIVE_PRIORITY[mkpasswd] = "40"

FILES_${PN}-dev = "${libdir_native}/expect${PV}/libexpect*.so \
                   ${includedir}/expect.h \
                   ${includedir}/expect_tcl.h \
                   ${includedir}/expect_comm.h \
                   ${includedir}/tcldbg.h \
                   ${includedir}/*.h \
                  "

FILES_${PN} += "${libdir}/libexpect${PV}.so \
                ${libdir}/expect${PV}/* \
               "

BBCLASSEXTEND = "native nativesdk"
