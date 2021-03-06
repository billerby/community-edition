package org.alfresco.share.util;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.enums.ViewType;
import org.alfresco.po.share.site.NewFolderPage;
import org.alfresco.po.share.site.document.*;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

import static org.testng.Assert.assertTrue;

/**
 * This class contains the utils for shared files.
 * 
 * @author Antonik Olga
 */
public class ShareUserSharedFilesPage extends AbstractUtils
{

    private static Log logger = LogFactory.getLog(ShareUserSharedFilesPage.class);

    public ShareUserSharedFilesPage()
    {
        if (logger.isTraceEnabled())
        {
            logger.debug(this.getClass().getSimpleName() + " instantiated");
        }
    }

    /**
     * Open Shared Files page: Top Level Assumes User is logged in
     * 
     * @param driver WebDrone Instance
     * @return SharedFilesPage
     */
    public static SharedFilesPage openSharedFiles(WebDrone driver)
    {
        SharePage page = ShareUser.getSharePage(driver);

        SharedFilesPage sharedFilesPage = page.getNav().selectSharedFilesPage().render();
        logger.info("Opened Shared Files page");

        return sharedFilesPage;

    }

    /**
     * Open Shared Files page in Simple View
     * Assumes User is logged in.
     * 
     * @param driver WebDrone Instance
     * @return SharedFilesPage
     */
    // TODO: Remove util as redundant
    public static SharedFilesPage openSharedFilesSimpleView(WebDrone driver)
    {
        return openSharedFilesInView(driver, ViewType.SIMPLE_VIEW);
    }
    
    /**
     * Open Shared Files page in Detailed View
     * Assumes User is logged in.
     * 
     * @param driver WebDrone Instance
     * @return SharedFilesPage
     */
    // TODO: Remove util as redundant
    public static SharedFilesPage openSharedFilesDetailedView(WebDrone driver)
    {
        return openSharedFilesInView(driver, ViewType.DETAILED_VIEW);
    }

    /**
     * Open Shared Files page in selected View
     * Assumes User is logged in
     * 
     * @param driver WebDrone Instance
     * @param ViewType View Type to be selected
     * @return SharedFilesPage
     */
    public static SharedFilesPage openSharedFilesInView(WebDrone driver, ViewType viewType)
    {
        SharedFilesPage sharedFilesPage = openSharedFiles(driver);
        sharedFilesPage = ((SharedFilesPage) ShareUserSitePage.selectView(driver, viewType)).render(maxWaitTime);
        logger.info("Opened Shared Files page in:" + viewType.getName());
        return sharedFilesPage;
    }

    /**
     * Assumes User is logged in and a Shared Files page is open,
     * parent Folder is pre-selected.
     * 
     * @param driver WebDrone Instance
     * @param file File Object for the file in reference
     * @return SharedFilesPage
     * @throws org.testng.SkipException if error in this API
     */
    public static SharedFilesPage uploadFileInSharedFiles(WebDrone driver, File file) throws Exception
    {
        return ((SharedFilesPage) ShareUserSitePage.uploadFile(driver, file)).render();
    }

    /**
     * This method is used to create content with name, title and description.
     * User should be logged in, parent Folder is pre-selected.
     * 
     * @param driver WebDrone Instance
     * @param contentDetails
     * @param contentType
     * @return SharedFilesPage
     */
    public static SharedFilesPage createContent(WebDrone driver, ContentDetails contentDetails, ContentType contentType)
    {

        SharedFilesPage sharedFilesPage = openSharedFiles(driver);
        CreatePlainTextContentPage contentPage = sharedFilesPage.getNavigation().selectCreateContent(contentType).render();
        DocumentDetailsPage detailsPage = contentPage.create(contentDetails).render();

        sharedFilesPage = detailsPage.getNav().selectSharedFilesPage();

        return sharedFilesPage.render();
    }

    /**
     * This method is used to create content from existing template
     * User should be logged in, parent Folder is pre-selected.
     * 
     * @param driver WebDrone Instance
     * @param templateName
     * @return SharedFilesPage
     */
    public static SharedFilesPage createContentFromTemplate(WebDrone driver, String templateName)
    {
        SharedFilesPage sharedFilesPage = openSharedFiles(driver);

        // TODO: Amend DocumentLibraryPage.createContentFromTemplate to return HtmlPage and return that
        sharedFilesPage.createContentFromTemplate(templateName).render();
        // TODO: Fix: as above: This could return StaleElementRef
        return sharedFilesPage.render();
    }

