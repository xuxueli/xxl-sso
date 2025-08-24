$(function(){

	// ---------------------- slideToTop ----------------------

	// slideToTop
	var slideToTop = $("<div />");
	slideToTop.html('<i class="fa fa-chevron-up"></i>');
	slideToTop.css({
		position: 'fixed',
		bottom: '20px',
		right: '25px',
		width: '40px',
		height: '40px',
		color: '#eee',
		'font-size': '',
		'line-height': '40px',
		'text-align': 'center',
		'background-color': '#222d32',
		cursor: 'pointer',
		'border-radius': '5px',
		'z-index': '99999',
		opacity: '.7',
		'display': 'none'
	});
	slideToTop.on('mouseenter', function () {
		$(this).css('opacity', '1');
	});
	slideToTop.on('mouseout', function () {
		$(this).css('opacity', '.7');
	});
	$('.wrapper').append(slideToTop);
	$(window).scroll(function () {
		if ($(window).scrollTop() >= 150) {
			if (!$(slideToTop).is(':visible')) {
				$(slideToTop).fadeIn(500);
			}
		} else {
			$(slideToTop).fadeOut(500);
		}
	});
	$(slideToTop).click(function () {
		$("html,body").animate({		// firefox ie not support body, chrome support body. but found that new version chrome not support body too.
			scrollTop: 0
		}, 100);
	});


	// ---------------------- body fixed ----------------------

	// init body fixed
	$('body').addClass('fixed');


	// ---------------------- menu, sidebar-toggle ----------------------

	// init menu speed
	$('.sidebar-menu').attr('data-animation-speed', 1);

	// init menu status
	if ( 'close' === $.cookie('sidebar_status') ) {
		$('body').addClass('sidebar-collapse');
	} else {
		$('body').removeClass('sidebar-collapse');
	}
	console.log(111);

	// change menu status
	$('.sidebar-toggle').click(function(){
		if ( 'close' === $.cookie('sidebar_status') ) {
			$.cookie('sidebar_status', 'open', { expires: 7 });
		} else {
			$.cookie('sidebar_status', 'close', { expires: 7 });	//$.cookie('the_cookie', '', { expires: -1 });
		}
	});

});
