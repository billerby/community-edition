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
      name: 'SingleTextFieldFormTest',
      'Forms': function () {
         var browser = this.remote;
         var testname = "SingleTextFieldFormTest";
         return TestCommon.bootstrapTest(this.remote, "./tests/alfresco/forms/page_models/SingleTextFieldForm_TestPage.json", testname)

         .end()

         // 1. Test that enter won't submit without any data in the field...
         .elementByCss("#STFF1 .dijitInputContainer input")
            .keys(specialKeys["Return"])
            .elementsByCss(TestCommon.topicSelector("TEST_PUBLISH", "publish", "any"))
               .then(function(elements) {
                  TestCommon.log(testname,45,"Check enter key cannot be used to submit data if field is empty");
                  assert(elements.length == 0, "Test #1 - enter key submitted data on empty field");
               })
            .end()

         // 2. Test entering some text and hitting enter (rather than the OK button)...
         .elementByCss("#STFF1 .dijitInputContainer input")
            .type("test")
            .keys(specialKeys["Return"])
            .end()
         .elementsByCss(TestCommon.pubSubDataCssSelector("last", "search", "test"))
            .then(function(elements) {
               TestCommon.log(testname,47,"Check enter key can be used to submit data");
               assert(elements.length == 1, "Test #2 - enter key doesn't submit data");
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