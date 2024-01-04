Warning: all variables are local except those in "Initial" block.
Warning: condition expression for "If" and "ElseIf" statement can only depend on variables in "initial" block.

Q:How to run examples?
A:Double click the batch files to run.


2012.12.05
WRESL+: A general formulation is applied for obsolete constraints.
for example, if a goal has zero penalties for both lhs>rhs and lhs<rhs, then this goal is obsolete. 
However, to be consistent with other goals the parser will still send the following expression to the evaluator.
lhs + slack - surplus = rhs


2012.12.03
WRESL+: Initial variable overwrite in config file is disabled.





