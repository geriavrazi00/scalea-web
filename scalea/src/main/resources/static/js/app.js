// Changing the language
$(document).ready(function() {
    $("#locales").change(function () {
        var selectedOption = $('#locales').val();
        if (selectedOption != ''){
            window.location.replace('?lang=' + selectedOption);
        }
    });
});

// Enabling tooltips
$(function () {
  	$('[data-toggle="tooltip"]').tooltip()
})

window.setTimeout(function() {
    $(".alert").fadeTo(500, 0).slideUp(500, function(){
        $(this).remove(); 
    });
}, 5000);

function changeVacancies() {
	if (document.getElementById("vacancy").disabled == true) {
		document.getElementById("vacancy").disabled = false;
	} else {
		document.getElementById("vacancy").disabled = true;
	}
}

function changeEmployees() {
	if (document.getElementById("employee") != null) {
		if (document.getElementById("employee").disabled == true) {
			document.getElementById("employee").disabled = false;
		} else {
			document.getElementById("employee").disabled = true;
		}
	}
}

function changeProductPrice() {
	if (document.getElementById("price") != null) {
		if (document.getElementById("price").disabled == true) {
			document.getElementById("price").disabled = false;
		} else {
			document.getElementById("price").disabled = true;
		}
	}
}

/* Stopwatch */
function loadProcessTimers() {
  var areas = document.getElementsByClassName("area");
  var started = document.getElementsByClassName("started");
  var status = document.getElementsByClassName("status");
  var timer = document.getElementsByClassName("timer");
  var elapsed = document.getElementsByClassName("elapsed");

  if (areas != null) {
	for (var i = 0; i < areas.length; i++) {
	  var areaId = document.getElementById("area-" + areas[i].value).value;
	  var startTime = document.getElementById("started-" + areas[i].value).value;
	  var status = document.getElementById("status-" + areas[i].value).value;
	  var timer = document.getElementById("timer-" + areas[i].value);
	  var elapsed = document.getElementById("elapsed-" + areas[i].value).value;
	    
	  startTimer(areaId, startTime, status, timer, elapsed);
	}
  }
}

// Convert time to a format of hours, minutes, seconds, and milliseconds
function timeToString(time) {
  let diffInHrs = time / 3600000;
  let hh = Math.floor(diffInHrs);

  let diffInMin = (diffInHrs - hh) * 60;
  let mm = Math.floor(diffInMin);

  let diffInSec = (diffInMin - mm) * 60;
  let ss = Math.floor(diffInSec);

  //let diffInMs = (diffInSec - ss) * 100;
  //let ms = Math.floor(diffInMs);

  let formattedHH = hh.toString().padStart(2, "0");
  let formattedMM = mm.toString().padStart(2, "0");
  let formattedSS = ss.toString().padStart(2, "0");
  //let formattedMS = ms.toString().padStart(2, "0");

  return `${formattedHH}:${formattedMM}:${formattedSS}`;
}

// Create function to modify innerHTML
function print(txt, id, elem) {
  elem.textContent = txt;
}

// Create "start" function
function startTimer(id, startTime, status, elem, elapsed) {

  // startTime = (Number(startTime) + Number(elapsed)).toString();
  // maybe elapsed should be added to elapsedTime
  if (status == 0) {    
	timerInterval = setInterval(function printTime() {
	  var elapsedTime = (Date.now() - startTime) + Number(elapsed);
	  
	  print(timeToString(elapsedTime), id, elem);
	}, 10);
  } else if (status == 1) {
    print(timeToString(elapsed), id, elem);
  }
}

// Areas
function loadArea(e, id) {
	e.preventDefault();
	
	var href = $("#editAreaHref" + id).attr('href');
	
	$.get(href, function(area) {
		$("#edit-area-id").val(area.id);
		$("#edit-area-name").val(area.name);
		$("#edit-area-capacity").val(area.capacity);
	});
	
	$('#editAreaModal').modal('show');
}

function setDeleteAreaId(id) {
	$("#delete-area-id").val(id);
}

function submitDeleteForm() {
	id = $("#delete-area-id").val();
	$("#deleteAreaForm" + id).submit(); // Submit the form
}

function setDeleteVacancyId(id) {
	$("#delete-vacancy-id").val(id);
}

