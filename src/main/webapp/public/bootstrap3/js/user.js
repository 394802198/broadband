(function($){
	
	$.scrollUp({
        scrollName: 'scrollUp', // Element ID
        topDistance: '300', // Distance from top before showing element (px)
        topSpeed: 300, // Speed back to top (ms)
        animation: 'fade', // Fade, slide, none
        animationInSpeed: 200, // Animation in speed (ms)
        animationOutSpeed: 200, // Animation out speed (ms)
        scrollText: '', // Text for element
        activeOverlay: false // Set CSS color to display scrollUp active point, e.g '#00FFFF'
    });
	
	$('a[data-toggle="tooltip"]').tooltip();
	
	$('span[csserrorclass="error"]').each(function(i){
		if (i == 0)
			$(this).closest('div.form-group').find('.form-control').focus();
		$(this).closest('div.form-group').addClass('has-error');
		console.log('error');
	});
	
})($);