    /**
     * This method is used to create new folder
     * User should be logged in, parent Folder is pre-selected.
     * 
     * @param driver WebDrone Instance   
     * @param folder
     * @return SharedFilesPage
     */
    public static SharedFilesPage createNewFolder(WebDrone driver, String folder)
    {
        SharedFilesPage sharedFilesPage = openSharedFiles(driver);
        NewFolderPage newFolderPage = sharedFilesPage.getNavigation().selectCreateNewFolder().render();
        return newFolderPage.createNewFolder(folder).render();
    }

    /**
     * This method is used to create new folder
     * User should be logged in
     * 
     * @param driver WebDrone Instance
     * @param String folder
     * @param String path to parent folder
     *            
     * @return SharedFilesPage
     */
    public static SharedFilesPage createNewFolderInPath(WebDrone driver, String folder, String path) throws Exception
    {
        SharedFilesPage sharedFilesPage = navigateToFolderInSharedFiles(driver, path);
        
        // TODO: Remove this, I have fixed above util to render the right page
        webDriverWait(driver, 2000);
        
        NewFolderPage newFolderPage = sharedFilesPage.getNavigation().selectCreateNewFolder().render();
        sharedFilesPage = ((SharedFilesPage) newFolderPage.createNewFolder(folder)).render();

        return sharedFilesPage;

    }

    /**
     * This method is used to create folder from existing template
     * User should be logged in, parent Folder is pre-selected.
     * 
     * @param driver WebDrone Instance         
     * @param String templateName
     * @return SharedFilesPage
     */
    public static SharedFilesPage createFolderFromTemplate(WebDrone driver, String templateName)
    {
        SharedFilesPage sharedFilesPage = openSharedFiles(driver);
        // TODO: Use render to ensure page has completely rendered
        sharedFilesPage.createFolderFromTemplate(templateName);

        // TODO: Fix this: Do not use old reference
        return sharedFilesPage.render();
    }

    /**
     * method to Navigate folder
     * 
     * @param driver
     * @param folderPath
     * @return SharedFilesPage
     */
    public static SharedFilesPage navigateToFolderInSharedFiles(WebDrone driver, String folderPath) throws Exception
    {
        SharedFilesPage sharedFilesPage = openSharedFiles(driver).render(maxWaitTime);
        sharedFilesPage = ((SharedFilesPage) ShareUserSitePage.navigateToFolder(driver, folderPath)).render(maxWaitTime);
        return sharedFilesPage;
    }

    /**
     * method to add comment to document
     * User should be logged in, parent Folder is pre-selected.
     * 
     * @param driver
     * @param fileName
     * @param comment
     * @param String path to parent folder
     *            
     */
    public static void addCommentToFile(WebDrone driver, String fileName, String comment, String path) throws Exception
    {
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(driver, fileName);
        DocumentDetailsPage documentDetailsPage = fileInfo.clickCommentsLink().render();
        documentDetailsPage.addComment(comment).render();
        
        // TODO: Remove assert from utils        
        navigateToFolderInSharedFiles(driver, path);
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(driver, fileName);       
        assertTrue(fileInfo.getCommentsCount() > 0, "Got comments count: " + fileInfo.getCommentsCount());
    }

    /**
     * method to add comment to folder
     * User should be logged in, Shared Files page is open.
     * 
     * @param driver
     * @param folderName
     * @param comment
     */

    public static void addCommentToFolder(WebDrone driver, String folderName, String comment)
    {
        FileDirectoryInfo fileInfo = ShareUserSitePage.getFileDirectoryInfo(driver, folderName);
        
        // TODO: Do not cast, if method returns HtmlPage, use render instead (as fixed above: in addCommentToFile)
        FolderDetailsPage folderDetailsPage = (FolderDetailsPage) fileInfo.clickCommentsLink();
        folderDetailsPage.addComment(comment).render();
        
        // TODO: Remove assert from utils   
        openSharedFiles(driver);
        fileInfo = ShareUserSitePage.getFileDirectoryInfo(driver, folderName);
        assertTrue(fileInfo.getCommentsCount() > 0, "Got comments count: " + fileInfo.getCommentsCount());
    }

}
