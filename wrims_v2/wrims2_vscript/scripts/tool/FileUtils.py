import shutil
import os
import Param



def erase(fileOrDirPath):
    
    try:
        os.remove(fileOrDirPath)
    
    except:
        try:
            shutil.rmtree(fileOrDirPath)
        except:
            pass
        


def copyAll(root_src_dir, root_dst_dir):

    if not os.path.exists(root_dst_dir):
        os.mkdir(root_dst_dir)
    
    for src_dir, dirs, files in os.walk(root_src_dir):
        dst_dir = src_dir.replace(root_src_dir, root_dst_dir)
        if not os.path.exists(dst_dir):
            os.mkdir(dst_dir)
        for file_ in files:
            src_file = os.path.join(src_dir, file_)
            dst_file = os.path.join(dst_dir, file_)
            if os.path.exists(dst_file):
                os.remove(dst_file)
            shutil.copy(src_file, dst_dir)
            
def deleteFile(directory, extensionToDelete):
    
    for item in os.listdir(directory):
        filePath = os.path.join(directory, item)
        if os.path.isfile(filePath):
            #print item
            extension = os.path.splitext(item)[1]
            #print extension
            if extensionToDelete in extension:
                # print "File deleted: "+item
                try: 
                    os.remove(filePath)
                    Param.logger.warning("File deleted: "+filePath)
                except:
                    pass       
                
def getRelativePath(targetDir, baseDir):

    return os.path.relpath(targetDir, baseDir)

