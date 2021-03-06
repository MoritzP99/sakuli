/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sakuli.services.forwarder.checkmk;

import org.sakuli.datamodel.AbstractTestDataEntity;
import org.sakuli.exceptions.SakuliForwarderCheckedException;
import org.sakuli.services.ResultService;
import org.sakuli.services.forwarder.AbstractTeardownService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * @author Georgi Todorov
 */
@ProfileCheckMK
@Component
public class CheckMKResultServiceImpl extends AbstractTeardownService implements ResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckMKResultServiceImpl.class);

    @Autowired
    private CheckMKProperties checkMKProperties;
    @Autowired
    private CheckMKTemplateOutputBuilder outputBuilder;

    @Override
    public int getServicePriority() {
        return 10;
    }

    @Override
    public void tearDown(Optional<AbstractTestDataEntity> dataEntity, boolean asyncCall) {
        dataEntity.ifPresent(data -> {
            try {
                LOGGER.info("======= WRITE FILE FOR CHECK_MK ======");
                String output = outputBuilder.createOutput(data);
                LOGGER.debug(String.format("Output for check_mk:\n%s", output));
                writeToFile(createSpoolFilePath(data.getId()), output);
                LOGGER.info("======= FINISHED: WRITE FILE FOR CHECK_MK ======");
            } catch (Exception e) {
                handleTeardownException(e, asyncCall, data);
            }
        });
    }

    protected Path createSpoolFilePath(String dataId) {
        String spoolDir = checkMKProperties.getSpoolDir();
        String fileName = new StringBuilder()
                .append(checkMKProperties.getFreshness())
                .append(isEmpty(checkMKProperties.getSpoolFilePrefix())
                        ? ""
                        : "_" + checkMKProperties.getSpoolFilePrefix()
                )
                .append("_")
                .append(dataId)
                .toString();
        return Paths.get(spoolDir + File.separator + fileName);
    }

    private void writeToFile(Path file, String output) throws SakuliForwarderCheckedException {
        try {
            LOGGER.info(String.format("Write file to '%s'", file));
            Files.write(file, output.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new SakuliForwarderCheckedException(e,
                    String.format("Unexpected error by writing the output for check_mk to the following file '%s'", file));
        }
    }

}
