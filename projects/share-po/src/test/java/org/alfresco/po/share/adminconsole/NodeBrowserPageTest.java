/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.adminconsole;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.util.FailedTestListener;
import org.alfresco.webdrone.exception.PageOperationException;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

import static org.alfresco.po.share.adminconsole.NodeBrowserPage.QueryType.*;
import static org.alfresco.po.share.adminconsole.NodeBrowserPage.Store.*;
import static org.testng.Assert.assertTrue;

/**
 * @author Aliaksei Boole
 */
@Listeners(FailedTestListener.class)
public class NodeBrowserPageTest extends AbstractTest
{

    NodeBrowserPage nodeBrowserPage;

    @Test(groups = "Enterprise-only", timeOut = 400000)
    public void checkThatFactoryReturnNodeBrowserPage() throws Exception
    {
        SharePage page = loginAs("admin", "admin");
        page.getNav().getNodeBrowserPage().render();
        drone.getCurrentPage().render();
    }

    @Test(dependsOnMethods = "checkThatFactoryReturnNodeBrowserPage", groups = "Enterprise-only", timeOut = 400000)
    public void executeCustomNodeSearch() throws Exception
    {
        nodeBrowserPage = drone.getCurrentPage().render();
        nodeBrowserPage.selectStore(WORKSPACE_SPACE_STORE);
        nodeBrowserPage.selectQueryType(STORE_ROOT);
        nodeBrowserPage.clickSearchButton();
        assertTrue(nodeBrowserPage.isSearchResults());
    }

    @Test(dependsOnMethods = "executeCustomNodeSearch", groups = "Enterprise-only")
    public void getSearchResults() throws Exception
    {
        nodeBrowserPage.selectQueryType(LUCENE);
        nodeBrowserPage.fillQueryField("TYPE:\"cm:category\"");
        nodeBrowserPage.clickSearchButton();
        List<NodeBrowserSearchResult> nodeBrowserSearchResults = nodeBrowserPage.getSearchResults();
        Assert.assertTrue(nodeBrowserSearchResults.size() > 0);

        NodeBrowserSearchResult language = nodeBrowserPage.getSearchResults("cm:Languages");
        Assert.assertNotNull(language);
    }

    @Test(dependsOnMethods = "getSearchResults", groups = "Enterprise-only")
    public void getSearchResultsNoResults() throws Exception
    {
        nodeBrowserPage.selectQueryType(LUCENE);
        nodeBrowserPage.fillQueryField(String.valueOf(System.currentTimeMillis()));
        nodeBrowserPage.clickSearchButton();
        List<NodeBrowserSearchResult> nodeBrowserSearchResults = nodeBrowserPage.getSearchResults();
        Assert.assertTrue(nodeBrowserSearchResults.size() == 0);
    }

    @Test(dependsOnMethods = "getSearchResultsNoResults", groups = "Enterprise-only", expectedExceptions = PageOperationException.class)
    public void getSearchResultsWithException() throws Exception
    {
        NodeBrowserSearchResult language = nodeBrowserPage.getSearchResults("sdfsadfsf");
    }

    @Test(dependsOnMethods = "getSearchResultsWithException", groups = "Enterprise-only", expectedExceptions = IllegalArgumentException.class)
    public void getSearchResultsWithIllegalArgException() throws Exception
    {
        NodeBrowserSearchResult language = nodeBrowserPage.getSearchResults("");
    }
}
