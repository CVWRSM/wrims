\ Date: 1921.10
\ Cycle: 01
\ Solver: LpSolve

\ objective function 
Maximize
+ 1.0 a1
+ 1.0 a2
+ 1.0 d3
+ 1.0 d4
-0.1 wg__wg1__a1__slack
-0.1 wg__wg1__a1__surplus
-0.1 wg__wg1__a2__slack
-0.1 wg__wg1__a2__surplus
-0.1 wg__wg2__d3__slack
-0.1 wg__wg2__d3__surplus
-0.1 wg__wg2__d4__slack
-0.1 wg__wg2__d4__surplus

\ constraint
Subject To
g1:  + a1 + a2 + d3 + d4 = 8.0
g2:  + a1 + a2 = 6.0
wg__wg1__a1__ss:  + a1 - mean__wg1 + wg__wg1__a1__slack - wg__wg1__a1__surplus = 0
wg__wg1__a2__ss:  + a2 - mean__wg1 + wg__wg1__a2__slack - wg__wg1__a2__surplus = 0
wg__wg1__mean:  - a1 - a2 + 2.0 mean__wg1 = 0
wg__wg2__d3__ss:  + d3 - mean__wg2 + wg__wg2__d3__slack - wg__wg2__d3__surplus = 0
wg__wg2__d4__ss:  + d4 - mean__wg2 + wg__wg2__d4__slack - wg__wg2__d4__surplus = 0
wg__wg2__mean:  - d3 - d4 + 2.0 mean__wg2 = 0

\ dvar
Bounds
 -inf <= mean__wg1 
 -inf <= mean__wg2 

End




\ objective value:   8.0
