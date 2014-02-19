/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.share.dashlet;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import junit.framework.Assert;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.po.share.util.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Integration test site welcome dashlet page elements.
 * 
 * @author Shan Nagarajan
 * @since  1.6.1
 */
@Test(groups = "alfresco-one")
@Listeners(FailedTestListener.class)
public class SiteWelcomeDashletTest extends AbstractSiteDashletTest
{
    private static final String SITE_WELCOME = "welcome-site";
    DashBoardPage dashBoard;

    @BeforeClass
    public void loadFile() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteName = "WelcomeDashletTests" + System.currentTimeMillis();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }
    
    @AfterClass
    public void deleteSite()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test
    public void instantiateDashlet()
    {
        SiteWelcomeDashlet dashlet = new SiteWelcomeDashlet(drone);
        assertNotNull(dashlet);
    }

    /**
     * Test process of accessing my documents dashlet from the dash board view.
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "instantiateDashlet")
    public void selectSiteWelcometDashlet() throws Exception
    {
        SiteWelcomeDashlet dashlet = siteDashBoard.getDashlet(SITE_WELCOME).render();
        if (alfrescoVersion.equals(AlfrescoVersion.Cloud))
        {
            assertEquals(dashlet.getOptions().size(), 3);
        }
        else
        {
            assertEquals(dashlet.getOptions().size(), 4);
        }
    }
    
    /**
     * Test the Remove welcome dashlet button.
     * Try to find the welcome Dashlet after removing it should throw Exception.
     * 
     * @throws Exception
     */
    @Test(dependsOnMethods = "selectSiteWelcometDashlet")
    public void removeAndFindDashlet() throws Exception 
    {
        SiteWelcomeDashlet dashlet = siteDashBoard.getDashlet(SITE_WELCOME).render();
        Assert.assertTrue(siteDashBoard.isWelcomeMessageDashletDisplayed());
        siteDashBoard = dashlet.removeDashlet().render();
        Assert.assertFalse(siteDashBoard.isWelcomeMessageDashletDisplayed());
    }

}