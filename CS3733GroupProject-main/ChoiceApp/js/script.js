//has 3 html pages in here

////////////////INITIAL OPTIONS PAGE//////////////////////////

function handleCreateClick() {
    window.location.href = "CreateChoice.html";
}

function handleChoiceLoginClick() {
    window.location.href = "ChoiceSignIn.html";
}

////////////////CREATE CHOICE PAGE/////////////////////////////////////////

function checkCreateChoice() {
    var choiceDesc = document.getElementById("choiceDesc").value;
    var alt1 = document.getElementById("alt1").value;
    var alt2 = document.getElementById("alt2").value;
    var numMembers = document.getElementById("numTeamMem").value;

    if (choiceDesc == "") {
        alert("Please enter a description before continuing");
        return false;
    }

    if (alt1 == "" || alt2 == "") {
        alert("Please enter at least two choices. Do not skip boxes.");
        return false;
    }


    if (numMembers == "") {
        alert("Please enter the number of team members before continuing");
        return false;
    }

    var alternatives = [];
    alternatives.push(alt1);
    alternatives.push(alt2);

    //only adds alts that have a value
    var altString = "alt";
    for (var i = 3; i < 6; i++) {
        var altStringVar = altString + i;

        if (document.getElementById(altStringVar).value != "") {
            alternatives.push(document.getElementById(altStringVar).value);
        }
    }

    var data = {};
    data["description"] = choiceDesc;
    data["alternatives"] = alternatives;
    data["numOfMembers"] = numMembers;

    var js = JSON.stringify(data);
    console.log("JS:" + js);
    var xhr = new XMLHttpRequest();
    xhr.open("POST", CreateChoice_url, true);

    console.log("after post");
    // send the collected data as JSON
    xhr.send(js);
    console.log("after send");
    // This will process results and update HTML as appropriate.
    xhr.onloadend = function() {
        console.log("in function");
        console.log("XHR:" + xhr);
        console.log(xhr.request);

        if (xhr.readyState == XMLHttpRequest.DONE) {
            console.log("XHR:" + xhr.responseText);
            processCreateChoiceResponse(xhr.responseText);
        } else {
            console.log("got an error");
            processCreateChoiceResponse("N/A");
        }
    };

    return false;
}

function processCreateChoiceResponse(result) {
    console.log("result:" + result);
    var js = JSON.parse(result);

    var status = js["statusCode"];
    var choice = js["choice"];

    if (status == 200) {
        var id = choice[0];
        id = id["cid"];

        var choiceText = document.createElement("p");

        var textString = "CHOICE ID IS: <b>" +
            id +
            "</b><br>Please copy and paste this to access your newly created choice.<br> Afterwards navigate to Homepage or Sign into Choice";


        choiceText.innerHTML = textString;

        //make button ID cid to easily add functionality later on
        choiceText.id = id;
        // Insert text
        document.body.appendChild(choiceText);
    } else {
        var msg = js["error"];
        console.log("error:" + msg);

        var textString = "<p> error: " + msg + "</p>";

        choiceText.innerHTML = textString;
    }
}