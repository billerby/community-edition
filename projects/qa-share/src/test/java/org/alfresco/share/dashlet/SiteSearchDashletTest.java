/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.share.dashlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.po.share.dashlet.SavedSearchDashlet;
import org.alfresco.po.share.dashlet.SiteSearchDashlet;
import org.alfresco.po.share.dashlet.SiteSearchItem;
import org.alfresco.po.share.enums.Dashlet;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.document.ContentDetails;
import org.alfresco.po.share.site.document.ContentType;
import org.alfresco.po.share.site.document.DocumentAspect;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.share.util.AbstractTests;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserDashboard;
import org.alfresco.share.util.ShareUserSitePage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.webdrone.testng.listener.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Shan Nagarajan
 */
@Listeners(FailedTestListener.class)
public class SiteSearchDashletTest extends AbstractTests
{

    private static Log logger = LogFactory.getLog(SiteSearchDashletTest.class);
    private String siteDomain = "siteSearch.test";
    private String expectedHelpBallonMsg = "Use this dashlet to perform a site search and view the results.\nClicking the item name takes you to the details page so you can preview or work with the item.";
    private static final String LAST_NAME = "SSLastName";
    private String firstName = "First Name";
    private String lastNameWithSpace = "Last Name";

    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        logger.info("Start Tests in: " + testName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly"})
    public void dataPrep_ALF_10608() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10608() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        Assert.assertNotNull(searchDashlet);
        Assert.assertEquals(searchDashlet.getTitle(), Dashlet.SITE_SEARCH.getDashletName());

        searchDashlet.clickOnHelpIcon();
        Assert.assertTrue(searchDashlet.isBalloonDisplayed());
        Assert.assertEquals(searchDashlet.getHelpBalloonMessage(), expectedHelpBallonMsg);
        searchDashlet.closeHelpBallon().render();
        Assert.assertFalse(searchDashlet.isBalloonDisplayed());

        Assert.assertEquals(searchDashlet.getAvailableResultSizes(), Arrays.asList("10", "25", "50", "100"));

        searchDashlet.search("").render();
        Assert.assertEquals(searchDashlet.getContent(), "No results found.");
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10609() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Add Saved Search Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10609() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        SiteDashboardPage siteDashBoard = ShareUser.openSiteDashboard(drone, siteName);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);

        String searchText = "£$%^&";
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search(searchText);
        Assert.assertEquals(searchDashlet.getContent(), "No results found.");

//        String searchText = ShareUser.getRandomStringWithNumders(1400);
        searchText = ShareUser.getRandomStringWithNumders(1400);

