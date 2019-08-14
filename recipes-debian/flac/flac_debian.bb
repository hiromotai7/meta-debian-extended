# base recipe: meta/recipes-multimedia/flac/flac_1.3.2.bb
# base branch: warrior
# base commit: f94f7450fbccb366952f5e91525008beb021f304

SUMMARY = "Free Lossless Audio Codec"
DESCRIPTION = "FLAC stands for Free Lossless Audio Codec, a lossless audio compression format."
HOMEPAGE = "https://xiph.org/flac/"
BUGTRACKER = "http://sourceforge.net/p/flac/bugs/"
SECTION = "libs"
LICENSE = "GFDL-1.2 & GPLv2+ & LGPLv2.1+ & BSD"
LIC_FILES_CHKSUM = "file://COPYING.FDL;md5=ad1419ecc56e060eccf8184a87c4285f \
                    file://src/Makefile.am;beginline=1;endline=17;md5=09501c864f89dfc7ead65553129817ca \
                    file://COPYING.GPL;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://src/flac/main.c;beginline=1;endline=18;md5=09777e2934947a36f13568d0beb81199 \
                    file://COPYING.LGPL;md5=fbc093901857fcd118f065f900982c24 \
                    file://src/plugin_common/all.h;beginline=1;endline=18;md5=f56cb4ba9a3bc9ec6102e8df03215271 \
                    file://COPYING.Xiph;md5=b59c1b6d7fc0fb7965f821a3d36505e3 \
                    file://include/FLAC/all.h;beginline=65;endline=70;md5=64474f2b22e9e77b28d8b8b25c983a48"

inherit debian-package
require recipes-debian/sources/flac.inc

DEPENDS = "libogg"

CVE_PRODUCT = "libflac flac"

inherit autotools gettext

EXTRA_OECONF = "--disable-oggtest \
                --with-ogg-libraries=${STAGING_LIBDIR} \
                --with-ogg-includes=${STAGING_INCDIR} \
                --disable-xmms-plugin \
                --without-libiconv-prefix \
                ac_cv_prog_NASM="" \
                "

EXTRA_OECONF += "${@bb.utils.contains("TUNE_FEATURES", "altivec", " --enable-altivec", " --disable-altivec", d)}"
EXTRA_OECONF += "${@bb.utils.contains("TUNE_FEATURES", "core2", " --enable-sse", "", d)}"
EXTRA_OECONF += "${@bb.utils.contains("TUNE_FEATURES", "corei7", " --enable-sse", "", d)}"

PACKAGES += "libflac libflac++ liboggflac liboggflac++"
FILES_${PN} = "${bindir}/*"
FILES_libflac = "${libdir}/libFLAC.so.*"
FILES_libflac++ = "${libdir}/libFLAC++.so.*"
FILES_liboggflac = "${libdir}/libOggFLAC.so.*"
FILES_liboggflac++ = "${libdir}/libOggFLAC++.so.*"

