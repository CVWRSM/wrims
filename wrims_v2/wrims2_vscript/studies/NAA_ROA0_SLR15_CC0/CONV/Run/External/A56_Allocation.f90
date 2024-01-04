!     Last change:  R    10 May 2007
! Dan Easton 5/10/2007

! This function allocates Article 56 carryover and spills based on Table A entitlement and carryover request.

  function a56_allocation(calc, contractor, spill,&
	ta_1,	req_1, &
	ta_2,	req_2, &
	ta_3,	req_3, &
	ta_4,	req_4, &
	ta_5,	req_5, &
	ta_6,	req_6, &
	ta_7,	req_7, &
	ta_8,	req_8, &
	ta_9,	req_9, &
	ta_10,	req_10, &
	ta_11,	req_11, &
	ta_12,	req_12, &
	ta_13,	req_13, &
	ta_14,	req_14, &
	ta_15,	req_15, &
	ta_16,	req_16, & 
	ta_17,	req_17, &
	ta_18,	req_18, &
	ta_19,	req_19, &
	ta_20,	req_20, &
	ta_21,	req_21, &
	ta_22,	req_22, &
	ta_23,	req_23, &
	ta_24,	req_24, &
	ta_25,	req_25, &
	ta_26,	req_26, &
	ta_27,	req_27, &
	ta_28,	req_28, &
	ta_29,	req_29, &
	ta_30,	req_30) RESULT (alloc)

        IMPLICIT NONE
        
        !Table A entitlements, spill, and Article 56 requests are necessary only when allocations are calculated (calc = 1).
        !Otherwise, those parameters are optional and you can pull the previously calculated allocation for a contractor by simply
        !making the following function call:  a56_allocation(0, contractor). That is why the following variable are declared optional.
        
        REAL,OPTIONAL,INTENT(IN) :: spill, ta_1, ta_2, ta_3, ta_4, ta_5, ta_6, ta_7, ta_8, ta_9, ta_10, ta_11, ta_12, ta_13, ta_14, ta_15
        REAL,OPTIONAL,INTENT(IN) :: ta_16, ta_17, ta_18, ta_19, ta_20, ta_21, ta_22, ta_23, ta_24, ta_25, ta_26, ta_27, ta_28, ta_29, ta_30
        REAL,OPTIONAL,INTENT(IN) :: req_1, req_2, req_3, req_4, req_5, req_6, req_7, req_8, req_9, req_10, req_11, req_12, req_13, req_14 
        REAL,OPTIONAL,INTENT(IN) :: req_15, req_16, req_17, req_18, req_19, req_20, req_21, req_22, req_23, req_24, req_25, req_26, req_27 
        REAL,OPTIONAL,INTENT(IN) :: req_28, req_29, req_30
        
        !Calc and contractor are not optional
        INTEGER,INTENT(IN) :: calc, contractor

	!These declared variables are for internal calculations
        REAL,DIMENSION(30) :: allocation, shortage, surplus, ta, req, req_alloc
        REAL :: tot_ta, tot_req, tot_avail, tot_short, tot_surp, adj
        INTEGER :: i, j, stop
        
        !And the result of the function is the final allocation fraction for the given contractor.
        REAL :: alloc
        
        !SAVE final_alloc for retrieval of results for each contractor at each timestep
        SAVE allocation
                
        !If calc == 1, then user want allocations calculated.
        !If calc != 1 (presently setting calc = 0 in wresl calls), bypass if block and
        !pull contractor allocation from previous calculation.
        if (calc==1) then

        	!Initialize TableA and Carryover Request Arrays
        	ta(1) = ta_1
        	ta(2) = ta_2
        	ta(3) = ta_3
        	ta(4) = ta_4
        	ta(5) = ta_5
        	ta(6) = ta_6
        	ta(7) = ta_7
        	ta(8) = ta_8
        	ta(9) = ta_9
        	ta(10) = ta_10
        	ta(11) = ta_11
        	ta(12) = ta_12
        	ta(13) = ta_13
        	ta(14) = ta_14
        	ta(15) = ta_15
        	ta(16) = ta_16
        	ta(17) = ta_17
        	ta(18) = ta_18
        	ta(19) = ta_19
        	ta(20) = ta_20
        	ta(21) = ta_21
        	ta(22) = ta_22
        	ta(23) = ta_23
        	ta(24) = ta_24
        	ta(25) = ta_25
        	ta(26) = ta_26
        	ta(27) = ta_27
        	ta(28) = ta_28
        	ta(29) = ta_29
        	ta(30) = ta_30
        	
        	req(1) = req_1
        	req(2) = req_2
        	req(3) = req_3
        	req(4) = req_4
        	req(5) = req_5
        	req(6) = req_6
        	req(7) = req_7
        	req(8) = req_8
        	req(9) = req_9
        	req(10) = req_10
        	req(11) = req_11
        	req(12) = req_12
        	req(13) = req_13
        	req(14) = req_14
        	req(15) = req_15
        	req(16) = req_16
        	req(17) = req_17
        	req(18) = req_18
        	req(19) = req_19
        	req(20) = req_20
        	req(21) = req_21
        	req(22) = req_22
        	req(23) = req_23
        	req(24) = req_24
        	req(25) = req_25
        	req(26) = req_26
        	req(27) = req_27
        	req(28) = req_28
        	req(29) = req_29
        	req(30) = req_30
        	
        	!Sum TableA and Carryover Request
        	tot_ta = 0.
        	tot_req = 0.
        	do i = 1, 30
        		tot_ta = tot_ta + ta(i)
        		tot_req = tot_req + req(i)
        	end do

        	!Calculate available carryover
        	tot_avail = max(0., tot_req - spill)
        	
        	!Initialize allocation and req_alloc
        	if (tot_avail < 0.005 .or. tot_ta < 0.005) then
        		do i = 1, 30
        			allocation(i) = 0.
        		end do
        		goto 10
        	else
        			
        		do i = 1, 30
        			req_alloc(i) = req(i)/tot_avail
        			allocation(i) = ta(i)/tot_ta
        		end do
        	end if
        	
        	!Allocate carryover and spills.
        	stop = 0
        	j = 0
        	
        	do while (stop == 0)
        	
        		j = j + 1		!loop index, j should not exceed 29 since only 30 contractors
        		tot_short = 0.		!initialize sum of contractor Article 56 shortages
        		tot_surp = 0.		!initialize sum of contractor Article 56 surplus allocation
        		tot_ta = 0.		!initialize sum of Table A entitlements of shorted contractors
        	
        		!With initial allocation calc shortages and surplus and new Table A denominator for short contractors
        		do i = 1, 30
        			surplus(i) = max(0., allocation(i) - req_alloc(i))
        			shortage(i) = max(0., req_alloc(i) - allocation(i))
        			
        			tot_surp = tot_surp + surplus(i)
        			tot_short = tot_short + shortage(i)
        			
        			if (shortage(i) > 0.00001) tot_ta = tot_ta + ta(i)
        		end do
        		
        		!Adjust allocations by allocating available surplus to shorted contractors based on their Table A entitlements
        		do i = 1, 30
        			if (shortage(i) > 0.00001) then
        				adj = 0.
        				if (tot_ta > 0.0005) adj = tot_surp*ta(i)/tot_ta
        				allocation(i) = allocation(i) + adj
        			else
        				allocation(i) = req_alloc(i)
        			end if
        			
        		end do
        		
        		!Check to see if allocation is complete
        		if (tot_surp < 0.0001 .or. tot_short < 0.0001 .or. j==29) stop = 1
        		
        	end do
        	
        end if
        
        10 continue
        
        alloc = allocation(contractor)

  end function a56_allocation

!end program
