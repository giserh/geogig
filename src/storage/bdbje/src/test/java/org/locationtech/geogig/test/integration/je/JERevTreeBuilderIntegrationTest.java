/* Copyright (c) 2012-2014 Boundless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/edl-v10.html
 *
 * Contributors:
 * Gabriel Roldan (Boundless) - initial implementation
 */
package org.locationtech.geogig.test.integration.je;

import org.junit.Test;
import org.locationtech.geogig.repository.Context;
import org.locationtech.geogig.repository.Hints;

public class JERevTreeBuilderIntegrationTest
        extends org.locationtech.geogig.test.integration.RevTreeBuilderIntegrationTest {
    @Override
    protected Context createInjector() {
        Hints hints = new Hints().uri(repositoryDirectory.toURI()).platform(createPlatform());
        return new JETestContextBuilder().build(hints);
    }

    @Test
    // $codepro.audit.disable unnecessaryOverride
    public void testPutIterate() throws Exception {
        super.testPutIterate();
    }

    @Test
    // $codepro.audit.disable unnecessaryOverride
    public void testPutRandomGet() throws Exception {
        super.testPutRandomGet();
    }

    public static void main(String... args) {
        JERevTreeBuilderIntegrationTest test = new JERevTreeBuilderIntegrationTest();
        try {
            test.setUp();
            test.testPutRandomGet();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }
}
