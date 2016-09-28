var FPS = 20;
var isStreamOn = false;
var isArrived = true;
var $actualFrame;
var $nextFrame;

$(document).ready(function() {

	updateContent();
	setInterval(updateContent, 3000);
});

function getStream() {
	
	if (isArrived === true) {
		
		isArrived = false;
		$.ajax({
			url: '/stream/next',
			type: 'GET',
			success: function(data) {

				var $imgElem = $('<img>')
				$imgElem.attr({
					src:data,
					class:'img-responsive center-block'
				});
				
				$imgElem.hide();
				$('#stream').append($imgElem);

				if ($actualFrame !== undefined) {
					$actualFrame.remove();
				}
				if ($nextFrame !== undefined) {
					$nextFrame.show();
				}

				$actualFrame = $nextFrame;
				$nextFrame = $imgElem; 
				
				isArrived = true;
			},
			error: function() {
				isStreamOn = false;
			}
		});
	}
	
	if (isStreamOn === true) {
		setTimeout(getStream, 1000 / FPS);
	}
}

function updateContent() {

	$.ajax({
		url: '/status',
		type: 'GET',
		success: function(json){
			
			if (json.stream === true) {
				
				if (isStreamOn === false) {
					
					$('#stream').html('');

					isStreamOn = true;
					isArrived = true;
					getStream();
				}

			} else {

				isStreamOn = false;
				$('#stream').html('<div class="alert alert-info">No streaming!</div>');
			}
		}
	});
}