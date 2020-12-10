// all access driven through BASE. Must end with a SLASH
// be sure you change to accommodate your specific API Gateway entry point
var base_url = "https://kz2hm5qn2b.execute-api.us-east-2.amazonaws.com/Iteration_1/";

var CreateChoice_url = base_url + "CreateChoice"; // POST
var SubmitFeedback_url = base_url + "SubmitFeedback"; // POST
var UpVote_url = base_url + "UpVote"; // POST
var DownVote_url = base_url + "DownVote"; // POST
var SignInChoice_url = base_url + "SignInChoice"; // POST
var DeleteChoice_url = base_url + "DeleteChoice"; // POST
var ShowAllChoices_url = base_url + "ShowAllChoices"; // GET
var ChooseAlternative_url = base_url + "ChooseAlternative"; // POST
var RemoveUpVote_url = base_url + "RemoveUpVote"; // POST //use this?
var RemoveDownVote_url = base_url + "RemoveDownVote"; // POST // use this?
var RefreshChoice_url = base_url + "RefreshChoice"; // POST/