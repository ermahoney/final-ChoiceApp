//deletes choices n days old
function checkDelete() {
    //check n days old
    var numDays = document.getElementById("numDays").value;

    if (numDays == 0) {
        return true;
    }

    var data = {};
    data["nDays"] = numDays;

    var js = JSON.stringify(data);
    console.log("JS:" + js);
    var xhr = new XMLHttpRequest();
    xhr.open("POST", DeleteChoice_url, true);

    // send the collected data as JSON
    xhr.send(js);

    // This will process results and update HTML as appropriate.
    xhr.onloadend = function() {
        console.log(xhr);
        console.log(xhr.request);

        if (xhr.readyState == XMLHttpRequest.DONE) {
            console.log("XHR:" + xhr.responseText);
            processShowChoicesResponse(xhr.responseText);
        } else {
            processShowChoicesResponse("N/A");
        }
    };

    return true;

}




function showAllChoices() {
    var xhr = new XMLHttpRequest();
    xhr.open("GET", ShowAllChoices_url, true);
    xhr.send();

    console.log("sent");

    // This will process results and update HTML as appropriate. 
    xhr.onloadend = function() {
        if (xhr.readyState == XMLHttpRequest.DONE) {
            console.log("XHR:" + xhr.responseText);
            processShowChoicesResponse(xhr.responseText);
        } else {
            processShowChoicesResponse("N/A");
        }
    };
}

function processShowChoicesResponse(result) {
    //this function should be the same as when you delete all choices as 
    //when you request to see all choices after logging in as an admin

    //console.log("result:" + result);
    var js = JSON.parse(result);
    //console.log("resultAfter:" + js);

    var status = js["statusCode"];
    var choices = js["choice"];

    var tableData = [];
    tableData.push(["CID", "Description", "Date of Creation", "Date of Completion"]);

    if (status == 200) {
        for (var i = 0; i < choices.length; i++) {
            //go through list of chocies
            var choice = choices[i];
            console.log(choice);

            var cid = choice["cid"];
            var cDescription = choice["description"];
            var cDateCreation = choice["dateOfCreation"];
            var cDateComplete = choice["dateOfCompletion"];


            tableData.push([cid, cDescription, cDateCreation, cDateComplete]);

        }

        GenerateTable(tableData);

    } else {
        var msg = js["error"];
        console.log("error:" + msg);
    }
}

function GenerateTable(tableData) {
    //Create a HTML Table element.
    var table = document.createElement("TABLE");
    table.border = "1";

    //Get the count of columns.
    var columnCount = tableData[0].length;

    //Add the header row.
    var row = table.insertRow(-1);
    for (var i = 0; i < columnCount; i++) {
        var headerCell = document.createElement("TH");
        headerCell.innerHTML = tableData[0][i];
        row.appendChild(headerCell);
    }

    //Add the data rows.
    for (var i = 1; i < tableData.length; i++) {
        row = table.insertRow(-1);
        for (var j = 0; j < columnCount; j++) {
            var cell = row.insertCell(-1);
            if (j == 3) {
                if (tableData[i][j] == null) {
                    cell.innerHTML = "Not Completed";
                } else {
                    cell.innerHTML = tableData[i][j];
                }
            } else {
                cell.innerHTML = tableData[i][j];
            }

        }
    }

    var dvTable = document.getElementById("dvTable");
    dvTable.innerHTML = "";
    dvTable.appendChild(table);
}