// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.


  const QUOTES =
      ['Toit Nups!',
       'NINE-NINE!',
        'Cool cool cool cool cool, no doubt no doubt.',
         'Why is no one having a good time? I specifically requested it.'];

/**
 * Adds a random quote from Brooklyn-99 to the page.
 */
function addRandomQuote() {
  // Pick a random greeting.
  const quote = QUOTES[Math.floor(Math.random() * QUOTES.length)];

  // Add it to the page.
  const quoteContainer = document.getElementById('quote-container');
  quoteContainer.innerText = quote;
}



/**
 * This function opens the tab we want to access and hides the unnecessary tabs
 */
function openTab(evt, tabName) {
    var tabcontent = document.getElementsByClassName("tabcontent");
    for(var i = 0; i <tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    var tablinks = document.getElementsByClassName("tablinks");
    for(i = 0; i< tablinks.length; i++) {
        tablinks[i].classList.remove("active");
    }
    document.getElementById(tabName).style.display = "block";
    evt.currentTarget.classList.add("active");
}



function toggleVisibility(id) {
    var x = document.getElementById(id);
    if (x.style.display === "none") {
    x.style.display = "block";
    } else {
    x.style.display = "none";
  }
}

function fixSite() {
    toggleVisibility("graphic")
    toggleVisibility("hide-div");
    var html = document.getElementById("html");
    var buttons = document.getElementsByClassName("button");
    html.style.fontFamily = "Georgia";
    html.style.color = "#000";
    html.style.backgroundColor = "white";
    for(var x = 0; x < buttons.length; x++) {
        buttons[x].style.fontFamily = "Georgia";
    }
}