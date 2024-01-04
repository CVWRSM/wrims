\ Date: 1921.10
\ Cycle: 01
\ Solver: LpSolve

\ objective function 
Maximize
+ 6.0 a
+ 6.0 b
-2.0 wg__ttt__group2__a__slack
-2.0 wg__ttt__group2__a__surplus
-2.0 wg__ttt__group2__b__slack
-2.0 wg__ttt__group2__b__surplus
+ 6.0 x
+ 6.0 y
+ 6.0 z

\ constraint
Subject To
g1:  + a + b + x + y + z <= 100.0
g2:  + x <= 3.0
g3:  + b <= 4.0
wg__ttt__group2__a__ss:  + a - group2 + wg__ttt__group2__a__slack - wg__ttt__group2__a__surplus = 0
wg__ttt__group2__b__ss:  + b - group2 + wg__ttt__group2__b__slack - wg__ttt__group2__b__surplus = 0
wg__ttt__group2__mean:  - a - b + 2.0 group2 = 0

\ dvar
Bounds
 -inf <= group2 

End




\ objective value:   600.0
