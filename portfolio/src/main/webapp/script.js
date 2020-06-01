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

/**
 * This function opens the tab we want to access and hides the unnecessary tabs
 */
function openTab(evt, tabName) {
  const tabcontent = document.getElementsByClassName('tabcontent');
  for (let i = 0; i < tabcontent.length; i++) {
    tabcontent[i].style.display = 'none';
  }
  const tablinks = document.getElementsByClassName('tablinks');
  for (let i = 0; i < tablinks.length; i++) {
    tablinks[i].classList.remove('active');
  }
  document.getElementById(tabName).style.display = 'block';
  evt.currentTarget.classList.add('active');
}



function toggleVisibility(id) {
  const x = document.getElementById(id);
  if (x.style.display === 'none') {
    x.style.display = 'block';
  } else {
    x.style.display = 'none';
  }
}

function fixSite() {
  const visuals = document.body;
  const buttons = document.getElementsByClassName('button');
  visuals.style.fontFamily = 'Georgia';
  visuals.style.color = '#000';
  visuals.style.backgroundColor = 'white';
  for (let x = 0; x < buttons.length; x++) {
    buttons[x].style.fontFamily = 'Georgia';
  }
  toggleVisibility('graphic')
  toggleVisibility('hide-div');
}

/**
 * The above code is organized to show each individual step, but we can use an
 * ES6 feature called arrow functions to shorten the code. This function
 * combines all of the above code into a single Promise chain. You can use
 * whichever syntax makes the most sense to you.
 */
function getGreeting() {
  fetch('/data').then(response => response.text()).then((quote) => {
    document.getElementById('greeting-container').innerText = quote;
  });
}
