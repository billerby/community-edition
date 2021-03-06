package org.alfresco.po.share.site.blog;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * New Post Form page object
 *
 * @author Marina.Nenadovets
 */
public class EditPostForm extends AbstractPostForm
{
    public EditPostForm(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditPostForm render(RenderTime timer)
    {
        elementRender(timer,
            getVisibleRenderElement(TITLE_FIELD),
            getVisibleRenderElement(DEFAULT_SAVE),
            getVisibleRenderElement(PUBLISH_INTERNALLY_EXTERNALLY),
            getVisibleRenderElement(CANCEL_BTN));

        return this;
    }

    public EditPostForm render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public EditPostForm render(long time)
    {
        return render(new RenderTime(time));
    }
}
