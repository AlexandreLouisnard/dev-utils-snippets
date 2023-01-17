T[0]='Hello'
T[1]='World'

echo ${T[0]} ${T[1]}

# or
echo ${T[*]}

# or
for V in ${T[*]}
 do echo $V
done