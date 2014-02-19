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
package org.alfresco.po.share;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

import org.alfresco.po.share.search.AdvanceSearchCRMPage;
import org.alfresco.po.share.search.AdvanceSearchFolderPage;
import org.alfresco.po.share.search.AdvanceSearchPage;
import org.alfresco.po.share.search.AllSitesResultsPage;
import org.alfresco.po.share.search.RepositoryResultsPage;
import org.alfresco.po.share.search.SiteResultsPage;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.InviteMembersPage;
import org.alfresco.po.share.site.ManageRulesPage;
import org.alfresco.po.share.site.RulesPage;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.po.share.site.SiteFinderPage;
import org.alfresco.po.share.site.SiteMembersPage;
import org.alfresco.po.share.site.datalist.DataListPage;
import org.alfresco.po.share.site.document.CreatePlainTextContentPage;
import org.alfresco.po.share.site.document.DocumentDetailsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.document.EditDocumentPropertiesPage;
import org.alfresco.po.share.site.document.EditInGoogleDocsPage;
import org.alfresco.po.share.site.document.FolderDetailsPage;
import org.alfresco.po.share.site.document.InlineEditPage;
import org.alfresco.po.share.site.document.ManagePermissionsPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.po.share.task.EditTaskPage;
import org.alfresco.po.share.user.CloudSyncPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.user.TrashCanPage;
import org.alfresco.po.share.workflow.MyWorkFlowsPage;
import org.alfresco.po.share.workflow.StartWorkFlowPage;
import org.alfresco.po.share.workflow.WorkFlowDetailsPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.PageFactory;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;


/**
 * Alfresco Share factory, creates the appropriate page object that corresponds
 * to the browser view.
 * 
 * @author Michael Suzuki
 * @version 1.7.1
 */
public class FactorySharePage implements PageFactory
{
    private static final String CREATE_PAGE_ERROR_MSG = "Unabel to instantiate the page";
    protected static final String NODE_REF_IDENTIFIER = "?nodeRef";
    public static final String DOCUMENTLIBRARY = "documentlibrary";
    public static final String NODE_REFRESH_META_DATA_IDENTIFIER = "?refreshMetadata"; 
    protected static final String FAILURE_PROMPT = "div[id='prompt']";
    protected static ConcurrentHashMap<String, Class<? extends SharePage>> pages;
    static 
    {
        pages = new ConcurrentHashMap<String, Class<? extends SharePage>>();
        pages.put("dashboard", DashBoardPage.class);
        pages.put("site-dashboard", SiteDashboardPage.class);
        pages.put("document-details", DocumentDetailsPage.class);
        pages.put("documentlibrary", DocumentLibraryPage.class);
        pages.put("folder-details", FolderDetailsPage.class);
        pages.put("my-tasks", MyTasksPage.class);
        pages.put("my-workflows", MyWorkFlowsPage.class);
        pages.put("workflow-details", WorkFlowDetailsPage.class);
        pages.put("people-finder", PeopleFinderPage.class);
        pages.put("profile", MyProfilePage.class);
        pages.put("user-trashcan", TrashCanPage.class);
        pages.put("site-finder", SiteFinderPage.class);
        pages.put("wiki-page", WikiPage.class);
        pages.put("change-password", ChangePasswordPage.class);
        pages.put("repository", RepositoryPage.class);
        pages.put("manage-permissions", ManagePermissionsPage.class);
        pages.put("plain", CreatePlainTextContentPage.class);
        pages.put("inline-edit", InlineEditPage.class);
        pages.put("edit-metadata", EditDocumentPropertiesPage.class);
        pages.put("site-members", SiteMembersPage.class);
        pages.put("invite", InviteMembersPage.class);
        pages.put("users-create", NewUserPage.class);
        pages.put("users-view", UserProfilePage.class);
        pages.put("users-update", EditUserPage.class);
        pages.put("users", UserSearchPage.class);
        pages.put("customise-site", CustomizeSitePage.class);
        pages.put("customise-site-dashboard", CustomiseSiteDashboardPage.class);
        pages.put("data-lists", DataListPage.class);
        pages.put("advsearch", AdvanceSearchPage.class);
        pages.put("advfolder-search", AdvanceSearchFolderPage.class);
        pages.put("advCRM-search", AdvanceSearchCRMPage.class);
        pages.put("googledocsEditor", EditInGoogleDocsPage.class);
        pages.put("siteResultsPage", SiteResultsPage.class);
        pages.put("repositoryResultsPage", RepositoryResultsPage.class);
        pages.put("allSitesResultsPage", AllSitesResultsPage.class);
        pages.put("folder-rules", ManageRulesPage.class);
        pages.put("rule-edit", RulesPage.class);
        pages.put("task-edit", EditTaskPage.class);
        pages.put("search", SiteResultsPage.class);
        pages.put("start-workflow", StartWorkFlowPage.class);
        pages.put("user-cloud-auth", CloudSyncPage.class);
    }
    
    public HtmlPage getPage(WebDrone drone)
    {
    	return resolvePage(drone);
    }
    
