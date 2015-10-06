#! /bin/bash

# Saves certain letters of the directory to the local disk so the mount can be deleted.
# The mount must be named /mnt/MSD-data
# The directory should be /mnt/MSD/data (already created on the native mount)

DIRECTORY=$3
START=$1
END=$2

if [ ! -n "$DIRECTORY" ]; then
    DIRECTORY=/mnt/MSD
fi

chars=( {A..Z} )

for letter in {A..Z}
do
    for ((i=$START; i<$END; i++))
    do 
	letter2=${chars[i]}
        if [ ! -d "$DIRECTORY/data/" ]; then
            sudo mkdir $DIRECTORY/data/
        fi
        if [ ! -d "$DIRECTORY/data/$letter/" ]; then
            sudo mkdir $DIRECTORY/data/$letter/
        fi
        if [ ! -d "$DIRECTORY/data/$letter/$letter2/" ]; then
            sudo mkdir $DIRECTORY/data/$letter/$letter2/
        fi
        for letter3 in {A..Z}; 
        do
            echo "Copying to $DIRECTORY/data/$letter/$letter2/$letter3"
            sudo cp -r /mnt/MSD-data/data/$letter/$letter2/$letter3 $DIRECTORY/data/$letter/$letter2/
        done
    done
done
