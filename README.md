
# About the project

In short, the objective is to create a client-server application, measuring its performance in realistic working conditions, in which the client sends the server an AES encrypted file alongside its SHA1 password (and its length) expecting the decrypted version in return.
The main scope is not to create a scalable, state-of-the-art server with decryption microservices, but rather the performance benchmark of the server itself.

# Development choices

Client and server are here meant to be fully independent, code duplication in ServerManager and ClientManager classes is therefore on purpose.
For testing's sake, passwords are here assumed to be solely comprised out of ASCII characters.

# How to run

For the rainbow table:
```
cd rainbow
javac RainbowMain.java
java RainbowMain -dic PWD_FILE
```
Where *PWD_FILE* is the password pool to be used to generate the rainbow table.
The process will generate a .txt containing the dimension of the chain size as its first line, following with the actual rainbow table contents after that.

For the server:
```
cd server
javac ServerMain.java
java ServerMain
```
For the client:
```
cd client
javac ClientMain.java
java ClientMain -dir DIR_NAME -pwd PWD_FILE
```
Where *DIR_NAME* is the directory containing the clear-text files to be used for performance benchmarking and *PWD_FILE* is the file path containing the passwords pool to be used for files AES encryption.