function submitVacancyDeleteForm() {
	id = $("#delete-vacancy-id").val();
	$("#deleteVacancyForm" + id).submit(); // Submit the form
}

function setDeleteProductId(id) {
	$("#delete-product-id").val(id);
}

function submitProductDeleteForm() {
	id = $("#delete-product-id").val();
	$("#deleteProductForm" + id).submit(); // Submit the form
}

function setDeleteSubProductId(id) {
	$("#delete-subproduct-id").val(id);
}

function submitSubProductDeleteForm() {
	id = $("#delete-subproduct-id").val();
	$("#deleteSubProductForm" + id).submit(); // Submit the form
}

function setDetachVacancyId(id) {
	$("#detach-vacancy-id").val(id);
}

function submitVacancyDetachForm() {
	id = $("#detach-vacancy-id").val();
	$("#detachVacancyForm" + id).submit(); // Submit the form
}

function incrementNumber(id) {
  	var value = document.getElementById(id).value;
  	
  	// the value is only numeric
  	if (/^\d+$/.test(value)) {
    	value = parseInt(value) + 1;
  		document.getElementById(id).value = value;
  	} else {
  		document.getElementById(id).value = 1;
  	}
}

function decrementNumber(id) {
  	var value = document.getElementById(id).value;
  	
  	// the value is only numeric
  	if (/^\d+$/.test(value) && parseInt(value) > 1) {
	  	value = parseInt(value) - 1;
	  	document.getElementById(id).value = value;
	} else {
  		document.getElementById(id).value = 1;
  	}
}

// Vacancies

function loadVacancy(e, vacancyId, employeesSize) {
	e.preventDefault();
	
	document.getElementById("edit-vacancy-id").value = vacancyId;
	if (employeesSize == 0) document.getElementById("updateVacancy").disabled = true;
	else document.getElementById("updateVacancy").disabled = false;
	
	$('#editVacancyModal').modal('show');
}

// Products
function loadProduct(e, id) {
	e.preventDefault();
	
	var href = $("#editProductHref" + id).attr('href');
	
	$.get(href, function(product) {
		$("#edit-product-id").val(product.id);
		$("#edit-product-name").val(product.name);
		$("#edit-product-current-image").attr("src", product.base64Image);
		$("#edit-product-image").val(product.image);
		// $("#edit-product-img").val(product.image);
		
		var withSubProducts = product.withSubProducts;
		$("#edit-product-subproducts").prop('checked', withSubProducts);
		
		if (withSubProducts) {
			document.getElementById("edit-product-price").disabled = true;
			$("#edit-product-price").val(0.0);
		} else {
			document.getElementById("edit-product-price").disabled = false;
			$("#edit-product-price").val(product.price);
		}
	});
	
	$('#editProductModal').modal('show');
}

function loadSubProduct(e, productId, subProductId) {
	e.preventDefault();
	
	var href = $("#editSubProductHref" + subProductId).attr('href');
	
	$.get(href, function(product) {
		console.log(product);
		$("#edit-subproduct-id").val(product.id);
		$("#edit-subproduct-name").val(product.name);
		$("#edit-subproduct-image").val(product.image);
		$("#edit-subproduct-current-image").attr("src", product.base64Image);
		$("#edit-subproduct-price").val(product.price);
		$("#edit-subproduct-father").val(productId);
		
		if (document.getElementsByClassName('validationError') !== null) {
			document.getElementsByClassName('validationError').style.display = 'none';
		}
	});
	
	$('#editSubProductModal').modal('show');
}

function changeEditProductPrice() {
	if (document.getElementById("edit-product-price") != null) {
		if (document.getElementById("edit-product-price").disabled == true) {
			document.getElementById("edit-product-price").disabled = false;
		} else {
			document.getElementById("edit-product-price").disabled = true;
		}
	}
}

function loadFatherProduct(id) {
	$("#father-product-id").val(id);
}

