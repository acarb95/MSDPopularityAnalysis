for ip in `cat ./ips`; do 
   gnome-terminal -x bash -c "ssh -o UserKnownHostsFile=/dev/null -o StrictHostKeyChecking=no -t -i ~/AWS/acarbona-east-key-pair.pem ec2-user@$ip"; 
done
