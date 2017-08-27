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

import org.apache.commons.io.FileUtils;
import org.gradle.api.UncheckedIOException;
import org.gradle.api.vcs.VcsRepository;

import java.io.File;
import java.io.IOException;

// TODO: Dummy VCS implementation that just uses the source directory as truth
// No caching, no concurrency protection, nada
public class DefaultVcsLifecycleHandler implements VcsLifecycleHandler {
    @Override
    public VcsCheckout init(VcsRepository vcsRepository) {
        DirectoryRepository directoryRepository = (DirectoryRepository) vcsRepository;
        try {
            FileUtils.touch(new File(directoryRepository.getSourceDir(), "checkedout"));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return new DefaultVcsCheckout(directoryRepository.getSourceDir(), vcsRepository);
    }
}
