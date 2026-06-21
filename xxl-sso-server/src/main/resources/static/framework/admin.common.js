$(function(){

	/**
	 * 1、slideToTop
	 * 2、menu, sidebar-toggle
	 */

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


	// ---------------------- menu, sidebar-toggle ----------------------

	// init menu speed
	$('.sidebar-menu').attr('data-animation-speed', 1);

	// init menu status
	let sidebar = loadStore('admin_sidebar_status');
	if ( 'close' === sidebar ) {
		$('body').addClass('sidebar-collapse');
	} else {
		$('body').removeClass('sidebar-collapse');
	}

	// change menu status
	$('.sidebar-toggle').click(function(){
		if ( 'close' === loadStore('admin_sidebar_status') ) {
			store('admin_sidebar_status', 'open')
		} else {
			store('admin_sidebar_status', 'close')
		}
	});

	// ---------------------- localStorage ----------------------

	/**
	 * store
	 */
	function store(name, val) {
		if (typeof (Storage) !== "undefined") {
			localStorage.setItem(name, val);
		} else {
			window.alert('Please use a modern browser to properly view this template!');
		}
	}

	/**
	 * get
	 */
	function loadStore(name) {
		if (typeof (Storage) !== "undefined") {
			return localStorage.getItem(name);
		} else {
			window.alert('Please use a modern browser to properly view this template!');
		}
	}

});
