/**
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

/**
 * Renders a thumbnail specific to search results. This is different from the usual thumbnail
 * in that it renders specific images for items that are neither documents nor folders.
 * 
 * @module alfresco/renderers/SearchThumbnail
 * @extends module:alfresco/renderers/Thumbnail
 * @author Dave Draper
 */
define(["dojo/_base/declare",
        "alfresco/renderers/Thumbnail", 
        "service/constants/Default",
        "dojo/_base/lang",
        "alfresco/core/NodeUtils"], 
        function(declare, Thumbnail, AlfConstants, lang, NodeUtils) {

   return declare([Thumbnail], {
      
      /**
       * Overrides the standard fallback to address specific site item types.
       *
       * @instance
       * @returns {string} The URL for the thumbnail.
       */
      generateFallbackThumbnailUrl: function alfresco_renderers_Thumbnail__generateFallbackThumbnailUrl() {
         var url;
         switch (this.currentItem.type)
         {
            case "blogpost":
               url = AlfConstants.URL_RESCONTEXT + 'components/search/images/blog-post.png';
               break;
   
            case "forumpost":
               url = AlfConstants.URL_RESCONTEXT + 'components/search/images/topic-post.png';
               break;
   
            case "calendarevent":
               url = AlfConstants.URL_RESCONTEXT + 'components/search/images/calendar-event.png';
               break;
   
            case "wikipage":
               url = AlfConstants.URL_RESCONTEXT + 'components/search/images/wiki-page.png';
               break;
   
            case "link":
               url = AlfConstants.URL_RESCONTEXT + 'components/search/images/link.png';
               break;
   
            case "datalist":
               url = AlfConstants.URL_RESCONTEXT + 'components/search/images/datalist.png';
               break;
   
            case "datalistitem":
               url = AlfConstants.URL_RESCONTEXT + 'components/search/images/datalistitem.png';
               break;
   
            default:
               url = AlfConstants.URL_RESCONTEXT + 'components/search/images/generic-result.png';
               break;
         }
         return url;
      }
   });
});