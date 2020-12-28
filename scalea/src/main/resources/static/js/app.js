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

function changeProductPrice() {
	if (document.getElementById("price").disabled == true) {
		document.getElementById("price").disabled = false;
	} else {
		document.getElementById("price").disabled = true;
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
