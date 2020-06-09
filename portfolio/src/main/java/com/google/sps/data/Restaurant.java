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

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Restaurant {
  public static Restaurant create(double lat, double lng, int star, String name,
                    String city, String region, String cuisine, String price) {
    return new AutoValue_Restaurant(lat, lng, star, name, city, region, cuisine, price);
  }

  abstract double lat();
  abstract double lng();
  abstract int star();
  abstract String name();
  abstract String city();
  abstract String region();
  abstract String cuisine();
  abstract String price();
}
