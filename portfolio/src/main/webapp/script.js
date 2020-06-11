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

/** Creates map of Michelin Star restaurants */
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

    restaurantMap.addListener('click', function(e) {
        user_marker = placeMarkerAndPanTo(e.latLng, restaurantMap, restaurants);
    });
    
  });
}

/** Returns icon corresponding to # of Michelin Stars received */
function getIcon(star){
    return "/images/number_" + star + ".png";
}

/** Returns informative description of restaurant */
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

/** Place marker at clicked point and get closest restaurant information window */
function placeMarkerAndPanTo(latLng, map, restaurants) {
    var marker = new google.maps.Marker({
        position: latLng,
        map: map
    });
    map.panTo(latLng);

    closest = getClosest(latLng, restaurants);
    var infowindow = new google.maps.InfoWindow({
        content: closest.name + " is the nearest Michelin Star restaurant!"
    });

    infowindow.open(map, marker);
  
}

/** Get closest restaurant to given latlng value */
function getClosest(latLng, restaurants){
    minimum_dist = Number.MAX_SAFE_INTEGER;
    closest = restaurants[1];

    min_dist = Number.MAX_SAFE_INTEGER;
    for (i = 0; i < restaurants.length; i++){
        dist = distance(latLng, restaurants[i]);
        if (dist < min_dist){
            min_dist = dist;
            closest = restaurants[i];
        }
    }
    return closest;
    
}

/** Find distance between a latlng value and a Restaurant object */
function distance(latLng, restaurant){
    return Math.pow(Math.pow((latLng.lat() - restaurant.lat), 2) 
    + Math.pow((latLng.lng() - restaurant.lng), 2), 0.5);
}

function loadChart(){
    google.charts.load('current', {packages:["orgchart"]});
    google.charts.setOnLoadCallback(drawChart);
}

/** Draws the organization chart with hoverable data */
function drawChart(){
    var data = new google.visualization.DataTable();
        data.addColumn('string', 'Name');
        data.addColumn('string', 'Manager');
        data.addColumn('string', 'Google Username');

        // For each orgchart box, we display name and position, and hoverable username access
        data.addRows([
          [{'v':'Sundar', 'f':'Sundar<div style="color:red; font-style:italic">CEO Man!</div>'},
           '', 'sundar@'],
          [{'v':'Thomas', 'f':'Thomas<div style="color:red; font-style:italic">Google Cloud Ninja</div>'},
           'Sundar', 'tkurian@'],
          [{'v':'Javier', 'f':'Javier<div style="color:red; font-style:italic">GSuite Legend</div>'},
           'Thomas', 'jsoltero@'],
          [{'v':'Aparna', 'f':'Aparna<div style="color:red; font-style:italic">Apps Engineering Goddess</div>'},
           'Javier', 'apappu@'],
          [{'v':'Jim', 'f':'Jim<div style="color:red; font-style:italic">DRIVEr of Workflow</div>'},
           'Aparna', 'jimgiles@'],
          [{'v':'Bryan', 'f':'Bryan<div style="color:red; font-style:italic">DRIVEr of Backend everything!</div>'},
           'Jim', 'bryanv@'],
          [{'v':'Till', 'f':'Till<div style="color:red; font-style:italic">Aaron\'s old boss</div>'},
           'Bryan', 'till@'],
          [{'v':'Lakshmanan', 'f':'Lakshmanan<div style="color:red; font-style:italic">Wipeout, most importantly</div>'},
           'Till', 'lakshmanans@'],
          [{'v':'Saurabh', 'f':'Saurabh<div style="color:red; font-style:italic">Wipeout wipeout wipeout!! woohoo!</div>'},
           'Lakshmanan', 'saur@'],
          [{'v':'Christian', 'f':'Christian<div style="color:red; font-style:italic">Wipeout wipeout wipeout!! woohoo!</div>'},
           'Lakshmanan', 'cjwert@'],
          [{'v':'Adrian', 'f':'Adrian<div style="color:red; font-style:italic">smol intern</div>'},
           'Saurabh', 'amanhey@'],
          [{'v':'Ankita', 'f':'Ankita<div style="color:green; font-style:italic">smol intern</div>'},
           'Saurabh', 'mitraan@'],
          [{'v':'Danya', 'f':'Danya<div style="color:red; font-style:italic">smol intern</div>'},
           'Saurabh', 'danyagao@'],
        ]);

        var chart = new google.visualization.OrgChart(document.getElementById('chart_div'));
        chart.draw(data, {'allowHtml':true, 'nodeClass': "chart", 'size': 'small'});
}

function loadTree(){
    google.charts.load('current', {packages:['wordtree']});
    google.charts.setOnLoadCallback(drawTree);
}

/** Draws the tree; sentiment value of a phrase ranges from 0(something Ankita is not at all proud of)
    to 10(something Ankita IS proud of!) */
function drawTree(){
    var data = google.visualization.arrayToDataTable(
          [ ['Phrases', 'size', 'sentiment'],
            ['ankita likes eating', 1, 5],
            ['ankita likes cooking', 1, 8],
            ['ankita likes drinking oatmilk', 1, 6],
            ['ankita likes drinking coffee', 1, 3],
            ['ankita is 19 years old', 1, 5], 
            ['ankita is passionate about weddings', 1, 10],
            ['ankita does not have her driver\'s license', 1, 0],
            ['ankita does not like rollercoasters', 1, 3],
            ['ankita is working at Google this summer', 1, 10],
            ['ankita likes eating Thai Eggplant', 1, 8],
            ['ankita likes eating Thai iced tea', 1, 8],
            ['ankita likes eating Ike\'s sandwiches', 1, 8],
          ]
        );

        var options = {
          wordtree: {
            format: 'implicit',
            word: 'ankita',
            colors: ['#c4820e', 'orange', '#003262']
          }
        }

        var chart = new google.visualization.WordTree(document.getElementById('wordtree'));
        chart.draw(data, options);
}

function handleResponse(response) {
    for (var i = 0; i < response.items.length; i++) {
        var item = response.items[i];
        document.getElementById("books").innerHTML += "<br>" + item.volumeInfo.title;
    }
}
