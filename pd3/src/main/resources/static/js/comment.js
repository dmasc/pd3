$(function() {
	$("div.comment form.comment-vote").submit(function(event) {
		event.preventDefault();
		var $form = $(this);
		var like = "like" == $(document.activeElement).val();
		$.post($form.attr("action") + "/" + like, $form.serialize(), function(result) {
			$form.find("input.like:first").attr("src", "/img/thumb-up-" + (result && like ? "black" : "white") + ".svg");
			$form.find("input.like:last").attr("src", "/img/thumb-down-" + (result && !like ? "black" : "white") + ".svg");
		});
	});
	
	$("#movingcommentpanel textarea.comment").keydown(function(e) {
		if (e.keyCode == 27) {
			hideMovingCommentPanel();
		}
	});
});

function showMovingCommentPanel(parentId, topicId, page) {
	var panel = $("#movingcommentpanel");

	$("#movingcommentpanel input[name='commentId']").val(parentId);
	panel.insertAfter("#comment_" + parentId);
	panel.show();
	$("#movingcommentpanel textarea").focus();
}

function hideMovingCommentPanel() {
	$("#movingcommentpanel textarea").text("");
	$("#movingcommentpanel").hide();
}

function submitReply() {
	$("#movingcommentpanel form").submit();
}

function deleteComment(commentId) {
	$("#comment_" + commentId).find(".comment-delete-form").submit();
}