/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * @author olubyans
 *
 */
public class XmlDiff {

    private static final XMLInputFactory inputFactory;
    static {
        final XMLInputFactory tmpIF = XMLInputFactory.newInstance();
        setIfSupported(tmpIF, XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
        setIfSupported(tmpIF, XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
        inputFactory = tmpIF;
    }

    private static void setIfSupported(final XMLInputFactory inputFactory, final String property, final Object value) {
        if (inputFactory.isPropertySupported(property)) {
            inputFactory.setProperty(property, value);
        }
    }

    private static final Path CORE_HOME = Paths.get("/home/aloubyansky/git/wildfly-core");
    private static final Path WFLY_HOME = Paths.get("/home/aloubyansky/git/wildfly");
    private static final Path WFULL_HOME = Paths.get("/home/aloubyansky/git/wildfly/build/target/wildfly-13.0.0.Alpha1-SNAPSHOT");

    public static void main(String[] args) throws Exception {

        //diffStandalone(CORE_HOME, "standalone.xml", "build");
        //diffStandalone(CORE_HOME, "standalone.xml", "dist");
        //diffDomain(CORE_HOME, "domain.xml", "build");
        //diffDomain(CORE_HOME, "domain.xml", "dist");
        //diffDomain(CORE_HOME, "host.xml", "build");
        //diffDomain(CORE_HOME, "host.xml", "dist");
        //diffDomain(CORE_HOME, "host-master.xml", "build");
        //diffDomain(CORE_HOME, "host-master.xml", "dist");
        //diffDomain(CORE_HOME, "host-slave.xml", "build");
        //diffDomain(CORE_HOME, "host-slave.xml", "dist");

        //diffStandalone(WFLY_HOME, "standalone.xml", "servlet-build");
        //diffStandalone(SERVLET_HOME, "standalone.xml", "servlet-dist");
        //diffStandalone(SERVLET_HOME, "standalone-load-balancer.xml", "servlet-build");
        //diffStandalone(SERVLET_HOME, "standalone-load-balancer.xml", "servlet-dist");
        //diffDomain(SERVLET_HOME, "domain.xml", "servlet-build");
        //diffDomain(SERVLET_HOME, "domain.xml", "servlet-dist");
        //diffDomain(SERVLET_HOME, "host.xml", "servlet-build");
        //diffDomain(SERVLET_HOME, "host.xml", "servlet-dist");
        //diffDomain(SERVLET_HOME, "host-slave.xml", "servlet-build");
        //diffDomain(SERVLET_HOME, "host-slave.xml", "servlet-dist");
        //diffDomain(SERVLET_HOME, "host-master.xml", "servlet-build");
        //diffDomain(SERVLET_HOME, "host-master.xml", "servlet-dist");

        //diffStandalone(WFLY_HOME, "standalone-activemq-colocated.xml", "build");
        //diffStandalone(WFLY_HOME, "standalone-picketlink.xml", "build");
        //diffStandalone(WFLY_HOME, "standalone-minimalistic.xml", "build");

        //diffStandalone(WFLY_HOME, "standalone.xml", "build");
        //diffStandalone(WFLY_HOME, "standalone.xml", "dist");
        //diffStandalone(WFLY_HOME, "standalone-load-balancer.xml", "build");
        //diffStandalone(WFLY_HOME, "standalone-load-balancer.xml", "dist");
        //diffStandalone(WFLY_HOME, "standalone-ha.xml", "build");
        //diffStandalone(WFLY_HOME, "standalone-ha.xml", "dist");
        //diffStandalone(WFLY_HOME, "standalone-full.xml", "build");
        //diffStandalone(WFLY_HOME, "standalone-full.xml", "dist");
        //diffStandalone(WFLY_HOME, "standalone-full-ha.xml", "build");
        //diffStandalone(WFLY_HOME, "standalone-full-ha.xml", "dist");
        diffStandalone(WFLY_HOME, "standalone-ee8.xml", "build");
        //diffStandalone(WFLY_HOME, "standalone-ee8.xml", "dist");
        //diffDomain(WFLY_HOME, "host.xml", "build");
        //diffDomain(WFLY_HOME, "host.xml", "dist");
        //diffDomain(WFLY_HOME, "host-slave.xml", "build");
        //diffDomain(WFLY_HOME, "host-slave.xml", "dist");
        //diffDomain(WFLY_HOME, "host-master.xml", "build");
        //diffDomain(WFLY_HOME, "host-master.xml", "dist");


        //diffExample(WFLY_HOME, "standalone-activemq-colocated.xml");
        //diffExample(WFLY_HOME, "standalone-azure-full-ha.xml");
        //diffExample(WFLY_HOME, "standalone-azure-ha.xml");
        //diffExample(WFLY_HOME, "standalone-ec2-full-ha.xml");
        //diffExample(WFLY_HOME, "standalone-ec2-ha.xml");
        //diffExample(WFLY_HOME, "standalone-genericjms.xml");
        //diffExample(WFLY_HOME, "standalone-gossip-full-ha.xml");
        //diffExample(WFLY_HOME, "standalone-gossip-ha.xml");
        //diffExample(WFLY_HOME, "standalone-jts.xml");
        //diffExample(WFLY_HOME, "standalone-minimalistic.xml");
        //diffExample(WFLY_HOME, "standalone-picketlink.xml");
        //diffExample(WFLY_HOME, "standalone-rts.xml");
        //diffExample(WFLY_HOME, "standalone-xts.xml");
    }

    public static void diffStandalone(Path originalHome, String config, String module) throws IOException, XMLStreamException {
        final DiffedElem diff = diffStandalone(resolveDistDir(originalHome, module + "-legacy"), "original", resolveDistDir(originalHome, module), "provisioned", config);
        DiffPrinter.print(diff);
    }

    public static void diffExample(Path originalHome, String config) throws IOException, XMLStreamException {
        final DiffedElem diff = diffExample(resolveDistDir(originalHome, "dist-legacy"), "original", resolveDistDir(originalHome, "dist"), "provisioned", config);
        DiffPrinter.print(diff);
    }

    public static void diffDomain(Path originalHome, String config, String module) throws IOException, XMLStreamException {
        final DiffedElem diff = diffDomain(resolveDistDir(originalHome, module + "-legacy"), "original", resolveDistDir(originalHome, module), "provisioned", config);
        DiffPrinter.print(diff);
    }

    private static DiffedElem diffStandalone(Path home1, String owner1, Path home2, String owner2, String config) throws IOException, XMLStreamException {
        return diff(home1.resolve("standalone").resolve("configuration").resolve(config), owner1,
                home2.resolve("standalone").resolve("configuration").resolve(config), owner2);
    }

    private static DiffedElem diffExample(Path home1, String owner1, Path home2, String owner2, String config) throws IOException, XMLStreamException {
        return diff(home1.resolve("docs").resolve("examples").resolve("configs").resolve(config), owner1,
                home2.resolve("docs").resolve("examples").resolve("configs").resolve(config), owner2);
    }

    private static DiffedElem diffDomain(Path home1, String owner1, Path home2, String owner2, String config) throws IOException, XMLStreamException {
        return diff(home1.resolve("domain").resolve("configuration").resolve(config), owner1,
                home2.resolve("domain").resolve("configuration").resolve(config), owner2);
    }

    public static DiffedElem diff(Path xml1, String owner1, Path xml2, String owner2) throws IOException, XMLStreamException {
        return diff(readElem(xml1), owner1, readElem(xml2), owner2);
    }

    private static DiffedElem diff(Elem e1, String owner1, Elem e2, String owner2) {
        if(!e1.name.equals(e2.name)) {
            throw new IllegalStateException(e1.name + " vs " + e2.name);
        }
        final DiffedElem.Builder builder = DiffedElem.builder(e1.name);

        if(e1.text != null) {
            if(e2.text == null) {
                builder.setText(owner1, e1.text);
            } else if(e1.text.equals(e2.text)) {
                builder.setText(e1.text, owner1, owner2);
            } else {
                builder.setText(e1.text, owner1, e2.text, owner2);
            }
        } else if (e2.text != null) {
            builder.setText(owner2, e2.text);
        }

        if(!e1.attrs.isEmpty()) {
            if (e2.attrs.isEmpty()) {
                for (Map.Entry<String, String> entry : e1.attrs.entrySet()) {
                    builder.addAttr(entry.getKey(), entry.getValue(), owner1);
                }
            } else {
                final Map<String, String> e1Copy = new HashMap<>(e1.attrs);
                for(Map.Entry<String, String> entry : e2.attrs.entrySet()) {
                    final String e1Value = e1Copy.remove(entry.getKey());
                    if(e1Value == null) {
                        builder.addAttr(entry.getKey(), entry.getValue(), owner2);
                    } else if(!e1Value.equals(entry.getValue())) {
                        builder.addAttr(entry.getKey(), e1Value, owner1, entry.getValue(), owner2);
                    } else {
                        builder.addAttr(entry.getKey(), e1Value, owner1, owner2);
                    }
                }
                for(Map.Entry<String, String> entry : e1Copy.entrySet()) {
                    builder.addAttr(entry.getKey(), entry.getValue(), owner1);
                }
            }
        } else if(!e2.attrs.isEmpty()) {
            for (Map.Entry<String, String> entry : e2.attrs.entrySet()) {
                builder.addAttr(entry.getKey(), entry.getValue(), owner2);
            }
        }

        if(!e1.elems.isEmpty()) {
            if (e2.elems.isEmpty()) {
                for (Elem e : e1.elems) {
                    builder.addElem(e, owner1);
                }
            } else {
                final Map<Elem.Key, Elem> e1Copy = new HashMap<>(e1.elems.size());
                for(Elem el : e1.elems) {
                    e1Copy.put(el.key, el);
                }
                for(Elem entry : e2.elems) {
                    final Elem e1Value = e1Copy.remove(entry.key);
                    if(e1Value == null) {
                        builder.addElem(entry, owner2);
                    } else {
                        builder.addElem(diff(e1Value, owner1, entry, owner2));
                    }
                }
                for(Elem entry : e1Copy.values()) {
                    builder.addElem(entry, owner1);
                }
            }
        } else if(!e2.elems.isEmpty()) {
            for (Elem entry : e2.elems) {
                builder.addElem(entry, owner2);
            }
        }

        return builder.build();
    }

    private static Elem readElem(Path p) throws XMLStreamException, IOException {
        try(BufferedReader reader = Files.newBufferedReader(p)) {
            final XMLEventReader xmlReader = inputFactory.createXMLEventReader(reader);
            while(xmlReader.hasNext()) {
                final XMLEvent event = xmlReader.nextEvent();
                if(event.isStartElement()) {
                    return readElem(event.asStartElement(), xmlReader);
                }
            }
        }
        throw new IllegalStateException();
    }

    private static Elem readElem(StartElement start, XMLEventReader reader) throws XMLStreamException {
        Elem.Builder builder = Elem.builder(start.getName().getLocalPart());

        final Iterator attrs = start.getAttributes();
        while (attrs.hasNext()) {
            final Attribute attr = (Attribute) attrs.next();
            builder.addAttr(attr.getName().getLocalPart(), attr.getValue());
        }

        final Iterator namespaces = start.getNamespaces();
        while(namespaces.hasNext()) {
            final Namespace ns = (Namespace) namespaces.next();
            builder.addAttr(ns.getPrefix(), ns.getNamespaceURI());
        }

        while(reader.hasNext()) {
            final XMLEvent event = reader.nextEvent();
            if(event.isEndElement()) {
                return builder.build();
            } else if(event.isCharacters()) {
                final String data = event.asCharacters().getData().trim();
                if(data.length() > 0) {
                    builder.setText(data);
                }
            } else if(event.isStartElement()) {
                builder.addElem(readElem(event.asStartElement(), reader));
            }
        }
        throw new IllegalStateException();
    }

    private static class Elem {

        static class Builder {

            private final String name;
            private String text;
            private Map<String, String> attrs = Collections.emptyMap();
            private List<Elem> elems = Collections.emptyList();

            private Builder(String name) {
                this.name = name;
            }

            Builder setText(String text) {
                this.text = text;
                return this;
            }

            Builder addAttr(String name, String value) {
                switch(attrs.size()) {
                    case 0:
                        attrs = Collections.singletonMap(name, value);
                        break;
                    case 1:
                        attrs = new HashMap<>(attrs);
                    default:
                        attrs.put(name, value);
                }
                return this;
            }

            Builder addElem(Elem e) {
                switch(elems.size()) {
                    case 0:
                        elems = Collections.singletonList(e);
                        break;
                    case 1:
                        elems = new ArrayList<>(elems);
                    default:
                        elems.add(e);
                }
                return this;
            }

            public Elem build() {
                return new Elem(this);
            }
        }

        static class Key {
            final String name;
            final Map<String, String> attrs;

            private Key(String name, Map<String, String> attrs) {
                this.name = name;
                this.attrs = attrs;
            }

            @Override
            public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result + ((attrs == null) ? 0 : attrs.hashCode());
                result = prime * result + ((name == null) ? 0 : name.hashCode());
                return result;
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj)
                    return true;
                if (obj == null)
                    return false;
                if (getClass() != obj.getClass())
                    return false;
                Key other = (Key) obj;
                if (attrs == null) {
                    if (other.attrs != null)
                        return false;
                } else if (!attrs.equals(other.attrs))
                    return false;
                if (name == null) {
                    if (other.name != null)
                        return false;
                } else if (!name.equals(other.name))
                    return false;
                return true;
            }
        }

        public static Builder builder(String name) {
            return new Builder(name);
        }

        final String name;
        final String text;
        final Map<String, String> attrs;
        final List<Elem> elems;
        final Key key;

        private Elem(Builder builder) {
            this.name = builder.name;
            this.text = builder.text;
            this.attrs = Collections.unmodifiableMap(builder.attrs);
            this.elems = Collections.unmodifiableList(builder.elems);
            key = new Key(name, attrs);
        }
    }

