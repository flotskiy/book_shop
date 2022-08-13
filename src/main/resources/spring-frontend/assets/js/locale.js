'use strict';
$(document).ready(function () {
    $("#locales").change(function () {
        var selectedOption = $('#locales').val();
        if (selectedOption != '') {
            window.location.replace('?lang=' + selectedOption);
        }
    });
});