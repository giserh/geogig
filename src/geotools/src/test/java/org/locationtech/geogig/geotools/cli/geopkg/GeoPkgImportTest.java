/* Copyright (c) 2016 Boundless and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/org/documents/edl-v10.html
 *
 * Contributors:
 * Johnathan Garrett (Prominent Edge) - initial implementation
 */
package org.locationtech.geogig.geotools.cli.geopkg;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.locationtech.geogig.api.NodeRef;
import org.locationtech.geogig.api.Platform;
import org.locationtech.geogig.api.plumbing.LsTreeOp;
import org.locationtech.geogig.api.plumbing.LsTreeOp.Strategy;
import org.locationtech.geogig.cli.Console;
import org.locationtech.geogig.cli.GeogigCLI;

import com.google.common.collect.Lists;

public class GeoPkgImportTest extends Assert {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private GeogigCLI cli;

    @Before
    public void setUp() throws Exception {
        Console consoleReader = new Console().disableAnsi();
        cli = spy(new GeogigCLI(consoleReader));

        setUpGeogig(cli);
    }

    @After
    public void tearDown() throws Exception {
        cli.close();
    }

    @Test
    public void testImportTable() throws Exception {
        GeopkgImport importCommand = new GeopkgImport();
        importCommand.commonArgs.database = GeopkgImport.class.getResource("sample.gpkg").getFile();
        importCommand.table = "Points";
        importCommand.run(cli);
        
        Iterator<NodeRef> nodeIterator = cli.getGeogig().command(LsTreeOp.class)
                .setStrategy(Strategy.DEPTHFIRST).call();
        assertTrue("Expected repo to have some nodes, but was empty",
                nodeIterator.hasNext());
        List<String> nodeList = Lists.transform(Lists.newArrayList(nodeIterator),
                (nr) -> nr.name());
        assertTrue(nodeList.contains("Points"));
        assertTrue(nodeList.contains("1"));
        assertTrue(nodeList.contains("2"));
        assertTrue(nodeList.contains("3"));
        assertFalse(nodeList.contains("Lines"));
    }

    @Test
    public void testImportAll() throws Exception {
        GeopkgImport importCommand = new GeopkgImport();
        importCommand.commonArgs.database = GeopkgImport.class.getResource("sample.gpkg").getFile();
        importCommand.all = true;
        importCommand.run(cli);

        Iterator<NodeRef> nodeIterator = cli.getGeogig().command(LsTreeOp.class)
                .setStrategy(Strategy.DEPTHFIRST).call();
        assertTrue("Expected repo to have some nodes, but was empty", nodeIterator.hasNext());
        List<String> nodeList = Lists.transform(Lists.newArrayList(nodeIterator),
                (nr) -> nr.name());
        // Since there are Lines/1 and Points/1 etc, 1, 2, and 3 should be in the list twice. Remove
        // one after checking the first time.
        assertTrue(nodeList.contains("Points"));
        assertTrue(nodeList.contains("1"));
        nodeList.remove("1");
        assertTrue(nodeList.contains("2"));
        nodeList.remove("2");
        assertTrue(nodeList.contains("3"));
        nodeList.remove("3");
        assertTrue(nodeList.contains("Lines"));
        assertTrue(nodeList.contains("1"));
        assertTrue(nodeList.contains("2"));
        assertTrue(nodeList.contains("3"));
    }

    @Test
    public void testImportFileNotExist() throws Exception {
        GeopkgImport importCommand = new GeopkgImport();
        importCommand.commonArgs.database = "file://nonexistent.gpkg";
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Database file not found.");
        importCommand.run(cli);
    }

    private void setUpGeogig(GeogigCLI cli) throws Exception {
        final File userhome = tempFolder.newFolder("mockUserHomeDir");
        final File workingDir = tempFolder.newFolder("mockWorkingDir");
        tempFolder.newFolder("mockWorkingDir", ".geogig");

        final Platform platform = mock(Platform.class);
        when(platform.pwd()).thenReturn(workingDir);
        when(platform.getUserHome()).thenReturn(userhome);

        cli.setPlatform(platform);
    }

}
