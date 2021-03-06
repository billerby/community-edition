/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have received a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing"
 */

package org.alfresco.repo.bulkimport;

import java.util.Date;

/**
 * Interface defining which information can be obtained from the Bulk Filesystem Import engine.
 *
 * @since 4.0
 */
public interface BulkImportStatus
{
    // General information
    boolean inProgress();

    int getNumThreads();
    int getBatchSize();

    String getSourceDirectory();
    String getTargetSpace();
    
    Date getStartDate();
    Date getEndDate();
    
    long   getNumberOfBatchesCompleted();

    Long      getDurationInNs();  // Note: java.lang.Long, _not_ primitive long - may be null
    Throwable getLastException();
    String    getLastExceptionAsString();
    

    // Read-side information
    long getNumberOfFoldersScanned();
    long getNumberOfFilesScanned();
    long getNumberOfUnreadableEntries();

    long getNumberOfContentFilesRead();
    long getNumberOfContentBytesRead();
    
    long getNumberOfMetadataFilesRead();
    long getNumberOfMetadataBytesRead();
    
    long getNumberOfContentVersionFilesRead();
    long getNumberOfContentVersionBytesRead();
    
    long getNumberOfMetadataVersionFilesRead();
    long getNumberOfMetadataVersionBytesRead();
    
    // Write-side information
    long getNumberOfSpaceNodesCreated();
    long getNumberOfSpaceNodesReplaced();
    long getNumberOfSpaceNodesSkipped();
    long getNumberOfSpacePropertiesWritten();
    
    long getNumberOfContentNodesCreated();
    long getNumberOfContentNodesReplaced();
    long getNumberOfContentNodesSkipped();
    long getNumberOfContentBytesWritten();
    long getNumberOfContentPropertiesWritten();
    
    long getNumberOfContentVersionsCreated();
    long getNumberOfContentVersionBytesWritten();
    long getNumberOfContentVersionPropertiesWritten();
    
    // Throughput
    public Long getFilesReadPerSecond();
    public Long getBytesReadPerSecond();
    public Long getEntriesScannedPerSecond();
    public Long getBytesWrittenPerSecond();
    public Long getNodesCreatedPerSecond();
}