    /**
     * Creates the appropriate page object based on the current page the
     * {@link WebDrone} is on.
     * 
     * @param drone WebDrone Alfresco unmanned web browser client
     * @return SharePage the page object response
     * @throws PageException
     */
    public static HtmlPage resolvePage(final WebDrone drone) throws PageException
    {
        // Determine if user is logged in if not return login page
        if (drone.getTitle().toLowerCase().contains("login"))
        {
            return new LoginPage(drone);
        }
        else
        {
            try
            {
                WebElement errorPrompt = drone.find(By.cssSelector(FAILURE_PROMPT));
                if(errorPrompt.isDisplayed())
                {
                    return new ShareErrorPopup(drone);
                }
            }
            catch(NoSuchElementException nse){ }

            // Determine what page we're on based on url
            return getPage(drone.getCurrentUrl(), drone);
        }
    }
    
    /**
     * Factory method to produce a page that defers all work until render time.
     * 
     * @param drone                 browser driver
     * @return                      a page that is meaningless until {@link SharePage#render()} is called
     */
    public static SharePage getUnknownPage(final WebDrone drone)
    {
        return new UnknownSharePage(drone);
    }

    /**
     * Instantiates the page object matching the argument.
     * @param drone {@link WebDrone}
     * @param pageClassToProxy expected Page object
     * @return {@link SharePage} page response
     */
    protected static <T extends HtmlPage> T instantiatePage(WebDrone drone, Class<T> pageClassToProxy) 
    {
        if(drone == null) throw new IllegalArgumentException("WebDrone is required");
        if(pageClassToProxy == null) throw new IllegalArgumentException("Page object is required");
        try
        {
            try
            {
                Constructor<T> constructor = pageClassToProxy.getConstructor(WebDrone.class);
                return constructor.newInstance(drone);
            } 
            catch (NoSuchMethodException e) 
            {
                return pageClassToProxy.newInstance();
            }
        } 
        catch (InstantiationException e) 
        {
            throw new PageException(CREATE_PAGE_ERROR_MSG,e);
        }
        catch (IllegalAccessException e) 
        {
            throw new PageException(CREATE_PAGE_ERROR_MSG,e);
        } 
        catch (InvocationTargetException e) 
        {
            throw new PageException(CREATE_PAGE_ERROR_MSG,e);
        }
    }

    /**
     * Resolves the required page based on the URL containing a keyword
     * that identify's the page the drone is currently on. Once a the name
     * is extracted it is used to get the class from the map which is
     * then instantiated.
     * 
     * @param driver WebDriver browser client
     * @return SharePage page object
     */
    public static SharePage getPage(final String url, WebDrone drone)
    {
        String pageName = resolvePage(url);
        return instantiatePage(drone, pages.get(pageName));
    }
    
    /**
     * Extracts the name from any url noise.
     * @param pageName String page name
     * @param marker # or ? that clutters the page name
     * @return the page name
     */
    private static String extractName(String pageName)
    {
        String regex = "([?&#])";
        String vals[] = pageName.split(regex);
        return vals[0];
    }
    /**
     * Extracts the String value from the last occurrence of slash in the url.
     * 
     * @param url String url.
     * @return String page title
     */
    protected static String resolvePage(String url)
    {
        if (url == null || url.isEmpty()) { throw new UnsupportedOperationException("Empty url is not allowed"); }
        
        if(url.endsWith("dashboard"))
        {
            if(url.endsWith("customise-site-dashboard"))
            {
                return "customise-site-dashboard";
            }
            if(url.contains("site"))
            {
                return "site-dashboard";
            }
            return "dashboard";
        }
        
        if(url.endsWith("create"))
        {
            return "users-create";
        }
        
        if (url.contains(NODE_REF_IDENTIFIER))
        {
            int index = url.indexOf(NODE_REF_IDENTIFIER);
            url = url.subSequence(0, index).toString();
        }
        
        if (url.contains(NODE_REFRESH_META_DATA_IDENTIFIER))
        {
            int index = url.indexOf(NODE_REFRESH_META_DATA_IDENTIFIER);
            url = url.subSequence(0, index).toString();
        }

        //Get the last element of url
        StringTokenizer st = new StringTokenizer(url,"/");
        String val = "";
        while(st.hasMoreTokens())
        {
            if(st.hasMoreTokens())
            {
                val = st.nextToken();
            }
        }
        
        //Check if its advance search folder or content
        if(val.contains("advsearch"))
        {
            if(val.contains("%3afolder"))
            {
                return "advfolder-search";
            }
            else if(val.contains("prop_crm"))
            {
                return "advCRM-search";
            }
            return "advsearch";
        }
        if (val.startsWith("search?"))
        {
            if (url.contains("/site/"))
            {
                return "siteResultsPage";
            }
            else if (val.endsWith("&a=true&r=true"))
            {
                return "repositoryResultsPage";
            }
            else
            {
                return "allSitesResultsPage";
            }
        }
        //Remove any clutter.
        if(val.contains("?") || val.contains("#"))
        {
            val = extractName(val);
        }
        return val;
    }
}