#! /bin/bash

DIRECTORY=$1
OUTPUTDIR=$2
START=$3
END=$4

chars=( {A..Z} )

#Directory should be /mnt/MSD/data/$letter/
echo "Starting 26 processes for $DIRECTORY..."

for ((i=$START; i<$END; i++))
do
    letter=${chars[i]}
    for letter2 in {A..Z}
    do
        DIRECTORY2=$DIRECTORY/$letter/$letter2
        echo "Running on $DIRECTORY2"; 
        for file in $DIRECTORY2
        do
            if [ -d "$file" ] && [ "$file" != "$DIRECTORY" ]; then 
                echo "     Starting process for $file"
                nohup java -classpath ./hdf-java/lib/*:. -Djava.library.path="./hdf-java/lib/linux/" HDF5GettersV2 $file $OUTPUTDIR
            fi
        done
    done
done
