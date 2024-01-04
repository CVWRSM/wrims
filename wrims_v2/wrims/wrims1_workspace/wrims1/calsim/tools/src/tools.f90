program tools								

    implicit none
    integer cnt, len1, len2, status
    character(120) c, b, message, arg1, arg2

    !call get_command (b, len, status)
    !call get_command_argument (i, c, len, status)
    
    cnt = command_argument_count ()
    if (cnt .eq. 2) then
    
        call get_command_argument (1, arg1, len1, status)
        call get_command_argument (2, arg2, len2, status)
        call process_argument(arg1(1:len1), arg2(1:len2))
        
    else
        message = "commandline argument error !" 
        call error_message( message )   
    
    end if                        
      
end

subroutine process_argument( name, path )

    implicit none
    character*(*) name, path
    character(120) fullpath
    
    !print *, name
    fullpath = path//"\"//"RAN2YEAR.TABLE"
    !print *, trim(fullpath)
    
    if ( trim(name) .eq. "RANGEN" ) then
        call RANGEN(trim(fullpath))
    else
        call error_message( "unknown function..." )    
    endif     

end

subroutine error_message( message )

    implicit none
    character*(*) message

    print *, trim(message)

end