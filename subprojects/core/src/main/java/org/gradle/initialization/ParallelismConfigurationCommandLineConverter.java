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

package org.gradle.initialization;

import org.gradle.cli.AbstractCommandLineConverter;
import org.gradle.cli.CommandLineArgumentException;
import org.gradle.cli.CommandLineParser;
import org.gradle.cli.ParsedCommandLine;
import org.gradle.concurrent.ParallelismConfiguration;

import static org.gradle.initialization.GradleBuildOptions.PARALLEL;
import static org.gradle.initialization.GradleBuildOptions.MAX_WORKERS;

public class ParallelismConfigurationCommandLineConverter extends AbstractCommandLineConverter<ParallelismConfiguration> {

    public ParallelismConfiguration convert(ParsedCommandLine options, ParallelismConfiguration target) throws CommandLineArgumentException {
        if (options.hasOption(PARALLEL.getCommandLineOption().getOption())) {
            target.setParallelProjectExecutionEnabled(true);
        }

        if (options.hasOption(MAX_WORKERS.getCommandLineOption().getOption())) {
            String value = options.option(MAX_WORKERS.getCommandLineOption().getOption()).getValue();
            try {
                int workerCount = Integer.parseInt(value);
                if (workerCount < 1) {
                    invalidMaxWorkersSwitchValue(value);
                }
                target.setMaxWorkerCount(workerCount);
            } catch (NumberFormatException e) {
                invalidMaxWorkersSwitchValue(value);
            }
        }

        return target;
    }

    private ParallelismConfiguration invalidMaxWorkersSwitchValue(String value) {
        throw new CommandLineArgumentException(String.format("Argument value '%s' given for --%s option is invalid (must be a positive, non-zero, integer)", value, MAX_WORKERS.getCommandLineOption().getOption()));
    }

    public void configure(CommandLineParser parser) {
        PARALLEL.getCommandLineOption().registerOption(parser);
        MAX_WORKERS.getCommandLineOption().registerOption(parser).hasArgument();
    }
}
