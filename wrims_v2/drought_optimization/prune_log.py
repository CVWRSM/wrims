
def prune_log(logfile,outfile):
    with open(logfile,"r") as f:
        with open(outfile,"w") as g:
            for line in f:
                if line.startswith("DEBUG"): continue
                g.write(line)
                
            
            
            

if __name__=="__main__":
    import sys
    prune_log(sys.argv[1],sys.argv[2])