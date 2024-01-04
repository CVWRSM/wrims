net = Network.read("test/Connectivity-Table.csv")
net.readPriorities("test/priorities.data")
from java.io import PrintWriter,FileWriter
pw1 = PrintWriter(FileWriter("file1.wresl"))
pw2 = PrintWriter(FileWriter("file2.wresl"))
GymUtils.genWRESL(net,pw1,pw2)
pw1.close()
pw2.close()
GymUtils.genWRESL("test/Connectivity-Table.csv","test/priorities.data","wt_cons.wresl","wt_defs.wresl")
