$(document).ready(function() {

	updateContent();
	setInterval(updateContent, 3000);
});

function updateContent() {
	
	$.ajax({
		url: '/status',
		type: 'GET',
		success: function(json){
			
			var $tableBody = $('#table-body');
			$tableBody.html('');

			for (i in json.status) {
				var element = json.status[i];

				var $trElement = $('<tr>');
				var $tdElement = $('<td>');
				var $spanElement = $('<span>');
				
				if (json[element.id] === true)
					$spanElement.attr('class', 'label label-success');
				else if (json[element.id] === false)
					$spanElement.attr('class', 'label label-danger');

				$trElement.html('<td>'+element.name+'</td>');
				$trElement.append($tdElement);
				$tdElement.append($spanElement);
				$spanElement.html(element.value);

				$tableBody.append($trElement);
			}
		}
	});
}