#! /bin/bash

# Uploads the specified files to all the EC2 instances home directory.

for ip in `cat ~/cs480/HDF5Converter/ips` 
do
    echo "Uploading to ip: $ip"
    scp -r -i ~/AWS/acarbona-east-key-pair.pem $@ ec2-user@$ip:~/
done