    private static class DiffedAttr {
        final String name;
        final OwnedValue owner1;
        final OwnedValue owner2;

        DiffedAttr(String name, String value, String owner1, String owner2) {
            this(name, new OwnedValue(owner1, value), new OwnedValue(owner2, value));
        }

        DiffedAttr(String name, String value, String owner) {
            this(name, new OwnedValue(owner, value), null);
        }

        DiffedAttr(String name, OwnedValue owner1, OwnedValue owner2) {
            this.name = name;
            this.owner1 = owner1;
            this.owner2 = owner2;
        }
    }

    private static class DiffedText {
        final OwnedValue owner1;
        final OwnedValue owner2;


        DiffedText(OwnedValue owner1, OwnedValue owner2) {
            this.owner1 = owner1;
            this.owner2 = owner2;
        }
    }

    private static class DiffedElem {

        static class Builder {

            private final String name;
            private DiffedText text;
            private Map<String, DiffedAttr> attrs = Collections.emptyMap();
            private List<DiffedElem> elems = Collections.emptyList();
            private String exclusiveOwner;

            private Builder(String name) {
                this.name = name;
            }

            Builder setExclusiveOwner(String owner) {
                this.exclusiveOwner = owner;
                return this;
            }

            Builder setText(String text, String owner1, String text2, String owner2) {
                return setText(new DiffedText(new OwnedValue(owner1, text), new OwnedValue(owner2, text2)));
            }

