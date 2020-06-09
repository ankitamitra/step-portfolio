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
 * Adds a random fact to the page.
 */
function addRandomFact() {
  const facts =
      ['Ankita is learning German this summer!',
       'Ankita has two brothers at Google :O', 
       'Ankita loves collecting socks', 
       'Ankita loves Thai food, and luckily lived right across Berkeley Thai House during her sophomore year'];

  // Pick a random fact.
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

/**
 * Fetches JSON String from the server
 */
function getComments() {
    console.log("Hello world!");
  fetch('/data').then(response => response.text()).then((comment) => {
    document.getElementById('comment-container').innerText = comment;
  });
}

function adjustTextarea(h) {
    h.style.height = "20px";
    h.style.height = (h.scrollHeight)+"px";
}

/** Fetches comments from the server and adds them to the DOM. */
function loadComments() {
  fetch('/data?num-results=' + getCommentLimit()).then(response => response.json()).then((comments) => {
    const commentListElement = document.getElementById('comment-list');
    comments.forEach((comment) => {
      commentListElement.appendChild(createCommentElement(comment));
    })
  });
}

/** Creates an element that represents a comment through manual string concatenation*/
function createCommentElement(comment) {
    const commentElement = document.createElement('li');
    commentElement.className = 'comment';
    const nameElement = document.createElement('span');

    var str = comment.name; // name
    str += " at ";
    str += comment.email; // email
    str += " says: ";
    str += comment.subject; // subject
    str += " // ";
    str += comment.comments; // comments
    nameElement.innerText = str;

    commentElement.appendChild(nameElement);
    return commentElement;
}

function getCommentLimit() {
    let searchParams = (new URL(document.location)).searchParams;
    let res = searchParams.get("num-results");
    if(!res || res.length === 0) {
        return "1";
    }
    return res;
}

function deleteComments() {
  if (getConfirmation()){
    const request = new Request('/delete-data', {method: 'POST'});
    fetch(request);   
  }
}

function getConfirmation(){
    return confirm("Are you sure you want to delete all comments");
}

/** Creates a map and adds it to the page. */
function createMap() {

  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: {lat: 23, lng: -42}, zoom: 1.3});

  const tacoMarker = new google.maps.Marker({
    position: {lat: 40.740937, lng: -73.981921},
    map: map,
    title: 'Street Taco',
    animation: google.maps.Animation.BOUNCE
  });
  makeClickable(tacoMarker, map);

  const bushkillMarker = new google.maps.Marker({
    position: {lat: 41.117635, lng: -75.007680},
    map: map,
    title: 'Bushkill Falls',
    animation: google.maps.Animation.BOUNCE
  });
  makeClickable(bushkillMarker, map);

  const buranoMarker = new google.maps.Marker({
    position: {lat: 45.485495, lng: 12.416705},
    map: map,
    title: 'Burano, Italy',
    animation: google.maps.Animation.BOUNCE
  });
  makeClickable(buranoMarker, map);

  const berkeleyMarker = new google.maps.Marker({
    position: {lat: 37.874217, lng: -122.268349},
    map: map,
    title: 'U:Dessert Story, Berkeley',
    animation: google.maps.Animation.BOUNCE
  });
  makeClickable(berkeleyMarker, map);
}

 function makeClickable(marker, map){
    marker.addListener('click', function() {
    map.setZoom(12);
    map.setCenter(marker.getPosition());
  });
 }

function createRestaurantMap() {
  fetch('/restaurant-data').then(response => response.json()).then((restaurants) => {
    const restaurantMap = new google.maps.Map(
        document.getElementById('restaurantMap'),
        {center: {lat: 37, lng: -119}, zoom: 5});

    restaurants.forEach((restaurant) => {
      var marker = new google.maps.Marker(
          {position: {lat: restaurant.lat, lng: restaurant.lng}, map: restaurantMap,
           icon: getIcon(restaurant.star),
           title: restaurant.name});
        var contentString = getBio(restaurant);
      
      var infowindow = new google.maps.InfoWindow({
        content: contentString
      });
      
      marker.addListener('click', function() {
        infowindow.open(map, marker);
      });
      
           
    });
    
    
  });
}

function getIcon(star){
    return "/images/number_" + star + ".png";
}

function getBio(restaurant){
    priceLevel = "";
    switch(restaurant.price){
        case "$":
            priceLevel = "pretty cheap!";
            break;
        case "$$":
            priceLevel = "moderately priced!";
            break;
        case "$$$":
            priceLevel = "expensive!";
            break;
        // $$$$ or higher:
        default:
            priceLevel = "very expensive!";
    }

    return restaurant.name + ", located in " + restaurant.city + ", " + restaurant.region
            + ". It serves " + restaurant.cuisine + " food. \n It is " + priceLevel; 
}
