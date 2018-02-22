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

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.provisioning.ProvisioningException;
import org.jboss.provisioning.plugin.ProvisionedConfigHandler;
import org.jboss.provisioning.runtime.ResolvedFeatureSpec;
import org.jboss.provisioning.runtime.ResolvedSpecId;
import org.jboss.provisioning.state.ProvisionedConfig;
import org.jboss.provisioning.state.ProvisionedFeature;
import org.jboss.provisioning.state.ProvisionedFeaturePack;
import org.jboss.provisioning.state.ProvisionedState;
import org.jboss.provisioning.xml.ProvisionedStateXmlParser;

/**
 *
 * @author Alexey Loubyansky
 */
public class Sandbox {

    private static class FeatureCounter implements ProvisionedConfigHandler {

        private int total;
        private Set<ResolvedSpecId> featureSpecs = new HashSet<>();

        public void calc(ProvisionedConfig config) throws ProvisioningException {
            config.handle(this);
        }

        public int getFeaturesTotal() {
            return total;
        }

        public int getSpecsTotal() {
            return featureSpecs.size();
        }

        public void prepare(ProvisionedConfig config) throws ProvisioningException {
            total = 0;
            featureSpecs.clear();
        }

        public void nextSpec(ResolvedFeatureSpec spec) throws ProvisioningException {
            featureSpecs.add(spec.getId());

        }

        public void nextFeature(ProvisionedFeature feature) throws ProvisioningException {
            ++total;
        }
    }

    private static final FeatureCounter FEATURE_COUNTER = new FeatureCounter();

    public static void main(String[] args) throws Exception {

        printInfo("Mine:", parse("mine.xml"));
        printInfo("Emmanuel's:", parse("emmanuel.xml"));
    }

    private static void printInfo(String msg, ProvisionedState state) throws Exception {
        log(msg);

        int offset = 2;

        final Collection<ProvisionedFeaturePack> fps = state.getFeaturePacks();
        log("Feature-packs: " + fps.size(), offset);
        for(ProvisionedFeaturePack fp : fps) {
            log(fp.getGav(), offset*2);
            log("Packages:" + fp.getPackageNames().size(), offset*3);
        }

        final Set<ResolvedSpecId> fpSpecs = new HashSet<>();

        final List<ProvisionedConfig> configs = state.getConfigs();
        log("Configs: " + configs.size(), offset);
        for(ProvisionedConfig config : configs) {
            log(config.getName(), offset*2);
            FEATURE_COUNTER.calc(config);
            log("Features specs: " + FEATURE_COUNTER.getSpecsTotal(), offset*3);
            log("Features: " + FEATURE_COUNTER.getFeaturesTotal(), offset*3);
            fpSpecs.addAll(FEATURE_COUNTER.featureSpecs);
        }

        log("Specs total:" + fpSpecs.size(), offset);

        System.out.println();
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

    private static ProvisionedState parse(String name) throws Exception {
        final Path p = Paths.get("/home/aloubyansky/Downloads/provisioned").resolve(name);
        try(Reader reader = Files.newBufferedReader(p)) {
            return ProvisionedStateXmlParser.getInstance().parse(reader);
        }
    }
}
