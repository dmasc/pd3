$(function() {
	$( "#user-search-field" ).autocomplete({
		serviceUrl: "/user/find",
	    onSelect: function (suggestion) {
	    	window.location.href = "/user/profile?id=" + suggestion.data;
	    }
	});
});
    