            Builder setText(String text, String owner1, String owner2) {
                return setText(new DiffedText(new OwnedValue(owner1, text), new OwnedValue(owner2, text)));
            }

            Builder setText(String owner, String text) {
                return setText(new DiffedText(new OwnedValue(owner, text), null));
            }

            Builder setText(DiffedText text) {
                this.text = text;
                return this;
            }

            Builder addAttr(String name, String value, String owner) {
                return addAttr(name, new OwnedValue(owner, value), null);
            }

            Builder addAttr(String name, String value, String owner1, String owner2) {
                return addAttr(name, new OwnedValue(owner1, value), new OwnedValue(owner2, value));
            }

            Builder addAttr(String name, String value1, String owner1, String value2, String owner2) {
                return addAttr(name, new OwnedValue(owner1, value1), new OwnedValue(owner2, value2));
            }

            Builder addAttr(String name, OwnedValue owner1, OwnedValue owner2) {
                switch(attrs.size()) {
                    case 0:
                        attrs = Collections.singletonMap(name, new DiffedAttr(name, owner1, owner2));
                        break;
                    case 1:
                        attrs = new HashMap<>(attrs);
                    default:
                        attrs.put(name, new DiffedAttr(name, owner1, owner2));
                }
                return this;
            }

            Builder addElem(DiffedElem e) {
                switch(elems.size()) {
                    case 0:
                        elems = Collections.singletonList(e);
                        break;
                    case 1:
                        elems = new ArrayList<>(elems);
                    default:
                        elems.add(e);
                }
                return this;
            }

