$(document).ready(function() {

	updateContent();
	// setInterval(updateContent, 3000);
});

function updateContent() {

	$.ajax({
		url: '/pictures/all',
		type: 'GET',
		success: function(json){
			
			json.sort();
			json.reverse();
			
			var $div = $('#pictures');
			
			if (json.length > 0)
				$div.html('');
			else
				$div.html('<div class="col-md-8 col-md-offset-2"><span class="text-info">No pictures!</span></div>');
			
			for (var i in json) {
				
				var $divElem = $('<div>');
				var $aElem = $('<a>');
				var $imgElem = $('<img>')
				
				$divElem.attr('class', 'col-lg-3 col-md-4 col-xs-6 center-block')

				$aElem.attr('href', 'public/pictures/'+json[i]);

				$imgElem.attr({
					src:'public/pictures/'+json[i],
					style:'margin-bottom: 30px;',
					class:'img-responsive'
				});

				$aElem.append($imgElem);
				$divElem.append($aElem);
				$div.append($divElem);
			}
		}
	});
}