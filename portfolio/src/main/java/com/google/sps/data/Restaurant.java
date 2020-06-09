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

package com.google.sps.data;

/** Represents a Michelin star restaurant at a specific lat lng point. */
public class Restaurant {
  private double lat;
  private double lng;
  private int star;
  private String name;
  private String city;
  private String region;
  private String cuisine;
  private String price;

  public Restaurant(double lat, double lng, int star, String name,
                    String city, String region, String cuisine, String price) {
    this.lat = lat;
    this.lng = lng;
    this.star = star;
    this.name = name;
    this.city = city;
    this.region = region;
    this.cuisine = cuisine;
    this.price = price;
  }
}