// Finance
function loadFinancialActivity(e, id) {
	e.preventDefault();
	
	var href = $("#financialDetailsHref" + id).attr('href');
	
	$.get(href, function(activity) {
		$("#employee-full-name").text(activity.employee.firstName + ' ' + activity.employee.lastName);
		$("#employee-personal-number").text(activity.employee.personalNumber);
		$("#gross-salary").text(activity.formattedGrossSalary + ' ALL');
		$("#social-insurance-salary").text(activity.formattedSocialInsuranceSalary + ' ALL');
		$("#health-insurance-salary").text(activity.formattedHealthInsuranceSalary + ' ALL');
		$("#employer-social-insurance").text(activity.formattedEmployerSocialInsurance + ' ALL');
		$("#employer-health-insurance").text(activity.formattedEmployerHealthInsurance + ' ALL');
		$("#employee-social-insurance").text(activity.formattedEmployeeSocialInsurance + ' ALL');
		$("#employee-health-insurance").text(activity.formattedEmployeeHealthInsurance + ' ALL');
		$("#progressive-tax").text(activity.formattedProgressiveTax + ' ALL');
		$("#employer-total-cost").text(activity.formattedTotalEmploymentCost + ' ALL');
		$("#net-salary").text(activity.formattedNetSalary + ' ALL');
		
		if (activity.belowMinimalWage) {
			$("#below-minimal-wage-message").css("display","block");
		} else {
			$("#below-minimal-wage-message").css("display","none");
		}
	});
	
	$('#financeDetailsModal').modal('show');
}

// Employees
function loadEmployee(e, id) {
	e.preventDefault();
	
	var href = $("#editEmployeeHref" + id).attr('href');
	
	$.get(href, function(employee) {
		$("#edit-employee-id").val(employee.id);
		$("#edit-employee-firstname").val(employee.firstName);
		$("#edit-employee-lastname").val(employee.lastName);
		$("#edit-employee-account-number").val(employee.accountNumber);
		$("#edit-employee-birthday").val(employee.birthday);
		$("#edit-employee-personal-number").val(employee.personalNumber);
		$("#edit-employee-email").val(employee.email);
		$("#edit-employee-phone-number").val(employee.phoneNumber);
		
		if (employee.vacancyId != null) {
			$('#vacancies-div').hide();
		} else {
			$('#vacancies-div').show();
		}
		
		if (employee.supervisor) {
			$("#edit-employee-supervisor").prop('checked', true);
		} else {
			$("#edit-employee-supervisor").prop('checked', false);
		}
	});
	
	$('#editEmployeeModal').modal('show');
}

function setDeleteEmployeeId(id) {
	$("#delete-employee-id").val(id);
}

function submitEmployeeDeleteForm() {
	id = $("#delete-employee-id").val();
	$("#deleteEmployeeForm" + id).submit(); // Submit the form
}

function setDetachEmployeeId(id) {
	$("#detach-employee-id").val(id);
}

function submitEmployeeDetachForm() {
	id = $("#detach-employee-id").val();
	$("#detachEmployeeForm" + id).submit(); // Submit the form
}

// USERS 
function loadUser(e, id) {
	e.preventDefault();
	
	var href = $("#editUserHref" + id).attr('href');
	$.get(href, function(user) {
		$("#edit-user-id").val(user.id);
		$("#edit-user-firstname").val(user.firstName);
		$("#edit-user-lastname").val(user.lastName);
		$("#edit-user-username").val(user.username);
		$("#edit-user-phonenumber").val(user.phoneNumber);
		$("#edit-user-role").val(user.roleId);
	});
	
	$('#editUserModal').modal('show');
}

function setChangePasswordUserId(id) {
	$("#password-user-id").val(id);
}

function setDeleteUserId(id) {
	$("#delete-user-id").val(id);
}

function submitUserDeleteForm() {
	id = $("#delete-user-id").val();
	$("#deleteUserForm" + id).submit(); // Submit the form
}

/********************************************** WIZARD *******************************************************/

/*$(document).ready(function() {
    
    var navListItems = $('ul.setup-panel li a'),
        allWells = $('.setup-content');

    allWells.hide();

    navListItems.click(function(e) {
        e.preventDefault();
        var $target = $($(this).attr('href')),
            $item = $(this).closest('li');
        
        if (!$item.hasClass('disabled')) {
            navListItems.closest('li').removeClass('active');
            $item.addClass('active');
            allWells.hide();
            $target.show();
        }
    });
    
    $('ul.setup-panel li.active a').trigger('click');
    
    // DEMO ONLY //
    $('#activate-step-2').on('click', function(e) {
        $('ul.setup-panel li:eq(1)').removeClass('disabled');
        $('ul.setup-panel li a[href="#step-2"]').trigger('click');
        $(this).remove();
    })    
});*/