            Builder addElem(Elem e, String owner) {
                final Builder builder = new Builder(e.name);
                builder.setExclusiveOwner(owner);
                if(e.text != null) {
                    builder.setText(new DiffedText(new OwnedValue(owner, e.text), null));
                }
                for(Map.Entry<String, String> entry : e.attrs.entrySet()) {
                    builder.addAttr(entry.getKey(), entry.getValue(), owner);
                }
                for(Elem c : e.elems) {
                    builder.addElem(c, owner);
                }
                addElem(builder.build());
                return this;
            }

            public DiffedElem build() {
                return new DiffedElem(this);
            }
        }

        public static Builder builder(String name) {
            return new Builder(name);
        }

        final String name;
        final DiffedText text;
        final Map<String, DiffedAttr> attrs;
        final List<DiffedElem> elems;
        final String exclusiveOwner;

        private DiffedElem(Builder builder) {
            this.name = builder.name;
            this.text = builder.text;
            this.attrs = Collections.unmodifiableMap(builder.attrs);
            this.elems = Collections.unmodifiableList(builder.elems);
            this.exclusiveOwner = builder.exclusiveOwner;
        }
    }

    private static class OwnedValue {
        final String owner;
        final String value;

        OwnedValue(String owner, String value) {
            this.owner = owner;
            this.value = value;
            if(value == null)
                throw new IllegalStateException();
        }
    }

