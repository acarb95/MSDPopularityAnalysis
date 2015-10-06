#! /bin/bash

for ip in `cat ~/cs480/HDF5Converter/ips` 
do
    echo "Starting conversion for: $ip"
    run_converters.sh $ip
done

