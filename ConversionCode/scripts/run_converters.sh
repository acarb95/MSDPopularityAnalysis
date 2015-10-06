#! /bin/bash

# Starts 26 processes on the EC2 instance. Each process is responsible for one letter to convert. 
# Clears out nohup.out before running. 
# run_converters.sh <ip_addr> <start_letter_index> <end_letter_index>
# Ex: run on letters CD
#    run_converters.sh <ip> 2 4

IP=$1
START=$2
END=$3

if [ -n "$4" ]; then
    LETTERLIST=`cat $4`
fi

echo "Starting 26 processes..."

for letter in {A..Z};
do
    DIRECTORY=/mnt/MSD
    if [ -n "$LETTERLIST" ]; then
        if [[ $LETTERLIST == *"$letter"* ]]; then
            echo $letter
            nohup gnome-terminal -x bash -c "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -t -i ~/AWS/acarbona-east-key-pair.pem ec2-user@$IP 'cd /mnt/MSD/output; sudo rm -rf *; cd ~; sudo rm -f nohup.out; sudo nohup bash run_converter.sh $DIRECTORY/data/$letter $DIRECTORY/output $START $END; bash;'" &
        fi
    else
        echo $letter
        nohup gnome-terminal -x bash -c "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -t -i ~/AWS/acarbona-east-key-pair.pem ec2-user@$IP 'cd /mnt/MSD/output; sudo rm -rf *; cd ~; sudo rm -f nohup.out; sudo nohup bash run_converter.sh $DIRECTORY/data/$letter $DIRECTORY/output $START $END; bash;'" &
    fi
done
