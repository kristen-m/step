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

function makeUglySite() {
  const visuals = document.body;
  const buttons = document.getElementsByClassName('button');
  visuals.style.fontFamily = 'Comic Sans MS';
  visuals.style.color = 'coral';
  visuals.style.backgroundColor = 'cyan';
  for (let x = 0; x < buttons.length; x++) {
    buttons[x].style.fontFamily = 'Comic Sans MS';
  }
  toggleVisibility('graphic')
  toggleVisibility('hide-div');
}

function getComments() {
  const url = '/data?limit=' + getLimit();
  fetch(url).then(response => response.json()).then((comments) => {
    const commentListElement = document.getElementById('comment-container');
    commentListElement.innerText = '';
    for(let i = 0; i < comments.length; i++) {
      const commentText = comments[i]['content'];
      const fullComment = createDivElement('', 'full-comment');
      const commentTextElement = createDivElement(commentText, 'comment-class');
      const replyButton = document.createElement('button');
      replyButton.textContent = 'Reply';
      replyButton.classList = 'reply-button';
      replyButton.addEventListener('click', () => {
        replyButton.style.display = 'none';
        showReplyField(fullComment);
      });
      fullComment.appendChild(commentTextElement);
      fullComment.appendChild(replyButton);
      commentListElement.append(fullComment);
    }
  });
}

function getLimit() {
  const limit = document.getElementById('limit').value;
  return limit;
}

function createDivElement(text, className) {
  const divElement = document.createElement('div');
  divElement.classList.add(className);
  divElement.innerText = text;
  return divElement;
}

function showReplyField(fullComment) {
  const replyBox = createDivElement('', 'reply-container');
  const replyForm = document.createElement('form');
  replyForm.action = '/reply';
  replyForm.method = 'POST';
  const replyText = document.createElement('textarea');
  replyText.name = 'reply-text';
  replyText.innerText = "[Your reply here]";
  replyForm.appendChild(replyText);
  const submitButton = document.createElement('button');
  submitButton.type = 'submit';
  submitButton.value = 'reply-text';
  submitButton.innerText = 'Reply';
  replyForm.appendChild(submitButton);
  replyBox.appendChild(replyForm);
  fullComment.appendChild(replyBox);
}

function viewReplies() {
   fetch('/reply').then(response => response.json()).then((replies) => {
    for (let i = 0; i < replies.length; i++) {
      const replyText = replies[i]['content'];
      document.getElementById('reply-content').innerText = replyText;
    }
  });
}

const locations = [
  ['Twelve Apostles', -38.6621, 143.1051],
  ['Sydney Opera House', -33.8568, 151.2153],
  ['Rocky Mountain NP', 40.3428, -105.6836],
  ['Channel Islands NP', 34.0069, -119.7785],
  ['Zion NP', 37.2982, -113.0263],
  ['Bryce Canyon NP', 37.5930, -112.1871],
  ['Arches NP', 38.7331, -109.5925],
  ['Canyonlands NP', 38.3269, -109.8783],
  ['Death Valley NP', 36.5054, -117.0794],
  ['Capitol Reef NP', 38.3670, -111.2615],
  ['Pinnacles NP', 36.4906, -121.1825],
  ['Joshua Tree NP', 33.8734, -115.9010],
  ['Petrified Forest NP', 34.9100, -109.8068],
  ['Scripps College', 34.1038, -117.7110],
  ['Antelope Valley', 34.7514, -118.2523]
  ];

const centerCoords = new google.maps.LatLng(0, 0);
function initMap() {
  var map = new google.maps.Map(document.getElementById('map'), {
    zoom: 1,
    center: centerCoords
  });
  for (let i = 0; i < locations.length; i++) {  
    var marker = new google.maps.Marker({
    position: new google.maps.LatLng(locations[i][1], locations[i][2]),
    map: map
    });
  }
  /* Saved for reference later
  map.addListener('center_changed', function() {
    window.setTimeout(function() {
    map.panTo(marker.getPosition());
    }, 3000);
  });
  marker.addListener('click', function() {
    map.setZoom(11);
    map.setCenter(marker.getPosition());
  });
  */
}




