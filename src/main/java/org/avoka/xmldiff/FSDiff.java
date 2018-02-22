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

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Alexey Loubyansky
 *
 */
public class FSDiff {

    private Path home1;

    public static void main(String[] args) throws Exception {

//        new FSDiff().diff(
//                "/home/aloubyansky/pm-scripts/kloop/home1",
//                "/home/aloubyansky/pm-scripts/kloop/home2");


        //diffCore();
        //diffServlet();
        diffFull();

    }

    private static void diffFull() throws Exception {
        new FSDiff().diff(
                "/home/aloubyansky/git/wildfly/dist/target/wildfly-11.0.0.Final-SNAPSHOT",
                "/home/aloubyansky/pm-test");
    }

    private static void diffServlet() throws Exception {
        new FSDiff().diff(
                "/home/aloubyansky/git/wildfly/servlet-dist/target/wildfly-servlet-11.0.0.Final-SNAPSHOT",
                "/home/aloubyansky/pm-test");
    }

    private static void diffCore() throws Exception {
        new FSDiff().diff(
                "/home/aloubyansky/git/wildfly-core/dist/target/wildfly-core-4.0.0.Alpha1-SNAPSHOT",
                "/home/aloubyansky/pm-test");
    }

    void diff(String p1, String p2) throws Exception {
        diff(Paths.get(p1), Paths.get(p2));
    }

    void diff(Path p1, Path p2) throws Exception {
        home1 = p1;
        diff(p1, p2, 0);
    }

    private void diff(Path p1, Path p2, int level) throws Exception {

        if(!Files.exists(p2)) {
            logPathMsg(p1, level, "does not exist");
            return;
        }
        if(Files.isDirectory(p1)) {
            if(Files.isDirectory(p2)) {
                logPath(p1, level);
                Set<String> processed = new HashSet<>();
                try(DirectoryStream<Path> stream = Files.newDirectoryStream(p1)) {
                    for(Path c1 : stream) {
                        final String c1Name = c1.getFileName().toString();
                        processed.add(c1Name);
                        diff(c1, p2.resolve(c1Name), level + 1);
                    }
                }
                try(DirectoryStream<Path> stream = Files.newDirectoryStream(p2)) {
                    for(Path c2 : stream) {
                        final String c2Name = c2.getFileName().toString();
                        if(!processed.contains(c2Name)) {
                            logPathMsg(c2, level, "extra content");
                        }
                    }
                }
            } else {
                logPathMsg(p1, level, "is not a directory");
            }
        } else if(Files.isDirectory(p2)) {
            logPathMsg(p1, level, "is not a file");
        } else {
            logPath(p1, level);
        }
    }

    private static void logPath(Path p, int level) {
        //logPathMsg(p, level, null);
    }

    private static void logPathMsg(Path p, int level, String msg) {
        final StringBuilder buf = new StringBuilder();
/*        if (level > 0) {
            int i = 0;
            while (i < level) {
                buf.append(' ');
                ++i;
            }
        }
*/        buf.append(p);
        if(msg != null) {
            buf.append(" - ").append(msg.toUpperCase());
        }
        log(buf.toString());
    }

    private static void log(String msg) {
        System.out.println(msg);
    }
}
