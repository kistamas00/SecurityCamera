var ALERT_HTML = '<div class="col-md-8 col-md-offset-2"><div class="alert alert-info">No pictures!</div></div>';

$(document).ready(function() {

	updateContent();
	setInterval(updateContent, 3000);
});

function updateContent() {

	$.ajax({
		url: '/pictures/all',
		type: 'GET',
		success: function(json) {

			var $div = $('#pictures');

			if (json.length == 0) {
				$div.html(ALERT_HTML);
			} else {
				
				if ($div.html() === ALERT_HTML) {
					$div.html('');
				}


				var $children = $div.children('div');
				var diplayedPictures = {};
				for (var i = 0; i < $children.length; i++) {
					var $element = $children.eq(i);

					if (json.indexOf($element.attr('id')) === -1) {
						$element.remove();
					} else {
						diplayedPictures[$element.attr('id')] = 1;
					}
				}

				for (var i in json) {

					if (!(json[i] in diplayedPictures)) {

						var $divElem = $('<div>');
						var $aElem = $('<a>');
						var $imgElem = $('<img>')

						$divElem.attr({
							'id':json[i],
							'class':'col-lg-3 col-md-4 col-xs-6 center-block'
						});

						$aElem.attr('href', 'public/pictures/'+json[i]);

						$imgElem.attr({
							src:'public/pictures/'+json[i],
							style:'margin-bottom: 30px;',
							class:'img-responsive'
						});

						$aElem.append($imgElem);
						$divElem.append($aElem);
						$div.prepend($divElem);
					}
				}
			}
		}
	});
}