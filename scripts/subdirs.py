#!/usr/bin/python2.7
#https://askubuntu.com/a/779372/129227
import os
import sys

# get the first command line argument
src = sys.argv[1]
for root, dirs, files in os.walk(src):
    for dr in dirs:
        directory = root+"/"+dr
        if len([sub for sub in os.listdir(directory) \
                if os.path.isdir(directory+"/"+sub)]) == 0:
            print(directory)
