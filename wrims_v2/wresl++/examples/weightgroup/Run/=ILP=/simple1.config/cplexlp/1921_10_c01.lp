\ Date: 1921.10
\ Cycle: 01
\ Solver: LpSolve

\ objective function 
Maximize
+ 6.0 a
+ 6.0 b
+ 6.0 c
-0.1 wg__ttt__a__slack
-0.1 wg__ttt__a__surplus
-0.1 wg__ttt__b__slack
-0.1 wg__ttt__b__surplus
-0.1 wg__ttt__c__slack
-0.1 wg__ttt__c__surplus
+ 5.0 x
+ 5.0 y

\ constraint
Subject To
g1:  + a + b + c + x + y + z <= 100.0
wg__ttt__a__ss:  + a - mean__ttt + wg__ttt__a__slack - wg__ttt__a__surplus = 0
wg__ttt__b__ss:  + b - mean__ttt + wg__ttt__b__slack - wg__ttt__b__surplus = 0
wg__ttt__c__ss:  + c - mean__ttt + wg__ttt__c__slack - wg__ttt__c__surplus = 0
wg__ttt__mean:  - a - b - c + 3.0 mean__ttt = 0

\ dvar
Bounds
 -inf <= mean__ttt 

End




\ objective value:   599.9999999999998
