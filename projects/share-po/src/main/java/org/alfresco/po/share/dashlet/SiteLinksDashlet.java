package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.site.links.LinksDetailsPage;
import org.alfresco.po.share.site.links.LinksPage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageRenderTimeException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;

/**
 * Page object to hold site links dashlet
 *
 * @author Marina.Nenadovets
 */
public class SiteLinksDashlet extends AbstractDashlet implements Dashlet
{
    @SuppressWarnings("unused")
    private static Log logger = LogFactory.getLog(SiteLinksDashlet.class);
    private static final By DASHLET_CONTAINER_PLACEHOLDER = By.cssSelector("div.dashlet.site-links");
    private static final By CREATE_LINK = By.cssSelector("a[href='links-linkedit']");
    private static final By LINK_DETAILS = By.cssSelector("div.actions>a.details");
    @SuppressWarnings("unused")
    private static final By LINKS_LIST =By.cssSelector("div.dashlet.site-links>div.scrollableList>div>div");

    /**
     * Constructor.
     */
    protected SiteLinksDashlet(WebDrone drone)
    {
        super(drone, DASHLET_CONTAINER_PLACEHOLDER);
        setResizeHandle(By.cssSelector("div.dashlet.site-links .yui-resize-handle"));
    }

    @SuppressWarnings("unchecked")
    @Override
    public synchronized SiteLinksDashlet render(RenderTime timer)
    {
        try
        {
            while (true)
            {
                timer.start();
                synchronized (this)
                {
                    try
                    {
                        this.wait(50L);
                    }
                    catch (InterruptedException e)
                    {
                    }
                }
                try
                {
                    this.dashlet = drone.findAndWait((DASHLET_CONTAINER_PLACEHOLDER), 100L, 10L);
                    break;
                }
                catch (NoSuchElementException e)
                {

                }
                catch (StaleElementReferenceException ste)
                {
                    // DOM has changed therefore page should render once change
                    // is completed
                }
                finally
                {
                    timer.end();
                }
            }
        }
        catch (PageRenderTimeException te)
        {
            throw new NoSuchDashletExpection(this.getClass().getName() + " failed to find site links dashlet", te);
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteLinksDashlet render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public SiteLinksDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * This method gets the focus by placing mouse over on Site Links Dashlet.
     */
    private void getFocus()
    {
        drone.mouseOver(drone.findAndWait(DASHLET_CONTAINER_PLACEHOLDER));
    }

    /**
     * Method to create a link
     * @param name
     * @param url
     * @return
     */

    public LinksDetailsPage createLink (String name, String url)
    {
        drone.findAndWait(CREATE_LINK).click();
        LinksPage linksPage = new LinksPage(drone);
        linksPage.createLink(name, url);
        return new LinksDetailsPage(drone).render();
    }

    /**
     * Method to verify whether Links details is available on site links dashlet
     *
     * @return boolean
     */

    public boolean isDetailsLinkDisplayed ()
    {
        try
        {
            getFocus();
            return drone.findAndWait(LINK_DETAILS).isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }
}
