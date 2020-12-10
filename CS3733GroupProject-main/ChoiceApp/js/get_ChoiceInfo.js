function checkChoiceLogin() {
    var cid = document.getElementById("cid").value;
    var username = document.getElementById("username").value;
    var usernameBox = document.getElementById("username");
    var cidBox = document.getElementById("cid");

    //make sure text input isn't empty
    if (cid == "") {
        alert("Please enter a cid before continuing");
        return false;
    }
    if (username == "") {
        alert("Please enter a username before continuing");
        return false;
    }

    var password = document.getElementById("password").value;

    usernameBox.value = username;
    usernameBox.setAttribute('readonly', 'readonly');

    var data = {};
    data["choiceID"] = cid;
    data["username"] = username;
    data["password"] = password;

    var js = JSON.stringify(data);
    console.log("JS:" + js);
    var xhr = new XMLHttpRequest();
    xhr.open("POST", SignInChoice_url, true);

    // send the collected data as JSON
    xhr.send(js);

    // This will process results and update HTML as appropriate.
    xhr.onloadend = function() {
        console.log(xhr);
        console.log(xhr.request);

        if (xhr.readyState == XMLHttpRequest.DONE) {
            console.log("XHR:" + xhr.responseText);
            processChoiceInfo(xhr.responseText);
        } else {
            console.log("got an error");
            processChoiceInfo("N/A");
        }
    };

    return false;
}

//displays choice on the page, called after signing into a choice
function processChoiceInfo(result) {
    var choiceDisplay = document.getElementById("choiceDisplay");
    choiceDisplay.style.visibility = "visible";

    var outputChoice = "";

    console.log("res:" + result);

    var js = JSON.parse(result);

    var status = js["statusCode"];

    var choices = js["choice"];

    //getting the username of the current user
    var mid = document.getElementById("username").value;

    for (var i = 0; i < choices.length; i++) {
        //go through list of chocies, should only be 1
        var choice = choices[i];
        console.log(choice);


        var cChosenAlt = choice["chosenAid"];
        if (cChosenAlt != null) {
            processClosedChoice(result);
            return true;
        }

        var cAlt = choice["alternatives"];
        var cDesc = choice["description"];
        var cid = choice["cid"];

        if (status == 200) {
            outputChoice +=
                "<div id=\"refreshChoice" + "\"><a href='javaScript:refreshRequest(\"" + cid + "\")'><img src='refreshIcon.png'></img></a></div>";


            outputChoice +=
                '<div id="description"><h2> Description: </h2>' + cDesc + "<br></div>";

            for (var i = 1; i <= cAlt.length; i++) {
                //console.log("here in alt loop")
                //loop through alternatives

                var alt = cAlt[i - 1];
                var cFeed = alt["feedback"];
                var altVote = alt["votes"];
                var altDesc = alt["description"];

                outputChoice += '<div id="choiceAlt' + i + '"><h2>Alternative ' + i + ":</h2> " + altDesc + "<br></div>";

                var sendInfoClose = alt["aid"];

                outputChoice += "<div id=\"choiceAltBtn" + i + "\"><a href='javaScript:choseAlternativeRequest(\"" + sendInfoClose + "\")'><img src='chooseAlternativeIcon.png'></img></a></div>";

                //prints vote buttons
                outputChoice += printVotes(altVote, mid, sendInfoClose, i);


                var numLikes = 0;
                var numDislikes = 0;

                var finalLike = "";
                var finalDislike = "";

                for (var k = 0; k < altVote.length; k++) {
                    var like = altVote[k]; //current member
                    var memberVoted = like["username"];

                    if (like["kind"] == "approval") {
                        numLikes++;
                        finalLike += " " + memberVoted;
                    } else {
                        numDislikes++;
                        finalDislike += " " + memberVoted;
                    }
                }


                outputChoice += '<div id="likeText' + i + '">Number of Approvals: ' + numLikes + "<br> " + finalLike + "<br></div>";
                outputChoice += '<div id="dislikeText' + i + '">Number of Disapprovals: ' + numDislikes + "<br> " + finalDislike + "<br></div>";

                //feedback
                outputChoice += "<div><b>FeedBack:</b></div>";
                for (var j = 0; j < cFeed.length; j++) {
                    var feed = cFeed[j];

                    var fTime = feed["timestamp"];

                    var d = new Date(fTime);
                    console.log(d);


                    var fContent = feed["content"];
                    var fMember = feed["username"];


                    outputChoice += '<div id="feedback' + i + '"><b>Time ' + ":</b> " + d + "<br><b>Content " + ":</b> " + fContent + "<br><b>Member " + ":</b> " + fMember + "<br></div>";

                }
                //need to get mid still
                var aid = alt["aid"];
                //mid set at tome
                var addFeedback = "feedbackInput" + i;



                outputChoice += "<div id=\"addFeedback" + i + "\"> <input id=\"feedbackInput" + i + "\" placeholder=\"enter feedback" + "\" /> <a href='javaScript:feedbackRequest(\"" + addFeedback + "\",\"" + mid + "\",\"" + aid + "\")'><img src='submitFeedbackIcon.png'></img></a><br></div>";

            }

        } else {
            var msg = js["error"];
            console.log("error:" + msg);
            outputChoice += "<p> error: " + msg + "</p>";
        }
    }
    //console.log(outputChoice);
    choiceDisplay.innerHTML = outputChoice;
}

