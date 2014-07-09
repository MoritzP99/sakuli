/*
 * Sakuli - Testing and Monitoring-Tool for Websites and common UIs.
 *
 * Copyright 2013 - 2014 the original author or authors.
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

package de.consol.sakuli.services.database;

import de.consol.sakuli.datamodel.TestCase;
import de.consol.sakuli.datamodel.TestCaseStep;
import de.consol.sakuli.datamodel.TestSuite;
import de.consol.sakuli.services.database.dao.DaoTestCase;
import de.consol.sakuli.services.database.dao.DaoTestCaseStep;
import de.consol.sakuli.services.database.dao.DaoTestSuite;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.*;

public class DatabaseResultServiceImplTest {

    @Mock
    private DaoTestCase daoTestCase;
    @Mock
    private DaoTestCaseStep daoTestCaseStep;
    @Mock
    private DaoTestSuite daoTestSuite;
    @Mock
    private TestSuite testSuite;
    @InjectMocks
    private DatabaseResultServiceImpl testling;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testSaveResultsInDatabase() throws Exception {
        when(testSuite.getTestCases()).thenReturn(null);
        testling.saveAllResults();
        verify(daoTestSuite).saveTestSuiteResult();
        verify(daoTestSuite).saveTestSuiteToSahiJobs();
        verify(daoTestCase, never()).saveTestCaseResult(any(TestCase.class));
        verify(daoTestCaseStep, never()).saveTestCaseSteps(anyListOf(TestCaseStep.class), anyInt());
    }

    @Test
    public void testSaveResultsInDatabaseWithTestcases() throws Exception {

        Integer tcPrimaryKey = 22;
        TestCase tc1 = mock(TestCase.class);
        TestCaseStep tcs1 = mock(TestCaseStep.class);
        when(tc1.getDbPrimaryKey()).thenReturn(tcPrimaryKey);
        List<TestCaseStep> tcStepList = Arrays.asList(tcs1);
        when(tc1.getSteps()).thenReturn(tcStepList);

        TestCase tc2 = mock(TestCase.class);
        when(tc2.getSteps()).thenReturn(new ArrayList<TestCaseStep>());

        Map<String, TestCase> testCaseMap = new HashMap<>();
        testCaseMap.put("1", tc1);
        testCaseMap.put("2", tc2);

        when(testSuite.getTestCases()).thenReturn(testCaseMap);
        testling.saveAllResults();

        verify(daoTestSuite).saveTestSuiteResult();
        verify(daoTestSuite).saveTestSuiteToSahiJobs();
        verify(daoTestCase).saveTestCaseResult(tc1);
        verify(daoTestCase).saveTestCaseResult(tc2);
        verify(daoTestCase, times(2)).saveTestCaseResult(any(TestCase.class));
        verify(daoTestCaseStep).saveTestCaseSteps(tcStepList, tcPrimaryKey);
        verify(daoTestCaseStep).saveTestCaseSteps(anyListOf(TestCaseStep.class), anyInt());
    }

}