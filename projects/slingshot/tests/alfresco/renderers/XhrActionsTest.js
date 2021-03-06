/**
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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @author David Webster
 */
define(["intern!object",
   "intern/chai!expect",
   "intern/chai!assert",
   "require",
   "alfresco/TestCommon"],
   function (registerSuite, expect, assert, require, TestCommon) {

      registerSuite({
         name: 'XHR Actions Renderer Test',
         'alfresco/renderers/XhrActions': function () {

            var browser = this.remote;
            var testname = "XhrActionsTest";
            return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/renderers/page_models/XhrActions_TestPage.json", testname)

               // Test spec:
               // 1: Check dropdown element exists

               .elementByCss(".alfresco-menus-AlfMenuBar span:first-child")
               .text()
               .then(function(resultText) {
                  TestCommon.log(testname, null,"Check Actions menu was rendered");
                  assert(resultText == "Actions", "Test #1- Actions should be rendered as a menu: " + resultText);
               })

               // 2: Click on it. Check event triggered: ALF_RETRIEVE_SINGLE_DOCUMENT_REQUEST

               .click()
               .end()
               .elementsByCss(TestCommon.topicSelector("ALF_RETRIEVE_SINGLE_DOCUMENT_REQUEST", "publish", "any"))
               .then(function(elements) {
                  TestCommon.log(testname, null,"Check that document request event was triggered");
                  assert(elements.length == 1, "Test #2 - Retrieve single doc request not triggered");
               })
               .end()

               // TODO: 3: Trigger response event with data
               // TODO: 4: See if response is rendered

               // Post the coverage results...
               .then(function() {
                  TestCommon.postCoverageResults(browser);
               });
         }
      });
   });