      SUBROUTINE WAIT(I1,I2,I3)                                     
C     TEMPORARY SUBROUTINE FOR NON-HARRIS SITES
C
      REAL TIM                                                          
      I3 = 1                                                   
      IF (I2.EQ.0) THEN                                        
         TIM = I1/120.0                                        
      ELSE IF (I2.EQ.1) THEN                                   
         TIM = .001*I1                                         
      ELSE IF (I2.EQ.2) THEN                                   
         TIM = I1*1.0                                          
      ELSE IF (I2.EQ.3) THEN                                   
         TIM = I1*60.0                                         
      ELSE                                                     
         I3 = 2                                                
C     RETURN                                                   
      ENDIF                                                    
      CALL WAITS (TIM)                                         
      RETURN                                                   
      END                                                      
