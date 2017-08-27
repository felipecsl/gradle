/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.vcs.internal;

import com.google.common.collect.Sets;
import org.gradle.api.Action;
import org.gradle.api.artifacts.component.ModuleComponentSelector;
import org.gradle.api.vcs.VcsMapping;
import org.gradle.api.vcs.VcsMappings;
import org.gradle.api.vcs.VcsRepository;
import org.gradle.internal.Actions;
import org.gradle.internal.Cast;
import org.gradle.internal.reflect.Instantiator;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class DefaultVcsMappings implements VcsMappingsInternal {
    private final Set<Action<VcsMapping>> vcsMappings;
    private final Instantiator instantiator;

    public DefaultVcsMappings(Instantiator instantiator) {
        this.instantiator = instantiator;
        this.vcsMappings = Sets.newLinkedHashSet();
    }

    @Override
    public VcsMappings all(Action<VcsMapping> rule) {
        vcsMappings.add(rule);
        return this;
    }

    @Override
    public VcsMappings withModule(String groupName, Action<VcsMapping> rule) {
        vcsMappings.add(new GavFilteredRule(groupName, rule));
        return this;
    }

    @Override
    public <T extends VcsRepository> T vcs(Class<T> type, Action<? super T> configuration) {
        T vcs = instantiator.newInstance(type);
        configuration.execute(vcs);
        return vcs;
    }

    @Override
    public Action<VcsMapping> getVcsMappingRule() {
        return Actions.composite(vcsMappings);
    }

    @Override
    public boolean hasRules() {
        return !vcsMappings.isEmpty();
    }

    @Override
    public Collection<File> getImplicitIncludedBuilds() {
        return Collections.emptyList();
    }

    private static class GavFilteredRule implements Action<VcsMapping> {
        private final String groupName;
        private final Action<VcsMapping> delegate;

        private GavFilteredRule(String groupName, Action<VcsMapping> delegate) {
            this.groupName = groupName;
            this.delegate = delegate;
        }

        @Override
        public void execute(VcsMapping mapping) {
            if (mapping.getRequested() instanceof ModuleComponentSelector) {
                ModuleComponentSelector moduleComponentSelector = Cast.uncheckedCast(mapping.getRequested());
                if (groupName.equals(moduleComponentSelector.getGroup() + ":" + moduleComponentSelector.getModule())) {
                    delegate.execute(mapping);
                }
            }
        }
    }
}
