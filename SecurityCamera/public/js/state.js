var init = false;

$(document).ready(function() {

	updateContent();
	setInterval(updateContent, 3000);
});

function updateContent() {
	
	$.ajax({
		url: '/status',
		type: 'GET',
		success: function(json){
			
			var $securityCameraTableBody = $('#sc-state-table-body');
			var $systemTableBody = $('#system-state-table-body');
			
			if (init === true) {

				for (i in json.securityCameraStatus) {

					var element = json.securityCameraStatus[i];

					switch (element.type) {

						case "PROGRESSBAR": {
							setProgressBar($('#'+element.id), element.value);
							break;
						}
						default: {
							setValue($('#'+element.id), element.value, json[element.id]);
							break;
						}
					}
				}

				for (i in json.systemStatus) {

					var element = json.systemStatus[i];

					switch (element.type) {

						case "PROGRESSBAR": {
							setProgressBar($('#'+element.id), element.value);
							break;
						}
						default: {
							setValue($('#'+element.id), element.value, json[element.id]);
							break;
						}
					}
				}

			} else {

				init = true;

				for (i in json.securityCameraStatus) {

					var element = json.securityCameraStatus[i];

					switch (element.type) {

						case "PROGRESSBAR": {

							var $trElement = $('<tr>');
							var $tdElement = $('<td>');

							$trElement.html('<td>'+element.name+'</td>');
							$trElement.append($tdElement);
							$tdElement.append(createProgressBar(element.id, element.value));

							$securityCameraTableBody.append($trElement);

							break;
						}
						default: {

							var $trElement = $('<tr>');
							var $tdElement = $('<td>');
							var $spanElement = $('<span>');

							if (json[element.id] === true) {
								$spanElement.attr({
									'id':element.id,
									'class':'label label-success'
								});
							} else if (json[element.id] === false) {
								$spanElement.attr({
									'id':element.id,
									'class':'label label-danger'
								});
							} else {
								$spanElement.attr({
									'id':element.id
								});
							}

							$trElement.html('<td>'+element.name+'</td>');
							$trElement.append($tdElement);
							$tdElement.append($spanElement);
							$spanElement.html(element.value);

							$securityCameraTableBody.append($trElement);

							break;
						}
					}
				}

				for (i in json.systemStatus) {

					var element = json.systemStatus[i];

					switch (element.type) {

						case "PROGRESSBAR": {

							var $trElement = $('<tr>');
							var $tdElement = $('<td>');

							$trElement.html('<td>'+element.name+'</td>');
							$trElement.append($tdElement);
							$tdElement.append(createProgressBar(element.id, element.value));

							$systemTableBody.append($trElement);

							break;
						}
						default: {

							var $trElement = $('<tr>');
							var $tdElement = $('<td>');
							var $spanElement = $('<span>');

							if (json[element.id] === true) {
								$spanElement.attr({
									'id':element.id,
									'class':'label label-success'
								});
							} else if (json[element.id] === false) {
								$spanElement.attr({
									'id':element.id,
									'class':'label label-danger'
								});
							} else {
								$spanElement.attr({
									'id':element.id
								});
							}

							$trElement.html('<td>'+element.name+'</td>');
							$trElement.append($tdElement);
							$tdElement.append($spanElement);
							$spanElement.html(element.value);

							$systemTableBody.append($trElement);

							break;
						}
					}
				}
			}
		}
	});
}

function createProgressBar(id, value) {

	var $div = $('<div>');
	$div.attr('class', 'progress marginless');

	var $div2 = $('<div>');
	$div2.attr({
		'id':id,
		'class':'progress-bar progress-bar-striped active',
		'role':'progressbar',
		'aria-valuenow':value,
		'aria-valuemin':'0',
		'aria-valuemax':'100',
		'style':'width:'+value+'%'
	});
	$div2.html(value+'%');

	$div.append($div2);

	return $div;
}

function setValue($element, text, value) {

	if (value === true) {
		$element.attr('class', 'label label-success');
	} else if (value === false) {
		$element.attr('class', 'label label-danger');
	}

	$element.html(text);
}

function setProgressBar($progressBar, value) {

	$progressBar.attr({
		'aria-valuenow':value,
		'style':'width:'+value+'%'
	});
	$progressBar.html(value+'%');
}