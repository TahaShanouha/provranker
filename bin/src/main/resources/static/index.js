window.onload = function(){
	var submit = document.getElementById("submit-btn");
	var errorDiv = document.getElementById("error");
	submit.onclick = function(){
	
		var url = document.getElementById("url").value;
		var pattern = new RegExp('^(https?:\\/\\/)?'+ // protocol
 '((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.)+[a-z]{2,}|'+ // domain name
    '((\\d{1,3}\\.){3}\\d{1,3}))'+ // OR ip (v4) address
    '(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*'+ // port and path
    '(\\?[;&a-z\\d%_.~+=-]*)?'+ // query string
    '(\\#[-a-z\\d_]*)?$','i'); // fragment locater
	
		 if(!pattern.test(url)) {
			error.style.visibility = 'visible'; 
			return false;
		} else {
			return true;
		}
		
	}
}