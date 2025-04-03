# getmytaxi-exercise
Implementation of the code challenge provided by akamas.

## Assumptions
1) In the provided json, there are some mistakes I fixed:
- The attribute "height" has a typo, I changed it from hight to height.
- One wall segment is not consistent: 
  {
  "x1": 10,
  "y1": 8,
  "x2": 11,
  "y2": 9
  }
changed to
  {
  "x1": 10,
  "y1": 8,
  "x2": 10,
  "y2": 9
  }
based on the consistent assumption that movements are only allowed in the x or y axis and not diagonally and as 
consequence walls are also only vertical or horizontal.

