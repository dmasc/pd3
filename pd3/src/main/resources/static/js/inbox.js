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

function showMoreMessages(roomId, lastMsgId) {
	var link = $("div.room-content a:last");

	$.post("/user/inbox-ajax?roomId=" + roomId + "&lastMsgId="+ lastMsgId, function(data) {
		link.replaceWith(data);
	});
}