function printVotes(altVote, mid, sendInfoClose, i) {
    var outputChoice = "";
    var kindA = "approval";
    var kindB = "disapproval";

    if (altVote.length != 0) {

        for (var m = 0; m < altVote.length; m++) {
            var current_like = altVote[m]; //current member

            var memVoted = current_like["username"];
            if (current_like["kind"] == "approval" && memVoted == mid) {
                outputChoice +=
                    "<div id=\"VoteID" + i + "\"><a href='javaScript:voteRequest(\"" + sendInfoClose + "\",\"" + mid + "\",\"" + kindA + "\")'><img src='thumbsUpSelect.png' style='width:5em; height:5em;'></img></a> <a href='javaScript:voteRequest(\"" + sendInfoClose + "\",\"" + mid + "\",\"" + kindB + "\")'><img src='thumbsDown.png' style='width:5em; height:5em;'></img></a></div>";
                return outputChoice;
            } else if (current_like["kind"] == "disapproval" && memVoted == mid) {
                outputChoice +=
                    "<div id=\"VoteID" + i + "\"><a href='javaScript:voteRequest(\"" + sendInfoClose + "\",\"" + mid + "\",\"" + kindA + "\")'><img src='thumbsUp.png' style='width:5em; height:5em;'></img></a> <a href='javaScript:voteRequest(\"" + sendInfoClose + "\",\"" + mid + "\",\"" + kindB + "\")'><img src='thumbsDownSelect.png' style='width:5em; height:5em;'></img></a></div>";
                return outputChoice;
            }
        }
        outputChoice +=
            "<div id=\"VoteID" + i + "\"><a href='javaScript:voteRequest(\"" + sendInfoClose + "\",\"" + mid + "\",\"" + kindA + "\")'><img src='thumbsUp.png' style='width:5em; height:5em;'></img></a> <a href='javaScript:voteRequest(\"" + sendInfoClose + "\",\"" + mid + "\",\"" + kindB + "\")'><img src='thumbsDown.png' style='width:5em; height:5em;'></img></a></div>";
        return outputChoice;
    } else {
        outputChoice +=
            "<div id=\"VoteID" + i + "\"><a href='javaScript:voteRequest(\"" + sendInfoClose + "\",\"" + mid + "\",\"" + kindA + "\")'><img src='thumbsUp.png' style='width:5em; height:5em;'></img></a> <a href='javaScript:voteRequest(\"" + sendInfoClose + "\",\"" + mid + "\",\"" + kindB + "\")'><img src='thumbsDown.png' style='width:5em; height:5em;'></img></a></div>";

    }
    return outputChoice;
}

