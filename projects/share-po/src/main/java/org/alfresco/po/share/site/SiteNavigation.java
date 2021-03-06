/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site;

import static org.alfresco.po.share.AlfrescoVersion.Enterprise41;

import java.util.List;

import org.alfresco.po.share.AlfrescoVersion;
import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.site.blog.BlogPage;
import org.alfresco.po.share.site.calendar.CalendarPage;
import org.alfresco.po.share.site.datalist.DataListPage;
import org.alfresco.po.share.site.discussions.DiscussionsPage;
import org.alfresco.po.share.site.document.DocumentLibraryPage;
import org.alfresco.po.share.site.links.LinksPage;
import org.alfresco.po.share.site.wiki.WikiPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Represent elements found on the html page relating to the
 * sub navigation bar that appears on the site pages.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class SiteNavigation extends AbstractSiteNavigation
{
    protected final String siteMembersCSS;
    protected final By moreButton;
    private final String documentLibLink;
    private final By customizeDashboardLink;

    /**
     * Constructor.
     */
    protected SiteNavigation(WebDrone drone)
    {
        super(drone);
        siteMembersCSS = drone.getElement("site.members");
        documentLibLink = getAlfrescoVersion().isDojoSupported() ? LABEL_DOCUMENTLIBRARY_PLACEHOLDER : String.format(SITE_LINK_NAV_PLACEHOLER, 3);
        customizeDashboardLink = getAlfrescoVersion().isDojoSupported() ? By.id("HEADER_CUSTOMIZE_SITE_DASHBOARD") : CUSTOMISE_DASHBOARD_BTN;
        moreButton = getAlfrescoVersion().isDojoSupported() ? By.cssSelector("span.alf-menu-arrow") : By.cssSelector("button[id$='_default-more-button']");
    }

    /**
     * Mimics the action of selecting the site
     * project library link.
     * 
     * @return HtmlPage site document lib page object
     */
    public HtmlPage selectSiteProjectLibrary()
    {
        if (getAlfrescoVersion().isDojoSupported())
        {
            extractLink(PROJECT_LIBRARY).click();
            return new DocumentLibraryPage(getDrone());
        }
        return select(PROJECT_LIBRARY);
    }

    /**
     * Mimics the action of selecting the site
     * document library link.
     * 
     * @return HtmlPage site document lib page object
     */
    public HtmlPage selectSiteDocumentLibrary()
    {
        if (getAlfrescoVersion().isDojoSupported())
        {
            // extractLink(drone.getLanguageValue("document.library")).click();
            extractLink(DOCUMENT_LIBRARY).click();
            return new DocumentLibraryPage(getDrone());
        }
        // return drone.getLanguageValue("document.library");
        return select(DOCUMENT_LIBRARY);
    }

    /**
     * Mimics the action of selecting the site
     * document library link.
     * 
     * @return HtmlPage site document lib page object
     */
    public HtmlPage selectSiteWikiPage()
    {
        if (getAlfrescoVersion().isDojoSupported())
        {
            extractLink(WIKI).click();
            return new WikiPage(getDrone());
        }
        return select(WIKI);
    }

    /**
     * Mimcs the action of selecting on the configuration
     * drop down that has been introduced in Alfresco Enterprise 4.2
     */
    private void selectConfigurationDropdown()
    {
        findElement(CONFIGURATION_DROPDOWN).click();
    }

    /**
     * Mimics the action clicking the configure button.
     * This Features available only in the Enterprise 4.2 and Cloud 2.
     */
    public void selectConfigure()
    {
        if (AlfrescoVersion.Enterprise41.equals(getAlfrescoVersion()))
        {
            throw new UnsupportedOperationException("It is not supported for this version of Alfresco : " + getAlfrescoVersion());
        }
        findAndWait(CONFIGURE_ICON).click();
    }

    /**
     * Mimics the action of clicking more button.
     */
    public void selectMore()
    {
        findElement(moreButton).click();
    }

    /**
     * Mimics the action of clicking the Customize Site.
     * This features is not available in the cloud.
     * 
     * @return {@link CustomizeSitePage}
     */
    public CustomizeSitePage selectCustomizeSite()
    {
        try
        {
            if (Enterprise41.equals(getAlfrescoVersion()))
            {
                selectMore();
                List<WebElement> elements = findAllWithWait(MORE_BUTTON_LINK);
                for (WebElement webElement : elements)
                {
                    ShareLink link = new ShareLink(webElement, getDrone());
                    if (CUSTOMIZE_LINK_TEXT.equalsIgnoreCase(link.getDescription()))
                    {
                        link.click();
                        break;
                    }
                }
            }
            else
            {
                selectConfigure();
                drone.find(CUSTOMIZE_SITE).click();
            }
            return new CustomizeSitePage(getDrone());
        }
        catch (TimeoutException te)
        {
        }
        throw new PageException("Not able to find Customize Site Link.");
    }

    /**
     * @return {@link CustomiseSiteDashboardPage}
     */
    public CustomiseSiteDashboardPage selectCustomizeDashboard()
    {
        if (getAlfrescoVersion().isDojoSupported())
        {
            selectConfigurationDropdown();
        }
        drone.findAndWait(customizeDashboardLink).click();
        return new CustomiseSiteDashboardPage(getDrone());

    }

    private WebElement extractLink(final String title)
    {
        List<WebElement> list;
        if (AlfrescoVersion.Cloud2 == getAlfrescoVersion())
        {
            list = findElements(By.cssSelector("span"));
        }
        else
        {
            list = findElements(By.cssSelector("span.alf-menu-bar-label-node"));
        }
        for (WebElement element : list)
        {
            String name = element.getText();
            if (title.equalsIgnoreCase(name))
            {
                return element;
            }
        }
        throw new PageException("Unable to find " + title);
    }

    /**
     * Selects on edit site details link.
     * 
     * @return {@link HtmlPage} page response
     */
    public EditSitePage selectEditSite()
    {
        if (getAlfrescoVersion().isDojoSupported())
        {
            selectConfigurationDropdown();
            drone.find(By.id("HEADER_EDIT_SITE_DETAILS_text")).click();
        }
        else
        {
            WebElement more = findElement(By.cssSelector("button[id$='_default-more-button']"));
            more.click();
            WebElement nav = findElement(By.cssSelector("div.links.title-button"));
            nav.findElement(By.cssSelector("ul.first-of-type>li>a.yuimenuitemlabel")).click();
        }
        return new EditSitePage(getDrone());
    }

    /**
     * This method is used to select the site Members link.
     * 
     * @return SiteMembersPage site Members page object
     */
    public SiteMembersPage selectMembers()
    {
        try
        {
            findElement(By.cssSelector(siteMembersCSS)).click();
            return new SiteMembersPage(getDrone());
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find the InviteMembersPage.", e);
        }
    }

    /**
     * This method returns the MembersPage object.
     * 
     * @return {@link InviteMembersPage}
     */
    public InviteMembersPage selectInvite()
    {
        try
        {
            if (Enterprise41 == getAlfrescoVersion())
            {
                findElement(By.cssSelector(INVITE_BUTTON)).click();
            }
            else
            {
                drone.findAndWait(By.cssSelector(".alf-user-icon")).click();
            }
        }
        catch (TimeoutException e)
        {
            throw new PageException("Unable to find the InviteMembersPage.", e);
        }
        return new InviteMembersPage(getDrone());
    }

    /**
     * Check if the site navigation has document library link highlighted.
     * 
     * @return if link is highlighted
     */
    public boolean isDocumentLibraryActive()
    {
        try
        {
            // This code needs to be removed when this cloud issue is fixed as
            // part of release-31
            // https://issues.alfresco.com/jira/browse/CLOUD-2092
            if (!AlfrescoVersion.Cloud2.equals(getAlfrescoVersion()) && !AlfrescoVersion.MyAlfresco.equals(getAlfrescoVersion()))
            {
                return isLinkActive(By.cssSelector(documentLibLink));
            }
            else
            {
                String active = "Hover";
                WebElement element = getDrone().findAndWait(By.cssSelector(documentLibLink));
                String value = element.getAttribute("class");
                if (value != null && !value.isEmpty())
                {
                    return value.contains(active);
                }
            }
        }
        catch (TimeoutException e)
        {
        }
        return false;
    }

    // this code is commented due to
    // https://issues.alfresco.com/jira/browse/CLOUD-2092
    // return isLinkActive(By.cssSelector(documentLibLink));

    /**
     * Mimics the action of selecting the site
     * calendar link.
     * 
     * @return HtmlPage site calendar page object
     */
    public CalendarPage selectCalendarPage()
    {
        if (drone.find(CALENDAR_LINK).isDisplayed())
        {
            drone.findAndWait(CALENDAR_LINK).click();
            return new CalendarPage(drone);
        }
        else
        {
            drone.findAndWait(SITE_MORE_PAGES).click();
            drone.findAndWait(CALENDAR_LINK).click();
            return new CalendarPage(drone);
        }
    }

    /**
     * Mimics the action of selecting the site
     * members link.
     * 
     * @return HtmlPage site members page object
     */
    public SiteMembersPage selectMembersPage()
    {
        if (drone.find(MEMBERS_LINK).isDisplayed())
        {
            drone.findAndWait(MEMBERS_LINK).click();
            return new SiteMembersPage(drone);
        }
        else
        {
            drone.findAndWait(SITE_MORE_PAGES).click();
            drone.findAndWait(MEMBERS_LINK).click();
            return new SiteMembersPage(drone);
        }
    }

    /**
     * Mimics the action of selecting the site
     * members link.
     * 
     * @return HtmlPage site members page object
     */
    public WikiPage selectWikiPage()
    {
        if (drone.find(WIKI_LINK).isDisplayed())
        {
            drone.findAndWait(WIKI_LINK).click();
            return new WikiPage(drone);
        }
        else
        {
            drone.findAndWait(SITE_MORE_PAGES).click();
            drone.findAndWait(WIKI_LINK).click();
            return new WikiPage(drone);
        }
    }

    /**
     * Mimics the action of selecting the site
     * discussions link.
     * 
     * @return HtmlPage site members page object
     */
    public DiscussionsPage selectDiscussionsPage()
    {
        if (drone.find(DISCUSSIONS_LINK).isDisplayed())
        {
            drone.findAndWait(DISCUSSIONS_LINK).click();
            return new DiscussionsPage(drone);
        }
        else
        {
            drone.findAndWait(SITE_MORE_PAGES).click();
            drone.findAndWait(DISCUSSIONS_LINK).click();
            return new DiscussionsPage(drone);
        }
    }

    /**
     * Mimics the action of selecting the site
     * Blog link.
     * 
     * @return HtmlPage site members page object
     */
    public BlogPage selectBlogPage()
    {
        if (drone.find(BLOG_LINK).isDisplayed())
        {
            drone.findAndWait(BLOG_LINK).click();
            return new BlogPage(drone);
        }
        else
        {
            drone.findAndWait(SITE_MORE_PAGES).click();
            drone.findAndWait(BLOG_LINK).click();
            return new BlogPage(drone);
        }
    }

    /**
     * Mimics the action of selecting the site
     * Links link.
     * 
     * @return HtmlPage site members page object
     */
    public LinksPage selectLinksPage()
    {
        if (drone.find(LINKS_LINK).isDisplayed())
        {
            drone.findAndWait(LINKS_LINK).click();
            return new LinksPage(drone);
        }
        else
        {
            drone.findAndWait(SITE_MORE_PAGES).click();
            drone.findAndWait(LINKS_LINK).click();
            return new LinksPage(drone);
        }
    }

    /**
     * Mimics the action of selecting the site
     * Data Lists link.
     * 
     * @return HtmlPage site members page object
     */
    public DataListPage selectDataListPage()
    {
        if (drone.find(DATA_LISTS_LINK).isDisplayed())
        {
            drone.findAndWait(DATA_LISTS_LINK).click();
            return new DataListPage(drone).render();
        }
        else
        {
            drone.findAndWait(SITE_MORE_PAGES).click();
            drone.findAndWait(DATA_LISTS_LINK).click();
            return new DataListPage(drone).render();
        }
    }
}