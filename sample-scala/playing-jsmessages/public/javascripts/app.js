if (window.console) {
  console.log("Welcome to your Play application's JavaScript!");
  console.log(Messages('en', 'hello', 'Anand'));
  console.log(Messages('fr', 'hello', 'Anand'));
  console.log(Messages('hi', 'hello', 'Anand'));
  console.log(Messages('la', 'hello', 'Anand'));
}

var populateMessages = function() {
	var name = $("#name").val();
	if(name.length > 0) {
		console.log(Messages('en', "hello", "Anand"));
	} else {
		name = "?"
	}
	
	$("#englishPanelContent").html(Messages('en', 'welcome', name));
	$("#hindiPanelContent").html(Messages('hi', 'welcome', name));
	$("#frenchPanelContent").html(Messages('fr', 'welcome', name));
	$("#latinPanelContent").html(Messages('la', 'welcome', name));
}

populateMessages();