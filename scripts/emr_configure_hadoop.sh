#!/bin/sh

#Config hadoop config
/usr/share/aws/emr/scripts/configure-hadoop -m mapreduce.child.java.opts=-Xmx2G
/usr/share/aws/emr/scripts/configure-hadoop -m mapreduce.reduce.java.opts=-Xmx2G
/usr/share/aws/emr/scripts/configure-hadoop -m mapreduce.reduce.memory.mb=3072