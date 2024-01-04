import os

f = open('dss_to_txt.txt','r')
t = open('transpose.txt','w')

lines = map(str.strip, f.readlines())

# lines=map(str.strip, lines)

print lines
    
lis = [x.split(",") for x in lines]

for x in zip(*lis):
    for y in x:
        t.write(y+',')
    t.write('\n')