//displays closed choice on the page, called after signing into a choice
function processClosedChoice(result) {
    var choiceDisplay = document.getElementById("choiceDisplay");
    choiceDisplay.style.visibility = "visible";

    var outputChoice = "";

    console.log("res:" + result);

    var js = JSON.parse(result);

    var status = js["statusCode"];

    var choices = js["choice"];

    for (var i = 0; i < choices.length; i++) {
        //go through list of chocies, should only be 1
        var choice = choices[i];
        console.log(choice);

        var cChosenAlt = choice["chosenAid"];


        var cAlt = choice["alternatives"];
        var cDesc = choice["description"];

        if (status == 200) {
            outputChoice +=
                '<div id="description"><h2> Description: </h2>' + cDesc + "<br></div>";

            for (var i = 1; i <= cAlt.length; i++) {
                //console.log("here in alt loop")
                //loop through alternatives

                var alt = cAlt[i - 1];
                var cFeed = alt["feedback"];
                var altVote = alt["votes"];
                var altDesc = alt["description"];



                //TODO: check this
                if (cChosenAlt == alt["aid"]) {
                    outputChoice += '<div id="choiceAlt' + i + '"><h2>CHOSEN ALTERNATIVE ' + ":</h2> " + altDesc + "<br></div>";

                } else {
                    outputChoice += '<div id="choiceAlt' + i + '"><h2>Alternative ' + i + ":</h2> " + altDesc + "<br></div>";
                }

                var numLikes = 0;
                var numDislikes = 0;

                var finalLike = "";
                var finalDislike = "";

                for (var k = 0; k < altVote.length; k++) {
                    var like = altVote[k]; //current member
                    var memberVoted = like["username"];

                    if (like["kind"] == "approval") {
                        numLikes++;
                        finalLike += " " + memberVoted;
                    } else {
                        numDislikes++;
                        finalDislike += " " + memberVoted;
                    }
                }


                outputChoice += '<div id="likeText' + i + '">Number of Approvals: ' + numLikes + "<br> " + finalLike + "<br></div>";
                outputChoice += '<div id="dislikeText' + i + '">Number of Disapprovals: ' + numDislikes + "<br> " + finalDislike + "<br></div>";

                //feedback
                outputChoice += "<div><b>FeedBack:</b></div>";
                for (var j = 0; j < cFeed.length; j++) {
                    var feed = cFeed[j];

                    var fTime = feed["timestamp"];

                    var d = new Date(fTime);
                    console.log(d);

                    var fContent = feed["content"];
                    var fMember = feed["username"];


                    outputChoice += '<div id="feedback' + i + '"><b>Time ' + ":</b> " + d + "<br><b>Content " + ":</b> " + fContent + "<br><b>Member " + ":</b> " + fMember + "<br></div>";

                }
            }

        } else {
            var msg = js["error"];
            console.log("error:" + msg);
            outputChoice += "<p> error: " + msg + "</p>";
        }
    }
    //console.log(outputChoice);
    choiceDisplay.innerHTML = outputChoice;
}


//add feedback to server
function feedbackRequest(content, mid, aid) {

    var contentValue = document.getElementById(content).value;
    console.log("content:" + content);
    console.log("content:" + contentValue);

    //make sure text input isn't empty
    if (contentValue == "") {
        alert("Please enter feedback continuing");
        return false;
    }
    //feedbackRequest
    var data = {};


    data["timestamp"] = formatDate();
    data["content"] = contentValue; //feedback
    data["member"] = mid; //mid
    data["alternative"] = aid; //aid

    var js = JSON.stringify(data);
    console.log("JS:" + js);
    var xhr = new XMLHttpRequest();
    xhr.open("POST", SubmitFeedback_url, true);

    // send the collected data as JSON
    xhr.send(js);

    // This will process results and update HTML as appropriate.
    xhr.onloadend = function() {
        console.log(xhr);
        console.log(xhr.request);

        if (xhr.readyState == XMLHttpRequest.DONE) {
            console.log("XHR:" + xhr.responseText);
            processChoiceInfo(xhr.responseText);
        } else {
            processChoiceInfo("N/A");
        }
    };
}