    private static class DiffPrinter {

        static void print(DiffedElem e) throws IOException {
            new DiffPrinter().print(e, "");
        }

        private String currentOwner;
        private final StringBuilder buf = new StringBuilder();

        private DiffPrinter() {
        }

        private void print(DiffedElem e, String offset) throws IOException {
            final boolean resetCurrentOwner = startElement(e, offset);
            if(!e.elems.isEmpty()) {
                for(DiffedElem c : e.elems) {
                    print(c, offset + "    ");
                }
            } else if(e.text != null) {
                if(e.text.owner1 != null) {
                    if(e.text.owner2 != null) {
                        if(e.text.owner1.value.equals(e.text.owner2.value)) {
                            print(e.text.owner1.value);
                        } else {
                            println();
                            print(offset);
                            print("    ");
                            print(e.text.owner1.value);
                            print("  <!-- ");
                            print(e.text.owner1.owner);
                            println(" -->");
                            print(offset);
                            print("    ");
                            print(e.text.owner2.value);
                            print("  <!-- ");
                            print(e.text.owner2.owner);
                            println(" -->");
                            println();
                        }
                    } else {
                        if(e.text.owner1.equals(e.exclusiveOwner)) {
                            print(e.text.owner1.value);
                        } else {
                            print(e.text.owner1.value);
                            print("  <!-- ");
                            print(e.text.owner1.owner);
                            print(" -->");
                        }
                    }
                } else if(e.text.owner2 != null) {
                    if(e.text.owner2.equals(e.exclusiveOwner)) {
                        print(e.text.owner2.value);
                    } else {
                        print(e.text.owner2.value);
                        print("  <!-- ");
                        print(e.text.owner2.owner);
                        print(" -->");
                    }
                }
            }
            endElement(e, offset, resetCurrentOwner);
        }

