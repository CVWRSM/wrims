\ Date: 1921.10
\ Cycle: 01
\ Solver: LpSolve

\ objective function 
Maximize
+ 1.0 d3
+ 1.0 d4
+ 1.0 m5
+ 1.0 m6
+ 1.0 m7
-0.1 wg__ttt__m_group__m5__slack
-0.1 wg__ttt__m_group__m5__surplus
-0.1 wg__ttt__m_group__m6__slack
-0.1 wg__ttt__m_group__m6__surplus
-0.1 wg__ttt__m_group__m7__slack
-0.1 wg__ttt__m_group__m7__surplus

\ constraint
Subject To
g1:  + d3 + d4 + m5 + m6 + m7 = 7.0
g2:  + d3 + d4 = 4.0
wg__ttt__m_group__m5__ss:  + m5 - m_group + wg__ttt__m_group__m5__slack - wg__ttt__m_group__m5__surplus = 0
wg__ttt__m_group__m6__ss:  + m6 - m_group + wg__ttt__m_group__m6__slack - wg__ttt__m_group__m6__surplus = 0
wg__ttt__m_group__m7__ss:  + m7 - m_group + wg__ttt__m_group__m7__slack - wg__ttt__m_group__m7__surplus = 0
wg__ttt__m_group__mean:  - m5 - m6 - m7 + 3.0 m_group = 0

\ dvar
Bounds
d3 >= -3.0 
 -inf <= m_group 

End




\ objective value:   7.000000000000001
