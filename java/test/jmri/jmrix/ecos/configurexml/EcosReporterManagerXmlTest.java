package jmri.jmrix.ecos.configurexml;

import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * EcosReporterManagerXmlTest.java
 *
 * Test for the EcosReporterManagerXml class
 *
 * @author   Paul Bender  Copyright (C) 2016
 */
public class EcosReporterManagerXmlTest {

    @Test
    public void testCtor(){
      Assert.assertNotNull("EcosReporterManagerXml constructor",new EcosReporterManagerXml());
    }

    @Before
    public void setUp() {
        JUnitUtil.setUp();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }

}