        // Search
        searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search(searchText).render();
        Assert.assertEquals(searchDashlet.getSearchText().length(), 1024);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10610() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };
        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        // Add Saved Search Dashlet
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Upload File
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10610() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, siteName);

        // Search
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName);
        Assert.assertEquals(ShareUser.getName(items.get(0).getItemName()), fileName);

        DocumentDetailsPage detailsPage = items.get(0).getItemName().click().render();
        Assert.assertTrue(detailsPage != null);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10615() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload File
        String fileName = "test.doc";
        String[] fileInfo = { fileName, DOCLIB };
        ShareUser.uploadFileInFolder(drone, fileInfo);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10615() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "test.doc";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "test");
        Assert.assertEquals(ShareUser.getName(items.get(0).getItemName()), fileName);

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "doc");
        Assert.assertEquals(ShareUser.getName(items.get(0).getItemName()), fileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" }, enabled = false)
    public void dataPrep_ALF_10616() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" }, enabled = false)
    public void alf_10616() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // TODO: Test 10616: Consider Enterprise equivalent test BasicSearch > cloud_421, which essentially does all the dataprep
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10617() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = "12345" + getSiteName(testName);
        String folderName = getFolderName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUserSitePage.createFolder(drone, folderName, folderName);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10617() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = "12345" + getSiteName(testName);
        String folderName = getFolderName(testName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, folderName);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), folderName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10618() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);

    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10618() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + System.currentTimeMillis() + "_1.1.txt";
        String[] fileInfo = { fileName, DOCLIB };

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, siteName);
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);
        documentLibraryPage.getSiteNav().selectSiteDashBoard().render();
        // Wait till index
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_CONTENT_DASHLET, fileName, true, siteName));

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, ".txt");
        Assert.assertEquals(ShareUser.getName(items.get(0).getItemName()), fileName);

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName);
        Assert.assertEquals(ShareUser.getName(items.get(0).getItemName()), fileName);

        DocumentDetailsPage detailsPage = items.get(0).getItemName().click().render();
        Assert.assertTrue(detailsPage != null);

        String newFileName = new StringBuffer(fileName).insert(4, '_').toString();
        ShareUser.editTextDocument(drone, newFileName, null, null);

        ShareUser.openSiteDashboard(drone, siteName);

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, ".txt");
        Assert.assertEquals(ShareUser.getName(items.get(0).getItemName()), newFileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10619() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB, "Les études de l'admissibilité de la solution dans cette société financière." };

        String[] testUserInfo = new String[] {testUser};
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10619() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName = getFileName(testName) + ".txt";
        String[] searchTerms = {"l'admissibilité", "l'admissibilite", "études", "etudes", "financière", "financiere"};
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items;

        // Loop through the search terms and verify the item is found
        for(String searchTerm: searchTerms)
        {
            items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
            Assert.assertEquals(items.size(), 1);
            Assert.assertEquals(ShareUser.getName(items.get(0).getItemName()), fileName);
        }
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10620() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10620() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName) + System.currentTimeMillis();
        String fileName = getFileName(testName) + System.currentTimeMillis() + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        List<String> searchTermsList = new ArrayList<String>();
        searchTermsList.add("modified: today" + " and modifier: " + testUser);
        // TODO - MNT-10733
        searchTermsList.add("modified: \"" + getDate("yyyy-MM-dd") + "\"" +  " and modifier: " + testUser);
        searchTermsList.add("modified: \"" + getDate("yyyy-MMM-dd") + "\"" +  " and modifier: " + testUser);
        searchTermsList.add("modified: [" + getDate("yyyy-MM-dd") + " TO TODAY]" +  " and modifier: " + testUser);
        searchTermsList.add("modified: [" + getDate("yyyy-MM-dd") + " TO NOW]" +  " and modifier: " + testUser);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload File
        DocumentLibraryPage documentLibraryPage = ShareUser.uploadFileInFolder(drone, fileInfo);
        documentLibraryPage.getSiteNav().selectSiteDashBoard().render();
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_CONTENT_DASHLET, fileName, true, siteName));

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        List<SiteSearchItem> items;

        for (String searchTerm : searchTermsList)
        {
            logger.info("Search Term: " + searchTerm);
            items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
            Assert.assertEquals(ShareUser.getName(items.get(0).getItemName()), fileName);
        }
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10621() throws Exception
    {
        String testName = getTestName();
        String firstName = testName;
        String userName = firstName + " " + LAST_NAME;
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String[] testUserInfo = new String[] { userName, firstName, LAST_NAME };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10621() throws Exception
    {
        String testName = getTestName();
        String firstName = testName;
        String userName = firstName + " " + LAST_NAME;
        String fileName = getFileName(testName) + ".txt";
        String siteName = getSiteName(testName);

        List<String> searchTermsList = new ArrayList<String>();
        searchTermsList.add("modifier: " + userName);
        searchTermsList.add("modifier: " + firstName);
        searchTermsList.add("modifier: " + LAST_NAME);

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items;

        for (String searchTerm : searchTermsList)
        {
            items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
            Assert.assertEquals(items.size(), 1);
            Assert.assertEquals(ShareUser.getName(items.get(0).getItemName()), fileName);
        }
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10622() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setDescription(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10622() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName1);
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(fileName1, fileName2);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "description: " + fileName1);
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));
        Assert.assertFalse(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10623() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;
        String fileName3 = testName + "_10.10.txt";
        String siteName = getSiteName(testName);

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setDescription(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName3);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10623() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;
        String fileName3 = testName + "_10.10.txt";
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        // Search with "file1"
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName1);
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(fileName1, fileName2);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }

        // Search with "name: file1"
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "name: " + fileName1);
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));

        // Search with "file3"
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName3);
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName3));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10624() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        // Create File1
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Create File2 with File1 as title
        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setTitle(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10624() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        // Search with File name 1 ("file1")
        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName1);
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(fileName1, fileName2);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }

        // Search with Title contains File name 1 ("title: file1")
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "title: " + fileName1);
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10625() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);


        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        DocumentLibraryPage libraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        DocumentDetailsPage detailsPage = libraryPage.selectFile(fileName2).render();
        EditDocumentPropertiesPage propertiesPage = detailsPage.selectEditProperties().render();
        propertiesPage.setAuthor(testUser);
        propertiesPage.selectSave().render();

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10625() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String fileName1 = "file1" + testName;
        String fileName2 = "file2" + testName;
        String siteName = getSiteName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName1);
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName1));

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "author: " + testUser);
        Assert.assertEquals(items.size(), 1);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName2));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10626() throws Exception
    {
        String testName = getTestName();
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";
        String[] fileInfo = { fileName, DOCLIB };

        String userName = testName + " " + LAST_NAME;
        String[] testUserInfo = new String[] { userName, firstName, lastNameWithSpace };

        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10626() throws Exception
    {
        String testName = getTestName();
        String userName = testName + " " + LAST_NAME;
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName) + ".txt";

        // Login
        ShareUser.login(drone, userName, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<String> searchTermList = new ArrayList<String>();
        searchTermList.add("creator: " + userName);
        searchTermList.add("creator: " + testName);
        searchTermList.add("creator: " + LAST_NAME);

        List<SiteSearchItem> items;

        for(String searchTerm: searchTermList)
        {
            logger.info("Search Term: " + searchTerm);
            items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
        }

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10627() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "file";
        String fileName1 = "Content1";
        String fileName2 = "Content2";
        String fileName3 = "Content3";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create Files
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setTitle(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setDescription(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName3);
        contentDetails.setContent(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10627() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = "file";
        String fileName1 = "Content1";
        String fileName2 = "Content2";
        String fileName3 = "Content3";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, fileName);
        Assert.assertEquals(items.size(), 4);
        List<String> fileNames = Arrays.asList(fileName, fileName1, fileName2, fileName3);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10628() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10628() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName);
        String[] fileInfo = { fileName, DOCLIB };

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ShareUser.uploadFileInFolder(drone, fileInfo);

        // Add Site Search Dashlet
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Wait till file has been indexed
        Assert.assertTrue(ShareUser.searchSiteDashBoardWithRetry(drone, SITE_CONTENT_DASHLET, fileName, true, siteName));
        // TODO - Remove commented code
        List<String> searchTerms = Arrays.asList("created: today",
//                "created: \"" + getDate("yyyy MM dd") + "\"",
                "created: \"" + getDate("yyyy-MM-dd") + "\"",
//                "created: \"" + getDate("yyyy MMM dd") + "\"",
                "created: \"" + getDate("yyyy-MMM-dd") + "\"",
                "created: [" + getDate("yyyy-MM-dd")+ " TO TODAY]",
                "created: [" + getDate("yyyy-MM-dd") + " TO NOW]");
        SavedSearchDashlet savedSearchDashlet;
        List<SiteSearchItem> items;

        for (String searchTerm : searchTerms)
        {
            logger.info("Search Term: " + searchTerm);
            items = ShareUserDashboard.searchSiteSearchDashlet(drone, searchTerm);
            Assert.assertEquals(items.size(), 1);
            Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));
        }
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10629() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String folderName = getFolderName(testName);
        String[] fileInfo = { fileName, DOCLIB };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ShareUser.uploadFileInFolder(drone, fileInfo);

        // Create Folder
        ShareUserSitePage.createFolder(drone, folderName, testName);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10629() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String folderName = getFolderName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "TYPE:\"cm:cmobject\"");
        Assert.assertEquals(items.size(), 2);
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, folderName));
        Assert.assertTrue(ShareUserDashboard.isContentDisplayedInSearchResults(items, fileName));

        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSavedSearchDashlet" })
    public void dataPrep_ALF_10630() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);
        String[] fileInfo = { fileName, DOCLIB };

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);
        ShareUser.uploadFileInFolder(drone, fileInfo);
        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);
        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise4.2" })
    public void alf_10630() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        DocumentLibraryPage documentLibraryPage = ShareUser.openSitesDocumentLibrary(drone, siteName);
        String nodeRef = documentLibraryPage.getFileDirectoryInfo(fileName).getNodeRef();
        documentLibraryPage.getSiteNav().selectSiteDashBoard().render();

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "ID: \"" + nodeRef + "\"");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
        ShareUser.logout(drone);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10631() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1";
        String fileName2 = "file2";
        String fileName3 = "file3";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName3);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        ShareUser.addAspects(drone, Arrays.asList(DocumentAspect.VERSIONABLE), fileName1);
        ShareUser.addAspects(drone, Arrays.asList(DocumentAspect.TAGGABLE), fileName2);
        ShareUser.addAspects(drone, Arrays.asList(DocumentAspect.CLASSIFIABLE), fileName3);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10631() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String fileName1 = "file1";
        String fileName2 = "file2";
        String fileName3 = "file3";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "ASPECT: \"cm:" + DocumentAspect.VERSIONABLE.getValue() + "\"");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName1);

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "ASPECT: \"cm:" + DocumentAspect.TAGGABLE.getValue() + "\"");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName2);

        // Changed to "generalclassifiable" as per MNT-10674
        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "ASPECT: \"cm:" + "generalclassifiable" + "\"");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName3);

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "ASPECT:*");
        Assert.assertEquals(items.size(), 3);
        List<String> fileNames = Arrays.asList(fileName1, fileName2, fileName3);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10632() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String textFileName = "file1.txt";
        String xmlFileName = "note.xml";
        String htmlFileName = "heading.html";

        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Upload TXT File
        String[] textFileInfo = { textFileName };
        ShareUser.uploadFileInFolder(drone, textFileInfo);

        // Uploading XML file.
        String[] xmlFileInfo = { xmlFileName };
        ShareUser.uploadFileInFolder(drone, xmlFileInfo);

        // Uploading HTML file.
        String[] htmlFileInfo = { htmlFileName };
        ShareUser.uploadFileInFolder(drone, htmlFileInfo);

        ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        ShareUser.logout(drone);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10632() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName);
        String textFileName = "file1.txt";
        String xmlFileName = "note.xml";
        String htmlFileName = "heading.html";

        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);
        ShareUser.openSiteDashboard(drone, siteName);

        List<SiteSearchItem> items = ShareUserDashboard.searchSiteSearchDashlet(drone, "content.mimetype:text\\/xml");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), xmlFileName);

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "content.mimetype:text\\/plain");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), textFileName);

        items = ShareUserDashboard.searchSiteSearchDashlet(drone, "content.mimetype:text\\/html");
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), htmlFileName);

        ShareUser.logout(drone);
    }

    /*
    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10634() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10634() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String textFileName = "Context.txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(textFileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("KEYWORD(" + textFileName + ")").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), textFileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10635() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10635() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = "qfile";
        String fileName1 = "file1";
        String fileName2 = "file2";
        String fileName3 = "file3";
        String fileName4 = "file4";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setTitle(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setDescription(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName3);
        contentDetails.setContent(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName4);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("ALL:\"*q*\"").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 4);
        List<String> fileNames = Arrays.asList(fileName, fileName1, fileName2, fileName3);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10636() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10636() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName) + ".txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        DocumentLibraryPage libraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        DocumentDetailsPage detailsPage = libraryPage.selectFile(fileName).render();
        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();
        List<DocumentAspect> aspects = Arrays.asList(DocumentAspect.DUBLIN_CORE);
        aspectsPage.add(aspects).render();
        aspectsPage.clickApplyChanges().render();

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search(" ASPECT:\"{http://www.alfresco.org/model/content/1.0}dublincore\"").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10637() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10637() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = "1234" + ".txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("12?4").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("*234").render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("=" + fileName).render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("\"1234\"").render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("????").render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("\"???4\"").render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10638() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10638() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName) + "_1";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setContent(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("{http://www.alfresco.org/model/content/1.0}name: " + fileName).render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

        searchDashlet.search("@{http://www.alfresco.org/model/content/1.0}name: " + fileName).render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10639() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10639() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName) + ".txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("cm_name = " + fileName).render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10640() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10640() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName1 = "test.txt";
        String fileName2 = "tab.txt";
        String fileName3 = "alf.txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName3);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("+created:[MIN TO NOW] AND +modifier: (" + testUser + ") AND !test AND -(tab)").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName3);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10641() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10641() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName) + ".txt";
        String fileName2 = getFileName(testName) + "_2.txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        DocumentLibraryPage libraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        DocumentDetailsPage detailsPage = libraryPage.selectFile(fileName).render();
        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();
        List<DocumentAspect> aspects = Arrays.asList(DocumentAspect.VERSIONABLE);
        aspectsPage.add(aspects).render();
        aspectsPage.clickApplyChanges().render();

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("cm:initialVersion:true").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10642() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10642() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName) + ".txt";
        String fileName2 = getFileName(testName) + "_2.txt";
        String comment = getComment(fileName);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        DocumentLibraryPage libraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        DocumentDetailsPage detailsPage = libraryPage.selectFile(fileName).render();
        detailsPage = detailsPage.addComment(comment).render();

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("fm:commentCount: 1").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10643() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10643() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName) + ".txt";
        String fileName2 = getFileName(testName) + "_2.txt";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        DocumentLibraryPage libraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        DocumentDetailsPage detailsPage = libraryPage.selectFile(fileName).render();
        SelectAspectsPage aspectsPage = detailsPage.selectManageAspects().render();
        List<DocumentAspect> aspects = Arrays.asList(DocumentAspect.VERSIONABLE);
        aspectsPage.add(aspects).render();
        detailsPage = aspectsPage.clickApplyChanges().render();
        EditTextDocumentPage textDocumentPage = detailsPage.selectInlineEdit().render();
        ContentDetails details = new ContentDetails();
        details.setName(fileName);
        details.setContent(fileName);
        detailsPage = textDocumentPage.save(details).render();

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("cm:versionLabel: 1.1").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);

    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10644() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10644() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName1 = getFileName(testName) + "_1";
        String fileName2 = getFileName(testName) + "_2";
        String fileName3 = getFileName(testName) + "_3";
        String comment = getComment(fileName1);

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setDescription(fileName2);
        DocumentLibraryPage libraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        DocumentDetailsPage detailsPage = libraryPage.selectFile(fileName2).render();
        detailsPage = detailsPage.addComment(comment).render();

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName3);
        libraryPage = ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);
        detailsPage = libraryPage.selectFile(fileName3).render();
        detailsPage = detailsPage.addComment(comment).render();
        detailsPage = detailsPage.addComment(comment).render();

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("fm:commentCount:1..2").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(fileName2, fileName3);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10645() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10645() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName1 = getFileName(testName) + "_1";
        String file1Content = "big red apple";
        String fileName2 = getFileName(testName) + "_2";
        String file2Content = "big red tasty sweet apple";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(file1Content);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setContent(file2Content);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("TEXT:(big * apple)").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(fileName1, fileName2);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }

        searchDashlet.search("TEXT:(big *(1) apple)").render();
        items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName1);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10646() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10646() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName) + "_1";
        String fileContent = "big red apple";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setContent(fileContent);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("\"big red apple\"^3").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10647() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10647() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName1 = getFileName(testName) + "_1";
        String file1Content = "this is an item";
        String fileName2 = getFileName(testName) + "_2";
        String file2Content = "this is the best item";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName1);
        contentDetails.setContent(file1Content);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        contentDetails = new ContentDetails();
        contentDetails.setName(fileName2);
        contentDetails.setContent(file2Content);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("\"this[^] item[$]\"").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 2);
        List<String> fileNames = Arrays.asList(fileName1, fileName2);
        for (SiteSearchItem siteSearchItem : items)
        {
            Assert.assertTrue(fileNames.contains(siteSearchItem.getItemName().getDescription()));
        }
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10648() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10648() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName) + "_1";
        String fileContent = "<type>d:text</type>";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setContent(fileContent);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("\\<type\\>d\\:text\\</type\\>").render();
        List<SiteSearchItem> items = searchDashlet.getSearchItems();
        Assert.assertEquals(items.size(), 1);
        Assert.assertEquals(items.get(0).getItemName().getDescription(), fileName);
    }

    @Test(groups = { "DataPrepSiteSearchDashlet", "EnterpriseOnly" })
    public void dataPrep_ALF_10649() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);

        // User
        String[] testUserInfo = new String[] { testUser };
        CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);
    }

    @Test(groups = { "Enterprise42", "EnterpriseOnly" })
    public void alf_10649() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameForDomain(testName, siteDomain);
        String siteName = getSiteName(testName + System.currentTimeMillis());
        String fileName = getFileName(testName) + "_1";
        String fileContent = "apple and banana";

        // Login
        ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

        // Create Site
        ShareUser.createSite(drone, siteName, SITE_VISIBILITY_PUBLIC);

        // Create File
        ContentDetails contentDetails = new ContentDetails();
        contentDetails.setName(fileName);
        contentDetails.setContent(fileContent);
        ShareUser.createContent(drone, contentDetails, ContentType.PLAINTEXT);

        // Add Site Search Dashlet
        SiteDashboardPage siteDashBoard = ShareUserDashboard.addDashlet(drone, siteName, Dashlet.SITE_SEARCH);

        // Verify dashlet is added successfully.
        Assert.assertNotNull(siteDashBoard);
        SiteSearchDashlet searchDashlet = ShareUserDashboard.getSiteSearchDashlet(drone);
        searchDashlet.search("TEXT:apple..banana").render();
        Assert.assertEquals(searchDashlet.getContent(), "No results found.");
    }
    */
}