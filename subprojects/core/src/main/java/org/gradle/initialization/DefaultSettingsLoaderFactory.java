/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.initialization;

import org.gradle.api.vcs.internal.VcsMappingsInternal;
import org.gradle.composite.internal.IncludedBuildFactory;
import org.gradle.initialization.buildsrc.BuildSourceBuilder;
import org.gradle.internal.composite.CompositeBuildSettingsLoader;
import org.gradle.internal.composite.CompositeContextBuilder;
import org.gradle.internal.sources.VcsMappingsSettingsLoader;

public class DefaultSettingsLoaderFactory implements SettingsLoaderFactory {
    private final ISettingsFinder settingsFinder;
    private final SettingsProcessor settingsProcessor;
    private final BuildSourceBuilder buildSourceBuilder;
    private final CompositeContextBuilder compositeContextBuilder;
    private final IncludedBuildFactory includedBuildFactory;
    private final VcsMappingsInternal vcsMappingsInternal;

    public DefaultSettingsLoaderFactory(ISettingsFinder settingsFinder, SettingsProcessor settingsProcessor, BuildSourceBuilder buildSourceBuilder,
                                        CompositeContextBuilder compositeContextBuilder, IncludedBuildFactory includedBuildFactory, VcsMappingsInternal vcsMappingsInternal) {
        this.settingsFinder = settingsFinder;
        this.settingsProcessor = settingsProcessor;
        this.buildSourceBuilder = buildSourceBuilder;
        this.compositeContextBuilder = compositeContextBuilder;
        this.includedBuildFactory = includedBuildFactory;
        this.vcsMappingsInternal = vcsMappingsInternal;
    }

    @Override
    public SettingsLoader forTopLevelBuild() {
        return notifyingSettingsLoader(compositeBuildSettingsLoader());
    }

    @Override
    public SettingsLoader forNestedBuild() {
        return notifyingSettingsLoader(defaultSettingsLoader());
    }

    private SettingsLoader compositeBuildSettingsLoader() {
        return new CompositeBuildSettingsLoader(
            new VcsMappingsSettingsLoader(defaultSettingsLoader(), vcsMappingsInternal),
            compositeContextBuilder,
            includedBuildFactory
        );
    }

    private SettingsLoader defaultSettingsLoader() {
        return new DefaultSettingsLoader(
            settingsFinder,
            settingsProcessor,
            buildSourceBuilder
        );
    }


    private SettingsLoader notifyingSettingsLoader(SettingsLoader settingsLoader) {
        return new NotifyingSettingsLoader(settingsLoader);
    }

}
