FUNCTION simplefunction( X1 )  RESULT (ans) 
               
    implicit none

    dll_export simplefunction
                  
    REAL,INTENT(IN)    ::  X1
      
    REAL   :: ans
    
      
    ans =  47*X1
   
END function simplefunction