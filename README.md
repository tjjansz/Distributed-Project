# Distributed-Project
# SOFE 4790 - Distributed Systems

Prof: Dr.Qusay Mahmoud <br>
Group: 8 <br><br>

Names:<br>
Thomas Jansz - 100642111<br>
Arda Celik - 100596185<br>
Ryuji Komai - 100631883<br><br>

In order to reduce the resolution time of a one-way hash such as NTLM, our proposed solution is to produce a lookup table of hashes which is distributed amongst a large pool of nodes. The burden of data storage is over all the nodes, due to the large generated file size of precomputed hash lookup tables. This solution is unique as it is a running dictionary of cleartext passwords and their corresponding hashes. Once generated the user can receive hashes at any time interval and resolve the hash to the decrypted password. This is contrasted with a brute-force attack which would need to be repeated for every hash that is obtained after commencing a previous attack.

Notes: Application currently configured to run client and server on localhost
      It is best to run client and server in seperate folders

Run:
Add jcifs-1.3.19.jar to build path. Or extract using: <br>
jar xf jcifs-1.3.19.jar

Compile java files using: <br>
javac *.java <br><br>

Run Server: <br>
java Server <br> <br>

Run Client: <br>
java Main <br><br>

Application developed and tested on Ubuntu 18.04
