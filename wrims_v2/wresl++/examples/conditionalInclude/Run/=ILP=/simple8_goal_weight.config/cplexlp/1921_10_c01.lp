\ Date: 1921.10
\ Cycle: 01
\ Solver: XA

\ objective function 
Maximize
-99.0 slack__goalw_2
+ 2.0 w
+ 2.0 x
+ 2.0 y
+ 2.0 z

\ constraint
Subject To
g0:  + x + y <= 100.0
goalw:  + slack__goalw_2 + w - x = 4000.0
goalz:  + z = 2.0
gx4:  + x = 4.0
gy4:  + y = 4.0

\ dvar
Bounds

End




\ objective value:   8028.0
