#     Name: Control.py
#   Author: Hao Xie
#   E-mail: hxie@water.ca.gov
#    Phone: 916.456.4400
# Last Rev: 2024.11.06 - zachary.roy@water.ca.gov
#  Purpose: Control Data

import time

# Control class
class Control:
   isLoading = False

   @staticmethod
   def cloneOp(g):
      while Control.isLoading:
         time.sleep(1)
      Control.isLoading=True
      gDi=g.clone()
      Control.isLoading=False
      return gDi
   
