Program test
use MatData

real*8 ::  temp(10,1),threeD(10,10,10)

temp = 567.
threeD = 989

call matwrite('result.mat','Temperature',temp,'w') 
call matwrite('3D.mat','threeDimension',threeD,'w') 

end program