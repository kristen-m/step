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
    comments.forEach((comment) => {
      const fullComment = createDivElement('', 'full-comment');
      const commentText = createDivElement(comment, 'comment-class');
      const replyButton = document.createElement('button');
      replyButton.textContent = 'Reply';
      replyButton.classList = 'reply-button';
      replyButton.addEventListener('click', () => {
        replyButton.style.display = 'none';
        showReplyField(fullComment);
      });
      fullComment.appendChild(commentText);
      fullComment.appendChild(replyButton);
      commentListElement.append(fullComment);
    })
  });
}

function getLimit() {
  const limit = document.getElementById('limit').value;
  return limit;
}

function createDivElement(text, className) {
  const divElement = document.createElement('div');
  divElement.classList = className;
  divElement.innerText = text;
  return divElement;
}

function showReplyField(fullComment) {
  const replyBox = createDivElement('', 'reply-container');
  const replyForm = document.createElement('form');
  replyForm.action = '/data';
  replyForm.method = 'POST';
  replyBox.appendChild(replyForm);
  const replyText = document.createElement('textarea');
  replyText.name = 'reply-text';
  replyText.innerText = "[Your reply here]";
  replyBox.appendChild(replyText);
  const submitButton = document.createElement('button');
  submitButton.type = 'submit';
  submitButton.value = 'Reply';
  submitButton.innerText = 'Reply';
  replyBox.appendChild(submitButton);
  fullComment.appendChild(replyBox);
}