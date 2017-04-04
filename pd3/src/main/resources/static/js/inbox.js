$(function() {
	$("#room-notifications-form input[type='checkbox']").change(function(event) {
		var $form = $("#room-notifications-form");
		
		$.post($form.attr("action"), $form.serialize());
	});
	
	$("div.room-send-panel form").submit(function(event) {
		var $area = $(this).find("textarea");

		if ($area.val().trim().length == 0) {
			event.preventDefault();
			$area.val("");
		}
		$area.focus();
	});
});

function showMoreMessages(roomId, page, size) {
	var link = $("div.room-content a:last");

	$.get("/user/inbox-ajax?roomId=" + roomId + "&page=" + page + "&size="+ size, function(data) {
		link.replaceWith(data);
	});
}
