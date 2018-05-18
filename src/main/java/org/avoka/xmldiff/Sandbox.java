/*
 * Copyright 2016-2017 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.avoka.xmldiff;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarFile;

import org.jboss.modules.Module;
import org.jboss.modules.ModuleLoader;

/**
 *
 * @author Alexey Loubyansky
 */
public class Sandbox {

    private static final String SYSPROP_KEY_CLASS_PATH = "java.class.path";
    private static final String SYSPROP_KEY_JBOSS_MODULES_DIR = "jboss.modules.dir";
    private static final String SYSPROP_KEY_LOGGING_PROVIDER = "org.jboss.logging.provider";
    private static final String SYSPROP_KEY_MODULE_PATH = "module.path";
    private static final String SYSPROP_KEY_SYSTEM_MODULES = "jboss.modules.system.pkgs";

    private static final String JBOSS_MODULES_DIR_NAME = "modules";

    public static void main(String[] args) throws Exception {

        final JarFile jarFile = new JarFile(Paths.get("/home/aloubyansky/git/wildfly-core/dist/target/wildfly-core-5.0.0.Beta4-SNAPSHOT/modules/system/layers/base/org/jboss/msc/main/jboss-msc-1.4.2.Final.jar").toFile());

        final File fileOfJar = new File(jarFile.getName());
        final URL rootUrl = getJarURI(fileOfJar.toURI(), null).toURL();
        final URLConnection openConnection = rootUrl.openConnection();
        log("url connection " + openConnection);

        log("sleeping ");
        Thread.sleep(1000*20);

        log("closing jar file");
        jarFile.close();

        log("sleeping ");
        Thread.sleep(1000*20);

/*        //final Path jbossHome = Paths.get("/home/aloubyansky/git/wildfly-core/build/target/wildfly-core-5.0.0.Beta4-SNAPSHOT");
        final Path jbossHome = Paths.get("/home/aloubyansky/git/wildfly-core/dist/target/wildfly-core-5.0.0.Beta4-SNAPSHOT");

        LocalModuleLoader moduleLoader = new LocalModuleLoader(new File[] {jbossHome.resolve(JBOSS_MODULES_DIR_NAME).resolve("system").resolve("layers").resolve("base").toFile()});

        //log("sleeping ");
        //Thread.sleep(1000*20);

        //log("loading msc");
        moduleLoader.loadModule("org.jboss.msc");

        //log("sleeping ");
        //Thread.sleep(1000*20);

        //log("closing module loader");
        ((LocalModuleLoader)moduleLoader).close();
*/
        /*
        final ModuleLoader moduleLoader = setupModuleLoader(jbossHome.resolve(JBOSS_MODULES_DIR_NAME).toString());
        log("moduleLoader " + moduleLoader.getClass());
        final Configuration config = Configuration.Builder.of(jbossHome)
                .addCommandArgument("--admin-only")
                .setModuleLoader(moduleLoader)
                .build();
//        log("sleeping ");
//        Thread.sleep(1000*20);
        StandaloneServer server = EmbeddedProcessFactory.createStandaloneServer(config);
        log("sleeping ");
        Thread.sleep(1000*20);

        log("closing module loader");
        ((LocalModuleLoader)moduleLoader).close();
//        try {
//            log("server starting");
//            server.start();
//            log("server started");
//        } finally {
//            server.stop();
//            log("server stopped");
//        }
 */

        /*
        final Path p = Paths.get("/home/aloubyansky/.m2/repository/org/picketlink/picketlink-federation/2.5.5.SP10/picketlink-federation-2.5.5.SP10.jar");
        final URL[] cp = new URL[] {p.toUri().toURL()};
        ClassLoader originalCl = Thread.currentThread().getContextClassLoader();
        try (final URLClassLoader ucl = new URLClassLoader(cp, originalCl)) {
            Thread.currentThread().setContextClassLoader(ucl);
            ucl.loadClass("org.picketlink.identity.federation.api.saml.v2.sig.SAML2Signature");
        } finally {
            Thread.currentThread().setContextClassLoader(originalCl);
            //ucl.close();
        }
        */

//        System.out.println("Sleeping");
//        Thread.sleep(1000*60);
//        System.out.println("Done");
    }

    private static URI getJarURI(final URI original, final String nestedPath) throws URISyntaxException {
        final StringBuilder b = new StringBuilder();
        b.append("file:");
        assert original.getScheme().equals("file");
        final String path = original.getPath();
        assert path != null;
        final String host = original.getHost();
        if (host != null) {
            final String userInfo = original.getRawUserInfo();
            b.append("//");
            if (userInfo != null) {
                b.append(userInfo).append('@');
            }
            b.append(host);
        }
        b.append(path).append("!/");
        if (nestedPath != null) {
            b.append(nestedPath);
        }
        return new URI("jar", b.toString(), null);
    }

    private static String trimPathToModulesDir(String modulePath) {
        int index = modulePath.indexOf(File.pathSeparator);
        return index == -1 ? modulePath : modulePath.substring(0, index);
    }

    private static ModuleLoader setupModuleLoader(final String modulePath, final String... systemPackages) {

        assert modulePath != null : "modulePath not null";

        // verify the the first element of the supplied modules path exists, and if it does not, stop and allow the user to correct.
        // Once modules are initialized and loaded we can't change Module.BOOT_MODULE_LOADER (yet).

        final Path moduleDir = Paths.get(trimPathToModulesDir(modulePath));
        if (Files.notExists(moduleDir) || !Files.isDirectory(moduleDir)) {
            throw new RuntimeException("The first directory of the specified module path " + modulePath + " is invalid or does not exist.");
        }

        // deprecated property
        System.setProperty(SYSPROP_KEY_JBOSS_MODULES_DIR, moduleDir.toAbsolutePath().toString());

        final String classPath = System.getProperty(SYSPROP_KEY_CLASS_PATH);
        try {
            // Set up sysprop env
            System.clearProperty(SYSPROP_KEY_CLASS_PATH);
            System.setProperty(SYSPROP_KEY_MODULE_PATH, modulePath);

            final StringBuilder packages = new StringBuilder("org.jboss.modules,org.jboss.dmr,org.jboss.threads,org.jboss.as.controller.client");
            if (systemPackages != null) {
                for (String packageName : systemPackages) {
                    packages.append(",");
                    packages.append(packageName);
                }
            }
            System.setProperty(SYSPROP_KEY_SYSTEM_MODULES, packages.toString());

            // Get the module loader
            return Module.getBootModuleLoader();
        } finally {
            // Return to previous state for classpath prop
            if (classPath != null) {
                System.setProperty(SYSPROP_KEY_CLASS_PATH, classPath);
            }
        }
    }

    private static void log(String msg) {
        log(msg, 0);
    }

    private static void log(Object msg, int offset) {
        log(msg == null ? "null" : msg.toString(), offset);
    }

    private static void log(String msg, int offset) {
        for(int i = 0; i < offset; ++i) {
            System.out.print(' ');
        }
        System.out.println(msg);
    }
}
