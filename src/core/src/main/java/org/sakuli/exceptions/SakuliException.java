/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2018 the original author or authors.
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

package org.sakuli.exceptions;

import org.sakuli.datamodel.AbstractTestDataEntity;

import java.util.Optional;

/**
 * Marker interface for all SakuliExceptions
 */
public interface SakuliException {

    /**
     * Provides the meta information on which execution step this exception is thrown, like e.g. {@link org.sakuli.services.TeardownService#handleTeardownException(Exception, boolean, AbstractTestDataEntity)} will use it.
     *
     * @return a reference on the {@link AbstractTestDataEntity} which was executed at the point of the exception is thrown.
     */
    Optional<? extends AbstractTestDataEntity> getAsyncTestDataRef();

    /**
     * Set additional meta information to provide on which execution step in async code like the {@link org.sakuli.services.TeardownService#handleTeardownException(Exception, boolean, AbstractTestDataEntity)} will use it.
     *
     * @param testDataRef extends {@link AbstractTestDataEntity}
     */
    <T extends AbstractTestDataEntity> void setAsyncTestDataRef(T testDataRef);

    String getMessage();

    default Exception castTo() {
        return SakuliExceptionHandler.castTo(this);
    }
}
