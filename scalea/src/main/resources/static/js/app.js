// Changing the language
$(document).ready(function() {
    $("#locales").change(function () {
        var selectedOption = $('#locales').val();
        if (selectedOption != ''){
            window.location.replace('?lang=' + selectedOption);
        }
    });
});

function changeVacancies() {
	if (document.getElementById("vacancy").disabled == true) {
		document.getElementById("vacancy").disabled = false;
	} else {
		document.getElementById("vacancy").disabled = true;
	}
}

function changeEmployees() {
	if (document.getElementById("employee").disabled == true) {
		document.getElementById("employee").disabled = false;
	} else {
		document.getElementById("employee").disabled = true;
	}
}