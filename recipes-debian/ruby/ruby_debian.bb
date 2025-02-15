# base recipe: meta/recipes-devtools/ruby/ruby_2.5.5.bb
# base branch: zeus 
# base commit: 94f6b31befda5c496f65e863a6f8152b42d7ebf0

inherit debian-package
require recipes-debian/sources/ruby2.5.inc

DEBIAN_UNPACK_DIR = "${WORKDIR}/ruby-${PV}"
require ruby.inc
SRC_URI += " \
           file://0001-configure.ac-check-finite-isinf-isnan-as-macros-firs.patch \
           file://run-ptest \
           file://add-test-preparation-and-fix-result-output.patch \
           "

PACKAGECONFIG ??= ""
PACKAGECONFIG += "${@bb.utils.filter('DISTRO_FEATURES', 'ipv6', d)}"

PACKAGECONFIG[valgrind] = "--with-valgrind=yes, --with-valgrind=no, valgrind"
PACKAGECONFIG[gmp] = "--with-gmp=yes, --with-gmp=no, gmp"
PACKAGECONFIG[ipv6] = "--enable-ipv6, --disable-ipv6,"

EXTRA_AUTORECONF += "--exclude=aclocal"

EXTRA_OECONF = "\
    --disable-versioned-paths \
    --disable-rpath \
    --disable-dtrace \
    --enable-shared \
    --enable-load-relative \
    --with-pkg-config=pkg-config \
"

do_install() {
    oe_runmake 'DESTDIR=${D}' install
}

do_install_append_class-target () {
    # Find out rbconfig.rb from .installed.list
    rbconfig_rb=`grep rbconfig.rb ${B}/.installed.list`
    # Remove build host directories
    sed -i -e 's:--sysroot=${STAGING_DIR_TARGET}::g' \
           -e s:'--with-libtool-sysroot=${STAGING_DIR_TARGET}'::g \
           -e 's|${DEBUG_PREFIX_MAP}||g' \
           -e 's:${HOSTTOOLS_DIR}/::g' \
           -e 's:${RECIPE_SYSROOT_NATIVE}::g' \
           -e 's:${RECIPE_SYSROOT}::g' \
           -e 's:${BASE_WORKDIR}/${MULTIMACH_TARGET_SYS}::g' \
        ${D}$rbconfig_rb

    # Find out created.rid from .installed.list
    created_rid=`grep created.rid ${B}/.installed.list`
    # Remove build host directories
    sed -i -e 's:${WORKDIR}::g' ${D}$created_rid

}

do_install_ptest () {
    cp -rf ${S}/test ${D}${PTEST_PATH}/
    cp -rf ${S}/debian/tests ${D}${PTEST_PATH}/
    mkdir -p ${D}${PTEST_PATH}/bin/
    ln -s ${bindir}/erb ${D}${PTEST_PATH}/bin/erb
    cp -r ${S}/include ${D}/${libdir}/ruby/
    test_case_rb=`grep rubygems/test_case.rb ${B}/.installed.list`
    sed -i -e 's:../../../test/:../../../ptest/test/:g' ${D}/$test_case_rb
}

PACKAGES =+ "${PN}-ri-docs ${PN}-rdoc"

SUMMARY_${PN}-ri-docs = "ri (Ruby Interactive) documentation for the Ruby standard library"
RDEPENDS_${PN}-ri-docs = "${PN}"
FILES_${PN}-ri-docs += "${datadir}/ri"

SUMMARY_${PN}-rdoc = "RDoc documentation generator from Ruby source"
RDEPENDS_${PN}-rdoc = "${PN}"
FILES_${PN}-rdoc += "${libdir}/ruby/*/rdoc ${bindir}/rdoc"

FILES_${PN} += "${datadir}/rubygems"
FILES_${PN} += "${libdir}/ruby/2.5.0/"
FILES_${PN} += "${libdir}/ruby/gems"
FILES_${PN} += "${libdir}/ruby/site_ruby"
FILES_${PN} += "${libdir}/ruby/vendor_ruby"

RDEPENDS_${PN}-ptest += " dpkg dpkg-perl findutils make coreutils"
RDEPENDS_${PN}-ptest += " ${PN}-rdoc ${PN}-ri-docs "
RDEPENDS_${PN}-ptest += " openssl readline libyaml zlib libffi gmp gperf tzdata openssh"

FILES_${PN}-ptest_append_class-target += "${libdir}/ruby/include"

BBCLASSEXTEND = "native"
