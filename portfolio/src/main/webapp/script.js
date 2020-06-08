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

const LOCATIONS = [
  {
    name: 'The Twelve Apostles',
    lat: -38.6621,
    lng: 143.1051
  },
  {
    name: 'Sydney Opera House',
    lat: -33.8568,
    lng: 151.2153
  },
  {
    name: 'Rocky Mountains National Park',
    lat: 40.3428,
    lng: -105.6836
  },
  {
    name: 'Sydney Opera House',
    lat: -33.8568,
    lng: 151.2153
  },
  {
    name: 'Channel Islands National Park', 
    lat: 34.0069,
    lng: -119.7785
  },
  {
    name: 'Zion National Park', 
    lat: 37.2982,
    lng: -113.0263
  },
  {
    name: 'Bryce Canyon National Park', 
    lat: 37.5930, 
    lng: -112.1871
  },
  {
    name: 'Arches National Park', 
    lat: 38.7331, 
    lng: -109.5925
  },
  {
    name: 'Canyonlands National Park', 
    lat: 38.3269, 
    lng: -109.8783
  },
  {
    name: 'Death Valley National Park', 
    lat: 36.5054, 
    lng: -117.0794
  },
  {
    name: 'Capitol Reef National Park', 
    lat: 38.3670, 
    lng: -111.2615
  },
  {
    name: 'Pinnacles National Park', 
    lat: 36.4906, 
    lng: -121.1825
  },
  {
    name: 'Joshua Tree National Park', 
    lat: 33.8734, 
    lng: -115.9010
  },
  {
    name: 'Petrified Forest National Park', 
    lat: 34.9100, 
    lng: -109.8068
  },
  {
    name: 'Scripps College', 
    lat: 34.1038, 
    lng: -117.7110
  },
  {
    name: 'Antelope Valley, California', 
    lat: 34.7514, 
    lng: -118.2523
  }];

const CENTER_COORDS = new google.maps.LatLng(0, 0);
function initMap() {
  let map = new google.maps.Map(document.getElementById('map'), {
    zoom: 1,
    center: CENTER_COORDS
  });
  LOCATIONS.forEach(location => {
  //for (let i = 0; i < LOCATIONS.length; i++)  
    let marker = new google.maps.Marker({
      position: new google.maps.LatLng(location.lat, location.lng),
      animation: google.maps.Animation.DROP,
      map: map
    });
    marker.addListener('click', () => {
      map.setZoom(11);
      map.setCenter(marker.getPosition());
    });
    let infowindow = new google.maps.InfoWindow({
      content: location.name
    });
    marker.addListener('mouseover', () => {
      infowindow.open(map, marker);
    });
    marker.addListener('mouseout', () => {
      infowindow.close();
    });  
  });

}




