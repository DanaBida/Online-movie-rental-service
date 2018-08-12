# Online-movie-rental-service

Implemented using server and client:
The implementation of the server is in Java, based on the Thread-Per-Client (TPC) and Reactor pattern.
The implementation of the client is in C++. it run 2 threads: One should read from keyboard while the other should
read from socket. Both threads may write to the socket.

The communication between the server and the client performed using a text based communication protocol, which support renting, listing
and returning of movies, support broadcast messages.

The data base - JSON text database which read when the server starts and updated each time a change is made.
