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

package com.google.sps.servlets;

import com.google.sps.data.Restaurant;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Returns restaurant data as a JSON array, e.g. [{"lat": 38.4404675, "lng": -122.7144313}] */
@WebServlet("/restaurant-data")
public class RestaurantDataServlet extends HttpServlet {

  private static final Map<Integer, String> NUMBERS = ImmutableMap.of(
    1, "one", 2, "two", 3, "three");
  private Collection<Restaurant> restaurants;
  private String my_region = "California";
  


  @Override
  public void init() {
    restaurants = new ArrayList<>();
    readRestaurants(1);
    readRestaurants(2);
    readRestaurants(3);
  }

  /** Reads restaurant data from CSV file.
      Dataset can be found here: https://www.kaggle.com/jackywang529/michelin-restaurants */
  private void readRestaurants(int star){
    String path = "/WEB-INF/" + NUMBERS.get(star) + "-stars-michelin-restaurants.csv";
    Scanner scanner = new Scanner(getServletContext().getResourceAsStream(path));
    String line = scanner.nextLine();
    while (scanner.hasNextLine()) {
      line = scanner.nextLine();
      String[] cells = line.split(",");

      String name = cells[0];
      double lat = Double.parseDouble(cells[2]);
      double lng = Double.parseDouble(cells[3]);
      String city = cells[4];
      String region = cells[5];
      String cuisine = cells[7];
      String price = cells[8];
      
      if(region.compareTo(my_region) == 0){
          restaurants.add(new Restaurant(lat, lng, star, name, city, region, cuisine, price));
      }
    }
    scanner.close();
  }


  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    Gson gson = new Gson();
    String json = gson.toJson(restaurants);
    response.getWriter().println(json);
  }
}
