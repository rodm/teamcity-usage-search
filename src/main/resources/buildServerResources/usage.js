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
                console.log("response: ", transport);

                var buildTypeTemplate = $j('#usageResultBuildTypeTemplate');
                var buildTemplateTemplate = $j('#usageResultBuildTemplateTemplate');
                var response = BS.Util.documentRoot(transport);
                var matches = response.getElementsByTagName('result');

                for (var i = 0; i < matches.length; i++) {
                    var match = matches[i];
                    var newRow = buildTypeTemplate.clone();
                    if ($j(match).attr('type') === "TEMPLATE") {
                        newRow = buildTemplateTemplate.clone();
                    }
                    var link = newRow.find('a');
                    link.attr('href', function () {
                        return $j(this).attr('href').replace('##BUILD_TYPE_ID##', match.getAttribute('id'));
                    });
                    link.text(match.getAttribute('name'));
                    var sections = $j(match.getElementsByTagName('section')).map(function() {
                        return this;
                    }).toArray();
                    var cell = $j('<td></td>');
                    for (var j = 0; j < sections.length; j++) {
                        var section = sections[j];
                        var names = $j(section.getElementsByTagName('name')).map(function () {
                            return $j(this).attr('value');
                        }).toArray();
                        $j('<div></div>').text('Uses ' + names.join(', ') + ' in ' + $j(section).attr('name')).appendTo(cell);
                    }
                    cell.appendTo(newRow);
                    results.append(newRow);
                }
            }
        });
    }
};
