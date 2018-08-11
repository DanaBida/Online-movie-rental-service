# All Targets
all: BBclient

BBclient: bin/Client.o bin/ConnectionHandler.o bin/keyboardHandler.o bin/serverHandler.o
	g++ -o bin/BBclient bin/Client.o bin/ConnectionHandler.o bin/keyboardHandler.o bin/serverHandler.o -lboost_system -lboost_thread

# Depends on the source and header files 
bin/Client.o: src/Client.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/Client.o src/Client.cpp
	
bin/ConnectionHandler.o: src/ConnectionHandler.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/ConnectionHandler.o src/ConnectionHandler.cpp

bin/keyboardHandler.o: src/keyboardHandler.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/keyboardHandler.o src/keyboardHandler.cpp
	
#bin/BidiMessagingProtocol.o: src/BidiMessagingProtocol.cpp
#	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/BidiMessagingProtocol.o src/BidiMessagingProtocol.cpp

bin/serverHandler.o: src/serverHandler.cpp
	g++ -g -Wall -Weffc++ -std=c++11 -c -Iinclude -o bin/serverHandler.o src/serverHandler.cpp

#Clean the build directory
clean: 
	rm -f bin/*