        private void printAttrs(DiffedElem e, String offset) throws IOException {
            if(e.attrs.isEmpty()) {
                return;
            }

            String newLineOffset = null;
            final Iterator<DiffedAttr> ia = e.attrs.values().iterator();
            while(ia.hasNext()) {
                DiffedAttr a = ia.next();
                if(newLineOffset == null) {
                    print(' ');
                } else {
                    print(newLineOffset);
                }

                print(a.name);
                print("=\"");

                if(a.owner1 != null) {
                    if(a.owner2 != null) {
                        print(a.owner1.value);
                        print('\"');
                        if(!a.owner1.value.equals(a.owner2.value)) {
                            print("  <!-- ");
                            print(a.owner1.owner);
                            print(" -->");
                            println();
                            if(newLineOffset == null) {
                                final StringBuilder buf = new StringBuilder();
                                buf.append(offset);
                                int attrOffset = e.name.length() + 2;
                                for(int i = 0; i < attrOffset; ++i) {
                                    buf.append(' ');
                                }
                                newLineOffset = buf.toString();
                            }
                            print(newLineOffset);
                            print(a.name);
                            print("=\"");
                            print(a.owner2.value);
                            print("\"  <!-- ");
                            print(a.owner2.owner);
                            print(" -->");
                        }
                    } else {
                        print(a.owner1.value);
                        print('\"');
                        if(!a.owner1.owner.equals(e.exclusiveOwner)) {
                            print("  <!-- ");
                            print(a.owner1.owner);
                            print(" -->");
                        }
                    }
                } else if(a.owner2 != null) {
                    print(a.owner2.value);
                    print('\"');
                    if(!a.owner2.owner.equals(e.exclusiveOwner)) {
                        print("  <!-- ");
                        print(a.owner2.owner);
                        print(" -->");
                    }
                }
                if(ia.hasNext()) {
                    if(newLineOffset == null) {
                        final StringBuilder buf = new StringBuilder();
                        buf.append(offset);
                        int attrOffset = e.name.length() + 2;
                        for(int i = 0; i < attrOffset; ++i) {
                            buf.append(' ');
                        }
                        newLineOffset = buf.toString();
                    }
                    println();
                }
            }
        }

        private boolean startOwner(DiffedElem e) {

            if(e.exclusiveOwner != null) {
                if(!e.exclusiveOwner.equals(currentOwner)) {
                    print("> --- ");
                    print(e.exclusiveOwner);
                    println(" ---");
                    print('>');
                }
                if(currentOwner == null) {
                    currentOwner = e.exclusiveOwner;
                    return true;
                }
            }
            return false;
        }

        private boolean startElement(DiffedElem e, String offset) throws IOException {
            final boolean resetCurrentOwner = startOwner(e);
            print(offset);
            print('<');
            print(e.name);
            printAttrs(e, offset);
            if(!e.elems.isEmpty()) {
                println('>');
            } else if(e.text != null) {
                print('>');
            }
            return resetCurrentOwner;
        }

        private void endElement(DiffedElem e, String offset, boolean resetCurrentOwner) throws IOException {
            if(!e.elems.isEmpty()) {
                print(offset);
                print("</");
                print(e.name);
                print('>');
            } else if(e.text != null) {
                print("</");
                print(e.name);
                print('>');
            } else {
                print("/>");
            }
            if(resetCurrentOwner) {
                currentOwner = null;
            }
            println();
        }

        private void print(char c) {
            buf.append(c);
        }

        private void println(char c) {
            buf.append(c);
            println();
        }

        private void print(String str) {
            buf.append(str);
        }

        private void println(String str) {
            buf.append(str);
            println();
        }

        private void println() {
            System.out.println(buf.toString());
            buf.setLength(0);
            if (currentOwner != null) {
                print('>');
            }
        }
    }

    private static Path resolveDistDir(Path home, String module) {
        Path dist = home.resolve(module).resolve("target");
        if(!Files.exists(dist)) {
            throw new IllegalStateException("Path does not exist " + dist);
        }
        try(Stream<Path> stream = Files.list(dist)) {
            final Iterator<Path> i = stream.iterator();
            dist = null;
            while(i.hasNext()) {
                final Path p = i.next();
                if(!Files.isDirectory(p) || !p.getFileName().toString().startsWith("wildfly-")) {
                    continue;
                }
                if(dist != null) {
                    throw new IllegalStateException("More than one candidate");
                }
                dist = p;
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read children of " + dist, e);
        }
        if(dist == null) {
            throw new IllegalStateException("Failed to locate a dist dir in " + home.resolve(module).resolve("target"));
        }
        return dist;
    }
}
