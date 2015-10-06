ps auxwww | grep -v grep | grep -i $1 | awk '{print $2}' | xargs kill -9
