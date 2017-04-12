$(function() {
	var timeout = 600, debugpanel = $("div.debug"), timer; 

	debugpanel.hover(function() {
		timer = setTimeout(function() {
			debugpanel.removeClass("hidden");
			debugpanel.animate({
		        height: "100px"
		    }, 200);
		}, timeout);
	}, function() {
		clearTimeout(timer);
		debugpanel.animate({
	        height: "0px"
	    }, 200, function() {
			debugpanel.addClass("hidden");
	    });
	});

	if (window.location.href.indexOf("/public/register") < 0) {
		$("#debug-panel-fillout-register-form").addClass("disabled");
	}
	
	$("#debug-panel-leap-in-time-form input[type='text']").keydown(function(e) {
		if (e.keyCode == 38 || e.keyCode == 40) {
			var $txt = $(this),
			segment = Math.floor(e.target.selectionStart / 3),
			index = segment * 3;
			
			if ($txt.val().length == 14) {
				e.preventDefault();
				$txt.val(debug_changeTimestampSegment($txt.val(), segment, e.keyCode == 38));
				e.target.selectionStart = index;
				e.target.selectionEnd = index + 2;
			}
		} 
	});
	
	$("#debug-panel-leap-in-time-form").submit(function(event) {
		event.preventDefault();
		var $form = $(this);
		$.post($form.attr("action"), $form.serialize(), function(result) {
			if (result != null) {
				$("#debug-panel-current-time").text(result);
			}
			indicateSuccess($("#debug-panel-current-time"), result != null, "black");
		});
	});	
});

function debug_changeTimestampSegment(timestamp, segment, increase) {
	var segmentValue = timestamp.substr(segment * 3, 2), inc = increase ? 1 : -1, 
	d = new Date(
			"20" + timestamp.substr(6, 2), timestamp.substr(3, 2) - 1, timestamp.substr(0, 2),
			timestamp.substr(9, 2), timestamp.substr(12, 2), 0, 0
	);
	switch (segment) {
		case 0: d.setDate(d.getDate() + inc); break;			
		case 1: d.setMonth(d.getMonth() + inc); break;			
		case 2: d.setFullYear(d.getFullYear() + inc); break;			
		case 3: d.setHours(d.getHours() + inc); break;			
		case 4: d.setMinutes(d.getMinutes() + inc); break;			
	}
	
	return debug_ensureTwoDigits(d.getDate()) + "." + debug_ensureTwoDigits(d.getMonth() + 1) + "." + debug_ensureTwoDigits(d.getFullYear() % 100) 
			+ " " + debug_ensureTwoDigits(d.getHours()) + ":" + debug_ensureTwoDigits(d.getMinutes());
}

function debug_ensureTwoDigits(number) {
	if (number < 10) {
		return "0" + number;
	}
	return number;
}

function debug_register() {
	if ($("input[name='idCardNumber']").length) {
		$("input[name='email']").val("tester1@mail.de");
		$("input[name='password']").val("tester1");
		$("input[name='passwordRepeat']").val("tester1");
		$("input[name='forename']").val("Tester");
		$("input[name='surname']").val("Eins");
		$("input[name='street']").val("Musterstraße 1");
		$("input[name='zip']").val("21235");
		$("input[name='district']").val("Hamburg");
		$("input[name='phone']").val("0172-1234567");
		$("input[name='birthday']").val("13.09.1955");
		$("input[name='idCardNumber']").val("1220000016D");
		indicateSuccess($("#debug-panel-fillout-register-form"), true);
	} else {
		indicateSuccess($("#debug-panel-fillout-register-form"), false);
	}
}

function debug_receiveMessage() {
	$.post("/test/receive-message", function(result) {
		indicateSuccess($("#debug-panel-receive-message"), result);
	});
}

function indicateSuccess(element, success, color) {
	element.attr("style", "color: " + (success ? "white" : "red") + ";");
	element.animate({color: (color != null ? color : "#296ABD")}, 150);
}
