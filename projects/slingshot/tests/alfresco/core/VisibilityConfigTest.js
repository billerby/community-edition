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
 * @author Dave Draper
 */
define(["intern!object",
        "intern/chai!expect",
        "intern/chai!assert",
        "require",
        "alfresco/TestCommon",
        "intern/dojo/node!wd/lib/special-keys"], 
        function (registerSuite, expect, assert, require, TestCommon, specialKeys) {

   registerSuite({
      name: 'VisibilityConfig Test',
      'VisibilityConfig': function () {
         var browser = this.remote;
         var testname = "VisibilityConfigTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/core/page_models/VisibilityConfig_TestPage.json", testname)

         .end()

         // Test 1: Check that LOGO1 is initially displayed and that LOGO2 is initially hidden...
         .elementsByCss("#LOGO1")
         .then(function (els) {
            TestCommon.log(testname,43,"Check LOGO1 is initially displayed");
            assert(els.length == 1, "Test #1a - LOGO1 was unexpectedly hidden");
         })
         .end()

         .elementByCss("#LOGO2")
            .getComputedCss("display")
            .then(function(result) {
               assert(result == "none", "Test #1b - LOGO2 was displayed unexpectedly");
            })
            .end()

         // Test 2: Check that LOGO1 can be hidden can then displayed by isNot rules
         .elementByCss("#HIDE_LOGO_1")
            .moveTo()
            .click()
            .end()
         .elementByCss("#LOGO1")
            .getComputedCss("display")
            .then(function(result) {
               assert(result == "none", "Test #2a - LOGO1 was not hidden");
            })
            .end()
         .elementByCss("#SHOW_LOGO_1")
            .moveTo()
            .click()
            .end()
         .elementByCss("#LOGO1")
            .getComputedCss("display")
            .then(function(result) {
               assert(result == "block", "Test #2b - LOGO1 was not revealed");
            })
            .end()

         // Test 3: Check that LOGO2 can be displayed and then hidden by is rules
         .elementByCss("#SHOW_LOGO_2_A")
            .moveTo()
            .click()
            .end()
         .elementByCss("#LOGO2")
            .getComputedCss("display")
            .then(function(result) {
               assert(result == "block", "Test #3a - LOGO2 was not revealed");
            })
            .end()
         .elementByCss("#HIDE_LOGO_2")
            .moveTo()
            .click()
            .end()
         .elementByCss("#LOGO2")
            .getComputedCss("display")
            .then(function(result) {
               assert(result == "none", "Test #3b - LOGO2 was not hidden");
            })
            .end()
         .elementByCss("#SHOW_LOGO_2_B")
            .moveTo()
            .click()
            .end()
         .elementByCss("#LOGO2")
            .getComputedCss("display")
            .then(function(result) {
               assert(result == "block", "Test #3c - LOGO2 was not hidden again");
            })
            .end()

         // Post the coverage results...
         .then(function() {
            TestCommon.postCoverageResults(browser);
         })
         .end();
      }
   });
});