/*
 * Copyright 2019 Rod MacKenzie.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

BS.UsageSearch = {

    actionsUrl: window['base_uri'] + "/admin/usage.html",

    initForm: function() {
        var paramName = $j('#paramName');
        paramName.focus();
    },

    search: function(projectId) {
        var paramName = $j('#paramName');
        var paramNameError = $j('#paramNameError');
        var results = $j('#usageResults');

        if (paramName.val().length < 2) {
            paramName.addClass('errorField');
            paramNameError.css({"margin-left": $j('#paramNameLabel').outerWidth(true)});
            paramNameError.show();
            return false;
        }
        paramName.removeClass('errorField');
        paramNameError.hide();
        results.empty();

        BS.ajaxRequest(this.actionsUrl, {
            parameters: Object.toQueryString({projectId: projectId, action: 'search', paramName: paramName.val()}),
            onComplete: function(transport) {
                console.log(transport);
                results.append(transport.responseText)
            }
        });
    }
};
