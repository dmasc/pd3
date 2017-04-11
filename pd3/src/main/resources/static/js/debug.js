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
});

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
		$("input[name='idCardNumber']").val("T227468995");
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

function indicateSuccess(element, success) {
	element.attr("style", "color: " + (success ? "white" : "red") + ";");
	element.animate({color: "#296ABD"}, 150);
}