//format date
function formatDate() {
    var date;
    date = new Date();
    date = date.getUTCFullYear() + '-' +
        ('00' + (date.getUTCMonth() + 1)).slice(-2) + '-' +
        ('00' + date.getUTCDate()).slice(-2) + ' ' +
        ('00' + date.getUTCHours()).slice(-2) + ':' +
        ('00' + date.getUTCMinutes()).slice(-2) + ':' +
        ('00' + date.getUTCSeconds()).slice(-2) + '.' +
        ('000' + date.getUTCMilliseconds()).slice(-3);
    console.log(date);

    return date;
    //new Date().toISOString().slice(0, 19).replace('T', ' ');
}

//format date
function formatUTCSeconds(seconds) {
    var date;
    date = new Date(seconds);
    date = date.getUTCFullYear() + '-' +
        ('00' + (date.getUTCMonth() + 1)).slice(-2) + '-' +
        ('00' + date.getUTCDate()).slice(-2) + ' ' +
        ('00' + date.getUTCHours()).slice(-2) + ':' +
        ('00' + date.getUTCMinutes()).slice(-2) + ':' +
        ('00' + date.getUTCSeconds()).slice(-2) + '.' +
        ('000' + date.getUTCMilliseconds()).slice(-3);
    console.log(date);

    return date;

}


//send request for chosen althernative
function choseAlternativeRequest(sendRequest) {
    var data = {};
    data["aid"] = sendRequest;

    var js = JSON.stringify(data);
    console.log("JS:" + js);
    var xhr = new XMLHttpRequest();

    xhr.open("POST", ChooseAlternative_url, true);

    // send the collected data as JSON
    xhr.send(js);

    // This will process results and update HTML as appropriate.
    xhr.onloadend = function() {
        console.log(xhr);
        console.log(xhr.request);

        if (xhr.readyState == XMLHttpRequest.DONE) {
            console.log("XHR:" + xhr.responseText);
            processChoiceInfo(xhr.responseText);
        } else {
            processChoiceInfo("N/A");
        }
    };
}

function refreshRequest(cid) {
    var data = {};
    data["cid"] = cid;

    var js = JSON.stringify(data);
    console.log("JS:" + js);
    var xhr = new XMLHttpRequest();

    xhr.open("POST", RefreshChoice_url, true);

    // send the collected data as JSON
    xhr.send(js);

    // This will process results and update HTML as appropriate.
    xhr.onloadend = function() {
        console.log(xhr);
        console.log(xhr.request);

        if (xhr.readyState == XMLHttpRequest.DONE) {
            console.log("XHR:" + xhr.responseText);
            processChoiceInfo(xhr.responseText);
        } else {
            processChoiceInfo("N/A");
        }
    };
}

function voteRequest(aid, mid, kind) {

    var data = {};
    data["alternative"] = aid; //aid
    data["member"] = mid; //mid
    data["kind"] = kind; //approval/disapproval
    // console.log(sendRequest[0]);
    // console.log(sendRequest[1]);
    // console.log(sendRequest[2]);

    var js = JSON.stringify(data);
    console.log("JS:" + js);
    var xhr = new XMLHttpRequest();

    if (kind == "approval") {
        xhr.open("POST", UpVote_url, true);
    } else if (kind == "disapproval") {
        xhr.open("POST", DownVote_url, true);
    }

    // send the collected data as JSON
    xhr.send(js);

    // This will process results and update HTML as appropriate.
    xhr.onloadend = function() {
        console.log(xhr);
        console.log(xhr.request);

        if (xhr.readyState == XMLHttpRequest.DONE) {
            console.log("XHR:" + xhr.responseText);
            processChoiceInfo(xhr.responseText);
        } else {
            processChoiceInfo("N/A");
        }
    };
}